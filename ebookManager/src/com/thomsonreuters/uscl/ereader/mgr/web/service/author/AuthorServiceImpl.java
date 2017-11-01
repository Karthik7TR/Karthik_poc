package com.thomsonreuters.uscl.ereader.mgr.web.service.author;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.AuthorDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring service that handles CRUD requests for Author entities
 *
 */
@Service("authorService")
public class AuthorServiceImpl implements AuthorService {
    private final AuthorDao authorDAO;

    @Autowired
    public AuthorServiceImpl(final AuthorDao authorDAO) {
        this.authorDAO = authorDAO;
    }

    /**
     * Save an existing Author entity
     *
     */
    @Override
    @Transactional
    public void saveAuthor(final Author author) {
        final Author existingAuthor = authorDAO.findAuthorById(author.getAuthorId());

        if (existingAuthor != null) {
            if (existingAuthor != author) {
                existingAuthor.setAuthorId(author.getAuthorId());
                existingAuthor.setAuthorFirstName(author.getAuthorFirstName());
                existingAuthor.setAuthorAddlText(author.getAuthorAddlText());
                existingAuthor.setAuthorLastName(author.getAuthorLastName());
                existingAuthor.setAuthorMiddleName(author.getAuthorMiddleName());
                existingAuthor.setAuthorNamePrefix(author.getAuthorNamePrefix());
                existingAuthor.setAuthorNameSuffix(author.getAuthorNameSuffix());
                existingAuthor.setEbookDefinition(author.getEbookDefinition());
            }
            authorDAO.saveAuthor(existingAuthor);
        } else {
            authorDAO.saveAuthor(author);
        }
    }

    /**
     * Delete an existing Author entity
     *
     */
    @Override
    @Transactional
    public void deleteAuthor(final Author author) {
        authorDAO.remove(author);
    }

    @Override
    @Transactional(readOnly = true)
    public Author findAuthorById(final Long authorId) {
        return authorDAO.findAuthorById(authorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> findAuthorsByEBookDefnId(final Long eBookDefnId) {
        return authorDAO.findAuthorsByEBookDefnId(eBookDefnId);
    }
}
