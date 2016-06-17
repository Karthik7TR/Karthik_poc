package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.mgr.dao.ManagerDao;

public class ManagerServiceImplTest {
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
		this.tempRootDir.mkdir();
		rootCodesDir = new File(tempRootDir, "codesDir");
		this.rootCodesDir.mkdir();
		envName = "";
		mockRestTemplate = EasyMock.createMock(RestTemplate.class);
		mockDao = EasyMock.createMock(ManagerDao.class);
		mockJobService = EasyMock.createMock(JobService.class);
		mockJobRequestService = EasyMock.createMock(JobRequestService.class);

		service.setGeneratorContextName(contextName);
		service.setRootWorkDirectory(tempRootDir);
		service.setRootCodesWorkbenchLandingStrip(rootCodesDir);
		service.setEnvironmentName(envName);
		service.setRestTemplate(mockRestTemplate);
		service.setManagerDao(mockDao);
		service.setJobService(mockJobService);
		service.setJobRequestService(mockJobRequestService);
	}

	@After
	public void tearDown() {
		try {
			FileUtils.deleteDirectory(tempRootDir);
		} catch (IOException e) {
		}
	}

	@Test
	public void testIsAnyJobsStartedOrQueued() {

		EasyMock.expect(mockJobService.getStartedJobCount()).andReturn(1);
		EasyMock.replay(mockJobService);
		Boolean jobs = service.isAnyJobsStartedOrQueued();
		Assert.assertTrue(jobs);

		EasyMock.reset(mockJobService);

		List<JobRequest> requestList = new ArrayList<JobRequest>();

		EasyMock.expect(mockJobService.getStartedJobCount()).andReturn(0);
		EasyMock.replay(mockJobService);
		EasyMock.expect(mockJobRequestService.findAllJobRequests()).andReturn(requestList);
		EasyMock.replay(mockJobRequestService);
		jobs = service.isAnyJobsStartedOrQueued();
		Assert.assertTrue(!jobs);

		EasyMock.reset(mockJobService);
		EasyMock.reset(mockJobRequestService);

		JobRequest request = new JobRequest();
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
		Long bookDefinitionId = new Long(127);
		JobExecution jobExecution = new JobExecution(bookDefinitionId);

		EasyMock.expect(mockDao.findRunningJobExecution(bookDefinitionId)).andReturn(jobExecution);
		EasyMock.replay(mockDao);

		JobExecution execution = service.findRunningJob(bookDefinitionId);
		Assert.assertEquals(jobExecution, execution);
	}

	@Ignore
	@Test
	public void testPushPlannedOutage() {
		String ipAddress = "";
		int port = 0;
		// can't Instantiate for some reason
		PlannedOutage outage = EasyMock.createMock(PlannedOutage.class);

		InetSocketAddress socketAddr = EasyMock.createMock(InetSocketAddress.class);
		EasyMock.expect(socketAddr.getHostName()).andReturn(ipAddress);
		EasyMock.expect(socketAddr.getPort()).andReturn(port);
		EasyMock.replay(socketAddr);

		EasyMock.expect(mockRestTemplate.postForObject("", outage, SimpleRestServiceResponse.class)).andReturn(null);
		EasyMock.replay(mockRestTemplate);

		service.pushPlannedOutage(outage, socketAddr);
	}

	@Test
	public void testCleanupOldSpringBatchDatabaseRecords() {
		int daysBack = 0;

		EasyMock.expect(mockDao.archiveAndDeleteSpringBatchJobRecordsBefore(EasyMock.anyObject(Date.class)))
				.andReturn(5);
		EasyMock.replay(mockDao);
		service.cleanupOldSpringBatchDatabaseRecords(daysBack);
		EasyMock.verify(mockDao);
	}

	@Test
	public void testCleanupOldFilesystemFiles() {
		int daysBack = 0;
		int cwbFilesDaysBack = 0;

		File envDir = new File(tempRootDir, envName);
		envDir.mkdir();
		File dataDir = new File(envDir, CoreConstants.DATA_DIR);
		dataDir.mkdir();
		new File(dataDir, "0").mkdir();
		new File(rootCodesDir, "0").mkdir();

		service.cleanupOldFilesystemFiles(daysBack, cwbFilesDaysBack);
	}

	@Test
	public void testCleanupOldPlannedOutages() {
		int daysBack = 0;
		mockDao.deletePlannedOutagesBefore(EasyMock.anyObject(Date.class));
		EasyMock.expectLastCall();

		service.cleanupOldPlannedOutages(daysBack);
	}
	
	@Test
	public void testCleanupOldTransientMetadata() {
		int numberLastMajorVersionKept = 0;
		int daysBeforeDocMetadataDelete = 0;
		mockDao.deleteTransientMetadata(numberLastMajorVersionKept, daysBeforeDocMetadataDelete);
		EasyMock.expectLastCall();

		service.cleanupOldTransientMetadata(numberLastMajorVersionKept, daysBeforeDocMetadataDelete);
	}
}
