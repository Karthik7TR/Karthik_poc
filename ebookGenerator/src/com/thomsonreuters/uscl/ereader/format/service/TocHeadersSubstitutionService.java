package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.DocMetadataServiceContainer;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TocHeadersSubstitutionService {
    private static final String TOP_LEVEL_EBOOK_TOC = "EBook > EBookToc";
    private static final String EBOOK_TOC = "EBookToc";
    private static final String DOCUMENT_GUID = "DocumentGuid";
    private static final String NAME = "Name";

    public void substituteTocHeadersWithDates(final Document tocDocument, final DocMetadataServiceContainer docMetadataService,
                                              final Integer substitutingLevel) {
        if (substitutingLevel != null && substitutingLevel > 0) {
            moveToLevel(tocDocument.select(TOP_LEVEL_EBOOK_TOC), 1, docMetadataService, substitutingLevel);
        }
    }

    private void moveToLevel(final List<Element> tocItems, final int level, final DocMetadataServiceContainer docMetadataService,
                             final int substitutingLevel) {
        tocItems.forEach(tocItem -> {
            if (level < substitutingLevel) {
                moveToLevel(tocItem.children().stream()
                                .filter(element -> EBOOK_TOC.equals(element.tagName()))
                                .collect(Collectors.toList()),
                        level + 1, docMetadataService, substitutingLevel);

            } else {
                substituteTocItemName(docMetadataService, tocItem);
            }
        });
    }

    private void substituteTocItemName(final DocMetadataServiceContainer docMetadataService, final Element tocItem) {
        String docUuid = getDocumentUuid(tocItem);
        DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(docUuid);
        String newHeaderText = getNewHeaderText(docMetadata);

        if (newHeaderText != null) {
            tocItem.selectFirst(NAME).text(newHeaderText);
        }
    }

    private String getNewHeaderText(final DocMetadata docMetadata) {
        String currencyDefault = docMetadata.getCurrencyDefault();
        return StringUtils.isNotEmpty(currencyDefault) ? currencyDefault : docMetadata.getFirstlineCite();
    }

    private String getDocumentUuid(final Element tocItem) {
        return tocItem.selectFirst(DOCUMENT_GUID).text();
    }
}
