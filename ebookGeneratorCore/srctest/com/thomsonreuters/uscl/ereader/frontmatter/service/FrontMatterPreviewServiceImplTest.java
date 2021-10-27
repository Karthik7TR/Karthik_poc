package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FrontMatterPreviewServiceImplTest {
    private static final long PAGE_ID = 1L;
    private static final String CW_PUBLISHER = "cw";
    private static final String TITLE_ID = "uscl/an/book";
    private static final String WL_LOGO_PNG = "EBook_Generator_WestlawNextLogo.png";
    private static final String TR_LOGO_PNG = "EBook_Generator_TRLogo.png";
    private static final String CSS_PLACEHOLDER = "er:#ebook_generator";
    private static final String TR_LOGO_PLACEHOLDER = "er:#EBook_Generator_TRLogo";
    private static final String WL_PLACEHOLDER = "er:#WestlawLogo";
    private static final String CSS_REPLACEMENT = "frontMatterCssPreview.mvc?cssName=ebook_generator.css";
    private static final String IMAGE_URL_TEMPLATE = "frontMatterImagePreview.mvc?imageName=%s";
    private static final String TR_LOGO_REPLACEMENT = String.format(IMAGE_URL_TEMPLATE, TR_LOGO_PNG);
    private static final String WL_REPLACEMENT = String.format(IMAGE_URL_TEMPLATE, WL_LOGO_PNG);
    private static final String PDF_URL_TEMPLATE = "frontMatterPdfPreview.mvc?pdfName=%s&publisher=%s";
    private static final String PDF_NAME = "pdf1";
    private static final String PDF_NAME_WITH_EXTENSION = PDF_NAME + ".pdf";
    private static final String PDF_PLACEHOLDER = "er:#" + PDF_NAME;
    private static final String PDF_REPLACEMENT = String.format(PDF_URL_TEMPLATE, PDF_NAME_WITH_EXTENSION, CW_PUBLISHER);
    private static final String COVER_IMAGE_PNG = "uscl_an_book_cover.png";
    private static final String GENERATED_TITLE_PAGE = "<img src=\"er:#titlePageImage-uscl-an-book\" alt=\"titlePageImage\"/>";
    private static final String EXPECTED_MODIFIED_TITLE_PAGE = "<img src=\"coverImage.mvc?imageName=uscl_an_book_cover.png\" alt=\"titlePageImage\"/>";

    private FrontMatterPreviewServiceImpl frontMatterService;
    @Mock
    private BaseFrontMatterService baseFrontMatterService;
    @Mock
    private BookDefinition bookDefinition;

    @Before
    public void setUp() {
        final Map<String, String> placeholders = new HashMap<>();
        placeholders.put(TR_LOGO_PLACEHOLDER, TR_LOGO_PNG);
        placeholders.put(WL_PLACEHOLDER, WL_LOGO_PNG);
        frontMatterService = new FrontMatterPreviewServiceImpl(baseFrontMatterService);
        frontMatterService.setFrontMatterLogoPlaceHolder(placeholders);
        when(bookDefinition.getFullyQualifiedTitleId()).thenReturn(TITLE_ID);
    }

    @Test
    public void testTitlePage() throws Exception {
        when(baseFrontMatterService.generateTitlePage(bookDefinition, false)).thenReturn(CSS_PLACEHOLDER + StringUtils.SPACE + TR_LOGO_PLACEHOLDER);
        final String expected = CSS_REPLACEMENT + StringUtils.SPACE + TR_LOGO_REPLACEMENT;
        final String actual = frontMatterService.getTitlePagePreview(bookDefinition);
        assertEquals(expected, actual);
    }

    @Test
    public void testTitlePageWithCoverImage() throws Exception {
        when(baseFrontMatterService.generateTitlePage(bookDefinition, false)).thenReturn(GENERATED_TITLE_PAGE);
        when(bookDefinition.getCoverImage()).thenReturn(COVER_IMAGE_PNG);
        final String actual = frontMatterService.getTitlePagePreview(bookDefinition);
        assertEquals(EXPECTED_MODIFIED_TITLE_PAGE, actual);
    }

    @Test
    public void testCopyrightPage() throws Exception {
        when(baseFrontMatterService.generateCopyrightPage(bookDefinition, false)).thenReturn(CSS_PLACEHOLDER + StringUtils.SPACE + TR_LOGO_PLACEHOLDER);
        final String expected = CSS_REPLACEMENT + StringUtils.SPACE + TR_LOGO_REPLACEMENT;
        assertEquals(expected, frontMatterService.getCopyrightPagePreview(bookDefinition));
    }

    @Test
    public void testAdditionaFrontMatterPage1() throws Exception {
        when(baseFrontMatterService.generateAdditionalFrontMatterPage(bookDefinition, PAGE_ID, Collections.emptyMap())).thenReturn(CSS_PLACEHOLDER);
        assertEquals(CSS_REPLACEMENT, frontMatterService.getAdditionalFrontPagePreview(bookDefinition, PAGE_ID));
    }

    @Test
    public void testAdditionaFrontMatterPage1WithPdfs() throws Exception {
        when(bookDefinition.getFrontMatterPdfFileNames()).thenReturn(Sets.newSet(PDF_NAME_WITH_EXTENSION));
        PublisherCode publisherCode = new PublisherCode();
        publisherCode.setName(CW_PUBLISHER);
        when(bookDefinition.getPublisherCodes()).thenReturn(publisherCode);
        when(baseFrontMatterService.generateAdditionalFrontMatterPage(bookDefinition, PAGE_ID, Collections.emptyMap())).thenReturn(CSS_PLACEHOLDER + StringUtils.SPACE + PDF_PLACEHOLDER);
        final String expected = CSS_REPLACEMENT + StringUtils.SPACE + PDF_REPLACEMENT;
        assertEquals(expected, frontMatterService.getAdditionalFrontPagePreview(bookDefinition, PAGE_ID));
    }

    @Test
    public void testResearchAssistancePage() throws Exception {
        when(baseFrontMatterService.generateResearchAssistancePage(bookDefinition, false)).thenReturn(CSS_PLACEHOLDER);
        assertEquals(CSS_REPLACEMENT, frontMatterService.getResearchAssistancePagePreview(bookDefinition));
    }

    @Test
    public void testWestlawNextPage() throws Exception {
        when(baseFrontMatterService.generateWestlawNextPage(false)).thenReturn(CSS_PLACEHOLDER + StringUtils.SPACE + WL_PLACEHOLDER);
        final String expected = CSS_REPLACEMENT + StringUtils.SPACE + WL_REPLACEMENT;
        assertEquals(expected, frontMatterService.getWestlawNextPagePreview(bookDefinition));
    }
}
