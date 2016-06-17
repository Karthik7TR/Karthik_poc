package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;

public interface PilotBookService {
	
	/**
	 * Query - findAuthorByPrimaryKey
	 * 
	 */
	public PilotBook findPilotBookByTitleId(String pilotBookTitleId)
			throws DataAccessException;

	/**
	 * Query - findAuthorsByEBookDefnId
	 * 
	 */
	public List<PilotBook> findPilotBooksByEBookDefnId(Long eBookDefnId)
			throws DataAccessException;


	public void deletePilotBook(PilotBook toRemove) throws DataAccessException;

	public void savePilotBook(PilotBook pilotBook);

}
