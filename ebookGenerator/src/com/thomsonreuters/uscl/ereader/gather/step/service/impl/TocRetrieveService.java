package com.thomsonreuters.uscl.ereader.gather.step.service.impl;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetaDataGuidParserService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Slf4j
@Service("tocRetrieveService")
public class TocRetrieveService extends NovusRetrieveServiceAbstract {
    @Autowired
    public TocRetrieveService(GatherService gatherService, BookDefinitionService bookDefinitionService, DocMetaDataGuidParserService docMetaDataParserService) {
        super(gatherService, bookDefinitionService, docMetaDataParserService);
    }

    @Override
    protected GatherResponse receiveGatherResponse(final BookDefinition bookDefinition, final File tocFile, final Integer thresholdValue, final List<String> splitTocGuidList) {
        final String tocCollectionName = bookDefinition.getTocCollectionName();
        final String tocRootGuid = bookDefinition.getRootTocGuid();
        final List<ExcludeDocument> excludeDocuments = bookDefinition.getExcludeDocuments();
        final List<RenameTocEntry> renameTocEntries = bookDefinition.getRenameTocEntries();
        if (tocCollectionName != null) {
            final GatherTocRequest gatherTocRequest = new GatherTocRequest(
                    tocRootGuid,
                    tocCollectionName,
                    tocFile,
                    excludeDocuments,
                    renameTocEntries,
                    bookDefinition.isFinalStage(),
                    splitTocGuidList,
                    thresholdValue);
            log.debug(gatherTocRequest.toString());
            return gatherService.getToc(gatherTocRequest);
        } else {
            final String errorMessage = "tocCollectionName was not defined for eBook";
            log.error(errorMessage);
            return new GatherResponse(
                    GatherResponse.CODE_UNHANDLED_ERROR,
                    errorMessage,
                    0,
                    0,
                    0,
                    "TOC STEP FAILED UNDEFINED KEY");
        }
    }
}
