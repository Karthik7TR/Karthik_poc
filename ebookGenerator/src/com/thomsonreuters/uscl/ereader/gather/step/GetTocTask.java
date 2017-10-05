package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 *
 * @author U0105927
 *
 */
public class GetTocTask extends AbstractSbTasklet {
    //TODO: Use logger API to get Logger instance to job-specific appender.
    private static final Logger LOG = LogManager.getLogger(PersistMetadataXMLTask.class);
    private GatherService gatherService;
    private PublishingStatsService publishingStatsService;
    private BookDefinitionService bookDefinitionService;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        GatherResponse gatherResponse = null;
        String publishStatus = "Completed";

        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final File tocFile = new File(jobExecutionContext.getString(JobExecutionKey.GATHER_TOC_FILE));
        final Long jobInstance =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        // TOC
        final String tocCollectionName = bookDefinition.getTocCollectionName();
        final String tocRootGuid = bookDefinition.getRootTocGuid();
        // NORT
        final String nortDomainName = bookDefinition.getNortDomain();
        final String nortExpressionFilter = bookDefinition.getNortFilterView();
        final List<ExcludeDocument> excludeDocuments = bookDefinition.getExcludeDocuments();
        final List<RenameTocEntry> renameTocEntries = bookDefinition.getRenameTocEntries();

        final List<String> splitTocGuidList = new ArrayList<>();
        List<SplitDocument> splitDocuments = null;
        if (bookDefinition.isSplitBook()) {
            splitDocuments = bookDefinition.getSplitDocumentsAsList();

            for (final SplitDocument splitDocument : splitDocuments) {
                splitTocGuidList.add(splitDocument.getTocGuid());
            }
        }
        final Integer thresholdValue = bookDefinition.getDocumentTypeCodes().getThresholdValue();

        Date nortCutoffDate = null;

