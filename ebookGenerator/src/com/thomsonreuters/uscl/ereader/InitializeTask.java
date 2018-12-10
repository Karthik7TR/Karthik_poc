package com.thomsonreuters.uscl.ereader;

import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.ASSEMBLE_ARTWORK_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.ASSEMBLE_ASSETS_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.ASSEMBLE_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.ASSEMBLE_DOCUMENTS_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.ASSEMBLE_TITLE_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_CREATED_LINKS_TRANSFORM_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_DE_DUPPING_ANCHOR_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_DOC_TO_IMAGE_MANIFEST_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_FIXED_TRANSFORM_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_FRONT_MATTER_HTML_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_HTML_WRAPPER_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_IMAGE_METADATA_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_POST_TRANSFORM_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_PREPROCESS_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_EBOOK_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_EBOOK_SPLIT_TITLE_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_DOC_TO_SPLIT_BOOK_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_SPLIT_TOC_SPLIT_NODE_INFO_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.FORMAT_TRANSFORMED_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOCS_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOCS_GUIDS_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOCS_METADATA_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DOC_MISSING_GUIDS_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_DYNAMIC_IMAGE_GUIDS_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_IMAGES_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_IMAGES_DYNAMIC_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_IMAGES_MISSING_IMAGE_GUIDS_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_IMAGES_STATIC_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_STATIC_IMAGE_MANIFEST_FILE;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_TOC_DIR;
import static com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants.GATHER_TOC_FILE;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.IllegalStateException;

import com.thomsonreuters.uscl.ereader.common.filesystem.NortTocCwbFileSystemConstants;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * Perform job setup for creating an ebook and place data into the JobExecutionContext for
 * use in later steps.
 * This includes various file system path calculations based on the JobParameters used to run the job.
 *
 */
public class InitializeTask extends AbstractSbTasklet {
    private static final Logger log = LogManager.getLogger(InitializeTask.class);

