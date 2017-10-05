package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.mgr.dao.ManagerDao;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

public class ManagerServiceImpl implements ManagerService {
    private static final Logger log = LogManager.getLogger(ManagerServiceImpl.class);

    private static final String GENERATOR_REST_SYNC_MISC_CONFIG_TEMPLATE =
        "http://%s:%d/%s/" + CoreConstants.URI_SYNC_MISC_CONFIG;
    private static final String GENERATOR_REST_SYNC_JOB_THROTTLE_CONFIG_TEMPLATE =
        "http://%s:%d/%s/" + CoreConstants.URI_SYNC_JOB_THROTTLE_CONFIG;
    private static final String GENERATOR_REST_SYNC_PLANNED_OUTAGE =
        "http://%s:%d/%s/" + CoreConstants.URI_SYNC_PLANNED_OUTAGE;

    private String environmentName;
    private String generatorContextName;
    private File rootWorkDirectory;
    private File rootCodesWorkbenchLandingStrip;
    /** Used to invoke the REST  job stop and restart operations on the ebookGenerator. */
    private RestTemplate restTemplate;
    /** The root web application context URL for the ebook generator. */
    private ManagerDao managerDao;
    private JobService jobService;
    private JobRequestService jobRequestService;

    @Override
    @Transactional(readOnly = true)
    public boolean isAnyJobsStartedOrQueued() {
        // Check for running jobs (in the generator web app)
        final int startedJobs = jobService.getStartedJobCount();
        if (startedJobs > 0) {
            log.debug(String.format("There are %d started job executions", startedJobs));
            return true;
        }
        // Check for queued jobs
        final List<JobRequest> jobRequests = jobRequestService.findAllJobRequests();
        if (jobRequests.size() > 0) {
            log.debug(String.format("There are %d queued jobs", jobRequests.size()));
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public JobExecution findRunningJob(@NotNull final BookDefinition book) {
        return managerDao.findRunningJobExecution(book);
    }

    @Override
    public SimpleRestServiceResponse pushMiscConfiguration(
        final MiscConfig config,
        final String contextName,
        final InetSocketAddress socketAddr) {
        final String url = String.format(
            GENERATOR_REST_SYNC_MISC_CONFIG_TEMPLATE,
            socketAddr.getHostName(),
            socketAddr.getPort(),
            contextName);
        log.debug("to URL: " + url);
        final SimpleRestServiceResponse response =
            restTemplate.postForObject(url, config, SimpleRestServiceResponse.class);
        return response;
    }

    @Override
    public SimpleRestServiceResponse pushJobThrottleConfiguration(
        final JobThrottleConfig config,
        final InetSocketAddress socketAddr) {
        final String url = String.format(
            GENERATOR_REST_SYNC_JOB_THROTTLE_CONFIG_TEMPLATE,
            socketAddr.getHostName(),
            socketAddr.getPort(),
            generatorContextName);
        log.debug("to URL: " + url);
        final SimpleRestServiceResponse response =
            restTemplate.postForObject(url, config, SimpleRestServiceResponse.class);
        return response;
    }

    @Override
    public SimpleRestServiceResponse pushPlannedOutage(final PlannedOutage outage, final InetSocketAddress socketAddr) {
        final String url = String.format(
            GENERATOR_REST_SYNC_PLANNED_OUTAGE,
            socketAddr.getHostName(),
            socketAddr.getPort(),
            generatorContextName);
        log.debug("to URL: " + url);
        final SimpleRestServiceResponse response =
            restTemplate.postForObject(url, outage, SimpleRestServiceResponse.class);
        return response;
    }

    @Override
    @Transactional
    public void cleanupOldSpringBatchDatabaseRecords(final int daysBack) {
        // Calculate the prior point in time before which data is to be removed
        final Date deleteJobsBefore = calculateDaysBackDate(daysBack);

        // Archive and then delete old BATCH_* database table records
        log.info(
            String.format(
                "Starting to archive/delete Spring Batch job records older than %d days old.  These are jobs run before: %s",
                daysBack,
                deleteJobsBefore.toString()));
        final int oldJobStepExecutionsRemoved =
            managerDao.archiveAndDeleteSpringBatchJobRecordsBefore(deleteJobsBefore);
        log.info(
            String.format(
                "Finished archiving/deleting %d old step executions that were older than %d days old.",
                oldJobStepExecutionsRemoved,
                daysBack));
    }

    @Override
    public void cleanupOldFilesystemFiles(final int daysBack, final int cwbFilesDaysBack) {
        // Calculate the prior point in time before which data is to be removed
        final Date deleteFilesBefore = calculateDaysBackDate(daysBack);
        // Remove old filesystem files that were used to create the book in the first place
        log.info(
            String.format(
                "Starting to remove job filesystem files older than %d days old.  These are files created before: %s",
                daysBack,
                deleteFilesBefore.toString()));
        removeOldJobFiles(deleteFilesBefore);
        log.info(String.format("Finished removing job files older than %d days old.", daysBack));

        // Remove old codes workbench files
        final Date cwbDeleteFilesBefore = calculateDaysBackDate(cwbFilesDaysBack);
        log.info(
            String.format(
                "Starting to remove codes workbench files older than %d days old.  These are files created before: %s",
                cwbFilesDaysBack,
                deleteFilesBefore.toString()));
        removeOldCwbFiles(cwbDeleteFilesBefore);
        log.info(String.format("Finished removing codes workbench files older than %d days old.", cwbFilesDaysBack));
    }

    @Override
    @Transactional
    public void cleanupOldPlannedOutages(final int daysBack) {
        final Date deleteOutagesBefore = calculateDaysBackDate(daysBack);
        log.debug(
            String.format(
                "Deleting expired planned outages older than %d days old.  These are outages that ended before: %s",
                daysBack,
                deleteOutagesBefore.toString()));
        managerDao.deletePlannedOutagesBefore(deleteOutagesBefore);
    }

    private Date calculateDaysBackDate(final int daysBack) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, -daysBack);
        return cal.getTime();
    }

