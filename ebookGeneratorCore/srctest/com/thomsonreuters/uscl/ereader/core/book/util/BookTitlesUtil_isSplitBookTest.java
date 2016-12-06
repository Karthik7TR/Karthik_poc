/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.util;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.book;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNode;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.splitNodes;
import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

@RunWith(MockitoJUnitRunner.class)
public class BookTitlesUtil_isSplitBookTest {
	@InjectMocks
	private BookTitlesUtilImpl util;

	@Test
	public void shouldReturnTrueForSplitBook() throws Exception {
		// given
		BookDefinition book = book("title");
		book.setSplitNodes(splitNodes(splitNode(book, "title_pt2", "1.0"), splitNode(book, "title_pt2", "1.1")));
		// when
		boolean splitBook = util.isSplitBook(book, version("v1.1"));
		// then
		assertThat(splitBook, is(true));
	}
	
	@Test
	public void shouldReturnFalseForSingleBook() throws Exception {
		// given
		BookDefinition book = book("title");
		book.setSplitNodes(splitNodes(splitNode(book, "title_pt2", "1.0"), splitNode(book, "title_pt2", "1.1")));
		// when
		boolean splitBook = util.isSplitBook(book, version("v1.2"));
		// then
		assertThat(splitBook, is(false));
	}
	

}
