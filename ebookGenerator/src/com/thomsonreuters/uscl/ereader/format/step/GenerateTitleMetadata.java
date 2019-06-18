package com.thomsonreuters.uscl.ereader.format.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.assemble.service.TitleMetadataService;
import com.thomsonreuters.uscl.ereader.common.proview.feature.FeaturesListBuilder;
import com.thomsonreuters.uscl.ereader.common.proview.feature.ProviewFeaturesListBuilderFactory;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata.TitleMetadataBuilder;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * This class is responsible for generating title metadata based on information taken from the Job Parameters, Execution Context, and the file-system.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class GenerateTitleMetadata extends AbstractSbTasklet {
    private static final Logger LOG = LogManager.getLogger(GenerateTitleMetadata.class);
    private TitleMetadataService titleMetadataService;

    /**
     * To update publishingStatsService table.
     */
    private PublishingStatsService publishingStatsService;
    private ProviewFeaturesListBuilderFactory featuresListBuilderFactory;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final JobParameters jobParameters = getJobParameters(chunkContext);
        final Long jobId = getJobInstance(chunkContext).getId();
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);
        final String versionNumber = jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);

        final TitleMetadataBuilder titleMetadataBuilder =
            TitleMetadata.builder(bookDefinition).versionNumber(versionNumber);

        //TODO: Remove the calls to these methods when the book definition object is introduced to this step.
//		addAuthors(bookDefinition, titleMetadata);

        final Long jobInstanceId =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

        LOG.debug("Generated title metadata for display name: " + bookDefinition.getProviewDisplayName());

        final File titleXml = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.TITLE_XML_FILE));

        final String tocXmlFile = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE);
        String status = "Completed";

        try (OutputStream titleManifest = new FileOutputStream(titleXml);
            InputStream tocXml = new FileInputStream(tocXmlFile);) {
            final File documentsDirectory =
                new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.ASSEMBLE_DOCUMENTS_DIR));
            //TODO: refactor the titleMetadataService to use the method that takes a book definition instead of a titleManifest object.

            final FeaturesListBuilder featureListBuilder =
                featuresListBuilderFactory.create(bookDefinition).withBookVersion(new Version("v" + versionNumber));
            if (bookDefinition.isSplitBook()) {
                splitBookTitle(titleMetadataBuilder.build(), jobInstanceId, jobExecutionContext);
            } else {
                titleMetadataBuilder
                    .artworkFile(
                        new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.COVER_ART_PATH)))
                    .assetFilesFromDirectory(
                        new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.ASSEMBLE_ASSETS_DIR)))
                    .proviewFeatures(featureListBuilder.getFeatures())
                    .build();
                titleMetadataService.generateTitleManifest(
                    titleManifest,
                    tocXml,
                    titleMetadataBuilder.build(),
                    jobInstanceId,
                    documentsDirectory,
                    JobExecutionKey.ALT_ID_DIR_PATH);
            }
        } catch (final Exception e) {
            status = "Failed";
            throw e;
        } finally {
            final PublishingStats jobstats = new PublishingStats();
            jobstats.setJobInstanceId(jobId);
            jobstats.setPublishStatus("generateTitleManifest : " + status);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }

        return ExitStatus.COMPLETED;
    }

    protected void splitBookTitle(
        final TitleMetadata titleMetadata,
        final Long jobInstanceId,
        final ExecutionContext jobExecutionContext) throws Exception {
        final File splitTitleXml =
            new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.SPLIT_TITLE_XML_FILE));

        final String splitTocXmlFile =
            getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_SPLITTOC_FILE);

        try (OutputStream splitTitleManifest = new FileOutputStream(splitTitleXml);
            InputStream splitTocXml = new FileInputStream(splitTocXmlFile);) {
            final File transformedDocsDir = new File(
                getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));
            final String docToSplitBookFile =
                getRequiredStringProperty(jobExecutionContext, JobExecutionKey.DOC_TO_SPLITBOOK_FILE);
            final String splitNodeInfoFile =
                getRequiredStringProperty(jobExecutionContext, JobExecutionKey.SPLIT_NODE_INFO_FILE);
            titleMetadataService.generateSplitTitleManifest(
                splitTitleManifest,
                splitTocXml,
                titleMetadata,
                jobInstanceId,
                transformedDocsDir,
                docToSplitBookFile,
                splitNodeInfoFile);
        } catch (final Exception e) {
            throw e;
        }
    }

    /**
     * Reads in a list of TOC Guids that are associated to each Doc Guid to later be used
     * for anchor insertion and generates a map.
     *
     * @param docGuidsFile file containing the DOC to TOC guid relationships
     * @return documentlist ordered by file
     */
    protected List<Doc> readTOCGuidList(final File docGuidsFile) throws EBookFormatException {
        try (BufferedReader reader = new BufferedReader(new FileReader(docGuidsFile));) {
            LOG.info("Reading in TOC anchor map file...");
            final List<Doc> docToTocGuidList = reader.lines()
                .map(line -> line.split(",", -1))
                .map(line -> new Doc(line[0], line[0] + ".html", 0, null))
                .collect(Collectors.toCollection(ArrayList::new));
            LOG.info("Generated Doc List " + docToTocGuidList.size());
            return docToTocGuidList;
        } catch (final IOException e) {
            final String message = "Could not read the DOC guid list file: " + docGuidsFile.getAbsolutePath();
            LOG.error(message);
            throw new EBookFormatException(message, e);
        }
    }

    public void setTitleMetadataService(final TitleMetadataService titleMetadataService) {
        this.titleMetadataService = titleMetadataService;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }

    @Required
    public void setFeaturesListBuilderFactory(final ProviewFeaturesListBuilderFactory featuresListBuilderFactory) {
        this.featuresListBuilderFactory = featuresListBuilderFactory;
    }
}
