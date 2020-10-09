package com.thomsonreuters.uscl.ereader.common.archive.step;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.archive.service.ArchiveAuditService;
import com.thomsonreuters.uscl.ereader.common.archive.service.ArchiveService;
import com.thomsonreuters.uscl.ereader.common.service.environment.EnvironmentUtil;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.common.step.SplitBookTitlesAwareStep;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseArchiveStep extends BookStepImpl implements SplitBookTitlesAwareStep {
    @Resource(name = "environmentUtil")
    private EnvironmentUtil environmentUtil;
    @Resource(name = "archiveAuditService")
    private ArchiveAuditService archiveAuditService;
    @Resource(name = "archiveService")
    private ArchiveService archiveService;
    @Autowired
    private BookDefinitionService bookDefinitionService;

    @Override
    public ExitStatus executeStep() throws Exception {
        archiveAuditService.saveAudit(this);
        if (environmentUtil.isProd()) {
            archiveService.archiveBook(this);
        }
        bookDefinitionService.cleanUpPreviousVersionValue(getBookDefinition());
        return ExitStatus.COMPLETED;
    }
}