    private File rootWorkDirectory; // "/nas/ebookbuilder/data"
    private File rootCodesWorkbenchLandingStrip;
    private String environmentName;
    private PublishingStatsService publishingStatsService;
    private EBookAuditService eBookAuditService;
    private BookDefinitionService bookDefnService;
    private File staticContentDirectory;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext)
        throws Exception {
        final StepContext stepContext = chunkContext.getStepContext();
        final StepExecution stepExecution = stepContext.getStepExecution();
        final JobExecution jobExecution = stepExecution.getJobExecution();
        final ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        final JobInstance jobInstance = jobExecution.getJobInstance();
        final JobParameters jobParams = jobExecution.getJobParameters();
        String publishStatus = "Completed";
        try {
            // get ebookDefinition
            final BookDefinition bookDefinition =
                bookDefnService.findBookDefinitionByEbookDefId(jobParams.getLong(JobParameterKey.BOOK_DEFINITION_ID));
            final String titleId = bookDefinition.getTitleId();

            // Place data on the JobExecutionContext for use in later steps
            jobExecutionContext.put(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);

            log.info("titleId (Fully Qualified): " + bookDefinition.getTitleId());
            log.info("hostname: " + jobParams.getString(JobParameterKey.HOST_NAME));
            log.info("TOC Collection: " + bookDefinition.getTocCollectionName());
            log.info("TOC Root Guid: " + bookDefinition.getRootTocGuid());
            log.info("NORT Domain: " + bookDefinition.getNortDomain());
            log.info("NORT Filter: " + bookDefinition.getNortFilterView());

            if (!staticContentDirectory.exists()) {
                throw new IllegalStateException(
                    "Expected staticContent directory does not exist: " + staticContentDirectory.getAbsolutePath());
            }
            jobExecutionContext.putString(JobExecutionKey.STATIC_CONTENT_DIR, staticContentDirectory.getAbsolutePath());

            if (bookDefinition.getSourceType().equals(SourceType.FILE)) {
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
            final File tocFile = newFile(tocDirectory, GATHER_TOC_FILE);
            final File docsMetadataDirectory = newFile(docsDirectory, GATHER_DOCS_METADATA_DIR);
            final File docsGuidsFile = newFile(gatherDirectory, GATHER_DOCS_GUIDS_FILE);
            final File docsMissingGuidsFile = newFile(gatherDirectory, GATHER_DOC_MISSING_GUIDS_FILE);

            // Image directories and files
            final File imageRootDirectory = newFile(gatherDirectory, GATHER_IMAGES_DIR);
            final File imageDynamicDirectory = newFile(imageRootDirectory, GATHER_IMAGES_DYNAMIC_DIR);
            final File imageStaticDirectory = newFile(imageRootDirectory, GATHER_IMAGES_STATIC_DIR);
            final File imageDynamicGuidsFile = newFile(gatherDirectory, GATHER_DYNAMIC_IMAGE_GUIDS_FILE);
            final File imageStaticManifestFile = newFile(gatherDirectory, GATHER_STATIC_IMAGE_MANIFEST_FILE);
            final File imageMissingGuidsFile = newFile(imageRootDirectory, GATHER_IMAGES_MISSING_IMAGE_GUIDS_FILE);

            final File assembleDirectory = newFile(workDirectory, ASSEMBLE_DIR);
            final File assembledTitleDirectory = new File(assembleDirectory, titleId);
            final File assembleDocumentsDirectory = newFile(assembledTitleDirectory, ASSEMBLE_DOCUMENTS_DIR);
            final File assembleAssetsDirectory = newFile(assembledTitleDirectory, ASSEMBLE_ASSETS_DIR);
            final File assembleArtworkDirectory = newFile(assembledTitleDirectory, ASSEMBLE_ARTWORK_DIR);

            // Create required directories
            gatherDirectory.mkdir();
            docsDirectory.mkdir();
            docsMetadataDirectory.mkdir();
            tocDirectory.mkdir();
            imageRootDirectory.mkdir(); // Root directory for images
            imageStaticDirectory.mkdir(); // where the copied static images go
            imageDynamicDirectory.mkdir(); // where images from the Image Vertical REST service go
            assembleDirectory.mkdir();
            assembledTitleDirectory.mkdir();
            assembleDocumentsDirectory.mkdir();
            assembleAssetsDirectory.mkdir();
            assembleArtworkDirectory.mkdir();

            // Create format directories
            final File formatDirectory = newFile(workDirectory, FORMAT_DIR);
            final File frontMatterHTMLDiretory = newFile(formatDirectory, FORMAT_FRONT_MATTER_HTML_DIR);
            final File formatImageMetadataDirectory = newFile(formatDirectory, FORMAT_IMAGE_METADATA_DIR);
            final File preprocessDirectory = newFile(formatDirectory, FORMAT_PREPROCESS_DIR);
            final File transformedDirectory = newFile(formatDirectory, FORMAT_TRANSFORMED_DIR);
            final File splitEbookTocDirectory = newFile(formatDirectory, FORMAT_SPLIT_TOC_DIR);
            final File postTransformDirectory = newFile(formatDirectory, FORMAT_POST_TRANSFORM_DIR);
            final File createdLinksTransformDirectory = newFile(formatDirectory, FORMAT_CREATED_LINKS_TRANSFORM_DIR);
            final File fixedTransformDirectory = newFile(formatDirectory, FORMAT_FIXED_TRANSFORM_DIR);
            final File htmlWrapperDirectory = newFile(formatDirectory, FORMAT_HTML_WRAPPER_DIR);
            final File splitEbookDirectory = newFile(formatDirectory, FORMAT_SPLIT_EBOOK_DIR);
            formatDirectory.mkdir();
            preprocessDirectory.mkdir();
            transformedDirectory.mkdir();
            formatImageMetadataDirectory.mkdir();
            splitEbookDirectory.mkdir();
            splitEbookTocDirectory.mkdir();
            postTransformDirectory.mkdir();
            createdLinksTransformDirectory.mkdir();
            fixedTransformDirectory.mkdir();
            htmlWrapperDirectory.mkdir();
            frontMatterHTMLDiretory.mkdir();

            final File splitTocFile = newFile(splitEbookTocDirectory, FORMAT_SPLIT_TOC_FILE);
            //Format\splitEbook\doc-To-SplitBook.txt holds Doc Information
            final File docToSplitBook = newFile(splitEbookDirectory, FORMAT_SPLIT_TOC_DOC_TO_SPLIT_BOOK_FILE);
            //Format\splitEbook\splitNodeInfo.txt holds Doc Information
            final File splitNodeInfoFile = newFile(splitEbookDirectory, FORMAT_SPLIT_TOC_SPLIT_NODE_INFO_FILE);
            final File imageToDocumentManifestFile = newFile(formatDirectory, FORMAT_DOC_TO_IMAGE_MANIFEST_FILE);

            final File deDuppingAnchorFile = newFile(formatDirectory, FORMAT_DE_DUPPING_ANCHOR_FILE);

            if (deDuppingAnchorFile.exists()) {
                deDuppingAnchorFile.delete();
            }

            deDuppingAnchorFile.createNewFile();

            //File htmlDirectory = new File(formatDirectory, "HTML");

            // Create the absolute path to the final e-book artifact - a GNU ZIP file
            // "<titleId>.gz" file basename is a function of the book title ID, like: "FRCP.gz"
            final File ebookFile = new File(workDirectory, titleId + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
            final File titleXmlFile = newFile(assembledTitleDirectory, ASSEMBLE_TITLE_FILE);
            //Format\splitEbook\splitTitle.xml holds Toc which is same in all split books.
            final File splitTitleXmlFile = newFile(splitEbookDirectory, FORMAT_SPLIT_EBOOK_SPLIT_TITLE_FILE);

            jobExecutionContext.putString(JobExecutionKey.WORK_DIRECTORY, workDirectory.getAbsolutePath());

            jobExecutionContext.putString(JobExecutionKey.DEDUPPING_FILE, deDuppingAnchorFile.getAbsolutePath());

            jobExecutionContext.putString(JobExecutionKey.EBOOK_DIRECTORY, assembledTitleDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.EBOOK_FILE, ebookFile.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.GATHER_DIR, gatherDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.GATHER_DOCS_DIR, docsDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.GATHER_DOCS_METADATA_DIR, docsMetadataDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.GATHER_TOC_DIR, tocDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.GATHER_TOC_FILE, tocFile.getAbsolutePath());

            jobExecutionContext.putString(JobExecutionKey.DOCS_DYNAMIC_GUIDS_FILE, docsGuidsFile.getAbsolutePath());

            jobExecutionContext
                .putString(JobExecutionKey.DOCS_MISSING_GUIDS_FILE, docsMissingGuidsFile.getAbsolutePath());

            // Images - static and dynamic directories and files
            jobExecutionContext
                .putString(JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR, imageDynamicDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.IMAGE_DYNAMIC_GUIDS_FILE, imageDynamicGuidsFile.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.IMAGE_ROOT_DIR, imageRootDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.IMAGE_STATIC_DEST_DIR, imageStaticDirectory.getAbsolutePath());
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
            jobExecutionContext.putString(JobExecutionKey.DOC_TO_SPLITBOOK_FILE, docToSplitBook.getAbsolutePath());
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
            jobExecutionContext
                .putString(JobExecutionKey.FORMAT_FRONT_MATTER_HTML_DIR, frontMatterHTMLDiretory.getAbsolutePath());

            jobExecutionContext.putString(JobExecutionKey.ASSEMBLE_DIR, assembleDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.ASSEMBLE_DOCUMENTS_DIR, assembleDocumentsDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.ASSEMBLE_ASSETS_DIR, assembleAssetsDirectory.getAbsolutePath());
            jobExecutionContext
                .putString(JobExecutionKey.ASSEMBLE_ARTWORK_DIR, assembleArtworkDirectory.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.TITLE_XML_FILE, titleXmlFile.getAbsolutePath());
            jobExecutionContext.putString(JobExecutionKey.SPLIT_TITLE_XML_FILE, splitTitleXmlFile.getAbsolutePath());

            log.info("Image Service URL: " + System.getProperty("image.vertical.context.url"));
            log.info("Proview Domain URL: " + System.getProperty("proview.domain"));
        } catch (final Exception e) {
            publishStatus = "Failed";
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
    public void setStaticContentDirectory(final File staticContentDirectory) {
        this.staticContentDirectory = staticContentDirectory;
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
