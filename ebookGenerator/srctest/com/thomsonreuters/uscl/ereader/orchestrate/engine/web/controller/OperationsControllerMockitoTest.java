package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.job.flow.FlowJob;

@RunWith(MockitoJUnitRunner.class)
public final class OperationsControllerMockitoTest {
    private static final String STEPS_CSV = "Step1,Step2";

    @Mock
    private FlowJob job;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletOutputStream out;

    private OperationsController controller;

    @Before
    public void setUp() {
        controller = new OperationsController(job);
        when(job.getStepNames()).thenReturn(Arrays.asList("Step1", "Step2"));
    }

     @Test
    public void shouldPrintStepNames() throws Exception {
        when(response.getOutputStream()).thenReturn(out);

        controller.getStepNames(response, null);

        then(out).should().print(eq(STEPS_CSV));
    }

    @Test
    public void shouldNotThrowNpeInGetStepNames() throws Exception {
         when(response.getOutputStream()).thenThrow(new IOException());

        controller.getStepNames(response, null);

        verify(out, never()).print(any());
    }

}