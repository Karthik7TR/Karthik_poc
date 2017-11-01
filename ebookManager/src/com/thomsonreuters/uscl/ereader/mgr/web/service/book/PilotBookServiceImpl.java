package com.thomsonreuters.uscl.ereader.mgr.web.service.book;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.PilotBookDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("pilotBookService")
public class PilotBookServiceImpl implements PilotBookService {
    private final PilotBookDao pilotBookDAO;

    @Autowired
    public PilotBookServiceImpl(final PilotBookDao pilotBookDAO) {
        this.pilotBookDAO = pilotBookDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public PilotBook findPilotBookByTitleId(final String pilotBookTitleId) throws DataAccessException {
        return pilotBookDAO.findPilotBookByTitleId(pilotBookTitleId);
    }

    /**
     * Query - findAuthorsByEBookDefnId
     *
     */
    @Override
    @Transactional(readOnly = true)
    public List<PilotBook> findPilotBooksByEBookDefnId(final Long eBookDefnId) throws DataAccessException {
        return pilotBookDAO.findPilotBooksByEBookDefnId(eBookDefnId);
    }

    @Override
    public void deletePilotBook(final PilotBook pilotBook) throws DataAccessException {
        pilotBookDAO.remove(pilotBook);
    }

    @Override
    public void savePilotBook(final PilotBook pilotBook) {
        final PilotBook existingPilotBook = pilotBookDAO.findPilotBookByTitleId(pilotBook.getPilotBookTitleId());

        if (existingPilotBook != null) {
            if (existingPilotBook != pilotBook) {
                existingPilotBook.setPilotBookTitleId(pilotBook.getPilotBookTitleId());
                existingPilotBook.setNote(pilotBook.getNote());
                existingPilotBook.setEbookDefinition(pilotBook.getEbookDefinition());
            }
            pilotBookDAO.savePilotBook(existingPilotBook);
        } else {
            pilotBookDAO.savePilotBook(pilotBook);
        }
    }
}
