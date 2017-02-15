package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;
import org.springframework.dao.DataAccessException;

public interface PilotBookService
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

    void deletePilotBook(PilotBook toRemove) throws DataAccessException;

    void savePilotBook(PilotBook pilotBook);
}
