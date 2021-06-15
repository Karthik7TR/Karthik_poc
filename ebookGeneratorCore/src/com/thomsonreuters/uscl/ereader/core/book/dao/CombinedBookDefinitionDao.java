package com.thomsonreuters.uscl.ereader.core.book.dao;

import com.thomsonreuters.uscl.ereader.core.book.domain.CombinedBookDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CombinedBookDefinitionDao  extends JpaRepository<CombinedBookDefinition, Long> {
}
