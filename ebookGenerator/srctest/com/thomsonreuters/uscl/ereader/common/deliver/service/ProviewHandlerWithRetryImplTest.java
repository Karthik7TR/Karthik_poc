package com.thomsonreuters.uscl.ereader.common.deliver.service;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewHandlerWithRetryImplTest
{
    @InjectMocks
    private ProviewHandlerWithRetryImpl service;
    @Mock
    private ProviewHandler proviewHandler;

    @Test
    public void shouldDeleteTitleIf200() throws Exception
    {
        //given
        given(proviewHandler.removeTitle("an/splitTitle", version("v1.1"))).willReturn("200");
        //when
        service.removeTitle("an/splitTitle", version("v1.1"));
        //then
        then(proviewHandler).should().deleteTitle("an/splitTitle", version("v1.1"));
    }

    @Test
    public void shouldNotDeleteTitleOtherwise() throws Exception
    {
        //given
        given(proviewHandler.removeTitle("an/splitTitle", version("v1.1"))).willReturn("400");
        //when
        service.removeTitle("an/splitTitle", version("v1.1"));
        //then
        then(proviewHandler).should(never()).deleteTitle("an/splitTitle", version("v1.1"));
    }
}
