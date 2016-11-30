package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

public class EditBookDefinitionFormTest {

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
		authorInfo = new ArrayList<Author>();
		pilotBookInfo = new ArrayList<PilotBook>();
		frontMatterTitle = new EbookName();
		frontMatterSubtitle = new EbookName();
		frontMatterSeries = new EbookName();
		frontMatters = new ArrayList<FrontMatterPage>();
		// excludeDocuments = new ArrayList<ExcludeDocument>();
		// excludeDocumentsCopy = new ArrayList<ExcludeDocument>();
		splitDocuments = new ArrayList<SplitDocument>();
		// renameTocEntries = new ArrayList<RenameTocEntry>();
		tableViewers = new ArrayList<TableViewer>();
		documentCopyrights = new ArrayList<DocumentCopyright>();
		documentCurrencies = new ArrayList<DocumentCurrency>();
		keywords = new ArrayList<String>();
		nortFileLocations = new ArrayList<NortFileLocation>();
	}

	@Test
	public void testLoadBookDefinition() {
		BookDefinition book = new BookDefinition();

		Author author = new Author();
		PilotBook pilot = new PilotBook();
		FrontMatterPage page = new FrontMatterPage();
		FrontMatterSection section = new FrontMatterSection();
		FrontMatterPdf pdf = new FrontMatterPdf();
		List<FrontMatterPdf> pdfs = new ArrayList<FrontMatterPdf>();
		pdfs.add(pdf);
		section.setPdfs(pdfs);
		List<FrontMatterSection> sections = new ArrayList<FrontMatterSection>();
		sections.add(section);
		page.setFrontMatterSections(sections);
		// ExcludeDocument exclude = new ExcludeDocument();
		// excludeDocuments.add(exclude);
		// excludeDocumentsCopy.add(exclude);
		SplitDocument doc = new SplitDocument();
		// RenameTocEntry entry = new RenameTocEntry();
		TableViewer viewer = new TableViewer();
		DocumentCopyright copyright = new DocumentCopyright();
		DocumentCurrency currency = new DocumentCurrency();
		String keyword = "0";
		NortFileLocation location = new NortFileLocation();

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

		try {
			form.loadBookDefinition(book);
		} catch (ParseException e) {
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
	}

	@Test
	public void testCopyBookDefinition() {
		DocumentTypeCode documentTypeCodes = new DocumentTypeCode();
		documentTypeCodes.setName("");

		BookDefinition book = new BookDefinition();
		book.setFullyQualifiedTitleId(titleId);
		book.setKeyciteToplineFlag(true);
		book.setAutoUpdateSupportFlag(true);
		book.setSearchIndexFlag(true);
		book.setIsAuthorDisplayVertical(true);
		book.setEnableCopyFeatureFlag(true);
		book.setIsSplitBook(false);
		book.setIsSplitTypeAuto(false);
		book.setDocumentTypeCodes(documentTypeCodes);

		List<KeywordTypeCode> keywordCodes = new ArrayList<KeywordTypeCode>();

		form.copyBookDefinition(book, keywordCodes);
		Assert.assertTrue(form.toString().length() > 0);
	}

	@Test
	public void testRemoveEmptyRows() {

		Author author = new Author();
		PilotBook pilot = new PilotBook();
		NortFileLocation location = new NortFileLocation();
		TableViewer viewer = new TableViewer();
		DocumentCopyright copyright = new DocumentCopyright();
		DocumentCurrency currency = new DocumentCurrency();
		FrontMatterPage page = new FrontMatterPage();
		FrontMatterSection section = new FrontMatterSection();
		FrontMatterPdf pdf = new FrontMatterPdf();
		List<FrontMatterPdf> pdfs = new ArrayList<FrontMatterPdf>();
		pdfs.add(pdf);
		section.setPdfs(pdfs);
		List<FrontMatterSection> sections = new ArrayList<FrontMatterSection>();
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
