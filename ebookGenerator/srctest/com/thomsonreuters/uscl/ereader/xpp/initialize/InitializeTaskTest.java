/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.xpp.initialize;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobExecutionContext;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobExecutionId;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobInstanceId;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobParameter;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

@RunWith(MockitoJUnitRunner.class)
public final class InitializeTaskTest
{
    @InjectMocks
    private InitializeTask step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition book;

    @Mock
    private BookDefinitionService bookDefnService;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Mock
    private ExecutionContext jobExecutionContext;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp()
    {
        step.setEnvironmentName("env");
        step.setRootWorkDirectory(temporaryFolder.getRoot());
    }

    @Test
    public void shouldSetBookDefinition() throws Exception
    {
        // given
        givenAll();
        // when
        step.executeStep();
        // then
        then(jobExecutionContext).should().put(JobExecutionKey.EBOOK_DEFINITION, book);
    }

    @Test
    public void shouldCreateAndSetWorkDir() throws Exception
    {
        // given
        givenAll();
        final String dateStr = new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(new Date());
        final File workDir = new File(temporaryFolder.getRoot(), "env/data/" + dateStr + "/titleId/1");
        // when
        step.executeStep();
        // then
        assertThat(workDir.exists(), is(true));
        then(jobExecutionContext).should().putString(JobExecutionKey.WORK_DIRECTORY, workDir.getAbsolutePath());
    }

    private void givenAll()
    {
        given(bookDefnService.findBookDefinitionByEbookDefId(1L)).willReturn(book);

        givenJobExecutionContext(chunkContext, jobExecutionContext);
        givenJobParameter(chunkContext, JobParameterKey.BOOK_DEFINITION_ID, 1L);

        given(book.getFullyQualifiedTitleId()).willReturn("titleId");
        given(book.getTitleId()).willReturn("titleId");
        given(book.getProviewDisplayName()).willReturn("proviewDisplayName");
        givenJobParameter(chunkContext, JobParameterKey.ENVIRONMENT_NAME, "env");
        givenJobInstanceId(chunkContext, 1L);
        givenJobExecutionId(chunkContext, 2L);
    }
}
