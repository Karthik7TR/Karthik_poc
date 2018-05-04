package com.thomsonreuters.uscl.ereader.request.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponentHistory;

/**
 * Service methods for print components history.
 */
public interface PrintComponentHistoryService {
    List<String> findPrintComponentVersionsList(Long bookDefinitionId);

    Set<PrintComponentHistory> findPrintComponentByVersion(Long bookDefinitionId, String combinedVersion);

    Optional<Integer> getLatestPrintComponentHistoryVersion(Long bookDefinitionId, String bookDefinitionVersion);

    void savePrintComponents(
        Collection<PrintComponent> printComponents,
        Long bookDefinitionId,
        String bookDefinitionVersion,
        int newPrintComponentHistoryVersion);
}
