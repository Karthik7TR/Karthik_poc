package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;

import java.util.List;

public interface CombinedBookDefinitionService {
    List<CombinedBookDefinition> findAllCombinedBookDefinitions();

    CombinedBookDefinition findCombinedBookDefinitionById(final Long id);

    CombinedBookDefinition saveCombinedBookDefinition(final CombinedBookDefinition combinedBookDefinition);

    void updateDeletedStatus(final Long id, final boolean isDeleted);

    void deleteCombinedBookDefinition(final CombinedBookDefinition combinedBookDefinition);
}
