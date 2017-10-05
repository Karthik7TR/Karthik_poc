package com.thomsonreuters.uscl.ereader.common.proview;

import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import org.aspectj.lang.ProceedingJoinPoint;
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
public final class ProviewRetryAspectTest {
    @InjectMocks
    @Spy
    private ProviewRetryAspect aspect;
    @Mock
    private ProceedingJoinPoint jp;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        doNothing().when(aspect).waitProview(anyInt());
        aspect.setMaxNumberOfRetries(2);
    }

    @Test
    public void shouldCallTargetMethod() throws Throwable {
        //given
        //when
        aspect.around(jp);
        //then
        then(jp).should().proceed();
    }

    @Test
    public void shouldThrowExceptionIfProviewFailure() throws Throwable {
        //given
        thrown.expect(ProviewRuntimeException.class);
        doThrow(new ProviewException(CoreConstants.TTILE_IN_QUEUE)).when(jp).proceed();
        //when
        aspect.around(jp);
        //then
    }

    @Test
    public void shouldThrowExceptionIfProviewUnexpectedFailure() throws Throwable {
        //given
        thrown.expect(ProviewRuntimeException.class);
        thrown.expectMessage("msg");
        doThrow(new ProviewException("msg")).when(jp).proceed();
        //when
        aspect.around(jp);
        //then
    }
}
