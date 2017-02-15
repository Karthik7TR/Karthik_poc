package com.thomsonreuters.uscl.ereader.core.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort.SortProperty;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public final class JobServiceIntegrationTest
{
    private static final Logger log = LogManager.getLogger(JobServiceIntegrationTest.class);
    @Autowired
    private JobService service;

    @Test
    public void testFindJobSummary()
    {
        final Long[] idArray = {46L, 9999L, 47L, 48L, 49L};
        final List<Long> ids = Arrays.asList(idArray);
        final List<JobSummary> js = service.findJobSummary(ids);
        Assert.assertNotNull(js);
        Assert.assertTrue(js.size() > 0);
        log.debug(js);
    }

    //@Test
    public void testFindJobExecutions()
    {
        final JobFilter filter =
            new JobFilter(new Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000), null, null, null, null, null);
        //filter.setTitleId("FRCP");
        //JobSort sort = new JobSort(SortParmeterKeyName.titleIdFullyQualified, true);
        final JobSort sort = new JobSort(SortProperty.JOB_INSTANCE_ID, false);
        final List<Long> executions = service.findJobExecutions(filter, sort);
//		Assert.assertTrue(executions.size() > 0);
        log.debug("size=" + executions.size());

        for (final Long jobExecutionId : executions)
        {
            Assert.assertNotNull(jobExecutionId);
            final JobExecution exec = service.findJobExecution(jobExecutionId);
            System.out.print(exec.getId() + ",");
            // log.debug(exec);
            //log.debug(id);
//			JobInstance inst = exec.getJobInstance();
//			JobParameters params = inst.getJobParameters();
            //log.debug(exec.getId() + "," + params.getString(JobParameterKey.TITLE_ID_FULLY_QUALIFIED));
        }
    }
}
