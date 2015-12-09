package com.thomsonreuters.uscl.ereader.mgr.group.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroup;

public class GroupListRowMapper  implements RowMapper<EbookGroup> {

	public EbookGroup mapRow(ResultSet resultSet, int rowNum)
			throws SQLException {
		Long bookDefinitionId = resultSet.getLong("EBOOK_DEFINITION_ID");
		String proviewName = resultSet.getString("PROVIEW_DISPLAY_NAME");
		String titleId = resultSet.getString("TITLE_ID");
		String groupName = resultSet.getString("GROUP_NAME");
		String version = resultSet.getString("BOOK_VERSION");
		
		return new EbookGroup(titleId,proviewName,groupName,bookDefinitionId,version);
	}
}
