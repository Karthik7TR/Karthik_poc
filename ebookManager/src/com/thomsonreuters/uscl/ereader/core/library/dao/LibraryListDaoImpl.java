/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.service.AuthorService;
import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;

public class LibraryListDaoImpl implements LibraryListDao {
	// private static final Logger log =
	// Logger.getLogger(LibraryListDaoImpl.class);
	private static final LibraryListRowMapper LIBRARY_LIST_ROW_MAPPER = new LibraryListRowMapper();
	private JdbcTemplate jdbcTemplate;

	@Override
	@Transactional(readOnly = true)
	public List<LibraryList> findBookDefinitions(String sortProperty,
			boolean isAscending, int pageNumber, int itemsPerPage) {
		StringBuffer sql = new StringBuffer(
				"select * from ( select row_.*, ROWNUM rownum_ from ( ");
		sql.append("select book.EBOOK_DEFINITION_ID, book.PROVIEW_DISPLAY_NAME, book.TITLE_ID, ");
		sql.append("book.LAST_UPDATED, book.IS_DELETED_FLAG, book.EBOOK_DEFINITION_COMPLETE_FLAG, ps.pub_date from ");
		sql.append("EBOOK_DEFINITION book LEFT JOIN (SELECT p.EBOOK_DEFINITION_ID, MAX(p.PUBLISH_END_TIMESTAMP) pub_date ");
		sql.append("FROM PUBLISHING_STATS p GROUP BY p.EBOOK_DEFINITION_ID ) ps ON book.EBOOK_DEFINITION_ID = ps.EBOOK_DEFINITION_ID ");

		String direction = isAscending ? "asc" : "desc";
		String sortPropertySQL = sortProperty;

		if (sortProperty.equalsIgnoreCase("isDeletedFlag")) {
			String sortDelete = "book.IS_DELETED_FLAG";
			String sortComplete = "book.EBOOK_DEFINITION_COMPLETE_FLAG";
			sql.append(String.format("order by %s %s, %s %s", sortDelete,
					direction, sortComplete, direction));
		} else {

			if (sortProperty.equals("fullyQualifiedTitleId")) {
				sortPropertySQL = "book.TITLE_ID";
			} else if (sortProperty.equals("proviewDisplayName")) {
				sortPropertySQL = "book.PROVIEW_DISPLAY_NAME";
			} else if (sortProperty.equals("publishEndTimestamp")) {
				sortPropertySQL = "ps.PUB_DATE";
			} else if (sortProperty.equals("lastUpdated")) {
				sortPropertySQL = "book.LAST_UPDATED";
			}

			sql.append(String.format("order by %s %s", sortPropertySQL,
					direction));
		}

		int minIndex = (pageNumber - 1) * (itemsPerPage);
		int maxIndex = itemsPerPage + minIndex;
		sql.append(String.format(
				") row_ ) where rownum_ <= %d and rownum_ > %d ", maxIndex,
				minIndex));

		List<LibraryList> libraryList = jdbcTemplate.query(sql.toString(),
				LIBRARY_LIST_ROW_MAPPER);
		return libraryList;
	}

	@Override
	public Long countNumberOfBookDefinitions() {
		return (Long) jdbcTemplate
				.queryForLong("SELECT COUNT (book.EBOOK_DEFINITION_ID) FROM EBOOK_DEFINITION book");
	}

	@Required
	public void setJdbcTemplate(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
}

class LibraryListRowMapper implements RowMapper<LibraryList> {
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
				isComplete, isDeleted, lastUpdate, lastPublished, authors,
				null, null);
	}

	@Required
	static public void setAuthorService(AuthorService service) {
		authorService = service;
	}
}