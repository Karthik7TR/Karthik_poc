package com.thomsonreuters.uscl.ereader.gather.step.service.impl;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetaDataGuidParserService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.gather.step.service.RetrieveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.thomsonreuters.uscl.ereader.gather.step.service.impl.CWBRetrieveService.getJobExecutionContext;
import static com.thomsonreuters.uscl.ereader.gather.step.service.impl.CWBRetrieveService.getRequiredStringProperty;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class NovusRetrieveServiceAbstract implements RetrieveService {
    protected final GatherService gatherService;
    protected final BookDefinitionService bookDefinitionService;
    protected final DocMetaDataGuidParserService docMetaDataParserService;

    @Override
    public GatherResponse retrieveToc(BookDefinition bookDefinition, File tocFile, ChunkContext chunkContext) throws Exception {
        final Integer thresholdValue = bookDefinition.getDocumentTypeCodes().getThresholdValue();
        final List<String> splitTocGuidList = new ArrayList<>();
        List<SplitDocument> splitDocuments = null;
        if (bookDefinition.isSplitBook()) {
            splitDocuments = bookDefinition.getSplitDocumentsAsList();
            for (final SplitDocument splitDocument : splitDocuments) {
                splitTocGuidList.add(splitDocument.getTocGuid());
            }
        }
        GatherResponse gatherResponse = receiveGatherResponse(bookDefinition, tocFile, thresholdValue, splitTocGuidList);

        log.debug(gatherResponse.toString());
        if (gatherResponse.getErrorCode() != 0) {
            throw new GatherException(gatherResponse.getErrorMessage(), gatherResponse.getErrorCode());
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
                    log.error(errorMessageBuffer.toString());

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
        return gatherResponse;
    }

    @Override
    public GatherResponse retrieveDocsAndMetadata(BookDefinition bookDefinition, File tocFile, File docsGuidsFile, ChunkContext chunkContext) throws Exception {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);

        final File docsDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_DIR));
        final File docsMetadataDir =
                new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_DOCS_METADATA_DIR));
        final String docCollectionName = bookDefinition.getDocCollectionName();
        docMetaDataParserService.generateDocGuidList(tocFile, docsGuidsFile);

        final List<String> docGuids = readDocGuidsFromTextFile(docsGuidsFile);

        final GatherDocRequest gatherDocRequest = new GatherDocRequest(
                docGuids,
                docCollectionName,
                docsDir,
                docsMetadataDir,
                bookDefinition.isFinalStage(),
                bookDefinition.getUseReloadContent());
        final GatherResponse gatherResponse = gatherService.getDoc(gatherDocRequest);
        log.debug("{}", gatherResponse);

        if (gatherResponse.getErrorCode() != 0) {
            throw new GatherException(gatherResponse.getErrorMessage(), gatherResponse.getErrorCode());
        }
        return gatherResponse;

    }

    abstract protected GatherResponse receiveGatherResponse(BookDefinition bookDefinition, File tocFile, final Integer thresholdValue, final List<String> splitTocGuidList) throws ParseException;

    protected static List<String> readDocGuidsFromTextFile(final File textFile) throws IOException {
        final List<String> lineList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String textLine;
            while ((textLine = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(textLine)) {
                    final int i = textLine.indexOf(",");
                    if (i != -1) {
                        textLine = textLine.substring(0, textLine.indexOf(","));
                        lineList.add(textLine.trim());
                    }
                }
            }
        }
        return lineList;
    }

    protected void duplicateTocCheck(final List<String> splitGuidList, final List<String> dupGuidList) throws Exception {
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
}
