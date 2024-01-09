package com.thomsonreuters.uscl.ereader.core.book.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public final class BookDefinitionTest {
    public static final String PDF_FILENAME = "cw_eg_cdn_el_fdc_en_frontmatter.pdf";
    private BookDefinition bookDefinition;

    @Before
    public void setUp() {
        bookDefinition = new BookDefinition();
        bookDefinition.setFrontMatterPages(getFrontMatterPages());
    }

    @Test
    public void getFrontMatterPdfFileNamesTest() {
        Set<String> pdfFileNames = bookDefinition.getFrontMatterPdfFileNames();
        assertEquals(pdfFileNames.iterator().next(), PDF_FILENAME);
    }

    private Collection<FrontMatterPage> getFrontMatterPages() {
        FrontMatterPage frontMatterPage = new FrontMatterPage();
        frontMatterPage.setFrontMatterSections(getFrontMatterSections());
        return Collections.singletonList(frontMatterPage);
    }

    private List<FrontMatterSection> getFrontMatterSections() {
        FrontMatterSection frontMatterSection = new FrontMatterSection();
        frontMatterSection.setPdfs(getFrontMatterPdfs());
        return Collections.singletonList(frontMatterSection);
    }

    private List<FrontMatterPdf> getFrontMatterPdfs() {
        FrontMatterPdf frontMatterPdf = new FrontMatterPdf();
        frontMatterPdf.setPdfFilename(PDF_FILENAME);
        return Collections.singletonList(frontMatterPdf);
    }

/*	@Test
	public void testBookDefinitionKey() {
		String path = "uscl/cr/";
		String titleId = "ak_2013_state";
		String fullyQualifiedTitleId = path + titleId;
		BookDefinitionKey key = new BookDefinitionKey(fullyQualifiedTitleId);
		Assert.assertEquals(fullyQualifiedTitleId, key.getFullyQualifiedTitleId());
		Assert.assertEquals(titleId, key.getTitleId());
		Assert.assertEquals(fullyQualifiedTitleId, key.toKeyString());  // string representation "<titleId>,<majorVersion>"
		// Check that null fields are rejected
		try { key.setFullyQualifiedTitleId(null);} catch (IllegalArgumentException e) {Assert.assertTrue(true); }
	}*/
    /*	@Test
    public void testTitleIdWithBackslashDelimiters() {
    	String path = "uscl\\cr\\";
    	String rightComponent = "ak_2013_state";
    	String fullyQualifiedTitleId = path + rightComponent;
    	BookDefinitionKey key = new BookDefinitionKey(fullyQualifiedTitleId);
    	Assert.assertEquals(fullyQualifiedTitleId, key.getFullyQualifiedTitleId());
    	Assert.assertEquals(fullyQualifiedTitleId, key.getTitleId());
    	Assert.assertEquals(fullyQualifiedTitleId, key.toKeyString());  // string representation "<titleId>,<majorVersion>"
    }
    
    @Test
    public void testParseAuthorNames() {
    	String obama = "Spends Toomuch";
    	String bush = "George Bush";
    	String clinton = "Bill Clinton";
    
    	// Check null
    	List<String> authors = BookDefinition.parseAuthorNames(null);
    	Assert.assertEquals(0, authors.size());
    
    	// Check empty list
    	authors = BookDefinition.parseAuthorNames("");
    	Assert.assertEquals(0, authors.size());
    
    	// Check populated list
    	String pipedNameString = String.format(" %s | %s | %s ", obama, bush, clinton);
    	authors = BookDefinition.parseAuthorNames(pipedNameString);
    	Assert.assertEquals(obama, authors.get(0));
    	Assert.assertEquals(bush, authors.get(1));
    	Assert.assertEquals(clinton, authors.get(2));
    }*/
}
