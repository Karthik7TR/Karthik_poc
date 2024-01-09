package com.thomsonreuters.uscl.ereader.mgr.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import com.thomsonreuters.uscl.ereader.mgr.web.service.author.AuthorService;
import org.springframework.jdbc.core.RowMapper;

public class LibraryListRowMapper implements RowMapper<LibraryList> {
    private final AuthorService authorService;

    public LibraryListRowMapper(final AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    public LibraryList mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        final Long bookDefinitionId = resultSet.getLong("EBOOK_DEFINITION_ID");
        return new LibraryList(
            bookDefinitionId,
            resultSet.getString("SOURCE_TYPE"),
            resultSet.getString("PROVIEW_DISPLAY_NAME"),
            resultSet.getString("TITLE_ID"),
            resultSet.getString("EBOOK_DEFINITION_COMPLETE_FLAG"),
            resultSet.getString("IS_DELETED_FLAG"),
            resultSet.getTimestamp("LAST_UPDATED"),
            resultSet.getTimestamp("PUB_DATE"),
            resultSet.getLong("COMB_BOOK_DEFN_ID"),
            resultSet.getString("COMB_BOOK_FLAG"),
            new HashSet<>(authorService.findAuthorsByEBookDefnId(bookDefinitionId)));
    }
}
