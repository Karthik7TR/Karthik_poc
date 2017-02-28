package com.thomsonreuters.uscl.ereader.common.archive.step;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.archive.service.ArchiveAuditService;
import com.thomsonreuters.uscl.ereader.common.archive.service.ArchiveService;
import com.thomsonreuters.uscl.ereader.common.service.environment.EnvironmentUtil;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import org.springframework.batch.core.ExitStatus;

public abstract class BaseArchiveStep extends BookStepImpl
{
    @Resource(name = "environmentUtil")
    private EnvironmentUtil environmentUtil;
    @Resource(name = "archiveAuditService")
    private ArchiveAuditService archiveAuditService;
    @Resource(name = "archiveService")
    private ArchiveService archiveService;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        archiveAuditService.saveAudit(this);
        if (environmentUtil.isProd())
        {
            archiveService.archiveBook(this);
        }
        return ExitStatus.COMPLETED;
    }
}
