package com.thomsonreuters.uscl.ereader.mgr.web.service;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.SimpleRestServiceResponse;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.domain.PlannedOutage;
import com.thomsonreuters.uscl.ereader.mgr.dao.ManagerDao;
import com.thomsonreuters.uscl.ereader.mgr.web.service.job.JobService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service("managerService")
@Slf4j
public class ManagerServiceImpl implements ManagerService {
    private static final FileVisitor<Path> DELETE_VISITOR = new DeleteFilesFileVisitor();

    private static final String HTTP_TEMPLATE = "http://%s:%d/%s/";
    private static final String TO_URL = "to URL: ";

    private static final String GENERATOR_REST_SYNC_MISC_CONFIG_TEMPLATE =
        HTTP_TEMPLATE + CoreConstants.URI_SYNC_MISC_CONFIG;
    private static final String GENERATOR_REST_SYNC_JOB_THROTTLE_CONFIG_TEMPLATE =
        HTTP_TEMPLATE + CoreConstants.URI_SYNC_JOB_THROTTLE_CONFIG;
    private static final String GENERATOR_REST_SYNC_PLANNED_OUTAGE =
        HTTP_TEMPLATE + CoreConstants.URI_SYNC_PLANNED_OUTAGE;

    @Autowired
    @Qualifier("environmentName")
    private String environmentName;
    @Value("${generator.context.name}")
    private String generatorContextName;
    @Value("${root.work.directory}")
    private File rootWorkDirectory;
    @Value("${codes.workbench.root.dir}")
    private File rootCodesWorkbenchLandingStrip;
    @Autowired
    @Qualifier("generatorRestTemplate")
    private RestTemplate restTemplate;
    @Autowired
    private ManagerDao managerDao;
    @Autowired
    private JobService jobService;
    @Autowired
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
        if (!jobRequests.isEmpty()) {
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
        log.debug(TO_URL + url);
        return restTemplate.postForObject(url, config, SimpleRestServiceResponse.class);
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
        log.debug(TO_URL + url);
        return restTemplate.postForObject(url, config, SimpleRestServiceResponse.class);
    }

    @Override
    public SimpleRestServiceResponse pushPlannedOutage(final PlannedOutage outage, final InetSocketAddress socketAddr) {
        final String url = String.format(
            GENERATOR_REST_SYNC_PLANNED_OUTAGE,
            socketAddr.getHostName(),
            socketAddr.getPort(),
            generatorContextName);
        log.debug(TO_URL + url);
        return restTemplate.postForObject(url, outage, SimpleRestServiceResponse.class);
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
        for (final String dateDirString : Optional.ofNullable(dataDir.list()).orElseGet(() -> new String[0])) {
            if (dateDirString.compareTo(deleteBeforeDateString) < 0) {
                final File dateDir = new File(dataDir, dateDirString);
                deleteDir(dateDir, "Deleted job directory: ");
            }
        }
    }

    /**
     * Recursively delete codes workbench data file directories that hold data prior to the specified delete before date.
     * @param deleteBefore work files created before this date will be deleted.
     */
    private void removeOldCwbFiles(final Date deleteBefore) {
        for (final File file : Optional.ofNullable(rootCodesWorkbenchLandingStrip.listFiles())
            .orElseGet(() -> new File[0])) {
            final Long lastModified = file.lastModified();
            final Long deleteBeforeTime = deleteBefore.getTime();
            if (lastModified.compareTo(deleteBeforeTime) < 0) {
                deleteDir(file, "Deleted codes workbench directory: ");
            }
        }
    }

    private void deleteDir(final File dir, final String message) {
        if (dir.isDirectory()) {
            try {
                Files.walkFileTree(dir.toPath(), DELETE_VISITOR);
                log.debug(message + dir.getAbsolutePath());
            } catch (final IOException e) {
                final String msg = String
                    .format("Failed to recursively delete directory %s - %s", dir.getAbsolutePath(), e.getMessage());
                log.error(msg, e);
            }
        }
    }

    private static class DeleteFilesFileVisitor implements FileVisitor<Path> {
        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.deleteIfExists(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}
