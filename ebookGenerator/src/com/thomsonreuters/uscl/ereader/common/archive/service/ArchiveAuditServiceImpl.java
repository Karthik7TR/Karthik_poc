package com.thomsonreuters.uscl.ereader.common.archive.service;

import java.util.Date;
import java.util.Set;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import com.thomsonreuters.uscl.ereader.common.service.splitnode.SplitNodesInfoService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.util.BookTitlesUtil;
import com.thomsonreuters.uscl.ereader.proviewaudit.domain.ProviewAudit;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import org.jetbrains.annotations.NotNull;

public class ArchiveAuditServiceImpl implements ArchiveAuditService
{
    @Resource(name = "bookDefinitionService")
    private BookDefinitionService bookService;
    @Resource(name = "proviewAuditService")
    private ProviewAuditService proviewAuditService;
    @Resource(name = "bookTitlesUtil")
    private BookTitlesUtil bookTitlesUtil;
    @Resource(name = "splitNodesInfoService")
    private SplitNodesInfoService splitNodesInfoService;

    @Override
    public void saveAudit(@NotNull final BaseArchiveStep step)
    {
        final BookDefinition bookDefinition = step.getBookDefinition();
        final ProviewAudit audit = createAudit(step, bookDefinition.getFullyQualifiedTitleId());
        proviewAuditService.save(audit);

        if (bookDefinition.isSplitBook())
        {
            saveSplitTitlesAudit(step, bookDefinition);
        }
    }

    private void saveSplitTitlesAudit(final BaseArchiveStep step, final BookDefinition bookDefinition)
    {
        final Set<SplitNodeInfo> submittedSplitNodes = splitNodesInfoService
            .getSubmittedSplitNodes(step.getSplitBookInfoFile(), bookDefinition, step.getBookVersion());
        saveSubmittedSplitInfo(step, submittedSplitNodes);

        final Set<SplitNodeInfo> persistedSplitNodes =
            bookTitlesUtil.getSplitNodeInfosByVersion(bookDefinition, step.getBookVersion());
        final boolean hasPersisted = !persistedSplitNodes.isEmpty();
        final boolean splitNodesInfoChanged = !persistedSplitNodes.equals(submittedSplitNodes);
        if (hasPersisted && splitNodesInfoChanged)
        {
            bookService.updateSplitNodeInfoSet(
                bookDefinition.getEbookDefinitionId(),
                submittedSplitNodes,
                step.getBookVersionString());
        }
    }

    private void saveSubmittedSplitInfo(final BaseArchiveStep step, final Set<SplitNodeInfo> submittedSplitNodes)
    {
        for (final SplitNodeInfo splitNodeInfo : submittedSplitNodes)
        {
            final ProviewAudit audit = createAudit(step, splitNodeInfo.getSplitBookTitle());
            proviewAuditService.save(audit);
        }
    }

    private ProviewAudit createAudit(final BaseArchiveStep step, final String titleId)
    {
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
