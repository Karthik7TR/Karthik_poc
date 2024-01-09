package com.thomsonreuters.uscl.ereader.common.archive.service;

import com.thomsonreuters.uscl.ereader.common.step.SplitBookTitlesAwareStep;
import org.jetbrains.annotations.NotNull;

public interface ArchiveService {
    /**
     * Archives generated book
     */
    void archiveBook(@NotNull SplitBookTitlesAwareStep step);
}
