package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.mgr.dao.ManagerDao;
import com.thomsonreuters.uscl.ereader.mgr.web.service.job.JobService;
import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.web.client.RestTemplate;

public final class ManagerServiceImplTest {
    private ManagerServiceImpl service;

    private String contextName;
    private File tempRootDir;
    private File rootCodesDir;
    private String envName;
    private RestTemplate mockRestTemplate;
    private ManagerDao mockDao;
    private JobService mockJobService;
    private JobRequestService mockJobRequestService;

    @Before
    public void setUp() {
        service = new ManagerServiceImpl();

        contextName = "";
        tempRootDir = new File(System.getProperty("java.io.tmpdir") + "\\EvenMoreTemp");
        tempRootDir.mkdir();
        rootCodesDir = new File(tempRootDir, "codesDir");
        rootCodesDir.mkdir();
        envName = "";
        mockRestTemplate = EasyMock.createMock(RestTemplate.class);
        mockDao = EasyMock.createMock(ManagerDao.class);
        mockJobService = EasyMock.createMock(JobService.class);
        mockJobRequestService = EasyMock.createMock(JobRequestService.class);

        org.springframework.test.util.ReflectionTestUtils.setField(service, "generatorContextName", contextName);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "rootWorkDirectory", tempRootDir);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "rootCodesWorkbenchLandingStrip", rootCodesDir);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "environmentName", envName);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "restTemplate", mockRestTemplate);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "managerDao", mockDao);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "jobService", mockJobService);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "jobRequestService", mockJobRequestService);
    }

    @After
    public void tearDown() {
        try {
            FileUtils.deleteDirectory(tempRootDir);
        } catch (final Exception e) {
            //Intentionally left blank
        }
    }

    @Test
    public void testIsAnyJobsStartedOrQueued() {
        EasyMock.expect(mockJobService.getStartedJobCount()).andReturn(1);
        EasyMock.replay(mockJobService);
        Boolean jobs = service.isAnyJobsStartedOrQueued();
        Assert.assertTrue(jobs);

        EasyMock.reset(mockJobService);

        final List<JobRequest> requestList = new ArrayList<>();

        EasyMock.expect(mockJobService.getStartedJobCount()).andReturn(0);
        EasyMock.replay(mockJobService);
        EasyMock.expect(mockJobRequestService.findAllJobRequests()).andReturn(requestList);
        EasyMock.replay(mockJobRequestService);
        jobs = service.isAnyJobsStartedOrQueued();
        Assert.assertTrue(!jobs);

        EasyMock.reset(mockJobService);
        EasyMock.reset(mockJobRequestService);

        final JobRequest request = new JobRequest();
        requestList.add(request);

        EasyMock.expect(mockJobService.getStartedJobCount()).andReturn(0);
        EasyMock.replay(mockJobService);
        EasyMock.expect(mockJobRequestService.findAllJobRequests()).andReturn(requestList);
        EasyMock.replay(mockJobRequestService);
        jobs = service.isAnyJobsStartedOrQueued();
        Assert.assertTrue(jobs);
    }

    @Test
    public void testFindRunningJob() {
        final BookDefinition book = new BookDefinition();
        final Long bookDefinitionId = Long.valueOf(127);
        final JobExecution jobExecution = new JobExecution(bookDefinitionId);

        EasyMock.expect(mockDao.findRunningJobExecution(book)).andReturn(jobExecution);
        EasyMock.replay(mockDao);

        final JobExecution execution = service.findRunningJob(book);
        Assert.assertEquals(jobExecution, execution);
    }

    @Ignore
    @Test
    public void testPushPlannedOutage() {
        final String ipAddress = "";
        final int port = 0;
        // can't Instantiate for some reason
        final PlannedOutage outage = EasyMock.createMock(PlannedOutage.class);

        final InetSocketAddress socketAddr = EasyMock.createMock(InetSocketAddress.class);
        EasyMock.expect(socketAddr.getHostName()).andReturn(ipAddress);
        EasyMock.expect(socketAddr.getPort()).andReturn(port);
        EasyMock.replay(socketAddr);

        EasyMock.expect(mockRestTemplate.postForObject("", outage, SimpleRestServiceResponse.class)).andReturn(null);
        EasyMock.replay(mockRestTemplate);

        service.pushPlannedOutage(outage, socketAddr);
    }

    @Test
    public void testCleanupOldSpringBatchDatabaseRecords() {
        final int daysBack = 0;

        EasyMock.expect(mockDao.archiveAndDeleteSpringBatchJobRecordsBefore(EasyMock.anyObject(Date.class)))
            .andReturn(5);
        EasyMock.replay(mockDao);
        service.cleanupOldSpringBatchDatabaseRecords(daysBack);
        EasyMock.verify(mockDao);
    }

    @Test
    public void testCleanupOldFilesystemFiles() {
        final int daysBack = 0;
        final int cwbFilesDaysBack = 0;

        final File envDir = new File(tempRootDir, envName);
        envDir.mkdir();
        final File dataDir = new File(envDir, CoreConstants.DATA_DIR);
        dataDir.mkdir();
        new File(dataDir, "0").mkdir();
        new File(rootCodesDir, "0").mkdir();

        service.cleanupOldFilesystemFiles(daysBack, cwbFilesDaysBack);
    }

    @Test
    public void testCleanupOldPlannedOutages() {
        final int daysBack = 0;
        mockDao.deletePlannedOutagesBefore(EasyMock.anyObject(Date.class));
        EasyMock.expectLastCall();

        service.cleanupOldPlannedOutages(daysBack);
    }

    @Test
    public void testCleanupOldTransientMetadata() {
        final int numberLastMajorVersionKept = 0;
        final int daysBeforeDocMetadataDelete = 0;
        mockDao.deleteTransientMetadata(numberLastMajorVersionKept, daysBeforeDocMetadataDelete);
        EasyMock.expectLastCall();

        service.cleanupOldTransientMetadata(numberLastMajorVersionKept, daysBeforeDocMetadataDelete);
    }
}
