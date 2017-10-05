package com.thomsonreuters.uscl.ereader.common.service.splitnode;

import java.io.File;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.jetbrains.annotations.NotNull;

/**
 * Service to work with splitNodesInfo file
 */
public interface SplitNodesInfoService {
    @NotNull
    List<String> getTitleIds(@NotNull File splitNodeInfoFile, @NotNull String fullyQualifiedTitleId);

    @NotNull
    Set<SplitNodeInfo> getSubmittedSplitNodes(
        @NotNull File splitNodeInfoFile,
        @NotNull BookDefinition bookDefinition,
        @NotNull Version version);
}