    @Override
    @Transactional
    public void cleanupOldTransientMetadata(
        final int numberLastMajorVersionKept,
        final int daysBeforeDocMetadataDelete) {
        log.debug(
            String.format(
                "Deleting Metadata and keeping only %d good major version prior to %d days ago",
                numberLastMajorVersionKept,
                daysBeforeDocMetadataDelete));

        managerDao.deleteTransientMetadata(numberLastMajorVersionKept, daysBeforeDocMetadataDelete);
    }

    /**
     * Recursively delete job data file directories that hold data prior to the specified delete before date.
     * @param deleteJobsBefore work files created before this date will be deleted.
     */
    private void removeOldJobFiles(final Date deleteJobsBefore) {
        final File environmentDir = new File(rootWorkDirectory, environmentName);
        final File dataDir = new File(environmentDir, CoreConstants.DATA_DIR); // like "/apps/eBookBuilder/prod/data"
        final String deleteBeforeDateString =
            new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(deleteJobsBefore); // like "20120513"
        final String[] dateFileArray = dataDir.list();
        for (final String dateDirString : dateFileArray) {
            if (dateDirString.compareTo(deleteBeforeDateString) < 0) {
                final File dateDir = new File(dataDir, dateDirString);
                try {
                    FileUtils.deleteDirectory(dateDir);
                    log.debug("Deleted job directory: " + dateDir.getAbsolutePath());
                } catch (final IOException e) {
                    log.error(
                        String.format(
                            "Failed to recursively delete directory %s - %s",
                            dateDir.getAbsolutePath(),
                            e.getMessage()));
                }
            }
        }
    }

    /**
     * Recursively delete codes workbench data file directories that hold data prior to the specified delete before date.
     * @param deleteBefore work files created before this date will be deleted.
     */
    private void removeOldCwbFiles(final Date deleteBefore) {
        final File codeWorkbenchDir = rootCodesWorkbenchLandingStrip;
        for (final File file : codeWorkbenchDir.listFiles()) {
            final Long lastModified = file.lastModified();
            final Long deleteBeforeTime = deleteBefore.getTime();
            if (lastModified.compareTo(deleteBeforeTime) < 0) {
                try {
                    FileUtils.deleteDirectory(file);
                    log.debug("Deleted codes workbench directory: " + file.getAbsolutePath());
                } catch (final IOException e) {
                    log.error(
                        String.format(
                            "Failed to recursively delete directory %s - %s",
                            file.getAbsolutePath(),
                            e.getMessage()));
                }
            }
        }
    }

    @Required
    public void setGeneratorContextName(final String contextName) {
        generatorContextName = contextName;
    }

    @Required
    public void setRootWorkDirectory(final File dir) {
        rootWorkDirectory = dir;
    }

    @Required
    public void setRootCodesWorkbenchLandingStrip(final File dir) {
        rootCodesWorkbenchLandingStrip = dir;
    }

    @Required
    public void setEnvironmentName(final String envName) {
        environmentName = envName;
    }

    @Required
    public void setRestTemplate(final RestTemplate template) {
        restTemplate = template;
    }

    @Required
    public void setManagerDao(final ManagerDao dao) {
        managerDao = dao;
    }

    @Required
    public void setJobService(final JobService service) {
        jobService = service;
    }

    @Required
    public void setJobRequestService(final JobRequestService jobRequestService) {
        this.jobRequestService = jobRequestService;
    }
}