        if (bookDefinition.getPublishCutoffDate() != null) {
            nortCutoffDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                DateFormatUtils.ISO_DATETIME_FORMAT.format(bookDefinition.getPublishCutoffDate()).replace("T", " ")));
        }
        final PublishingStats jobstats = new PublishingStats();
        try {
            if (tocCollectionName != null) // TOC
            {
                final GatherTocRequest gatherTocRequest = new GatherTocRequest(
                    tocRootGuid,
                    tocCollectionName,
                    tocFile,
                    excludeDocuments,
                    renameTocEntries,
                    bookDefinition.isFinalStage(),
                    splitTocGuidList,
                    thresholdValue);
                LOG.debug(gatherTocRequest);

                gatherResponse = gatherService.getToc(gatherTocRequest);
            } else if (nortDomainName != null) // NORT
            {
                //			GatherNortRequest gatherNortRequest = new GatherNortRequest(nortDomainName, nortExpressionFilter, tocFile, nortCutoffDate, jobInstance);
                final GatherNortRequest gatherNortRequest = new GatherNortRequest(
                    nortDomainName,
                    nortExpressionFilter,
                    tocFile,
                    nortCutoffDate,
                    excludeDocuments,
                    renameTocEntries,
                    bookDefinition.isFinalStage(),
                    bookDefinition.getUseReloadContent(),
                    splitTocGuidList,
                    thresholdValue);
                LOG.debug(gatherNortRequest);

                gatherResponse = gatherService.getNort(gatherNortRequest);
            } else {
                final String errorMessage = "Neither tocCollectionName nor nortDomainName were defined for eBook";
                LOG.error(errorMessage);
                gatherResponse = new GatherResponse(
                    GatherResponse.CODE_UNHANDLED_ERROR,
                    errorMessage,
                    0,
                    0,
                    0,
                    "TOC STEP FAILED UNDEFINED KEY");
            }
            jobstats.setGatherTocDocCount(gatherResponse.getDocCount());
            jobstats.setGatherTocNodeCount(gatherResponse.getNodeCount());
            jobstats.setGatherTocSkippedCount(gatherResponse.getSkipCount());
            jobstats.setGatherTocRetryCount(gatherResponse.getRetryCount());

            // TODO: update doc count used in Job Execution Context

            LOG.debug(gatherResponse);
            if (gatherResponse.getErrorCode() != 0) {
                final GatherException gatherException =
                    new GatherException(gatherResponse.getErrorMessage(), gatherResponse.getErrorCode());
                throw gatherException;
            }

            boolean deletePreviousSplits = false;
            //Error out if the splitGuid does not exist
            if (bookDefinition.isSplitBook()) {
                //Check for duplicate tocGuids for manual splits
                if (splitDocuments != null) {
                    final List<String> splitTocGuids = new ArrayList<>();
                    for (final SplitDocument splitDocument : splitDocuments) {
                        splitTocGuids.add(splitDocument.getTocGuid());
                    }
                    duplicateTocCheck(splitTocGuids, gatherResponse.getDuplicateTocGuids());
                }
                if (gatherResponse.getSplitTocGuidList() != null && gatherResponse.getSplitTocGuidList().size() > 0) {
                    if (bookDefinition.isSplitTypeAuto()) {
                        deletePreviousSplits = true;
                    } else {
                        final StringBuffer errorMessageBuffer =
                            new StringBuffer("TOC/NORT guid provided for the split does not exist.");
                        int i = 1;
                        for (final String tocGuid : gatherResponse.getSplitTocGuidList()) {
                            if (i == gatherResponse.getSplitTocGuidList().size()) {
                                errorMessageBuffer.append(tocGuid);
                            } else {
                                errorMessageBuffer.append(tocGuid + ", ");
                            }
                            i++;
                        }
                        LOG.error(errorMessageBuffer);

                        final GatherException gatherException =
                            new GatherException(errorMessageBuffer.toString(), GatherResponse.CODE_UNHANDLED_ERROR);
                        throw gatherException;
                    }
                }
            }

            if (bookDefinition.isSplitBook() && bookDefinition.isSplitTypeAuto()) {
                final Integer tocNodeCount = gatherResponse.getNodeCount();
                if (tocNodeCount < thresholdValue) {
                    final StringBuffer eMessage = new StringBuffer(
                        "Cannot split the book into parts as node count "
                            + tocNodeCount
                            + " is less than threshold value "
                            + thresholdValue);
                    throw new RuntimeException(eMessage.toString());
                } else if (gatherResponse.isFindSplitsAgain() || deletePreviousSplits) {
                    bookDefinitionService.deleteSplitDocuments(bookDefinition.getEbookDefinitionId());
                }
                //Check for duplicate tocGuids for Auto splits
                duplicateTocCheck(null, gatherResponse.getDuplicateTocGuids());
            }
        } catch (final Exception e) {
            publishStatus = "Failed";
            throw (e);
        } finally {
            jobstats.setJobInstanceId(jobInstance);
            jobstats.setPublishStatus("getToc : " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GATHERTOC);
        }

        return ExitStatus.COMPLETED;
    }

    /**
     * Thows Excpetion if duplicate Toc exists for split book
     * @param splitGuidList
     * @param dupGuidList
     * @throws Exception
     */
    public void duplicateTocCheck(final List<String> splitGuidList, final List<String> dupGuidList) throws Exception {
        if (dupGuidList != null && dupGuidList.size() > 0) {
            final StringBuffer eMessage = new StringBuffer("Duplicate TOC guids Found. Cannot split the book. ");

            if (splitGuidList == null) {
                for (final String dupTocGuid : dupGuidList) {
                    eMessage.append(dupTocGuid + " ");
                }
                throw new RuntimeException(eMessage.toString());
            } else {
                boolean dupFound = false;
                for (final String dupTocGuid : dupGuidList) {
                    if (splitGuidList.contains(dupTocGuid)) {
                        eMessage.append(dupTocGuid + " ");
                        dupFound = true;
                    }
                }
                if (dupFound) {
                    throw new RuntimeException(eMessage.toString());
                }
            }
        }
    }

    @Required
    public void setGatherService(final GatherService gatherService) {
        this.gatherService = gatherService;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }

    @Required
    public void setBookDefinitionService(final BookDefinitionService bookDefinitionService) {
        this.bookDefinitionService = bookDefinitionService;
    }
}
