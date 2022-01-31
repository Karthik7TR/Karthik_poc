package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;

import java.util.Optional;

public interface CombinedBookDefinitionSourceService {
    Optional<CombinedBookDefinitionSource> findPrimarySourceWithBookDefinition(long ebookDefinitionId);
}
