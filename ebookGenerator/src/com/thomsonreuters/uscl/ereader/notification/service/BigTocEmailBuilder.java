package com.thomsonreuters.uscl.ereader.notification.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsService;

public class BigTocEmailBuilder extends AbstractEmailBuilder
{
    private static final String SUBJECT_PART = " THRESHOLD WARNING";

    @Resource(name = "autoSplitGuidsService")
    private AutoSplitGuidsService autoSplitGuidsService;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.notification.AbstractGeneratorEmailBuilder#getAdditionalSubjectPart()
     */
    @Override
    protected String getAdditionalSubjectPart()
    {
        return SUBJECT_PART;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.notification.AbstractGeneratorEmailBuilder#getAdditionalBodyPart()
     */
    @Override
    protected String getAdditionalBodyPart()
    {
        final int totalSplitParts = getTotalSplitParts();
        final Map<String, String> splitGuidTextMap = autoSplitGuidsService.getSplitGuidTextMap();

        final StringBuilder sb = new StringBuilder();
        sb.append("\t\n\t\n**WARNING**: The book exceeds threshold value " + step.getThresholdValue());
        sb.append("\t\nTotal node count is " + step.getTocNodeCount());
        sb.append("\t\nPlease find the below system suggested information");
        sb.append("\t\nTotal split parts : " + totalSplitParts);
        sb.append("\t\nTOC/NORT guids :");
        for (final Map.Entry<String, String> entry : splitGuidTextMap.entrySet())
        {
            final String uuid = entry.getKey();
            final String name = entry.getValue();
            sb.append("\t\n" + uuid + "  :  " + name);
        }
        return sb.toString();
    }

    int getTotalSplitParts()
    {
        final BookDefinition bookDefinition = step.getBookDefinition();
        final String tocXmlFile = step.getJobExecutionPropertyString(JobExecutionKey.GATHER_TOC_FILE);
        final Integer tocNodeCount = step.getTocNodeCount();
        final Long jobInstanceId = step.getJobInstanceId();

        try (InputStream tocInputSteam = new FileInputStream(tocXmlFile))
        {
            return autoSplitGuidsService
                .getAutoSplitNodes(tocInputSteam, bookDefinition, tocNodeCount, jobInstanceId, true).size() + 1;
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Cannot read file " + tocXmlFile, e);
        }
    }
}
