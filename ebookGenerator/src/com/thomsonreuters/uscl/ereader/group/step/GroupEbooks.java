package com.thomsonreuters.uscl.ereader.group.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.format.FormatConstants;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

public class GroupEbooks extends AbstractSbTasklet
{
    private static final Logger LOG = LogManager.getLogger(GroupEbooks.class);

    private PublishingStatsService publishingStatsService;
    private GroupService groupService;

    private int maxNumberOfRetries = 3;
    private int sleepTimeInMinutes = 15;
    private int baseSleepTimeInMinutes = 2;

    public void setSleepTimeInMinutes(final int sleepTimeInMinutes)
    {
        this.sleepTimeInMinutes = sleepTimeInMinutes;
    }

    public void setBaseSleepTimeInMinutes(final int baseSleepTimeInMinutes)
    {
        this.baseSleepTimeInMinutes = baseSleepTimeInMinutes;
    }

    public int getMaxNumberOfRetries()
    {
        return maxNumberOfRetries;
    }

    public GroupService getGroupService()
    {
        return groupService;
    }

    @Required
    public void setGroupService(final GroupService groupService)
    {
        this.groupService = groupService;
    }

    @Override
    public ExitStatus executeStep(final StepContribution contribution, final ChunkContext chunkContext) throws Exception
    {
        final ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
        final JobParameters jobParameters = getJobParameters(chunkContext);

        final Long jobInstance =
            chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getId();

        final BookDefinition bookDefinition =
            (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITION);

        final String versionNumber =
            FormatConstants.VERSION_NUMBER_PREFIX + jobParameters.getString(JobParameterKey.BOOK_VERSION_SUBMITTED);
        final String fullyQualifiedTitleId = bookDefinition.getFullyQualifiedTitleId();
        final long startTime = System.currentTimeMillis();
        LOG.debug("Publishing eBook [" + fullyQualifiedTitleId + "] to Proview.");
        String publishStatus = "Completed";

        Long groupVersion = Long.valueOf(0);

        try
        {
            if (!StringUtils.isEmpty(bookDefinition.getGroupName()))
            {
                List<String> splitTitles = null;
                if (bookDefinition.isSplitBook())
                {
                    final String splitNodeInfoFile =
                        getRequiredStringProperty(jobExecutionContext, JobExecutionKey.SPLIT_NODE_INFO_FILE);
                    splitTitles = readSplitNodeInforFile(splitNodeInfoFile, fullyQualifiedTitleId);
                }
                final GroupDefinition groupDefinition =
                    groupService.createGroupDefinition(bookDefinition, versionNumber, splitTitles);
                final GroupDefinition previousGroupDefinition = groupService.getLastGroup(bookDefinition);
                if (!groupDefinition.isSimilarGroup(previousGroupDefinition))
                {
                    createGroupWithRetry(groupDefinition);
                    groupVersion = groupDefinition.getGroupVersion();
                }
            }
            else if (publishingStatsService.hasBeenGrouped(bookDefinition.getEbookDefinitionId()))
            {
                groupService.removeAllPreviousGroups(bookDefinition);
            }
        }
        catch (final Exception e)
        {
            groupVersion = null;
            publishStatus = "Failed";
            throw (e);
        }
        finally
        {
            final PublishingStats jobstats = new PublishingStats();
            jobstats.setJobInstanceId(jobInstance);
            jobstats.setPublishStatus("GroupEBook : " + publishStatus);
            jobstats.setGroupVersion(groupVersion);
            publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GROUPEBOOK);
        }

        final long processingTime = System.currentTimeMillis() - startTime;
        LOG.debug("Publishing complete. Time elapsed: " + processingTime + "ms");

        return ExitStatus.COMPLETED;
    }

    protected void createGroupWithRetry(final GroupDefinition groupDefinition)
    {
        boolean retryRequest = true;

        // Most of the books should finish in two minutes
        try
        {
            TimeUnit.MINUTES.sleep(baseSleepTimeInMinutes);
        }
        catch (final InterruptedException e)
        {
            LOG.error("InterruptedException during HTTP retry", e);
        }

        int retryCount = 0;
        String errorMsg = "";
        do
        {
            try
            {
                groupService.createGroup(groupDefinition);
                retryRequest = false;
            }
            catch (final ProviewException ex)
            {
                errorMsg = ex.getMessage();
                if (errorMsg.equalsIgnoreCase(CoreConstants.NO_TITLE_IN_PROVIEW))
                {
                    // retry a retriable request

                    LOG.warn(
                        "Retriable status received: waiting "
                            + sleepTimeInMinutes
                            + "minutes (retryCount: "
                            + retryCount
                            + ")");

                    retryRequest = true;
                    retryCount++;

                    try
                    {
                        TimeUnit.MINUTES.sleep(sleepTimeInMinutes);
                    }
                    catch (final InterruptedException e)
                    {
                        LOG.error("InterruptedException during HTTP retry", e);
                    }
                }
                else if (errorMsg.equalsIgnoreCase(CoreConstants.GROUP_AND_VERSION_EXISTS))
                {
                    retryRequest = true;
                    retryCount++;
                    final Long groupVersion = groupDefinition.getGroupVersion() + 1;
                    LOG.warn("Incrementing group version " + groupVersion);
                    groupDefinition.setGroupVersion(groupVersion);
                }
                else
                {
                    throw new ProviewRuntimeException(errorMsg);
                }
            }
        }
        while (retryRequest && retryCount < maxNumberOfRetries);
        if (retryRequest && retryCount >= maxNumberOfRetries)
        {
            throw new ProviewRuntimeException(
                "Tried "
                    + maxNumberOfRetries
                    + " times to create group without success. Proview might be down or still in"
                    + " the process of loading parts of the book. Please try again later. ");
        }
    }

    /*
     * Reads the file at Format\splitEbook\splitNodeInfo.txt and gets the split titles
     */
    protected List<String> readSplitNodeInforFile(
        final String splitNodeInfoFilePath,
        final String fullyQualifiedTitleId)
    {
        final File splitNodeInfoFile = new File(splitNodeInfoFilePath);
        final List<String> splitTitles = new ArrayList<>();
        splitTitles.add(fullyQualifiedTitleId);
        String line = null;
        BufferedReader stream = null;
        try
        {
            stream = new BufferedReader(new FileReader(splitNodeInfoFile));
            while ((line = stream.readLine()) != null)
            {
                final String[] splitted = line.split("\\|");
                splitTitles.add(splitted[1]);
            }
        }
        catch (final IOException iox)
        {
            throw new RuntimeException("Unable to find File : " + splitNodeInfoFile.getAbsolutePath() + " " + iox);
        }
        finally
        {
            if (stream != null)
            {
                try
                {
                    stream.close();
                }
                catch (final IOException e)
                {
                    throw new RuntimeException("An IOException occurred while closing a file ", e);
                }
            }
        }
        return splitTitles;
    }

    public Long getGroupVersionByBookDefinition(final Long bookDefinitionId)
    {
        return publishingStatsService.getMaxGroupVersionById(bookDefinitionId);
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService)
    {
        this.publishingStatsService = publishingStatsService;
    }
}
