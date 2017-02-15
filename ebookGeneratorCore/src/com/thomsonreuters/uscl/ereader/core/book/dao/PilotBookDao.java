package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import org.springframework.dao.DataAccessException;

public interface PilotBookDao
{
    /**
     * Query - findAuthorByPrimaryKey
     *
     */
    PilotBook findPilotBookByTitleId(String pilotBookTitleId) throws DataAccessException;

    /**
     * Query - findAuthorsByEBookDefnId
     *
     */
    List<PilotBook> findPilotBooksByEBookDefnId(Long eBookDefnId) throws DataAccessException;

    void remove(PilotBook toRemove) throws DataAccessException;

    void savePilotBook(PilotBook pilotBook);
}
