package com.thomsonreuters.uscl.ereader.config;

import java.util.Date;
import java.util.HashSet;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.DocumentTypeCodeService;
import com.thomsonreuters.uscl.ereader.core.book.service.PublisherCodeService;

public final class BookDefinitionUtils {
    private BookDefinitionUtils() {
    }

    public static BookDefinition fillBookDefinition(
        final PublisherCodeService publisherCodeService,
        final DocumentTypeCodeService documentTypeCodeService,
        final String title) {
        final BookDefinition eBook = new BookDefinition();
        eBook.setFullyQualifiedTitleId(title);
        eBook.setProviewDisplayName("Integration Test Book");
        eBook.setCopyright("2012 Copyright Integration Test");
        eBook.setDocCollectionName("invalidCollection");
        eBook.setRootTocGuid("roottocguid");
        eBook.setTocCollectionName("invalidTocCollection");
        eBook.setIsbn("1234");
        eBook.setMaterialId("12345");
        eBook.setSourceType(SourceType.TOC);
        eBook.setAutoUpdateSupportFlag(true);
        eBook.setEbookDefinitionCompleteFlag(true);
        eBook.setIsDeletedFlag(false);
        eBook.setKeyciteToplineFlag(true);
        eBook.setOnePassSsoLinkFlag(true);
        eBook.setSearchIndexFlag(true);
        eBook.setPublishedOnceFlag(false);
        eBook.setEnableCopyFeatureFlag(false);
        eBook.setLastUpdated(new Date());
        eBook.setAuthors(new HashSet<Author>());
        eBook.setEbookNames(new HashSet<EbookName>());
        eBook.setFrontMatterPages(new HashSet<FrontMatterPage>());
        eBook.setExcludeDocuments(new HashSet<ExcludeDocument>());
        final DocumentTypeCode dc = documentTypeCodeService.getDocumentTypeCodeById(1L);
        eBook.setDocumentTypeCodes(dc);
        eBook.setFrontMatterTheme("West");
        eBook.setIsSplitBook(false);
        eBook.setIsSplitTypeAuto(true);

        final PublisherCode publisherCode = publisherCodeService.getPublisherCodeById(1L);
        eBook.setPublisherCodes(publisherCode);

        // Setup Front Matter
        final FrontMatterPage fmp = new FrontMatterPage();
        fmp.setEbookDefinition(eBook);
        fmp.setPageTocLabel("TOC Label");
        fmp.setPageHeadingLabel("Label 1");
        fmp.setSequenceNum(1);
        eBook.getFrontMatterPages().add(fmp);

        final FrontMatterSection fms = new FrontMatterSection();
        fms.setSectionHeading("Section Heading");
        fms.setSectionText("some text");
        fms.setSequenceNum(1);
        fms.setFrontMatterPage(fmp);

        final FrontMatterPdf frontMatterPdf = new FrontMatterPdf();
        frontMatterPdf.setPdfFilename("somefile.pdf");
        frontMatterPdf.setPdfLinkText("Link Text");
        frontMatterPdf.setSection(fms);
        fms.getPdfs().add(frontMatterPdf);

        fmp.getFrontMatterSections().add(fms);

        return eBook;
    }

    public static BookDefinition minimalBookDefinition() {
        BookDefinition bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId("uscl/an/test");
        bookDefinition.setMaterialId("materialId");
        bookDefinition.setCopyright("copyright");
        bookDefinition.setSourceType(BookDefinition.SourceType.NORT);
        bookDefinition.setIsDeletedFlag(false);
        bookDefinition.setEbookDefinitionCompleteFlag(false);
        bookDefinition.setAutoUpdateSupportFlag(true);
        bookDefinition.setSearchIndexFlag(true);
        bookDefinition.setPublishedOnceFlag(false);
        bookDefinition.setOnePassSsoLinkFlag(true);
        bookDefinition.setKeyciteToplineFlag(true);
        bookDefinition.setEnableCopyFeatureFlag(false);
        bookDefinition.setIsSplitBook(false);
        bookDefinition.setIsSplitTypeAuto(true);
        bookDefinition.setLastUpdated(new Date());
        return bookDefinition;
    }
}
