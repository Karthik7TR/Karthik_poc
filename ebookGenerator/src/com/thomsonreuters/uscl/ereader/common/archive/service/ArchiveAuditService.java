package com.thomsonreuters.uscl.ereader.common.archive.service;

import com.thomsonreuters.uscl.ereader.common.archive.step.BaseArchiveStep;
import org.jetbrains.annotations.NotNull;

public interface ArchiveAuditService {
    /**
     * Saves audit for book generation
     */
    void saveAudit(@NotNull BaseArchiveStep step);
}
