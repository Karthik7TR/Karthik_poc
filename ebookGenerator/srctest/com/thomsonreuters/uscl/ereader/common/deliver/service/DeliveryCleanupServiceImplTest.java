package com.thomsonreuters.uscl.ereader.common.deliver.service;

import static java.util.Arrays.asList;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.thomsonreuters.uscl.ereader.common.deliver.step.DeliverStep;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class DeliveryCleanupServiceImplTest
{
    @InjectMocks
    @Spy
    private DeliveryCleanupServiceImpl service;
    @Mock
    private DeliverStep step;
    @Mock
    private ProviewHandler proviewHandler;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp()
    {
        doNothing().when(service).waitProview(anyInt());
        given(step.getBookVersion()).willReturn(version("v1.1"));
        service.setMaxNumberOfRetries(2);
    }

    @Test
    public void shouldCleanUp() throws ProviewException
    {
        //given
        given(step.getPublishedSplitTitles()).willReturn(asList("splitTitle", "splitTitle_pt2"));
        given(proviewHandler.removeTitle("splitTitle", version("v1.1"))).willReturn("200");
        given(proviewHandler.removeTitle("splitTitle_pt2", version("v1.1"))).willReturn("200");
        //when
        service.cleanup(step);
        //then
        then(proviewHandler).should().deleteTitle("splitTitle", version("v1.1"));
    }

    @Test
    public void shouldThrowExceptionIfCannotCleanUp() throws ProviewException
    {
        //given
        thrown.expect(ProviewRuntimeException.class);
        given(step.getPublishedSplitTitles()).willReturn(asList("splitTitle", "splitTitle_pt2"));
        given(proviewHandler.removeTitle("splitTitle", version("v1.1"))).willReturn("200");
        doThrow(new ProviewException(CoreConstants.TTILE_IN_QUEUE)).when(proviewHandler)
            .removeTitle("splitTitle_pt2", version("v1.1"));
        //when
        service.cleanup(step);
        //then
    }

    @Test
    public void shouldThrowExceptionIfProviewUnexpectedFailure() throws ProviewException
    {
        //given
        thrown.expect(ProviewRuntimeException.class);
        thrown.expectMessage("msg");
        given(step.getPublishedSplitTitles()).willReturn(asList("splitTitle", "splitTitle_pt2"));
        given(proviewHandler.removeTitle("splitTitle", version("v1.1"))).willReturn("200");
        doThrow(new ProviewException("msg")).when(proviewHandler)
            .removeTitle("splitTitle_pt2", version("v1.1"));
        //when
        service.cleanup(step);
        //then
    }
}
