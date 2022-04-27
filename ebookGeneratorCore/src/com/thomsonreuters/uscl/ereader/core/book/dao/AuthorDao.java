package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * DAO to manage Author entities.
 *
 */
//commenting to complete user story
    //KT purpose
public interface AuthorDao extends JpaRepository<Author, Long> {
    @Query("from Author a where a.ebookDefinition.ebookDefinitionId = :eBookDefnId")
    List<Author> findAuthorsByEBookDefnId(@Param("eBookDefnId") Long eBookDefnId);
}
