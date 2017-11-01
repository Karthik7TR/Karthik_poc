package com.thomsonreuters.uscl.ereader.mgr.web.service.author;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;

/**
 * Spring service that handles CRUD requests for Author entities
 *
 */
public interface AuthorService {
    /**
     * Save an existing Author entity
     *
     */
    void saveAuthor(Author author);

    /**
     * Delete an existing Author entity
     *
     */
    void deleteAuthor(Author author);

    /**
     */
    Author findAuthorById(Long authorId);

    /**
     */
    List<Author> findAuthorsByEBookDefnId(Long eBookDefnId);
}
