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
    private final AuthorDao authorDao;

    @Autowired
    public AuthorServiceImpl(final AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> findAuthorsByEBookDefnId(final Long eBookDefnId) {
        return authorDao.findAuthorsByEBookDefnId(eBookDefnId);
    }
}
