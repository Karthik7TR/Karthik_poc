package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.DocMetadataServiceContainer;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TocHeadersSubstitutionService {
    private static final int SUBSTITUTING_HEADER_LEVEL = 3;
    private static final String TOP_LEVEL_EBOOK_TOC = "EBook > EBookToc";
    private static final String EBOOK_TOC = "EBookToc";
    private static final String DOCUMENT_GUID = "DocumentGuid";
    private static final String NAME = "Name";

    public void substituteTocHeadersWithDates(final Document tocDocument, final DocMetadataServiceContainer docMetadataService, final boolean shouldSubstituteTocHeaders) {
        if (shouldSubstituteTocHeaders) {
            moveToLevel(tocDocument.select(TOP_LEVEL_EBOOK_TOC), 1, docMetadataService);
        }
    }

    private void moveToLevel(final List<Element> tocItems, final int level, final DocMetadataServiceContainer docMetadataService) {
        tocItems.forEach(tocItem -> {
            if (level < SUBSTITUTING_HEADER_LEVEL) {
                moveToLevel(tocItem.children().stream()
                                .filter(element -> EBOOK_TOC.equals(element.tagName()))
                                .collect(Collectors.toList()),
                        level + 1, docMetadataService);

            } else {
                substituteTocItemName(docMetadataService, tocItem);
            }
        });
    }

    private void substituteTocItemName(final DocMetadataServiceContainer docMetadataService, final Element tocItem) {
        String docUuid = getDocumentUuid(tocItem);
        DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(docUuid);
        String newHeaderText = docMetadata.getCurrencyDefault();
        tocItem.selectFirst(NAME).text(newHeaderText);
    }

    private String getDocumentUuid(final Element tocItem) {
        return tocItem.selectFirst(DOCUMENT_GUID).text();
    }
}
