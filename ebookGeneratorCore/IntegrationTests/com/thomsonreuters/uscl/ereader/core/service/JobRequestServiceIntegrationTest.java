package com.thomsonreuters.uscl.ereader.core.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static com.thomsonreuters.uscl.ereader.config.BookDefinitionUtils.minimalBookDefinition;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JobRequestServiceIntegrationTestConf.class)
@ActiveProfiles("IntegrationTests")
public class JobRequestServiceIntegrationTest {

    @Autowired
    private JobRequestService jobRequestService;
    @Autowired
    private BookDefinitionService bookDefinitionService;
    private static final String BOOK_VERSION = "3.2";
    private static final int PRIORITY = 5;
    private static final int NEW_PRIORITY = 95;
    private static final String SUBMITTED_BY = "Hans Bethe";
    private BookDefinition bookDefinition;

    @Before
    public void setUp() {
        bookDefinition = bookDefinitionService.saveBookDefinition(minimalBookDefinition());
    }

    @Test
    public void testSaveJobRequest() {
        final Long pk = jobRequestService.saveQueuedJobRequest(bookDefinition, BOOK_VERSION, PRIORITY, SUBMITTED_BY);
        Assert.assertNotNull(pk);
        final JobRequest actualJobRequest = jobRequestService.findByPrimaryKey(pk);
        Assert.assertEquals(bookDefinition.getEbookDefinitionId(), actualJobRequest.getBookDefinition().getEbookDefinitionId());
        Assert.assertEquals(BOOK_VERSION, actualJobRequest.getBookVersion());
        Assert.assertEquals(PRIORITY, actualJobRequest.getPriority());
        Assert.assertEquals(SUBMITTED_BY, actualJobRequest.getSubmittedBy());
        Assert.assertNotNull(actualJobRequest.getBookDefinition());
    }

    @Test
    public void testUpdateJobPriority() {
        final Long pk = jobRequestService.saveQueuedJobRequest(bookDefinition, BOOK_VERSION, PRIORITY, SUBMITTED_BY);
        Assert.assertNotNull(pk);
        jobRequestService.updateJobPriority(pk, NEW_PRIORITY);
        final JobRequest found = jobRequestService.findByPrimaryKey(pk);
        Assert.assertEquals(NEW_PRIORITY, found.getPriority());
    }

    @Test
    public void testCleanupJobRequest() {
        jobRequestService.saveQueuedJobRequest(bookDefinition, BOOK_VERSION, PRIORITY, SUBMITTED_BY);
        Assert.assertEquals(1, jobRequestService.findAllJobRequests().size());
        jobRequestService.cleanupQueue();
        Assert.assertEquals(0, jobRequestService.findAllJobRequests().size());
    }
}
