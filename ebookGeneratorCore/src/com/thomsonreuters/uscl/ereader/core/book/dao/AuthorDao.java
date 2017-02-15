package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import org.springframework.dao.DataAccessException;

/**
 * DAO to manage Author entities.
 *
 */
public interface AuthorDao
{
    /**
     * Query - findAuthorByPrimaryKey
     *
     */
    Author findAuthorById(Long authorId) throws DataAccessException;

    /**
     * Query - findAuthorsByEBookDefnId
     *
     */
    List<Author> findAuthorsByEBookDefnId(Long eBookDefnId) throws DataAccessException;

    void remove(Author toRemove) throws DataAccessException;

    void saveAuthor(Author author);
}
