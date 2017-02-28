package com.thomsonreuters.uscl.ereader.common.archive.service;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import org.jetbrains.annotations.NotNull;

public interface ArchiveService
{
    /**
     * Archives generated book
     */
    void archiveBook(@NotNull BaseArchiveStep step);
}
