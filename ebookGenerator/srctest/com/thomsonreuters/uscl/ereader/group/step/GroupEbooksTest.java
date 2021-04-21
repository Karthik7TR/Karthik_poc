package com.thomsonreuters.uscl.ereader.group.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupEbooksTest {
    private static final String GROUP_NAME = "groupName";
    private static final String BOOK_VERSION = "1.0";
    @InjectMocks
    private GroupEbooks step;
    @Mock
    private GroupService groupService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition bookDefinition;

    @Test
    public void shouldSetParameterExceptionOnGroupStepOccurred() throws Exception {
        setUpBookDefinition();
        setUpBookVersion();
        when(groupService.createGroupDefinition(any(), any(), any())).thenThrow(ProviewException.class);
        boolean isExceptionThrown = false;

        try {
            step.executeStep();
        } catch (ProviewException e) {
            isExceptionThrown = true;
        }

        assertTrue(isExceptionThrown);
        verify(step.getJobExecutionContext())
                .put(JobExecutionKey.EXCEPTION_ON_GROUP_STEP_OCCURRED, true);
    }

    private void setUpBookDefinition() {
        when(chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.EBOOK_DEFINITON)).thenReturn(bookDefinition);
        when(bookDefinition.getGroupName()).thenReturn(GROUP_NAME);
    }

    private void setUpBookVersion() {
        when(chunkContext.getStepContext()
                .getStepExecution()
                .getJobParameters()
                .getString(JobParameterKey.BOOK_VERSION_SUBMITTED)).thenReturn(BOOK_VERSION);
    }
}
