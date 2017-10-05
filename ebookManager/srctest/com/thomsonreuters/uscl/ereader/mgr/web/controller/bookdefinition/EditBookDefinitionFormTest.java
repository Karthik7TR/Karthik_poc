package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit.EditBookDefinitionForm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class EditBookDefinitionFormTest {
    private static final String printComponentsJson =
        "[{\"printComponentId\":\"1\",\"componentOrder\":1,\"materialNumber\":\"123\",\"componentName\":\"c1\"},{\"printComponentId\":\"2\",\"componentOrder\":2,\"materialNumber\":\"234\",\"componentName\":\"c2\"}]";

    private EditBookDefinitionForm form;

    private String titleId = "test/test/titleId";
    private List<Author> authorInfo;
    private List<PilotBook> pilotBookInfo;
    private EbookName frontMatterTitle;
    private EbookName frontMatterSubtitle;
    private EbookName frontMatterSeries;
    private List<FrontMatterPage> frontMatters;
    // private List<ExcludeDocument> excludeDocuments;
    // private List<ExcludeDocument> excludeDocumentsCopy;
    private List<SplitDocument> splitDocuments;
    // private List<RenameTocEntry> renameTocEntries;
    private List<TableViewer> tableViewers;
    private List<DocumentCopyright> documentCopyrights;
    private List<DocumentCurrency> documentCurrencies;
    private List<String> keywords;
    private List<NortFileLocation> nortFileLocations;

    @Before
    public void setUp() {
        form = new EditBookDefinitionForm();
        authorInfo = new ArrayList<>();
        pilotBookInfo = new ArrayList<>();
        frontMatterTitle = new EbookName();
        frontMatterSubtitle = new EbookName();
        frontMatterSeries = new EbookName();
        frontMatters = new ArrayList<>();
        // excludeDocuments = new ArrayList<ExcludeDocument>();
        // excludeDocumentsCopy = new ArrayList<ExcludeDocument>();
        splitDocuments = new ArrayList<>();
        // renameTocEntries = new ArrayList<RenameTocEntry>();
        tableViewers = new ArrayList<>();
        documentCopyrights = new ArrayList<>();
        documentCurrencies = new ArrayList<>();
        keywords = new ArrayList<>();
        nortFileLocations = new ArrayList<>();
    }

    @Test
    public void testLoadBookDefinition() throws JsonParseException, JsonMappingException, IOException {
        final BookDefinition book = new BookDefinition();

        final Author author = new Author();
        final PilotBook pilot = new PilotBook();
        final FrontMatterPage page = new FrontMatterPage();
        final FrontMatterSection section = new FrontMatterSection();
        final FrontMatterPdf pdf = new FrontMatterPdf();
        final List<FrontMatterPdf> pdfs = new ArrayList<>();
        pdfs.add(pdf);
        section.setPdfs(pdfs);
        final List<FrontMatterSection> sections = new ArrayList<>();
        sections.add(section);
        page.setFrontMatterSections(sections);
        // ExcludeDocument exclude = new ExcludeDocument();
        // excludeDocuments.add(exclude);
        // excludeDocumentsCopy.add(exclude);
        final SplitDocument doc = new SplitDocument();
        // RenameTocEntry entry = new RenameTocEntry();
        final TableViewer viewer = new TableViewer();
        final DocumentCopyright copyright = new DocumentCopyright();
        final DocumentCurrency currency = new DocumentCurrency();
        final String keyword = "0";
        final NortFileLocation location = new NortFileLocation();

        form.setTitleId(titleId);
        form.setAuthorInfo(authorInfo);
        authorInfo.add(author);
        form.setPilotBookInfo(pilotBookInfo);
        pilotBookInfo.add(pilot);
        form.setFrontMatterTitle(frontMatterTitle);
        frontMatterTitle.setBookNameText("a");
        form.setFrontMatterSubtitle(frontMatterSubtitle);
        frontMatterSubtitle.setBookNameText("b");
        form.setFrontMatterSeries(frontMatterSeries);
        frontMatterSeries.setBookNameText("c");
        form.setFrontMatters(frontMatters);
        frontMatters.add(page);
        form.setSplitDocuments(splitDocuments);
        splitDocuments.add(doc);
        // form.setRenameTocEntries(renameTocEntries);
        // renameTocEntries.add(entry);
        form.setTableViewers(tableViewers);
        form.setTableViewersCopy(tableViewers);
        tableViewers.add(viewer);
        form.setDocumentCopyrights(documentCopyrights);
        form.setDocumentCopyrightsCopy(documentCopyrights);
        documentCopyrights.add(copyright);
        form.setDocumentCurrencies(documentCurrencies);
        form.setDocumentCurrenciesCopy(documentCurrencies);
        documentCurrencies.add(currency);
        form.setKeywords(keywords);
        keywords.add(keyword);
        form.setNortFileLocations(nortFileLocations);
        nortFileLocations.add(location);
        form.setPrintComponents(printComponentsJson);

        try {
            form.loadBookDefinition(book);
        } catch (final ParseException e) {
            Assert.fail(e.getMessage());
        }
        Assert.assertEquals(1, book.getAuthors().size());
        Assert.assertEquals(1, book.getPilotBooks().size());
        Assert.assertEquals(3, book.getEbookNames().size());
        Assert.assertEquals(1, book.getFrontMatterPages().size());
        Assert.assertEquals(1, book.getSplitDocuments().size());
        Assert.assertEquals(1, book.getTableViewers().size());
        Assert.assertEquals(1, book.getDocumentCopyrights().size());
        Assert.assertEquals(1, book.getDocumentCurrencies().size());
        Assert.assertEquals(1, book.getKeywordTypeValues().size());
        Assert.assertEquals(1, book.getNortFileLocations().size());
        Assert.assertEquals(2, book.getPrintComponents().size());
    }

    @Test
    public void testCopyBookDefinition() {
        final DocumentTypeCode documentTypeCodes = new DocumentTypeCode();
        documentTypeCodes.setName("");

        final BookDefinition book = new BookDefinition();
        book.setFullyQualifiedTitleId(titleId);
        book.setKeyciteToplineFlag(true);
        book.setAutoUpdateSupportFlag(true);
        book.setSearchIndexFlag(true);
        book.setIsAuthorDisplayVertical(true);
        book.setEnableCopyFeatureFlag(true);
        book.setIsSplitBook(false);
        book.setIsSplitTypeAuto(false);
        book.setDocumentTypeCodes(documentTypeCodes);

        final List<KeywordTypeCode> keywordCodes = new ArrayList<>();

        form.copyBookDefinition(book, keywordCodes);
        Assert.assertTrue(form.toString().length() > 0);
    }

    @Test
    public void testRemoveEmptyRows() {
        final Author author = new Author();
        final PilotBook pilot = new PilotBook();
        final NortFileLocation location = new NortFileLocation();
        final TableViewer viewer = new TableViewer();
        final DocumentCopyright copyright = new DocumentCopyright();
        final DocumentCurrency currency = new DocumentCurrency();
        final FrontMatterPage page = new FrontMatterPage();
        final FrontMatterSection section = new FrontMatterSection();
        final FrontMatterPdf pdf = new FrontMatterPdf();
        final List<FrontMatterPdf> pdfs = new ArrayList<>();
        pdfs.add(pdf);
        section.setPdfs(pdfs);
        final List<FrontMatterSection> sections = new ArrayList<>();
        sections.add(section);
        page.setFrontMatterSections(sections);

        form.setAuthorInfo(authorInfo);
        authorInfo.add(author);
        form.setPilotBookInfo(pilotBookInfo);
        pilotBookInfo.add(pilot);
        form.setNortFileLocations(nortFileLocations);
        nortFileLocations.add(location);
        form.setTableViewers(tableViewers);
        tableViewers.add(viewer);
        form.setDocumentCopyrights(documentCopyrights);
        documentCopyrights.add(copyright);
        form.setDocumentCurrencies(documentCurrencies);
        documentCurrencies.add(currency);
        form.setFrontMatters(frontMatters);
        frontMatters.add(page);

        form.removeEmptyRows();
    }

    @Test
    public void defaultNotesOfDecisionsValue() {
        assertTrue(form.isIncludeNotesOfDecisions());
    }
}
