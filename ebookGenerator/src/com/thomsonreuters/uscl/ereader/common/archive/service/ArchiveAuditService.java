package com.thomsonreuters.uscl.ereader.common.archive.service;

import com.thomsonreuters.uscl.ereader.common.step.SplitBookTitlesAwareStep;
import org.jetbrains.annotations.NotNull;

public interface ArchiveAuditService {
    /**
     * Saves audit for book generation
     */
    void saveAudit(@NotNull SplitBookTitlesAwareStep step);
}
