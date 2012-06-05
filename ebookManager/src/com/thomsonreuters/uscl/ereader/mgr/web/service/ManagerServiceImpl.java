package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.mgr.dao.ManagerDao;

public class ManagerServiceImpl implements ManagerService {
	private static final Logger log = Logger.getLogger(ManagerServiceImpl.class);
	
	private static final String GENERATOR_REST_SYNC_MISC_CONFIG_TEMPLATE =
							"http://%s:%d/%s/"+CoreConstants.URI_SYNC_MISC_CONFIG;
	private static final String GENERATOR_REST_SYNC_JOB_THROTTLE_CONFIG_TEMPLATE =
							"http://%s:%d/%s/"+CoreConstants.URI_SYNC_JOB_THROTTLE_CONFIG;
	private static final String GENERATOR_REST_SYNC_PLANNED_OUTAGE =
			"http://%s:%d/%s/"+CoreConstants.URI_SYNC_PLANNED_OUTAGE;

	private String environmentName;
	private String generatorContextName;
	private File rootWorkDirectory;
	/** Used to invoke the REST  job stop and restart operations on the ebookGenerator. */
	private RestTemplate restTemplate;
	/** The root web application context URL for the ebook generator. */
	private ManagerDao managerDao;
	
	@Override
	@Transactional(readOnly=true)
	public JobExecution findRunningJob(long bookDefinitionId, String bookVersion) {
		JobExecution jobExecution = managerDao.findRunningJobExecution(bookDefinitionId, bookVersion);
		return jobExecution;
	}
	
	@Override
	public SimpleRestServiceResponse pushMiscConfiguration(MiscConfig config, String contextName, InetSocketAddress socketAddr) {
		String url = String.format(GENERATOR_REST_SYNC_MISC_CONFIG_TEMPLATE,
								   socketAddr.getHostName(), socketAddr.getPort(), contextName);
		log.debug("to URL: " + url);
		SimpleRestServiceResponse response = (SimpleRestServiceResponse)
				restTemplate.postForObject(url, config, SimpleRestServiceResponse.class);
		return response;
	}

	@Override
	public SimpleRestServiceResponse pushJobThrottleConfiguration(JobThrottleConfig config, InetSocketAddress socketAddr) {
		String url = String.format(GENERATOR_REST_SYNC_JOB_THROTTLE_CONFIG_TEMPLATE,
								   socketAddr.getHostName(), socketAddr.getPort(), generatorContextName);
		log.debug("to URL: " + url);
		SimpleRestServiceResponse response = (SimpleRestServiceResponse)
				restTemplate.postForObject(url, config, SimpleRestServiceResponse.class);
		return response;
	}
	
	@Override
	public SimpleRestServiceResponse pushPlannedOutage(PlannedOutage outage, InetSocketAddress socketAddr) {
		String url = String.format(GENERATOR_REST_SYNC_PLANNED_OUTAGE,
								   socketAddr.getHostName(), socketAddr.getPort(), generatorContextName);
		log.debug("to URL: " + url);
		SimpleRestServiceResponse response = (SimpleRestServiceResponse)
				restTemplate.postForObject(url, outage, SimpleRestServiceResponse.class);
		return response;
	}
	
	@Override
	@Transactional
	public void cleanupOldSpringBatchJobs(int daysBack) {
		// Calculate the prior point in time before which data is to be removed
		Date deleteJobsBefore = calculateDaysBackDate(daysBack);
		
		// Archive and then delete old BATCH_* database table records
		log.info(String.format("Archiving/Deleting Spring Batch job data older than %d days old.  These are jobs run before: %s", daysBack, deleteJobsBefore.toString()));
		int oldJobStepExecutionsRemoved = managerDao.archiveAndDeleteSpringBatchJobRecordsBefore(deleteJobsBefore);
		log.info(String.format("Archived/Deleted %d old step executions that were older than %d days old.", oldJobStepExecutionsRemoved, daysBack));
		
		// Remove old filesystem files that were used to create the book in the first place 
		removeOldJobFiles(deleteJobsBefore);
	}
	
	
	@Override
	@Transactional
	public void cleanupOldPlannedOutages(int daysBack) {
		Date deleteOutagesBefore = calculateDaysBackDate(daysBack);
		log.debug(String.format("Deleting expired planned outages older than %d days old.  These are outages that ended before: %s", daysBack, deleteOutagesBefore.toString()));
		managerDao.deletePlannedOutagesBefore(deleteOutagesBefore);
	}

	private Date calculateDaysBackDate(int daysBack) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -daysBack);
		return  cal.getTime();
	}
	
	@Override
	@Transactional
	public void cleanupOldTransientMetadata(int numberLastMajorVersionKept, int daysBeforeDocMetadataDelete) {

		log.debug(String.format("Deleting Metadata and keeping only %d good major version prior to %d days ago", numberLastMajorVersionKept, daysBeforeDocMetadataDelete));
			
		managerDao.deleteTransientMetadata(numberLastMajorVersionKept, daysBeforeDocMetadataDelete);
		
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
	public void setGeneratorContextName(String contextName) {
		this.generatorContextName = contextName;
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
	public void setManagerDao(ManagerDao dao) {
		this.managerDao = dao;
	}


}
