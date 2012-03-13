package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.AuthorDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;

/**
 * Spring service that handles CRUD requests for Author entities
 * 
 */

@Transactional
public class AuthorServiceImpl implements AuthorService {

	/**
	 * DAO injected by Spring that manages Author entities
	 * 
	 */

	private AuthorDao authorDAO;


	/**
	 * Save an existing Author entity
	 * 
	 */
	@Transactional
	public void saveAuthor(Author author) {

		Author existingAuthor = authorDAO.findAuthorById(author.getAuthorId());

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
	@Transactional
	public void deleteAuthor(Author author) {
		authorDAO.remove(author);
	}

	@Override
	public Author findAuthorById(Long authorId) {
		return authorDAO.findAuthorById(authorId);
	}

	@Override
	public List<Author> findAuthorsByEBookDefnId(Long eBookDefnId) {
		return authorDAO.findAuthorsByEBookDefnId(eBookDefnId);
	}

	@Required
	public void setAuthorDAO(AuthorDao authorDAO) {
		this.authorDAO = authorDAO;
	}
}
