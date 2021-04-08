package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class EditBookDefinitionFormTest {
    private static final String COMPONENT_NAME_ESCAPED = "&lt;script&gt;&lt;/script&gt;";
    private static final String COMPONENT_NAME_CROSS_SITE_INJECTION_ATTEMPT = "<script></script>";
    private static final String printComponentsJson =
        "[{\"printComponentId\":\"1\",\"componentOrder\":1,\"materialNumber\":\"123\",\"componentName\":\"c1\"},"
       + "{\"printComponentId\":\"2\",\"componentOrder\":2,\"materialNumber\":\"234\",\"componentName\":\"" + COMPONENT_NAME_ESCAPED + "\"},"
       + "{\"printComponentId\":\"3\",\"componentOrder\":3,\"materialNumber\":\"345\",\"componentName\":\"" + COMPONENT_NAME_CROSS_SITE_INJECTION_ATTEMPT + "\"}]";
    private static final String USCL_TITLE_ID = "uscl/an/title_id";
    private static final String USCL_TITLE_ID_WITH_LANG = "uscl/an/title_id_en";
    private static final String CW_TITLE_ID = "cw/eg/title_id_fr";
    private static final String BOOK_LANG_FR = "fr";
    private static final String USCL_PUB_INFO_WITH_LANG = "title_id_en";
    private static final String CW_PUB_INFO = "title_id";

    private EditBookDefinitionForm form;

    private List<Author> authorInfo;
    private List<PilotBook> pilotBookInfo;
    private EbookName frontMatterTitle;
    private EbookName frontMatterSubtitle;
    private EbookName frontMatterSeries;
    private List<FrontMatterPage> frontMatters;
    private List<SplitDocument> splitDocuments;
    private List<TableViewer> tableViewers;
    private List<DocumentCopyright> documentCopyrights;
    private List<DocumentCurrency> documentCurrencies;
    private Map<Long, Collection<Long>> keywords;
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
        splitDocuments = new ArrayList<>();
        tableViewers = new ArrayList<>();
        documentCopyrights = new ArrayList<>();
        documentCurrencies = new ArrayList<>();
        keywords = new HashMap<>();
        nortFileLocations = new ArrayList<>();
    }

    @Test
    public void testLoadBookDefinition() throws IOException {
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
        final SplitDocument doc = new SplitDocument();
        final TableViewer viewer = new TableViewer();
        final DocumentCopyright copyright = new DocumentCopyright();
        final DocumentCurrency currency = new DocumentCurrency();
        final Long keyword = 1L;
        final Long keywordId = 1L;
        final Long noneKeyword = 0L;
        final Long noneKeywordId = -1L;
        final NortFileLocation location = new NortFileLocation();

        form.setTitleId(USCL_TITLE_ID);
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
        keywords.put(keywordId, Collections.singleton(keyword));
        keywords.put(noneKeywordId, Collections.singleton(noneKeyword));
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
        Assert.assertEquals(3, book.getPrintComponents().size());

        validateEscapingPrintComponentName(book.getPrintComponents());
    }

    private void validateEscapingPrintComponentName(final Set<PrintComponent> printComponents) {
        final Map<String, String> componentsMap = printComponents.stream().collect(
            Collectors.toMap(PrintComponent::getMaterialNumber, PrintComponent::getComponentName));

        Assert.assertEquals(COMPONENT_NAME_ESCAPED, componentsMap.get("234"));
        Assert.assertEquals(COMPONENT_NAME_ESCAPED, componentsMap.get("345"));
    }

    @Test
    public void testCopyUsclBookDefinition() {
        final BookDefinition book = createBookDefinition(USCL_TITLE_ID);
        final List<KeywordTypeCode> keywordCodes = new ArrayList<>();

        form.copyBookDefinition(book, keywordCodes);

        Assert.assertTrue(form.toString().length() > 0);
        Assert.assertTrue(form.isSplitTypeAuto());
        Assert.assertNull(form.getBookLanguage());
    }

    @Test
    public void testCopyUsclBookDefinitionWithLanguageInTitleId() {
        final BookDefinition book = createBookDefinition(USCL_TITLE_ID_WITH_LANG);
        final List<KeywordTypeCode> keywordCodes = new ArrayList<>();

        form.copyBookDefinition(book, keywordCodes);

        Assert.assertTrue(form.toString().length() > 0);
        Assert.assertTrue(form.isSplitTypeAuto());
        Assert.assertNull(form.getBookLanguage());
        Assert.assertEquals(USCL_PUB_INFO_WITH_LANG, form.getPubInfo());
    }

    @Test
    public void testCopyCwBookDefinition() {
        final BookDefinition book = createBookDefinition(CW_TITLE_ID);
        final List<KeywordTypeCode> keywordCodes = new ArrayList<>();

        form.copyBookDefinition(book, keywordCodes);

        Assert.assertTrue(form.toString().length() > 0);
        Assert.assertTrue(form.isSplitTypeAuto());
        Assert.assertEquals(BOOK_LANG_FR, form.getBookLanguage());
        Assert.assertEquals(CW_PUB_INFO, form.getPubInfo());
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

    private BookDefinition createBookDefinition(final String titleId) {
        final BookDefinition book = new BookDefinition();
        book.setFullyQualifiedTitleId(titleId);
        book.setKeyciteToplineFlag(true);
        book.setAutoUpdateSupportFlag(true);
        book.setSearchIndexFlag(true);
        book.setIsAuthorDisplayVertical(true);
        book.setEnableCopyFeatureFlag(true);
        book.setIsSplitBook(false);
        book.setIsSplitTypeAuto(false);
        book.setDocumentTypeCodes(getEmptyDocumentTypeCode());
        return book;
    }

    private DocumentTypeCode getEmptyDocumentTypeCode() {
        final DocumentTypeCode documentTypeCode = new DocumentTypeCode();
        documentTypeCode.setName(StringUtils.EMPTY);
        return documentTypeCode;
    }
}
