package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.PilotBookDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.PilotBook;

public class PilotBookServiceImpl implements PilotBookService{
	
	private PilotBookDao pilotBookDAO;
	
	@Override
	@Transactional(readOnly = true)
	public PilotBook findPilotBookByTitleId(String pilotBookTitleId)
			throws DataAccessException{
		return pilotBookDAO.findPilotBookByTitleId(pilotBookTitleId);
	}

	/**
	 * Query - findAuthorsByEBookDefnId
	 * 
	 */
	@Override
	@Transactional(readOnly = true)
	public List<PilotBook> findPilotBooksByEBookDefnId(Long eBookDefnId)
			throws DataAccessException{
		return pilotBookDAO.findPilotBooksByEBookDefnId(eBookDefnId);
	}


	public void deletePilotBook(PilotBook pilotBook) throws DataAccessException{
		pilotBookDAO.remove(pilotBook);
	}

	public void savePilotBook(PilotBook pilotBook){
		PilotBook existingPilotBook = pilotBookDAO.findPilotBookByTitleId(pilotBook.getPilotBookTitleId());

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
	
	@Required
	public void setPilotBookDAO(PilotBookDao pilotBookDAO) {
		this.pilotBookDAO = pilotBookDAO;
	}

}
