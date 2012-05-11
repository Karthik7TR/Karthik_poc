package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobOperationResponse;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.mgr.dao.ManagerDao;

public class ManagerServiceImpl implements ManagerService {
	private static final Logger log = Logger.getLogger(ManagerServiceImpl.class);
	private static String GENERATOR_REST_STOP_JOB_URL_PATTERN =
							"{context}/service/stop/job/{jobExecutionId}";
	private static String GENERATOR_REST_RESTART_JOB_URL_PATTERN =
							"{context}/service/restart/job/{jobExecutionId}";
	private static String GENERATOR_REST_GET_STEP_NAMES_PATTERN =
							"{context}/service/get/step/names";	

	private String environmentName;
	private File rootWorkDirectory;
	/** Used to invoke the REST  job stop and restart operations on the ebookGenerator. */
	private RestTemplate restTemplate;
	/** The root web application context URL for the ebook generator. */
	private URL generatorContextUrl;
	private ManagerDao managerDao;
	
	@Override
	public JobOperationResponse restartJob(long jobExecutionId) {
		
		JobOperationResponse response = (JobOperationResponse) 
					restTemplate.getForObject(GENERATOR_REST_RESTART_JOB_URL_PATTERN,
					JobOperationResponse.class,
					generatorContextUrl.toString(), jobExecutionId);
		return response;
	}
	
	@Override
	public JobOperationResponse stopJob(long jobExecutionId) {
		JobOperationResponse response = (JobOperationResponse)
					restTemplate.getForObject(GENERATOR_REST_STOP_JOB_URL_PATTERN,
					JobOperationResponse.class,
					generatorContextUrl.toString(), jobExecutionId);
		return response;
	}
	
	@Override
	public List<String> getStepNames() {
		String csvStepNames = (String)
					restTemplate.getForObject(GENERATOR_REST_GET_STEP_NAMES_PATTERN,
					String.class, generatorContextUrl.toString());
		ArrayList<String> stepNames = new ArrayList<String>();
		if (csvStepNames != null) {
			StringTokenizer tokenizer = new StringTokenizer(csvStepNames, ",");
			while (tokenizer.hasMoreTokens()) {
				String stepName = tokenizer.nextToken();
				stepNames.add(stepName);
			}
		}
		Collections.sort(stepNames);
		return stepNames;
	}
	
	@Override
	public JobOperationResponse pushJobThrottleConfiguration(InetSocketAddress socketAddr, JobThrottleConfig newJobThrottleConfig) {
		String url = String.format("http://%s:%d/ebookGenerator/service/update/job/throttle/config",
								   socketAddr.getHostName(), socketAddr.getPort());		
		JobOperationResponse response = (JobOperationResponse)
				restTemplate.postForObject(url, newJobThrottleConfig, JobOperationResponse.class);
		return response;
	}
	
	@Override
	@Transactional
	public void cleanupOldSpringBatchJobs(int daysBack) {
		// Calculate the prior point in time before which data is to be removed
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -daysBack);
		Date deleteJobsBefore = cal.getTime();
		
		// Archive and then delete old BATCH_* database table records
		log.info(String.format("Deleting Spring Batch job data older than %d days old, jobs before: %s", daysBack, deleteJobsBefore.toString()));
		int oldJobStepExecutionsRemoved = managerDao.archiveAndDeleteSpringBatchJobRecordsBefore(deleteJobsBefore);
		log.info(String.format("Archived/Deleted %d old step executions.", oldJobStepExecutionsRemoved));
		
		// Remove old filesystem files that were used to create the book in the first place 
		removeOldJobFiles(deleteJobsBefore);
	}
	
	/**
	 * Recursively delete job data file directories that hold data prior to the specified delete before date.
	 * @param deleteJobsBefore work files created before this date will be deleted.
	 */
	private void removeOldJobFiles(Date deleteJobsBefore) {
		File environmentDir = new File(rootWorkDirectory, environmentName);
		File dataDir = new File(environmentDir, CoreConstants.DATA_DIR);  // like "/apps/eBookBuilder/prod/data"
		String deleteBeforeDateString = new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(deleteJobsBefore); // like "20120513"
		String[] dateFileArray = dataDir.list();
		for (String dateDirString : dateFileArray) {
			if (dateDirString.compareTo(deleteBeforeDateString) < 0) {
				File dateDir = new File(dataDir, dateDirString);
				try {
					FileUtils.deleteDirectory(dateDir);
					log.debug("Deleted job directory: " + dateDir.getAbsolutePath());
				} catch (IOException e) {
					log.error(String.format("Failed to recursively delete directory %s - %s", dateDir.getAbsolutePath(), e.getMessage()));
				}
			}
		}
	}
	
	@Required
	public void setRootWorkDirectory(File dir) {
		this.rootWorkDirectory = dir;
	}
	@Required
	public void setEnvironmentName(String envName) {
		this.environmentName = envName;
	}
	@Required
	public void setRestTemplate(RestTemplate template) {
		this.restTemplate = template;
	}
	@Required
	public void setGeneratorContextUrl(URL url) {
		this.generatorContextUrl = url;
	}
	@Required
	public void setManagerDao(ManagerDao dao) {
		this.managerDao = dao;
	}
}
