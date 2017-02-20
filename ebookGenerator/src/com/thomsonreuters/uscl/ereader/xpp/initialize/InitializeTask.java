/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.xpp.initialize;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.IllegalStateException;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.xpp.common.XppBookStep;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Required;

/**
 * Perform job setup for creating an ebook and place data into the JobExecutionContext for use in
 * later steps. This includes various file system path calculations based on the JobParameters used
 * to run the job.
 */
public class InitializeTask extends BookStepImpl implements XppBookStep
{
    private static final Logger LOG = LogManager.getLogger(InitializeTask.class);

    private File rootWorkDirectory; // "/nas/ebookbuilder/data"
    private String environmentName;
    private BookDefinitionService bookDefnService;

    @Override
    public boolean isInitialStep()
    {
        return true;
    }

    @Override
    public ExitStatus executeStep() throws Exception
    {
        final BookDefinition bookDefinition = setBookDefinition();
        createWorkDir(bookDefinition);
        LOG.debug("Proview Domain URL: " + System.getProperty("proview.domain"));
        return ExitStatus.COMPLETED;
    }

    private BookDefinition setBookDefinition()
    {
        final BookDefinition bookDefinition = bookDefnService.findBookDefinitionByEbookDefId(getBookDefinitionId());
        setJobExecutionProperty(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);
        LOG.debug("titleId (Fully Qualified): " + bookDefinition.getTitleId());
        LOG.debug("hostname: " + getJobParameterString(JobParameterKey.HOST_NAME));
        return bookDefinition;
    }

    /**
     * Create the work directory for the ebook and create the physical directory in the filesystem
     * "yyyyMMdd/titleId/jobInstanceId". Sample: "/apps/eBookBuilder/prod/data/20120131/FRCP/356"
     *
     * @param jobExecutionContext
     * @param jobInstance
     * @param bookDefinition
     */
    private void createWorkDir(final BookDefinition bookDefinition) throws IllegalStateException
    {
        final String dateStr = new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(new Date());
        final String dynamicPath = String.format(
            "%s/%s/%s/%s/%d",
            environmentName,
            CoreConstants.DATA_DIR,
            dateStr,
            bookDefinition.getTitleId(),
            getJobInstanceId());
        final File workDirectory = new File(rootWorkDirectory, dynamicPath);
        workDirectory.mkdirs();
        if (!workDirectory.exists())
        {
            throw new IllegalStateException(
                "Expected work directory was not created in the filesystem: " + workDirectory.getAbsolutePath());
        }
        setJobExecutionPropertyString(JobExecutionKey.WORK_DIRECTORY, workDirectory.getAbsolutePath());
        LOG.debug("workDirectory: " + workDirectory.getAbsolutePath());
    }

    @Required
    public void setRootWorkDirectory(final File rootDir)
    {
        rootWorkDirectory = rootDir;
    }

    @Required
    public void setEnvironmentName(final String envName)
    {
        environmentName = envName;
    }

    @Required
    public void setBookDefnService(final BookDefinitionService bookDefnService)
    {
        this.bookDefnService = bookDefnService;
    }
}
