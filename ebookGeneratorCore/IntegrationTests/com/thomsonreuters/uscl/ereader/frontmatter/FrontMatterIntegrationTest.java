package com.thomsonreuters.uscl.ereader.frontmatter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration 
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class FrontMatterIntegrationTest {
	//private static Logger log = Logger.getLogger(FrontMatterIntegrationTest.class);
	private static String BOOK_TITLE = "uscl/an/frontmatter";
	private static Date UPDATE_DATE = new Date();
	
	@Autowired
	protected BookDefinitionService bookDefinitionService;
	protected BookDefinition eBook;
	
	@Autowired
	protected CodeService codeService;


	/**
	 * Operation Unit Test Save an existing Audit entity
	 * 
	 */
	public void saveBook() {
		eBook = new com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition();
		
		eBook.setFullyQualifiedTitleId(BOOK_TITLE);
		eBook.setProviewDisplayName("Integration Test Book");
		eBook.setCopyright("2012 Copyright Integration Test");
		eBook.setDocCollectionName("invalidCollection");
		eBook.setRootTocGuid("roottocguid");
		eBook.setTocCollectionName("invalidTocCollection");
		eBook.setIsbn("1234");
		eBook.setMaterialId("12345");
		eBook.setIsTocFlag(true);
		eBook.setAutoUpdateSupportFlag(true);
		eBook.setEbookDefinitionCompleteFlag(true);
		eBook.setIsDeletedFlag(false);
		eBook.setIsProviewTableViewFlag(false);
		eBook.setKeyciteToplineFlag(true);
		eBook.setOnePassSsoLinkFlag(true);
		eBook.setSearchIndexFlag(true);
		eBook.setPublishedOnceFlag(false);
		eBook.setLastUpdated(UPDATE_DATE);
		eBook.setAuthors(new HashSet<Author>());
		eBook.setEbookNames(new HashSet<EbookName>());
		
		DocumentTypeCode dc = codeService.getDocumentTypeCodeById((long) 1);
		eBook.setDocumentTypeCodes(dc);
		
		PublisherCode publisherCode = codeService.getPublisherCodeById((long) 1);
		eBook.setPublisherCodes(publisherCode);
		
		// Setup Front Matter
		FrontMatterPage fmp = new FrontMatterPage();
		fmp.setEbookDefinition(eBook);
		fmp.setPageTocLabel("TOC Label");
		fmp.setPageHeadingLabel("Label 1");
		fmp.setSequenceNum(1);
		eBook.getFrontMatterPages().add(fmp);
		
		FrontMatterSection fms = new FrontMatterSection();
		fms.setSectionHeading("Section Heading");
		fms.setSectionText("some text");
		fms.setSequenceNum(1);
		fms.setFrontMatterPage(fmp);
		
		FrontMatterPdf frontMatterPdf = new FrontMatterPdf();
		frontMatterPdf.setPdfFilename("somefile.pdf");
		frontMatterPdf.setPdfLinkText("Link Text");
		frontMatterPdf.setSection(fms);
		fms.setPdf(frontMatterPdf);
		
		fmp.getFrontMatterSections().add(fms);
		
		eBook = bookDefinitionService.saveBookDefinition(eBook);
	}
	
	/**
	 * Operation Unit Test
	 * 
	 * @author Dong Kim
	 */
	@Test
	public void AddFrontMatterSection() {
		saveBook();
		eBook = bookDefinitionService.findBookDefinitionByTitle(BOOK_TITLE);
		
		BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(BOOK_TITLE);
		Set<FrontMatterPage> frontMatterPages = book.getFrontMatterPages();
		for(FrontMatterPage page : frontMatterPages) {
			FrontMatterSection fms = new FrontMatterSection();
			fms.setSectionHeading("Section Heading 2");
			fms.setSectionText("some text 2");
			fms.setSequenceNum(2);
			fms.setFrontMatterPage(page);
			page.getFrontMatterSections().add(fms);
			
			FrontMatterPdf frontMatterPdf = new FrontMatterPdf();
			frontMatterPdf.setPdfFilename("somefile.pdf 2");
			frontMatterPdf.setPdfLinkText("Link Text 2");
			frontMatterPdf.setSection(fms);
			fms.setPdf(frontMatterPdf);
		}
		
		Assert.assertEquals(1, frontMatterPages.size());
		
		eBook = bookDefinitionService.saveBookDefinition(book);
		frontMatterPages = eBook.getFrontMatterPages();
		for(FrontMatterPage page : frontMatterPages) {
			Assert.assertEquals(2, page.getFrontMatterSections().size());
		}
	}
	
	@Test
	public void DeleteFrontMatterPage() {
		BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(BOOK_TITLE);
		book.getFrontMatterPages().clear();
		eBook = bookDefinitionService.saveBookDefinition(book);
		Assert.assertEquals(0, eBook.getFrontMatterPages().size());
	}

}
