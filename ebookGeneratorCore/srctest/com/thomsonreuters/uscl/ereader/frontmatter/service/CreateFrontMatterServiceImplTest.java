package com.thomsonreuters.uscl.ereader.frontmatter.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

/**
 * Test the Service that generates HTML for all the Front Matter pages.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a>
 * u0095869
 */
public final class CreateFrontMatterServiceImplTest {
    private CreateFrontMatterServiceImpl frontMatterService;
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
        final String expected = IOUtils.toString(getClass().getResourceAsStream("TitlePage.html"));
        final String actual = frontMatterService.getTitlePage(bookDefinition).replaceAll("(?m)^[ \t]*\r?\n", "");
        assertEquals(expected, actual);
    }

    @Test
    public void testCopyrightPage() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("CopyrightPage.html"));
        assertEquals(expected, frontMatterService.getCopyrightPage(bookDefinition));
    }

    @Test
    public void testCanadianCopyrightPage() throws Exception {
        bookDefinition.setFullyQualifiedTitleId("cw/eg/test");
        final String expected = IOUtils.toString(getClass().getResourceAsStream("CanadianCopyrightPage.html"));
        assertEquals(expected, frontMatterService.getCopyrightPage(bookDefinition));
    }

    @Test
    public void testAdditionaFrontMatterPage1() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("AdditionalFrontMatterPage1.html"));
        assertEquals(expected, frontMatterService.getAdditionalFrontPage(bookDefinition, 1L));
    }

    @Test
    public void testResearchAssistancePage() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("ResearchAssistancePage.html"));
        assertEquals(expected, frontMatterService.getResearchAssistancePage(bookDefinition));
    }

    @Test
    public void testWestlawNextPage() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("WestlawNextPage.html"));
        assertEquals(expected, frontMatterService.getWestlawNextPage(bookDefinition));
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
        frontMatterService = new CreateFrontMatterServiceImpl();
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
        final Map<String, String> logoMap = new HashMap<>();
        logoMap.put("er:#EBook_Generator_TRLogo", "EBook_Generator_TRLogo.png");
        frontMatterService.setFrontMatterLogoPlaceHolder(logoMap);
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
}
