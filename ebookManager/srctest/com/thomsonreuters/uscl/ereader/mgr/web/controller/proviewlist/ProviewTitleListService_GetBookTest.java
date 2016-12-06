/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.titleId;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;

@RunWith(MockitoJUnitRunner.class)
public class ProviewTitleListService_GetBookTest {
	@InjectMocks
	private ProviewTitleListServiceImpl service;
	@Mock
	private BookDefinitionService bookDefinitionService;
	@Mock
	private BookDefinition book;

	@Test
	public void shouldReturnBookByTitleId() throws Exception {
		//given
		given(bookDefinitionService.findBookDefinitionByTitle("title")).willReturn(book);
		//when
		BookDefinition bookDefinition = service.getBook(titleId("title"));
		//then
		assertThat(bookDefinition, is(book));
	}
	
	@Test
	public void shouldReturnBookByHeadTitleId() throws Exception {
		//given
		given(bookDefinitionService.findBookDefinitionByTitle("title")).willReturn(book);
		//when
		BookDefinition bookDefinition = service.getBook(titleId("title_pt2"));
		//then
		assertThat(bookDefinition, is(book));
	}
	
	@Test
	public void shouldReturnBookForStrangeTitleId() throws Exception {
		//given
		given(bookDefinitionService.findBookDefinitionByTitle("title_pt_pt2")).willReturn(book);
		//when
		BookDefinition bookDefinition = service.getBook(titleId("title_pt_pt2"));
		//then
		assertThat(bookDefinition, is(book));
	}
	
}
