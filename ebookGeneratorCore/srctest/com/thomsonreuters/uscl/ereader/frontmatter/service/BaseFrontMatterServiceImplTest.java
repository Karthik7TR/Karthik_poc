package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BaseFrontMatterServiceImplTest {
    private static final String VALID_ISSN = "0317-8471";
    private static final String TITLE_ID_CW = "cw/eg/test";
    private BaseFrontMatterServiceImpl frontMatterService;
    private BookDefinition bookDefinition;

    @Before
    public void setUp() {
        initializeFrontMatterService();
        initializeBookDefinition();
        createBookNames();
        createAuthors();
        createFrontMatterPages();
    }

    @Test
    public void testTitlePage() throws Exception {
        bookDefinition.setTitlePageImageIncluded(true);
        final String expected = IOUtils.toString(getClass().getResourceAsStream("TitlePage.html"));
        final String actual = frontMatterService.generateTitlePage(bookDefinition, false).replaceAll("(?m)^[ \t]*\r?\n", "");
        assertEquals(expected, actual);
    }

    @Test
    public void testCopyrightPage() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("CopyrightPage.html"));
        assertEquals(expected, frontMatterService.generateCopyrightPage(bookDefinition, false));
    }

    @Test
    public void testCanadianCopyrightPageWithISSN() throws Exception {
        bookDefinition.setFullyQualifiedTitleId(TITLE_ID_CW);
        bookDefinition.setIssn(VALID_ISSN);
        final String expected = IOUtils.toString(getClass().getResourceAsStream("CanadianCopyrightPageWithISSN.html"));
        assertEquals(expected, frontMatterService.generateCopyrightPage(bookDefinition, false));
    }

    @Test
    public void testCanadianCopyrightPageWithoutISSN() throws Exception {
        bookDefinition.setFullyQualifiedTitleId(TITLE_ID_CW);
        final String expected = IOUtils.toString(getClass().getResourceAsStream("CanadianCopyrightPageWithoutISSN.html"));
        assertEquals(expected, frontMatterService.generateCopyrightPage(bookDefinition, false));
    }

    @Test
    public void testAdditionaFrontMatterPage1() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("AdditionalFrontMatterPage1.html"));
        assertEquals(expected, frontMatterService.generateAdditionalFrontMatterPage(bookDefinition, 1L, Collections.emptyMap()));
    }

    @Test
    public void testAdditionaFrontMatterPage1WithPdfs() throws Exception {
        final FrontMatterSection section = bookDefinition.getFrontMatterPages().get(0).getFrontMatterSections().get(0);
        section.setPdfs(getPdfs(section));
        final String expected = IOUtils.toString(getClass().getResourceAsStream("AdditionalFrontMatterPage1WithPdfs.html"));
        assertEquals(expected, frontMatterService.generateAdditionalFrontMatterPage(bookDefinition, 1L, Collections.emptyMap()));
    }

    @Test
    public void testResearchAssistancePage() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("ResearchAssistancePage.html"));
        assertEquals(expected, frontMatterService.generateResearchAssistancePage(bookDefinition, false));
    }

    @Test
    public void testWestlawNextPage() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("WestlawNextPage.html"));
        assertEquals(expected, frontMatterService.generateWestlawNextPage(false));
    }

    private void initializeBookDefinition() {
        bookDefinition = new BookDefinition();
        bookDefinition.setFullyQualifiedTitleId("uscl/an/test");
        bookDefinition.setCurrency("Currency Test");
        bookDefinition.setCopyright("&#169; 2012 Thomson Reuters \r\n Test Copyright");
        bookDefinition.setCopyrightPageText(
                "Copyright is not claimed as to any part of the original work prepared "
                        + "by a United States Government officer or employee as part of that person's official duties."
                        + "\r\nTest paragraph 2.");
        bookDefinition.setIsbn("978-0-314-93983-8");
        bookDefinition.setAdditionalTrademarkInfo(
                "McKINNEY'S is registerd in the U.S. Patent and Trademark Office.\r\n"
                        + "McKINNEY'S NEW YORK CIVIL PRACTICE LAW AND RULES\r\n"
                        + "is a trademark of West Publishing Corporation.");
        bookDefinition.setFrontMatterTheme("Westlaw Next");
        bookDefinition.setIsAuthorDisplayVertical(true);
        PublisherCode publisherCode = new PublisherCode();
        publisherCode.setName(CoreConstants.USCL_PUBLISHER_NAME);
        bookDefinition.setPublisherCodes(publisherCode);
    }

    private void createFrontMatterPages() {
        final FrontMatterPage page = getFrontMatterPage();
        final List<FrontMatterSection> sections = Arrays.asList(
                getFrontMatterSection(page, 10L, "Section 1", "Section 1 Text", 1),
                getFrontMatterSection(page, 11L, "Section 2", "Section 2 Text", 2));
        page.setFrontMatterSections(sections);
        final List<FrontMatterPage> pages = Collections.singletonList(page);
        bookDefinition.setFrontMatterPages(pages);
    }

    private void createBookNames() {
        final List<EbookName> ebookNames = Arrays.asList(
                getEbookName("TEST Title", 1),
                getEbookName("TEST Edition", 2),
                getEbookName("TEST Series", 3));
        bookDefinition.setEbookNames(ebookNames);
    }

    @NotNull
    private EbookName getEbookName(final String bookNameText, final int sequenceNum) {
        final EbookName edition = new EbookName();
        edition.setBookNameText(bookNameText);
        edition.setSequenceNum(sequenceNum);
        return edition;
    }

    private void initializeFrontMatterService() {
        frontMatterService = new BaseFrontMatterServiceImpl();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        frontMatterService
                .setFrontMatterTitlePageTemplate(resourceLoader.getResource("classpath:templates/frontMatterTitleTemplate.xml"));
        frontMatterService
                .setFrontMatterCopyrightPageTemplate(resourceLoader.getResource("classpath:templates/frontMatterCopyrightTemplate.xml"));
        frontMatterService
                .setFrontMatterCanadianCopyrightPageTemplate(resourceLoader.getResource("classpath:templates/frontMatterCanadianCopyrightTemplate.xml"));
        frontMatterService
                .setFrontMatterAdditionalPagesTemplate(resourceLoader.getResource("classpath:templates/frontMatterAdditionalPagesTemplate.xml"));
        frontMatterService
                .setFrontMatterResearchAssistancePageTemplate(resourceLoader.getResource("classpath:templates/frontMatterResearchAssistanceTemplate.xml"));
        frontMatterService
                .setFrontMatterWestlawNextPageTemplate(resourceLoader.getResource("classpath:templates/frontMatterWestlawNextTemplate.xml"));
    }

    private void createAuthors() {
        final List<Author> authors = Arrays.asList(
                getAuthor("John", "Tester1", "Pirate, Arrrrrrrrrrr", 1),
                getAuthor("Joel", "Tester2", "Also a \r\nPirate, Arrrrrrrrrrr", 2),
                getAuthor("Ender", "Tester3", "Another \r\nPirate, Arrrrrrrrrrr", 3));
        bookDefinition.setAuthors(authors);
    }

    @NotNull
    private Author getAuthor(final String authorFirstName, final String authorLastName,
                             final String authorAddlText, final int sequenceNum) {
        final Author author = new Author();
        author.setAuthorFirstName(authorFirstName);
        author.setAuthorLastName(authorLastName);
        author.setAuthorAddlText(authorAddlText);
        author.setSequenceNum(sequenceNum);
        return author;
    }

    @NotNull
    private FrontMatterPage getFrontMatterPage() {
        final FrontMatterPage page = new FrontMatterPage();
        page.setEbookDefinition(bookDefinition);
        page.setId(1L);
        page.setPageTocLabel("Test Additional Page");
        page.setSequenceNum(1);
        return page;
    }

    @NotNull
    private FrontMatterSection getFrontMatterSection(final FrontMatterPage page, final long id, final String sectionHeading,
                                                     final String sectionText, final int sequenceNum) {
        final FrontMatterSection section = new FrontMatterSection();
        section.setFrontMatterPage(page);
        section.setId(id);
        section.setPdfs(new ArrayList<>());
        section.setSectionHeading(sectionHeading);
        section.setSectionText(sectionText);
        section.setSequenceNum(sequenceNum);
        return section;
    }

    @NotNull
    List<FrontMatterPdf> getPdfs(FrontMatterSection section) {
        FrontMatterPdf frontMatterPdf = new FrontMatterPdf();
        frontMatterPdf.setPdfFilename("fileName");
        frontMatterPdf.setPdfLinkText("linkText");
        frontMatterPdf.setSequenceNum(1);
        frontMatterPdf.setId(1L);
        frontMatterPdf.setSection(section);
        return Collections.singletonList(frontMatterPdf);
    }
}
