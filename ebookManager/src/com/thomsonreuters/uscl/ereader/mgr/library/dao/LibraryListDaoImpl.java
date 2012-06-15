/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.library.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListFilter;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryListSort.SortProperty;

public class LibraryListDaoImpl implements LibraryListDao {
	// private static final Logger log = Logger.getLogger(LibraryListDaoImpl.class);
	private static final LibraryListRowMapper LIBRARY_LIST_ROW_MAPPER = new LibraryListRowMapper();
	private JdbcTemplate jdbcTemplate;

	@Override
	@Transactional(readOnly = true)
	public List<LibraryList> findBookDefinitions(LibraryListFilter filter, LibraryListSort sort) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ( select row_.*, ROWNUM rownum_ from ( ");
		sql.append("select book.EBOOK_DEFINITION_ID, book.PROVIEW_DISPLAY_NAME, book.TITLE_ID, ");
		sql.append("book.LAST_UPDATED, book.IS_DELETED_FLAG, book.EBOOK_DEFINITION_COMPLETE_FLAG, ps.pub_date from ");
		sql.append("EBOOK_DEFINITION book LEFT JOIN (SELECT p.EBOOK_DEFINITION_ID, MAX(p.PUBLISH_START_TIMESTAMP) pub_date ");
		sql.append("FROM PUBLISHING_STATS p GROUP BY p.EBOOK_DEFINITION_ID ) ps ON book.EBOOK_DEFINITION_ID = ps.EBOOK_DEFINITION_ID ");
		
		sql.append(addFiltersToQuery(filter));
		
		String direction = sort.isAscending() ? "asc" : "desc";
		String sortPropertySQL = "book.PROVIEW_DISPLAY_NAME";
		
		SortProperty sortProperty = sort.getSortProperty();

		if (sortProperty.equals(SortProperty.DEFINITION_STATUS)) {
			String sortDelete = "book.IS_DELETED_FLAG";
			String sortComplete = "book.EBOOK_DEFINITION_COMPLETE_FLAG";
			sql.append(String.format("order by %s %s, %s %s", sortDelete,
					direction, sortComplete, direction));
		} else {
			if (sortProperty.equals(SortProperty.TITLE_ID)) {
				sortPropertySQL = "book.TITLE_ID";
			} else if (sortProperty.equals(SortProperty.LAST_GENERATED_DATE)) {
				sortPropertySQL = "ps.PUB_DATE";
			} else if (sortProperty.equals(SortProperty.LAST_EDIT_DATE)) {
				sortPropertySQL = "book.LAST_UPDATED";
			}

			sql.append(String.format("order by %s %s", sortPropertySQL,
					direction));
		}

		Object[] args = argumentsAddToFilter(filter);
		
		// Only get a part of the result set back
		int minIndex = (sort.getPageNumber() - 1) * (sort.getItemsPerPage());
		int maxIndex = sort.getItemsPerPage() + minIndex;
		
		sql.append(String.format(") row_ ) where rownum_ <= %d and rownum_ > %d ", maxIndex, minIndex));

		return jdbcTemplate.query(sql.toString(), LIBRARY_LIST_ROW_MAPPER, args);
	}
	
	@Override
	public Integer numberOfBookDefinitions(LibraryListFilter filter) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(book.EBOOK_DEFINITION_ID) FROM ");
		sql.append("EBOOK_DEFINITION book ");
		
		sql.append(addFiltersToQuery(filter));
		
		Object[] args = argumentsAddToFilter(filter);
		
		return(Integer) jdbcTemplate.queryForInt(sql.toString(), args);
	}
	
	
	private StringBuffer addFiltersToQuery(LibraryListFilter filter) {
		StringBuffer sql = new StringBuffer();
		
		if(filter.getKeywordValue() != null) {
			sql.append("INNER JOIN EBOOK_KEYWORDS k ON book.EBOOK_DEFINITION_ID = k.EBOOK_DEFINITION_ID ");
		}
		
		sql.append("WHERE ");
		if(filter.getKeywordValue() != null) {
			sql.append(String.format("(k.KEYWORD_TYPE_VALUES_ID = '%d') and ", filter.getKeywordValue()));
		}
		if (filter.getFrom() != null) {
			sql.append("(book.LAST_UPDATED >= ?) and ");
		}
		if (filter.getTo() != null) {
			sql.append("(book.LAST_UPDATED < ?) and ");
		}
		if (StringUtils.isNotBlank(filter.getAction())) {
			String action = filter.getAction();
			if (action.equalsIgnoreCase("DELETED")) {
				sql.append(String.format("(book.IS_DELETED_FLAG = '%s') and ", "Y"));
			} else {
				String flag;
				
				if(action.equalsIgnoreCase("READY")) {
					flag = "Y";
				} else {
					flag = "N";
				}
				sql.append(String.format("(book.IS_DELETED_FLAG = '%s') and ", "N"));
				sql.append(String.format("(book.EBOOK_DEFINITION_COMPLETE_FLAG = '%s') and ", flag));
			}
		}
		if (StringUtils.isNotBlank(filter.getIsbn())) {
			sql.append(String.format("(book.ISBN LIKE ?) and ", filter.getIsbn()));
		}
		if (StringUtils.isNotBlank(filter.getMaterialId())) {
			sql.append(String.format("(book.MATERIAL_ID LIKE ?) and ", filter.getMaterialId()));
		}
		if (StringUtils.isNotBlank(filter.getProviewDisplayName())) {
			sql.append(String.format("(UPPER(book.PROVIEW_DISPLAY_NAME) LIKE UPPER(?)) and ", filter.getProviewDisplayName()));
		}
		if (StringUtils.isNotBlank(filter.getTitleId())) {
			sql.append(String.format("(UPPER(book.TITLE_ID) LIKE UPPER(?)) and ", filter.getTitleId()));
		}
		sql.append("(1=1) "); // end of WHERE clause, ensure proper SQL syntax
		
		return sql;
	}
	
	private Object[] argumentsAddToFilter(LibraryListFilter filter) {
		List<Object> args = new ArrayList<Object>();
		// The order of the arguments being added needs to match the order in
		// addFiltersToQuery method.
		if (filter.getFrom() != null) {
			args.add(filter.getFrom());
		}
		if (filter.getTo() != null) {
			args.add(filter.getTo());
		}
		if (StringUtils.isNotBlank(filter.getIsbn())) {
			args.add(filter.getIsbn());
		}
		if (StringUtils.isNotBlank(filter.getMaterialId())) {
			args.add(filter.getMaterialId());
		}
		if (StringUtils.isNotBlank(filter.getProviewDisplayName())) {
			args.add(filter.getProviewDisplayName());
		}
		if (StringUtils.isNotBlank(filter.getTitleId())) {
			args.add(filter.getTitleId());
		}
		
		return args.toArray();
	}
	
	@Required
	public void setJdbcTemplate(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
}