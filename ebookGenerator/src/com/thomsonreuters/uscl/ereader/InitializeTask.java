package com.thomsonreuters.uscl.ereader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.IllegalStateException;

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
public class InitializeTask extends AbstractSbTasklet
{
    private static final Logger log = LogManager.getLogger(InitializeTask.class);

    private static final String DE_DUPPING_ANCHOR_FILE = "eBG_deDupping_anchors.txt";

    private File rootWorkDirectory; // "/nas/ebookbuilder/data"
    private File rootCodesWorkbenchLandingStrip;
    private String environmentName;
    private PublishingStatsService publishingStatsService;
    private EBookAuditService eBookAuditService;
    private BookDefinitionService bookDefnService;
    private File staticContentDirectory;

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        final StepContext stepContext = chunkContext.getStepContext();
        final StepExecution stepExecution = stepContext.getStepExecution();
        final JobExecution jobExecution = stepExecution.getJobExecution();
        final ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        final JobInstance jobInstance = jobExecution.getJobInstance();
        final JobParameters jobParams = jobExecution.getJobParameters();
        String publishStatus = "Completed";
        try
        {
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

            if (!staticContentDirectory.exists())
            {
                throw new IllegalStateException(
                    "Expected staticContent directory does not exist: " + staticContentDirectory.getAbsolutePath());
            }
            jobExecutionContext.putString(JobExecutionKey.STATIC_CONTENT_DIR, staticContentDirectory.getAbsolutePath());

            if (bookDefinition.getSourceType().equals(SourceType.FILE))
            {
                if (!rootCodesWorkbenchLandingStrip.exists())
                {
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

            if (!workDirectory.exists())
            {
                throw new IllegalStateException(
                    "Expected work directory was not created in the filesystem: " + workDirectory.getAbsolutePath());
            }

            // Create gather directories
            final File gatherDirectory = new File(workDirectory, "Gather");
            final File docsDirectory = new File(gatherDirectory, "Docs");
            final File tocDirectory = new File(gatherDirectory, "Toc");
            final File tocFile = new File(tocDirectory, "toc.xml");
            final File docsMetadataDirectory = new File(docsDirectory, "Metadata");
            final File docsGuidsFile = new File(gatherDirectory, "docs-guids.txt");
            final File docsMissingGuidsFile = new File(gatherDirectory, "Docs_doc_missing_guids.txt");

            // Image directories and files
            final File imageRootDirectory = new File(gatherDirectory, "Images");
            final File imageDynamicDirectory = new File(imageRootDirectory, "Dynamic");
            final File imageStaticDirectory = new File(imageRootDirectory, "Static");
            final File imageDynamicGuidsFile = new File(gatherDirectory, "dynamic-image-guids.txt");
            final File imageStaticManifestFile = new File(gatherDirectory, "static-image-manifest.txt");
            final File imageMissingGuidsFile = new File(imageRootDirectory, "missing_image_guids.txt");

            final File assembleDirectory = new File(workDirectory, "Assemble");
            final File assembledTitleDirectory = new File(assembleDirectory, titleId);
            final File assembleDocumentsDirectory = new File(assembledTitleDirectory, "documents");
            final File assembleAssetsDirectory = new File(assembledTitleDirectory, "assets");
            final File assembleArtworkDirectory = new File(assembledTitleDirectory, "artwork");

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
            final File formatDirectory = new File(workDirectory, "Format");
            final File preprocessDirectory = new File(formatDirectory, "Preprocess");
            final File transformedDirectory = new File(formatDirectory, "Transformed");
            final File formatImageMetadataDirectory = new File(formatDirectory, "ImageMetadata");
            final File splitEbookTocDirectory = new File(formatDirectory, "splitToc");
            final File splitEbookDirectory = new File(formatDirectory, "splitEbook");
            final File postTransformDirectory = new File(formatDirectory, "PostTransform");
            final File createdLinksTransformDirectory = new File(formatDirectory, "CreatedLinksTransform");
            final File fixedTransformDirectory = new File(formatDirectory, "FixedTransform");
            final File htmlWrapperDirectory = new File(formatDirectory, "HTMLWrapper");
            final File frontMatterHTMLDiretory = new File(formatDirectory, "FrontMatterHTML");
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

            final File splitTocFile = new File(splitEbookTocDirectory, "splitToc.xml");
            //Format\splitEbook\doc-To-SplitBook.txt holds Doc Information
            final File docToSplitBook = new File(splitEbookDirectory, "doc-To-SplitBook.txt");
            //Format\splitEbook\splitNodeInfo.txt holds Doc Information
            final File splitNodeInfoFile = new File(splitEbookDirectory, "splitNodeInfo.txt");
            final File imageToDocumentManifestFile = new File(formatDirectory, "doc-to-image-manifest.txt");

            final File deDuppingAnchorFile = new File(formatDirectory, DE_DUPPING_ANCHOR_FILE);

            if (deDuppingAnchorFile.exists())
            {
                deDuppingAnchorFile.delete();
            }

            deDuppingAnchorFile.createNewFile();

            //File htmlDirectory = new File(formatDirectory, "HTML");

            // Create the absolute path to the final e-book artifact - a GNU ZIP file
            // "<titleId>.gz" file basename is a function of the book title ID, like: "FRCP.gz"
            final File ebookFile = new File(workDirectory, titleId + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
            final File titleXmlFile = new File(assembledTitleDirectory, "title.xml");
            //Format\splitEbook\splitTitle.xml holds Toc which is same in all split books.
            final File splitTitleXmlFile = new File(splitEbookDirectory, "splitTitle.xml");

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
        }
        catch (final Exception e)
        {
            publishStatus = "Failed";
            throw (e);
        }
        finally
        {
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

    @Required
    public void setRootWorkDirectory(final File rootDir)
    {
        rootWorkDirectory = rootDir;
    }

    @Required
    public void setRootCodesWorkbenchLandingStrip(final File rootDir)
    {
        rootCodesWorkbenchLandingStrip = rootDir;
    }

    @Required
    public void setStaticContentDirectory(final File staticContentDirectory)
    {
        this.staticContentDirectory = staticContentDirectory;
    }

    @Required
    public void setEnvironmentName(final String envName)
    {
        environmentName = envName;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }

    @Required
    public void setEbookAuditService(final EBookAuditService eBookAuditService)
    {
        this.eBookAuditService = eBookAuditService;
    }

    @Required
    public void setBookDefnService(final BookDefinitionService bookDefnService)
    {
        this.bookDefnService = bookDefnService;
    }
}
