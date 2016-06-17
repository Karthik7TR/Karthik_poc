package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;


public interface PilotBookDao {
	
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


	public void remove(PilotBook toRemove) throws DataAccessException;

	public void savePilotBook(PilotBook pilotBook);

}
