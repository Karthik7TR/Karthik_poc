package com.thomsonreuters.uscl.ereader.mgr.web.service.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.dao.JobDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;

public final class JobServiceTest {
    private static final Long JOB_EXECUTION_ID = 100L;
    private static final JobExecution EXPECTED_JOB_EXECUTION = new JobExecution(JOB_EXECUTION_ID);
    private JobExplorer mockJobExplorer;
    private JobDao mockJobDao;
    private JobServiceImpl jobService;

    @Before
    public void setUp() {
        mockJobExplorer = EasyMock.createMock(JobExplorer.class);
        mockJobDao = EasyMock.createMock(JobDao.class);
        jobService = new JobServiceImpl(mockJobDao, mockJobExplorer);
    }

    @Test
    public void testFindJobExecutionByPrimaryKey() {
        EasyMock.expect(mockJobExplorer.getJobExecution(JOB_EXECUTION_ID)).andReturn(EXPECTED_JOB_EXECUTION);
        EasyMock.replay(mockJobExplorer);

        final JobExecution actualJobExecution = jobService.findJobExecution(JOB_EXECUTION_ID);
        Assert.assertNotNull(actualJobExecution);
        Assert.assertEquals(EXPECTED_JOB_EXECUTION.getId(), actualJobExecution.getId());

        EasyMock.verify(mockJobExplorer);
    }

    @Test
    public void testFindJobExecutionsFiltered() {
        final int SIZE = 5;
        final JobFilter filter = new JobFilter();
        final JobSort sort = new JobSort();
        final List<Long> listOfLong = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            listOfLong.add(Long.valueOf(i));
        }
        EasyMock.expect(mockJobDao.findJobExecutions(filter, sort)).andReturn(listOfLong);
        EasyMock.replay(mockJobDao);

        final List<Long> ids = jobService.findJobExecutions(filter, sort);
        Assert.assertNotNull(ids);
        Assert.assertEquals(SIZE, ids.size());

        EasyMock.verify(mockJobDao);
    }

    @Test
    public void testFindJobExecutionByPrimaryKeys() {
        final int SIZE = 3;
        final Long[] ids = new Long[SIZE];
        final List<JobExecution> expectedJobExecutions = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            ids[i] = Long.valueOf(i + 101L);
            expectedJobExecutions.add(new JobExecution(ids[i]));
            EasyMock.expect(mockJobExplorer.getJobExecution(ids[i])).andReturn(expectedJobExecutions.get(i));
        }
        EasyMock.replay(mockJobExplorer);

        final List<JobExecution> actualJobExecutions = jobService.findJobExecutions(Arrays.asList(ids));
        Assert.assertNotNull(actualJobExecutions);
        Assert.assertEquals(SIZE, actualJobExecutions.size());
        for (int i = 0; i < SIZE; i++) {
            Assert.assertEquals(expectedJobExecutions.get(i).getId(), actualJobExecutions.get(i).getId());
        }
        EasyMock.verify(mockJobExplorer);
    }
}
