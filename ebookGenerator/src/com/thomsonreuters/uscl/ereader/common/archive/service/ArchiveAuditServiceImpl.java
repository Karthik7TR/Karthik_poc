package com.thomsonreuters.uscl.ereader.common.archive.service;

import java.util.Date;
import java.util.Set;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.service.splitnode.SplitNodesInfoService;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.common.step.SplitBookTitlesAwareStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtil;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import org.jetbrains.annotations.NotNull;

public class ArchiveAuditServiceImpl implements ArchiveAuditService {
    @Resource(name = "bookDefinitionService")
    private BookDefinitionService bookService;
    @Resource(name = "proviewAuditService")
    private ProviewAuditService proviewAuditService;
    @Resource(name = "bookTitlesUtil")
    private BookTitlesUtil bookTitlesUtil;
    @Resource(name = "splitNodesInfoService")
    private SplitNodesInfoService splitNodesInfoService;
    @Resource(name = "formatFileSystem")
    private FormatFileSystem fileSystem;

    @Override
    public void saveAudit(@NotNull final SplitBookTitlesAwareStep step) {
        final BookDefinition bookDefinition = step.getBookDefinition();

        if (bookDefinition.isSplitBook()) {
            saveSplitTitlesAudit(step, bookDefinition);
        } else {
            final ProviewAudit audit = createAudit(step, bookDefinition.getFullyQualifiedTitleId());
            proviewAuditService.save(audit);
        }
    }

    private void saveSplitTitlesAudit(final SplitBookTitlesAwareStep step, final BookDefinition bookDefinition) {
        saveSubmittedSplitInfo(step);

        if (SourceType.XPP != bookDefinition.getSourceType()) {
            final Set<SplitNodeInfo> submittedSplitNodes = splitNodesInfoService
                .getSubmittedSplitNodes(fileSystem.getSplitBookInfoFile(step), bookDefinition, step.getBookVersion());
            final Set<SplitNodeInfo> persistedSplitNodes =
                bookTitlesUtil.getSplitNodeInfosByVersion(bookDefinition, step.getBookVersion());

            if (!persistedSplitNodes.equals(submittedSplitNodes)) {
                bookService.updateSplitNodeInfoSet(
                    bookDefinition.getEbookDefinitionId(),
                    submittedSplitNodes,
                    step.getBookVersionString());
            }
        }
    }

    private void saveSubmittedSplitInfo(final SplitBookTitlesAwareStep step) {
        step.getSplitTitles().stream()
            .map(titleId -> createAudit(step, titleId))
            .forEach(proviewAuditService::save);
    }

    private ProviewAudit createAudit(final BookStep step, final String titleId) {
        final ProviewAudit audit = new ProviewAudit();
        audit.setAuditNote("Book Generated");
        audit.setTitleId(titleId);
        audit.setBookLastUpdated(step.getBookDefinition().getLastUpdated());
        audit.setBookVersion(step.getBookVersion().getFullVersion());
        audit.setProviewRequest("REVIEW");
        audit.setRequestDate(new Date());
        audit.setUsername(step.getUserName());
        return audit;
    }
}
