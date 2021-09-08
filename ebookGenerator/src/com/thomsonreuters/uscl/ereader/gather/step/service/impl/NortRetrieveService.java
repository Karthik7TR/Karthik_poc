package com.thomsonreuters.uscl.ereader.gather.step.service.impl;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetaDataGuidParserService;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Service("nortRetrieveService")
public class NortRetrieveService extends NovusRetrieveServiceAbstract {
    @Autowired
    public NortRetrieveService(final GatherService gatherService,
                               final BookDefinitionService bookDefinitionService,
                               final DocMetaDataGuidParserService docMetaDataParserService) {
        super(gatherService, bookDefinitionService, docMetaDataParserService);
    }

    @Override
    protected GatherResponse receiveGatherResponse(final BookDefinition bookDefinition,
                                                   final File tocFile,
                                                   final Integer thresholdValue,
                                                   final List<String> splitTocGuidList) throws ParseException {
        final String nortDomainName = bookDefinition.getNortDomain();
        final String nortExpressionFilter = bookDefinition.getNortFilterView();
        final List<ExcludeDocument> excludeDocuments = bookDefinition.getExcludeDocuments();
        final List<RenameTocEntry> renameTocEntries = bookDefinition.getRenameTocEntries();
        Date nortCutoffDate = null;
        if (bookDefinition.getPublishCutoffDate() != null) {
            nortCutoffDate = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                    DateFormatUtils.ISO_DATETIME_FORMAT.format(bookDefinition.getPublishCutoffDate()).replace("T", " ")));
        }
        if (nortDomainName != null) {
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
            log.debug(gatherNortRequest.toString());
            return gatherService.getNort(gatherNortRequest);
        } else {
            final String errorMessage = "nortDomainName was not defined for eBook";
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
