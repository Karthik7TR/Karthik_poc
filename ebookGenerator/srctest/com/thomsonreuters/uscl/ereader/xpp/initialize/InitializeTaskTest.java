/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.xpp.initialize;

import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobExecutionContext;
import static com.thomsonreuters.uscl.ereader.StepTestUtil.givenJobParameter;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.io.File;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
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
public final class InitializeTaskTest {
    @InjectMocks
    private InitializeTask step;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition book;
    @Mock
    private BookDefinitionService bookService;
    @Mock
    private BookFileSystem fileSystem;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Mock
    private ExecutionContext jobExecutionContext;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldSetBookDefinition() throws Exception {
        // given
        givenAll();
        // when
        step.executeStep();
        // then
        then(jobExecutionContext).should().put(JobExecutionKey.EBOOK_DEFINITION, book);
    }

    @Test
    public void shouldCreateAndSetWorkDir() throws Exception {
        // given
        givenAll();
        final File workDir = temporaryFolder.getRoot();
        // when
        step.executeStep();
        // then
        assertThat(workDir.exists(), is(true));
        then(jobExecutionContext).should().putString(JobExecutionKey.WORK_DIRECTORY, workDir.getAbsolutePath());
    }

    private void givenAll() {
        givenJobParameter(chunkContext, JobParameterKey.BOOK_DEFINITION_ID, 1L);
        given(bookService.findBookDefinitionByEbookDefId(1L)).willReturn(book);
        givenJobExecutionContext(chunkContext, jobExecutionContext);
        given(book.getTitleId()).willReturn("titleId");
        given(fileSystem.getWorkDirectory(step)).willReturn(temporaryFolder.getRoot());
    }
}
