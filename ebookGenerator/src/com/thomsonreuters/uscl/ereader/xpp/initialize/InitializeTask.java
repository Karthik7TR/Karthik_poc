/*
 * Copyright 2017: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.xpp.initialize;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;

/**
 * Perform job setup for creating an ebook and place data into the JobExecutionContext for use in
 * later steps. This includes various file system path calculations based on the JobParameters used
 * to run the job.
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.INITIALIZE)
public class InitializeTask extends BookStepImpl {
    private static final Logger LOG = LogManager.getLogger(InitializeTask.class);

    @Resource(name = "bookDefinitionService")
    private BookDefinitionService bookService;
    @Resource(name = "bookFileSystem")
    private BookFileSystem fileSystem;

    @Override
    public ExitStatus executeStep() throws Exception {
        setBookDefinition();
        createWorkDir();

        LOG.debug("Proview Domain URL: " + System.getProperty("proview.domain"));
        LOG.debug("hostname: " + getHostName());
        return ExitStatus.COMPLETED;
    }

    private void setBookDefinition() {
        final BookDefinition bookDefinition = bookService.findBookDefinitionByEbookDefId(getBookDefinitionId());
        setJobExecutionProperty(JobExecutionKey.EBOOK_DEFINITION, bookDefinition);
        //TODO: remove this later - for dummy book only. Ignore Split books
        bookDefinition.setIsSplitBook(false);

        LOG.debug("titleId (Fully Qualified): " + bookDefinition.getFullyQualifiedTitleId());
    }

    /**
     * Create the work directory for the ebook and create the physical directory in the filesystem
     * "yyyyMMdd/titleId/jobInstanceId". Sample: "/apps/eBookBuilder/prod/data/20120131/FRCP/356"
     */
    private void createWorkDir() throws IOException {
        final File workDirectory = fileSystem.getWorkDirectory(this);
        FileUtils.forceMkdir(workDirectory);
        setJobExecutionPropertyString(JobExecutionKey.WORK_DIRECTORY, workDirectory.getAbsolutePath());
        LOG.debug("workDirectory: " + workDirectory.getAbsolutePath());
    }
}
