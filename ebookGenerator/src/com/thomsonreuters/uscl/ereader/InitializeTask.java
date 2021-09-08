package com.thomsonreuters.uscl.ereader;

import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CombinedBookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.PublishingStatus;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.jms.IllegalStateException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_CREATED_LINKS_TRANSFORM_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_DOC_TO_IMAGE_MANIFEST_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_FIXED_TRANSFORM_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_HTML_WRAPPER_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_IMAGE_METADATA_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_POST_TRANSFORM_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_PREPROCESS_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_EBOOK_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_SPLIT_NODE_INFO_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_TRANSFORMED_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_TRANSFORM_CHAR_SEQUENCES_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_TRANSFORM_TOC;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOCS_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOCS_GUIDS_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOCS_METADATA_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOC_MISSING_GUIDS_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DYNAMIC_IMAGE_GUIDS_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_IMAGES_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_IMAGES_MISSING_IMAGE_GUIDS_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_STATIC_IMAGE_MANIFEST_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_TOC_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.TOC_FILE;
import static com.thomsonreuters.uscl.ereader.stats.PublishingStatus.COMPLETED;
import static com.thomsonreuters.uscl.ereader.stats.PublishingStatus.FAILED;

/**
 * Perform job setup for creating an ebook and place data into the JobExecutionContext for
 * use in later steps.
 * This includes various file system path calculations based on the JobParameters used to run the job.
 *
 */

@Slf4j
public class InitializeTask extends AbstractSbTasklet {
    private File rootWorkDirectory; // "/nas/ebookbuilder/data"
    private File rootCodesWorkbenchLandingStrip;
    private String environmentName;
    private PublishingStatsService publishingStatsService;
    private EBookAuditService eBookAuditService;
    private BookDefinitionService bookDefnService;
    @Autowired
    private CombinedBookDefinitionService combinedBookDefnService;
    @Autowired
    private NasFileSystem nasFileSystem;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final StepContext stepContext = chunkContext.getStepContext();
        final StepExecution stepExecution = stepContext.getStepExecution();
        final JobExecution jobExecution = stepExecution.getJobExecution();
        final ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        final JobInstance jobInstance = jobExecution.getJobInstance();
        final JobParameters jobParams = jobExecution.getJobParameters();
        PublishingStatus publishStatus = COMPLETED;
        try {
            // get ebookDefinition
            Optional.ofNullable(jobParams.getLong(JobParameterKey.COMBINED_BOOK_DEFINITION_ID))
                    .map(combinedBookDefinitionId -> combinedBookDefnService.findCombinedBookDefinitionById(combinedBookDefinitionId))
                    .ifPresent(combinedBookDefinition -> {
                        jobExecutionContext.put(JobExecutionKey.COMBINED_BOOK_DEFINITION, combinedBookDefinition);
                        jobExecutionContext.put(JobExecutionKey.HAS_FILE_SOURCE_TYPE, combinedBookDefinition.hasFileSourceType());
                    });
            final BookDefinition bookDefinition = Optional.ofNullable((CombinedBookDefinition) jobExecutionContext.get(JobExecutionKey.COMBINED_BOOK_DEFINITION))
                    .map(combinedBookDefinition -> combinedBookDefinition.getPrimaryTitle().getBookDefinition())
                    .orElseGet(() -> bookDefnService.findBookDefinitionByEbookDefId(jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID)));
            final String titleId = bookDefinition.getTitleId();

