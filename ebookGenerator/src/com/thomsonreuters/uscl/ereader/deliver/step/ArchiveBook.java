package com.thomsonreuters.uscl.ereader.deliver.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * This class is responsible for archiving the created book artifact.
 * Only performed if this is the production ("prod") environment.
 * The last delivered major and minor number version of the file is archived.
 */
public class ArchiveBook extends AbstractSbTasklet
{
    private static final Logger log = LogManager.getLogger(ArchiveBook.class);
    public static final String MAJOR_ARCHIVE_DIR = "major";
    public static final String MINOR_ARCHIVE_DIR = "minor";

    private String environmentName;
    private File archiveBaseDirectory;
    private PublishingStatsService publishingStatsService;
    private DocMetadataService docMetadataService;
    private BookDefinitionService bookService;
    private ProviewAuditService proviewAuditService;

    @Required
    public void setProviewAuditService(final ProviewAuditService service)
    {
        proviewAuditService = service;
    }

    public DocMetadataService getDocMetadataService()
    {
        return docMetadataService;
    }

    public void setDocMetadataService(final DocMetadataService docMetadataService)
    {
        this.docMetadataService = docMetadataService;
    }

    @Required
    public void setBookDefinitionService(final BookDefinitionService service)
    {
        bookService = service;
    }

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        final JobInstance jobInstance = getJobInstance(chunkContext);
        String publishStatus = "Completed";
        final PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId(jobInstance.getId());
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final JobParameters jobParameters = getJobParameters(chunkContext);
        final String bookVersion = jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);
        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);
        try
        {
            try
            {
                ProviewAudit audit = createAudit(
                    bookDefinition.getFullyQualifiedTitleId(),
                    "v" + jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED),
                    bookDefinition.getLastUpdated(),
                    "REVIEW",
                    jobParameters.getString(JobParameterKey.USER_NAME));
                proviewAuditService.save(audit);

                if (bookDefinition.isSplitBook())
                {
                    // update table with Node Info
                    final String splitNodeInfoFile =
                        getRequiredStringProperty(jobExecutionContext, JobExecutionKey.SPLIT_NODE_INFO_FILE);
                    final List<SplitNodeInfo> currentsplitNodeList = new ArrayList<>();
                    readDocImgFile(new File(splitNodeInfoFile), currentsplitNodeList, bookVersion, bookDefinition);

                    if (!currentsplitNodeList.isEmpty())
                    {
                        //Save to Proview Audit
                        for (final SplitNodeInfo splitNodeInfo : currentsplitNodeList)
                        {
                            audit = createAudit(
                                splitNodeInfo.getSplitBookTitle(),
                                "v" + splitNodeInfo.getBookVersionSubmitted(),
                                bookDefinition.getLastUpdated(),
                                "REVIEW",
                                jobParameters.getString(JobParameterKey.USER_NAME));
                            proviewAuditService.save(audit);
                        }

                        final List<SplitNodeInfo> persistedSplitNodes = bookDefinition.getSplitNodesAsList();

                        final boolean same = hasChanged(persistedSplitNodes, currentsplitNodeList, bookVersion);
                        if (!same)
                        {
                            bookService.updateSplitNodeInfoSet(
                                bookDefinition.getEbookDefinitionId(),
                                currentsplitNodeList,
                                bookVersion);
                        }
                    }
                }
            }
            catch (final Exception e)
            {
                log.error("Failed to update splitBookInfo", e);
                throw e;
            }
            // We only archive in the production environment
            if (CoreConstants.PROD_ENVIRONMENT_NAME.equalsIgnoreCase(environmentName))
            {
                // Calculate and create the target archive directory
                final File archiveDirectory = (bookVersion.endsWith(".0"))
                    ? new File(archiveBaseDirectory, MAJOR_ARCHIVE_DIR)
                    : new File(archiveBaseDirectory, MINOR_ARCHIVE_DIR);
                if (!archiveDirectory.exists())
                {
                    archiveDirectory.mkdirs();
                }

                if (bookDefinition.isSplitBook())
                {
                    final File workDirectory =
                        new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.WORK_DIRECTORY));
                    if (!workDirectory.isDirectory())
                    {
                        throw new IOException("workDirectory must not be null and must be a directory.");
                    }
                    final List<String> splitTitles =
                        docMetadataService.findDistinctSplitTitlesByJobId(jobInstance.getId());
                    for (String splitTitleId : splitTitles)
                    {
                        splitTitleId = StringUtils.substringAfterLast(splitTitleId, "/");
                        final File sourceFilename =
                            new File(workDirectory, splitTitleId + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
                        if (!sourceFilename.exists())
                        {
                            throw new IOException("eBook must not be null and should exists.");
                        }
                        archiveBook(jobExecutionContext, archiveDirectory, sourceFilename.getAbsolutePath());
                    }
                }
                else
                {
                    final String sourceFilename =
                        getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_FILE);
                    archiveBook(jobExecutionContext, archiveDirectory, sourceFilename);
                }
            }
        }
        catch (final Exception e)
        {
            publishStatus = "Failed";
            log.error("Failed to archive ebook file", e);
            throw e;
        }
        finally
        {
            jobstats.setPublishStatus("archiveBook: " + publishStatus);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
        }
        return ExitStatus.COMPLETED;
    }

    public ProviewAudit createAudit(
        final String splitTitleId,
        final String bookVersion,
        final Date lastUpdate,
        final String command,
        final String userName)
    {
        final ProviewAudit audit = new ProviewAudit();
        audit.setAuditNote("Book Generated");
        audit.setBookLastUpdated(lastUpdate);
        audit.setBookVersion(bookVersion);
        audit.setProviewRequest(command);
        audit.setRequestDate(new Date());
        audit.setTitleId(splitTitleId);
        audit.setUsername(userName);
        return audit;
    }

    /**
     *
     * @param persistedsplitNodeList
     * @param currentsplitNodeList
     * @param currentVersion
     * @return
     */
    public boolean hasChanged(
        final List<SplitNodeInfo> persistedsplitNodeList,
        final List<SplitNodeInfo> currentsplitNodeList,
        final String currentVersion)
    {
        if (persistedsplitNodeList == null || persistedsplitNodeList.size() == 0)
        {
            return false;
        }
        else
        {
            final List<SplitNodeInfo> sameVersionSplitNodes = new ArrayList<>();
            for (final SplitNodeInfo splitNodeInfo : persistedsplitNodeList)
            {
                if (splitNodeInfo.getBookVersionSubmitted().equalsIgnoreCase(currentVersion))
                {
                    sameVersionSplitNodes.add(splitNodeInfo);
                }
            }
            if (sameVersionSplitNodes.size() == 0)
            {
                return false;
            }
            else if (sameVersionSplitNodes.size() >= 0 && sameVersionSplitNodes.size() != currentsplitNodeList.size())
            {
                return false;
            }
            else
            {
                for (final SplitNodeInfo splitNodeInfo : currentsplitNodeList)
                {
                    if (!sameVersionSplitNodes.contains(splitNodeInfo))
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * The file contents are in this format bookDefinitionId|TocGuid|splitTitleID
     * @param docToSplitBook
     * @param splitNodeInfoList
     */
    public void readDocImgFile(
        final File docToSplitBook,
        final List<SplitNodeInfo> splitNodeInfoList,
        final String bookVersion,
        final BookDefinition bookDefinition)
    {
        String line = null;
        try (BufferedReader stream = new BufferedReader(new FileReader(docToSplitBook)))
        {
            while ((line = stream.readLine()) != null)
            {
                final String[] splitted = line.split("\\|");

                final SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
                splitNodeInfo.setBookDefinition(bookDefinition);
                splitNodeInfo.setBookVersionSubmitted(bookVersion);
                String guid = splitted[0];
                if (guid.length() > 33)
                {
                    guid = StringUtils.substring(guid, 0, 33);
                }
                splitNodeInfo.setSplitNodeGuid(guid);
                splitNodeInfo.setSpitBookTitle(splitted[1]);
                splitNodeInfoList.add(splitNodeInfo);
            }
        }
        catch (final IOException iox)
        {
            throw new RuntimeException("Unable to find File : " + docToSplitBook.getAbsolutePath() + " " + iox);
        }
    }

    private void archiveBook(
        final ExecutionContext jobExecutionContext,
        final File archiveDirectory,
        final String sourceFilename) throws IOException
    {
        // Copy the ebook artifact file to the archive directory
        final File sourceFile = new File(sourceFilename);
        final String targetBasename = sourceFile.getName();
        final File targetFile = new File(archiveDirectory, targetBasename);
        copyFile(sourceFile, targetFile);
    }

    private void copyFile(final File source, final File target) throws IOException
    {
        log.debug(String.format("Archive copying %s to %s", source.getAbsolutePath(), target.getAbsolutePath()));
        ImageServiceImpl.copyFile(source, target);
    }

    @Required
    public void setEnvironmentName(final String environmentName)
    {
        this.environmentName = environmentName;
    }

    @Required
    public void setArchiveBaseDirectory(final File archiveBaseDirectory)
    {
        this.archiveBaseDirectory = archiveBaseDirectory;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }
}
