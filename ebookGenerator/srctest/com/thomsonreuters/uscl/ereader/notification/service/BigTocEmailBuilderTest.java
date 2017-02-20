package com.thomsonreuters.uscl.ereader.notification.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsService;
import com.thomsonreuters.uscl.ereader.notification.step.SendEmailNotificationStep;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class BigTocEmailBuilderTest
{
    @InjectMocks
    private BigTocEmailBuilder bigTocEmailBuilder;
    @Mock
    private SendEmailNotificationStep step;
    @Mock
    private AutoSplitGuidsService autoSplitGuidsService;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File root;

    @Before
    public void setUp()
    {
        root = temporaryFolder.getRoot();
    }

    @Test
    public void additionalSubjectPartIsCorrect()
    {
        //given
        //when
        final String subjectPart = bigTocEmailBuilder.getAdditionalSubjectPart();
        //then
        assertThat(subjectPart, is(" THRESHOLD WARNING"));
    }

    @Test
    public void additionalBodyPartIsCorrect() throws IOException
    {
        //given
        givenAll();
        //when
        final String bodyPart = bigTocEmailBuilder.getAdditionalBodyPart();
        //then
        assertThat(bodyPart, containsString("**WARNING**: The book exceeds threshold value 3"));
        assertThat(bodyPart, containsString("Total node count is 1"));
        assertThat(bodyPart, containsString("Total split parts : 1"));
        assertThat(bodyPart, containsString("id1  :  text1"));
        assertThat(bodyPart, containsString("id2  :  text2"));
    }

    @Test
    public void shouldThrowExceptionIfCannotReadFile() throws IOException
    {
        //given
        givenAll();
        given(step.getJobExecutionPropertyString(JobExecutionKey.GATHER_TOC_FILE)).willReturn(root.getAbsolutePath());
        thrown.expect(RuntimeException.class);
        //when
        bigTocEmailBuilder.getAdditionalBodyPart();
        //then
    }

    private void givenAll() throws IOException
    {
        final Map<String, String> guids2Text = new HashMap<>();
        guids2Text.put("id1", "text1");
        guids2Text.put("id2", "text2");
        given(autoSplitGuidsService.getSplitGuidTextMap()).willReturn(guids2Text);
        given(
            autoSplitGuidsService.getAutoSplitNodes(
                any(InputStream.class),
                any(BookDefinition.class),
                anyInt(),
                anyLong(),
                anyBoolean())).willReturn(Collections.EMPTY_LIST);
        final File testFile = new File(root, "test.txt");
        testFile.createNewFile();
        given(step.getJobExecutionPropertyString(JobExecutionKey.GATHER_TOC_FILE))
            .willReturn(testFile.getAbsolutePath());
        given(step.getTocNodeCount()).willReturn(1);
        given(step.getThresholdValue()).willReturn(3);
    }
}
