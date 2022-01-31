package com.thomsonreuters.uscl.ereader.core.book.dao;

import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinitionSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CombinedBookDefinitionSourceDao extends JpaRepository<CombinedBookDefinitionSource, Long> {
    Optional<CombinedBookDefinitionSource> findCombinedBookDefinitionSourceByIsPrimarySourceAndBookDefinition_EbookDefinitionId(String isPrimarySource, long ebookDefinitionId);
}
