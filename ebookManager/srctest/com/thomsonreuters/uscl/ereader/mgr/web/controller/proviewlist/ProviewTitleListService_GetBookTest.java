package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewTitleListService_GetBookTest
{
    @InjectMocks
    private ProviewTitleListServiceImpl service;
    @Mock
    private BookDefinitionService bookDefinitionService;
    @Mock
    private BookDefinition book;

    @Test
    public void shouldReturnBookByTitleId()
    {
        //given
        given(bookDefinitionService.findBookDefinitionByTitle("title")).willReturn(book);
        //when
        final BookDefinition bookDefinition = service.getBook(titleId("title"));
        //then
        assertThat(bookDefinition, is(book));
    }

    @Test
    public void shouldReturnBookByHeadTitleId()
    {
        //given
        given(bookDefinitionService.findBookDefinitionByTitle("title")).willReturn(book);
        //when
        final BookDefinition bookDefinition = service.getBook(titleId("title_pt2"));
        //then
        assertThat(bookDefinition, is(book));
    }

    @Test
    public void shouldReturnBookForStrangeTitleId()
    {
        //given
        given(bookDefinitionService.findBookDefinitionByTitle("title_pt_pt2")).willReturn(book);
        //when
        final BookDefinition bookDefinition = service.getBook(titleId("title_pt_pt2"));
        //then
        assertThat(bookDefinition, is(book));
    }

    @NotNull
    private static TitleId titleId(final String titleId)
    {
        return new TitleId(titleId);
    }
}
