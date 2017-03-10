package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class TransformationUtilTest
{
    @InjectMocks
    private TransformationUtil transformationUtil;
    @Mock
    private File xppDirectory;
    @Mock
    private BookStep step;

    @Test
    public void shouldSkipIfNoXppDirectory()
    {
        //given
        given(xppDirectory.exists()).willReturn(false);
        //when
        final boolean shouldSkip = transformationUtil.shouldSkip(step);
        //then
        assertThat(shouldSkip, is(true));
    }

    @Test
    public void shouldNotSkipIfOtherwise()
    {
        //given
        given(xppDirectory.exists()).willReturn(true);
        //when
        final boolean shouldSkip = transformationUtil.shouldSkip(step);
        //then
        assertThat(shouldSkip, is(false));
    }
}
