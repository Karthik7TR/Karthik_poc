package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.VersionIsbn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionIsbnDao extends JpaRepository<VersionIsbn, Long> {

    List<VersionIsbn> getAllByEbookDefinition(BookDefinition bookDefinition);

    VersionIsbn findDistinctByEbookDefinitionAndVersion(BookDefinition bookDefinition, String version);

    @Query("SELECT v FROM VersionIsbn v JOIN FETCH v.ebookDefinition")
    List<VersionIsbn> findAllVersionIsbnBookDefinition();


}
