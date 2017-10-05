package com.thomsonreuters.uscl.ereader.frontmatter.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

/**
 * Test the Service that generates HTML for all the Front Matter pages.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a>
 *         u0095869
 */
public final class CreateFrontMatterServiceImplTest {
    private CreateFrontMatterServiceImpl frontMatterService;
    private BookDefinition bookDefinition;

    @Before
    public void setUp() {
        frontMatterService = new CreateFrontMatterServiceImpl();
        frontMatterService.setResourceLoader(new DefaultResourceLoader());

        frontMatterService.setFrontMatterTitlePageTemplateLocation("classpath:templates/frontMatterTitleTemplate.xml");
        frontMatterService
            .setFrontMatterCopyrightPageTemplateLocation("classpath:templates/frontMatterCopyrightTemplate.xml");
        frontMatterService.setFrontMatterAdditionalPagesTemplateLocation(
            "classpath:templates/frontMatterAdditionalPagesTemplate.xml");
        frontMatterService.setFrontMatterResearchAssistancePageTemplateLocation(
            "classpath:templates/frontMatterResearchAssistanceTemplate.xml");
        frontMatterService
            .setFrontMatterWestlawNextPageTemplateLocation("classpath:templates/frontMatterWestlawNextTemplate.xml");

        final Map<String, String> logoMap = new HashMap<>();
        logoMap.put("er:#EBook_Generator_TRLogo", "EBook_Generator_TRLogo.png");

        frontMatterService.setFrontMatterLogoPlaceHolder(logoMap);

        bookDefinition = new BookDefinition();
        final List<EbookName> ebookNames = new ArrayList<>();
        final EbookName title = new EbookName();
        title.setBookNameText("TEST Title");
        title.setSequenceNum(1);
        ebookNames.add(title);
        final EbookName edition = new EbookName();
        edition.setBookNameText("TEST Edition");
        edition.setSequenceNum(2);
        ebookNames.add(edition);
        final EbookName series = new EbookName();
        series.setBookNameText("TEST Series");
        series.setSequenceNum(3);
        ebookNames.add(series);
        bookDefinition.setEbookNames(ebookNames);
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

        final List<Author> authors = new ArrayList<>();
        final Author author1 = new Author();
        author1.setAuthorFirstName("John");
        author1.setAuthorLastName("Tester1");
        author1.setAuthorAddlText("Pirate, Arrrrrrrrrrr");
        author1.setSequenceNum(1);
        authors.add(author1);

        final Author author2 = new Author();
        author2.setAuthorFirstName("Joel");
        author2.setAuthorLastName("Tester2");
        author2.setAuthorAddlText("Also a \r\nPirate, Arrrrrrrrrrr");
        author2.setSequenceNum(2);
        authors.add(author2);

        final Author author3 = new Author();
        author3.setAuthorFirstName("Ender");
        author3.setAuthorLastName("Tester3");
        author3.setAuthorAddlText("Another \r\nPirate, Arrrrrrrrrrr");
        author3.setSequenceNum(3);
        authors.add(author3);

        bookDefinition.setAuthors(authors);
        bookDefinition.setIsAuthorDisplayVertical(true);

        final List<FrontMatterPage> pages = new ArrayList<>();
        final FrontMatterPage page = new FrontMatterPage();
        page.setEbookDefinition(bookDefinition);
        page.setId(1L);
        page.setPageTocLabel("Test Additional Page");
        page.setSequenceNum(1);

        final List<FrontMatterSection> sections = new ArrayList<>();
        final FrontMatterSection section1 = new FrontMatterSection();
        section1.setFrontMatterPage(page);
        section1.setId(10L);
        section1.setPdfs(new ArrayList<FrontMatterPdf>());
        section1.setSectionHeading("Section 1");
        section1.setSectionText("Section 1 Text");
        section1.setSequenceNum(1);
        sections.add(section1);

        final FrontMatterSection section2 = new FrontMatterSection();
        section2.setFrontMatterPage(page);
        section2.setId(11L);
        section2.setPdfs(new ArrayList<FrontMatterPdf>());
        section2.setSectionHeading("Section 2");
        section2.setSectionText("Section 2 Text");
        section2.setSequenceNum(2);
        sections.add(section2);

        page.setFrontMatterSections(sections);
        pages.add(page);

        bookDefinition.setFrontMatterPages(pages);
    }

    @Ignore
    @Test
    public void testTitlePage() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("TitlePage.html"));
        final String actual = frontMatterService.getTitlePage(bookDefinition).replaceAll("(?m)^[ \t]*\r?\n", "");
        assertEquals(expected, actual);
    }

    @Ignore
    @Test
    public void testCopyrightPage() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("CopyrightPage.html"))
            .replace("/apps/eBookBuilder/coreStatic/css/", "C:\\apps\\eBookBuilder\\coreStatic\\css\\");
        assertEquals(expected, frontMatterService.getCopyrightPage(bookDefinition));
    }

    @Ignore
    @Test
    public void testAdditionaFrontMatterPage1() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("AdditionalFrontMatterPage1.html"))
            .replace("/apps/eBookBuilder/coreStatic/css/", "C:\\apps\\eBookBuilder\\coreStatic\\css\\");
        assertEquals(expected, frontMatterService.getAdditionalFrontPage(bookDefinition, 1L));
    }

    @Ignore
    @Test
    public void testResearchAssistancePage() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("ResearchAssistancePage.html"))
            .replace("/apps/eBookBuilder/coreStatic/css/", "C:\\apps\\eBookBuilder\\coreStatic\\css\\");
        assertEquals(expected, frontMatterService.getResearchAssistancePage(bookDefinition));
    }

    @Ignore
    @Test
    public void testWestlawNextPage() throws Exception {
        final String expected = IOUtils.toString(getClass().getResourceAsStream("WestlawNextPage.html"))
            .replace("/apps/eBookBuilder/coreStatic/images/", "C:\\apps\\eBookBuilder\\coreStatic\\images\\")
            .replace("/apps/eBookBuilder/coreStatic/css/", "C:\\apps\\eBookBuilder\\coreStatic\\images\\");
        assertEquals(expected, frontMatterService.getWestlawNextPage(bookDefinition));
    }
}
