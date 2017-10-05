package com.thomsonreuters.uscl.ereader.common.group.step;

import java.io.File;
import java.util.List;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.group.service.GroupServiceWithRetry;
import com.thomsonreuters.uscl.ereader.common.service.splitnode.SplitNodesInfoService;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.batch.core.ExitStatus;

public abstract class BaseGroupStep extends BookStepImpl implements GroupStep {
    @Resource(name = "groupService")
    private GroupService groupService;
    @Resource(name = "groupServiceWithRetry")
    private GroupServiceWithRetry groupServiceWithRetry;
    @Resource(name = "splitNodesInfoService")
    private SplitNodesInfoService splitNodesInfoService;
    @Resource(name = "publishingStatsService")
    private PublishingStatsService publishingStatsService;
    @Resource(name = "formatFileSystem")
    private FormatFileSystem fileSystem;

    private Long groupVersion;

    @Override
    public ExitStatus executeStep() throws Exception {
        final BookDefinition bookDefinition = getBookDefinition();
        final String groupName = bookDefinition.getGroupName();
        if (!StringUtils.isEmpty(groupName)) {
            createGroup(bookDefinition);
        } else if (publishingStatsService.hasBeenGrouped(bookDefinition.getEbookDefinitionId())) {
            groupService.removeAllPreviousGroups(bookDefinition);
        }
        return ExitStatus.COMPLETED;
    }

    private void createGroup(final BookDefinition bookDefinition) throws Exception {
        final List<String> splitTitles = getSplitTitles();
        final GroupDefinition groupDefinition =
            groupService.createGroupDefinition(bookDefinition, getBookVersion().getFullVersion(), splitTitles);
        final GroupDefinition previousGroupDefinition = groupService.getLastGroup(bookDefinition);
        if (!groupDefinition.isSimilarGroup(previousGroupDefinition)) {
            groupServiceWithRetry.createGroup(groupDefinition);
            groupVersion = groupDefinition.getGroupVersion();
        }
    }

    @Nullable
    private List<String> getSplitTitles() {
        final boolean isSplitBook = getBookDefinition().isSplitBook();
        final File splitBookInfoFile = fileSystem.getSplitBookInfoFile(this);
        final String fullyQualifiedTitleId = getBookDefinition().getFullyQualifiedTitleId();
        return isSplitBook ? splitNodesInfoService.getTitleIds(splitBookInfoFile, fullyQualifiedTitleId) : null;
    }

    @Override
    public Long getGroupVersion() {
        return groupVersion;
    }
}
