package com.thomsonreuters.uscl.ereader.mgr.group.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroup;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroupSort;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroupSort.SortProperty;

public class GroupDefinitionDaoImpl implements GroupDefinitionDao{
	
	private SessionFactory sessionFactory;

	private JdbcTemplate jdbcTemplate;
	private static final GroupListRowMapper GROUP_LIST_ROW_MAPPER = new GroupListRowMapper();
	
	public GroupDefinitionDaoImpl(SessionFactory sessFactory) {
		this.sessionFactory = sessFactory;
	}
	
	@Required
	public void setJdbcTemplate(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
	
	@Override
	@Transactional(readOnly = true)
	public int getEBooksWithGroupsCount(EbookGroup ebookGroup){	
		Criteria criteria = addFilters(ebookGroup);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ebookDefinitionId"), "ebookDefinitionId"));
		return criteria.list().size();
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public List<EbookGroup> findAllBooksWithGroups(EbookGroup ebookGroup, EbookGroupSort sort) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from ( select row_.*, ROWNUM rownum_ from ( ");
		sql.append("select book.EBOOK_DEFINITION_ID, book.PROVIEW_DISPLAY_NAME, ");
		sql.append("book.TITLE_ID,book.GROUP_NAME, sn.book_version from ");
		sql.append("EBOOK_DEFINITION book LEFT JOIN (SELECT s.EBOOK_DEFINITION_ID,  MAX(s.BOOK_VERSION_SUBMITTED) book_version ");
		sql.append("FROM SPLIT_NODE_INFO s GROUP BY s.EBOOK_DEFINITION_ID ) sn ON book.EBOOK_DEFINITION_ID = sn.EBOOK_DEFINITION_ID ");
		
		sql.append(addFiltersToQuery(ebookGroup));
		
		String direction = sort.isAscending() ? "asc" : "desc";
		String sortPropertySQL = " book.PROVIEW_DISPLAY_NAME";
		
		SortProperty sortProperty = sort.getSortProperty();

		
			if (sortProperty.equals(SortProperty.TITLE_ID)) {
				sortPropertySQL = " book.TITLE_ID";
			} else if (sortProperty.equals(SortProperty.GROUP_NAME)) {
				sortPropertySQL = " book.GROUP_NAME";
			}

			sql.append(String.format("order by %s %s", sortPropertySQL,
					direction));
		

		Object[] args = argumentsAddToFilter(ebookGroup);
		
		// Only get a part of the result set back
		int minIndex = (sort.getPageNumber() - 1) * (sort.getItemsPerPage());
		int maxIndex = sort.getItemsPerPage() + minIndex;
		
		sql.append(String.format(") row_ ) where rownum_ <= %d and rownum_ > %d ", maxIndex, minIndex));
		

		return jdbcTemplate.query(sql.toString(), GROUP_LIST_ROW_MAPPER, args);
	}
	
	private StringBuffer addFiltersToQuery(EbookGroup ebookGroup) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" WHERE ");
		
		if (StringUtils.isNotBlank(ebookGroup.getProviewDisplayName())) {
			sql.append("(UPPER(book.PROVIEW_DISPLAY_NAME) LIKE UPPER(?)) and ");
		}
		if (StringUtils.isNotBlank(ebookGroup.getGroupName())) {
			sql.append(" (UPPER(book.GROUP_NAME ) LIKE UPPER(?)) and ");
		}
		if (StringUtils.isNotBlank(ebookGroup.getTitleId())) {
			sql.append("(UPPER(book.TITLE_ID) LIKE UPPER(?)) and ");
		}
		if (ebookGroup.getBookDefinitionId() != null) {
			sql.append(String.format(" (book.EBOOK_DEFINITION_ID = '%d') and ", ebookGroup.getBookDefinitionId()));
		}
		sql.append(" (book.GROUP_NAME is not null  and book.IS_SPLIT_BOOK='Y') ");
		
		return sql;
	}
	
	private Object[] argumentsAddToFilter(EbookGroup ebookGroup) {
		List<Object> args = new ArrayList<Object>();
		// The order of the arguments being added needs to match the order in
		// addFiltersToQuery method.
		if (ebookGroup.getBookDefinitionId() !=null) {
			args.add(ebookGroup.getBookDefinitionId());
		}
		if (ebookGroup.getProviewDisplayName() != null) {
			args.add(ebookGroup.getProviewDisplayName());
		}
		if (StringUtils.isNotBlank( ebookGroup.getGroupName())) {
			args.add(ebookGroup.getGroupName());
		}
		if (StringUtils.isNotBlank(ebookGroup.getTitleId())) {
			args.add(ebookGroup.getTitleId());
		}
		
		
		return args.toArray();
	}
		
	
	private Criteria addFilters(EbookGroup ebookGroup) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(BookDefinition.class);		
		
		if (StringUtils.isNotBlank(ebookGroup.getProviewDisplayName())) {
			criteria.add(Restrictions.like("proviewDisplayName", ebookGroup.getProviewDisplayName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(ebookGroup.getGroupName())) {
			criteria.add(Restrictions.like("groupName", ebookGroup.getGroupName()).ignoreCase());
		}
		if (StringUtils.isNotBlank(ebookGroup.getTitleId())) {
			criteria.add(Restrictions.like("fullyQualifiedTitleId", ebookGroup.getTitleId()).ignoreCase());
		}
		if (ebookGroup.getBookDefinitionId() != null) {
			criteria.add(Restrictions.eq("ebookDefinitionId", ebookGroup.getBookDefinitionId()));
		}
		criteria.add(Restrictions.isNotNull("groupName"));
		criteria.add(Restrictions.eq("isSplitBook", "Y"));
		return criteria;
	}


}
