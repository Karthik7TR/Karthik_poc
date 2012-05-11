package com.thomsonreuters.uscl.ereader.mgr.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.RowMapper;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.service.AuthorService;
import com.thomsonreuters.uscl.ereader.mgr.library.domain.LibraryList;

public class LibraryListRowMapper implements RowMapper<LibraryList> {
	static private AuthorService authorService;

	public LibraryList mapRow(ResultSet resultSet, int rowNum)
			throws SQLException {
		Long bookDefinitionId = resultSet.getLong("EBOOK_DEFINITION_ID");
		String proviewName = resultSet.getString("PROVIEW_DISPLAY_NAME");
		String titleId = resultSet.getString("TITLE_ID");
		String isComplete = resultSet
				.getString("EBOOK_DEFINITION_COMPLETE_FLAG");
		String isDeleted = resultSet.getString("IS_DELETED_FLAG");
		Date lastUpdate = resultSet.getTimestamp("LAST_UPDATED");
		Date lastPublished = resultSet.getTimestamp("PUB_DATE");

		// Add the author information
		Set<Author> authors = new HashSet<Author>();
		authors.addAll(authorService.findAuthorsByEBookDefnId(bookDefinitionId));

		return new LibraryList(bookDefinitionId, proviewName, titleId,
				isComplete, isDeleted, lastUpdate, lastPublished, authors);
	}

	@Required
	static public void setAuthorService(AuthorService service) {
		authorService = service;
	}
}