            // Place data on the JobExecutionContext for use in later steps
            jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);

            log.info("titleId (Fully Qualified): " + bookDefinition.getTitleId());
            log.info("hostname: " + jobParams.getString(JobParameterKey.HOST_NAME));
            log.info("TOC Collection: " + bookDefinition.getTocCollectionName());
            log.info("TOC Root Guid: " + bookDefinition.getRootTocGuid());
            log.info("NORT Domain: " + bookDefinition.getNortDomain());
            log.info("NORT Filter: " + bookDefinition.getNortFilterView());

            if (!nasFileSystem.getStaticContentDirectory().exists()) {
                throw new IllegalStateException(
                    "Expected staticContent directory does not exist: " + nasFileSystem.getStaticContentDirectory().getAbsolutePath());
            }

            if (bookDefinition.getSourceType().equals(SourceType.FILE) ||
                    Optional.ofNullable(jobExecutionContext.get(JobExecutionKey.HAS_FILE_SOURCE_TYPE))
                            .map(item -> (boolean) item)
                            .orElse(false)) {
                if (!rootCodesWorkbenchLandingStrip.exists()) {
                    throw new IllegalStateException(
                        "Expected Codes Workbench landing strip directory does not exist: "
                            + rootCodesWorkbenchLandingStrip.getAbsolutePath());
                }

                jobExecutionContext.putString(
                    JobExecutionKey.CODES_WORKBENCH_ROOT_LANDING_STRIP_DIR,
                    rootCodesWorkbenchLandingStrip.getAbsolutePath());
            }

            // Create the work directory for the ebook and create the physical directory in the filesystem
            // "<yyyyMMdd>/<titleId>/<jobInstanceId>"
            // Sample: "/apps/eBookBuilder/prod/data/20120131/FRCP/356"
            final String dynamicPath = String.format(
                "%s/%s/%s/%s/%d",
                environmentName,
                CoreConstants.DATA_DIR,
                new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(new Date()),
                titleId,
                jobInstance.getId());
            final File workDirectory = new File(rootWorkDirectory, dynamicPath);
            workDirectory.mkdirs();

            log.info("workDirectory: " + workDirectory.getAbsolutePath());

            if (!workDirectory.exists()) {
                throw new IllegalStateException(
                    "Expected work directory was not created in the filesystem: " + workDirectory.getAbsolutePath());
            }

            // Create gather directories
            final File gatherDirectory = newFile(workDirectory, GATHER_DIR);
            final File tocDirectory = newFile(gatherDirectory, GATHER_TOC_DIR);
            final File docsDirectory = newFile(gatherDirectory, GATHER_DOCS_DIR);
            final File tocFile = newFile(tocDirectory, TOC_FILE);
            final File docsMetadataDirectory = newFile(docsDirectory, GATHER_DOCS_METADATA_DIR);
            final File docsGuidsFile = newFile(gatherDirectory, GATHER_DOCS_GUIDS_FILE);
            final File docsMissingGuidsFile = newFile(gatherDirectory, GATHER_DOC_MISSING_GUIDS_FILE);

            // Image directories and files
            final File imageRootDirectory = newFile(gatherDirectory, GATHER_IMAGES_DIR);
            final File imageDynamicGuidsFile = newFile(gatherDirectory, GATHER_DYNAMIC_IMAGE_GUIDS_FILE);
            final File imageStaticManifestFile = newFile(gatherDirectory, GATHER_STATIC_IMAGE_MANIFEST_FILE);
            final File imageMissingGuidsFile = newFile(imageRootDirectory, GATHER_IMAGES_MISSING_IMAGE_GUIDS_FILE);

            // Create required directories
            gatherDirectory.mkdir();
            docsDirectory.mkdir();
            docsMetadataDirectory.mkdir();
            tocDirectory.mkdir();
            imageRootDirectory.mkdir(); // Root directory for images

            // Create format directories
            final File formatDirectory = newFile(workDirectory, FORMAT_DIR);
            final File transformTocDirectory = newFile(formatDirectory, FORMAT_TRANSFORM_TOC);
            final File transformedToc = newFile(transformTocDirectory, TOC_FILE);
            final File formatImageMetadataDirectory = newFile(formatDirectory, FORMAT_IMAGE_METADATA_DIR);
            final File transformCharSequencesDir = newFile(formatDirectory, FORMAT_TRANSFORM_CHAR_SEQUENCES_DIR);
            final File preprocessDirectory = newFile(formatDirectory, FORMAT_PREPROCESS_DIR);
            final File transformedDirectory = newFile(formatDirectory, FORMAT_TRANSFORMED_DIR);
            final File splitEbookTocDirectory = newFile(formatDirectory, FORMAT_SPLIT_TOC_DIR);
            final File postTransformDirectory = newFile(formatDirectory, FORMAT_POST_TRANSFORM_DIR);
            final File createdLinksTransformDirectory = newFile(formatDirectory, FORMAT_CREATED_LINKS_TRANSFORM_DIR);
            final File fixedTransformDirectory = newFile(formatDirectory, FORMAT_FIXED_TRANSFORM_DIR);
            final File htmlWrapperDirectory = newFile(formatDirectory, FORMAT_HTML_WRAPPER_DIR);
            final File splitEbookDirectory = newFile(formatDirectory, FORMAT_SPLIT_EBOOK_DIR);
            formatDirectory.mkdir();
            transformTocDirectory.mkdir();
            preprocessDirectory.mkdir();
            transformedDirectory.mkdir();
            formatImageMetadataDirectory.mkdir();
            transformCharSequencesDir.mkdir();
            splitEbookDirectory.mkdir();
            splitEbookTocDirectory.mkdir();
            postTransformDirectory.mkdir();
            createdLinksTransformDirectory.mkdir();
            fixedTransformDirectory.mkdir();
            htmlWrapperDirectory.mkdir();

            final File splitTocFile = newFile(splitEbookTocDirectory, FORMAT_SPLIT_TOC_FILE);
            //Format\splitEbook\splitNodeInfo.txt holds Doc Information
            final File splitNodeInfoFile = newFile(splitEbookDirectory, FORMAT_SPLIT_TOC_SPLIT_NODE_INFO_FILE);
            final File imageToDocumentManifestFile = newFile(formatDirectory, FORMAT_DOC_TO_IMAGE_MANIFEST_FILE);

            //File htmlDirectory = new File(formatDirectory, "HTML");

            // Create the absolute path to the final e-book artifact - a GNU ZIP file
            // "<titleId>.gz" file basename is a function of the book title ID, like: "FRCP.gz"
            final File ebookFile = new File(workDirectory, titleId + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);

            jobExecutionContext.putString(JobExecutionKey.WORK_DIRECTORY, workDirectory.getAbsolutePath());

            jobExecutionContext.putString(JobExecutionKey.EBOOK_FILE, ebookFile.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.GATHER_DIR, gatherDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.GATHER_DOCS_DIR, docsDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.GATHER_DOCS_METADATA_DIR, docsMetadataDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.GATHER_TOC_DIR, tocDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.GATHER_TOC_FILE, tocFile.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.TRANSFORMED_TOC_DIR, transformTocDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.TRANSFORMED_TOC_FILE, transformedToc.getAbsolutePath());

            jobExecutionContext.putString(JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE, docsGuidsFile.getAbsolutePath());

            jobExecutionContext
                .putString(JobExecutionKey.DOCS_MISSING_GUIDS_FILE, docsMissingGuidsFile.getAbsolutePath());

            // Images - static and dynamic directories and files
            jobExecutionContext
                .putString(JobExecutionKey.IMAGE_DYNAMIC_GUIDS_FILE, imageDynamicGuidsFile.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.IMAGE_ROOT_DIR, imageRootDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.IMAGE_STATIC_MANIFEST_FILE, imageStaticManifestFile.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.IMAGE_TO_DOC_MANIFEST_FILE, imageToDocumentManifestFile.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.IMAGE_MISSING_GUIDS_FILE, imageMissingGuidsFile.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.FORMAT_PREPROCESS_DIR, preprocessDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.FORMAT_TRANSFORMED_DIR, transformedDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.FORMAT_IMAGE_METADATA_DIR, formatImageMetadataDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.SPLIT_EBOOK_TOC_DIR, splitEbookTocDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.SPLIT_EBOOK_DIR, splitEbookDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.FORMAT_SPLITTOC_FILE, splitTocFile.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.SPLIT_NODE_INFO_FILE, splitNodeInfoFile.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.FORMAT_POST_TRANSFORM_DIR, postTransformDirectory.getAbsolutePath());
            jobExecutionContext.putString(
                JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_CREATED_DIR,
                createdLinksTransformDirectory.getAbsolutePath());
            jobExecutionContext.putString(
                JobExecutionKey.FORMAT_TRANSFORM_INTERNAL_LINKS_FIXED_DIR,
                fixedTransformDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.FORMAT_HTML_WRAPPER_DIR, htmlWrapperDirectory.getAbsolutePath());

            log.info("Image Service URL: " + System.getProperty("image.vertical.context.url"));
            log.info("Proview Domain URL: " + System.getProperty("proview.domain"));
        } catch (final Exception e) {
            publishStatus = FAILED;
            throw (e);
        } finally {
            final PublishingStats pubStats = new PublishingStats();

            // TODO: replace with call to job queue?
            final Date rightNow = publishingStatsService.getSysDate();
            final Long ebookDefId = jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID);
            pubStats.setEbookDefId(ebookDefId);
            final Long auditId = eBookAuditService.findEbookAuditByEbookDefId(ebookDefId);
            final EbookAudit audit = new EbookAudit();
            audit.setAuditId(auditId);
            pubStats.setAudit(audit);
            pubStats.setBookVersionSubmitted(jobParams.getString(JobParameterKey.BOOK_VERSION_SUBMITTED));
            pubStats.setJobHostName(jobParams.getString(JobParameterKey.HOST_NAME));
            pubStats.setJobInstanceId(Long.valueOf(jobInstance.getId().toString()));
            pubStats.setJobSubmitterName(jobParams.getString(JobParameterKey.USER_NAME));
            pubStats.setJobSubmitTimestamp(jobParams.getDate(JobParameterKey.TIMESTAMP));
            pubStats.setPublishStatus("initializeJob : " + publishStatus);
            pubStats.setPublishStartTimestamp(rightNow);
            pubStats.setLastUpdated(rightNow);
            publishingStatsService.savePublishingStats(pubStats);
        }

        return ExitStatus.COMPLETED;
    }

    private File newFile(final File parentDir, final NortTocCwbFileSystemConstants fileSystemItem) {
        return new File(parentDir, fileSystemItem.getName());
    }

    @Required
    public void setRootWorkDirectory(final File rootDir) {
        rootWorkDirectory = rootDir;
    }

    @Required
    public void setRootCodesWorkbenchLandingStrip(final File rootDir) {
        rootCodesWorkbenchLandingStrip = rootDir;
    }

    @Required
    public void setEnvironmentName(final String envName) {
        environmentName = envName;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }

    @Required
    public void setEbookAuditService(final EBookAuditService eBookAuditService) {
        this.eBookAuditService = eBookAuditService;
    }

    @Required
    public void setBookDefnService(final BookDefinitionService bookDefnService) {
        this.bookDefnService = bookDefnService;
    }
}
