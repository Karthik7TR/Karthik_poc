package com.thomsonreuters.uscl.ereader.mgr.web.service.author;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;

/**
 * Spring service that handles CRUD requests for Author entities
 *
 */
public interface AuthorService {
    List<Author> findAuthorsByEBookDefnId(Long eBookDefnId);
}
