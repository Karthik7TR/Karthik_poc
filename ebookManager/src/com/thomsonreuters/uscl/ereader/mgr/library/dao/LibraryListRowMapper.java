package com.thomsonreuters.uscl.ereader.mgr.library.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.service.AuthorService;
import com.thomsonreuters.uscl.ereader.mgr.library.vdo.LibraryList;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.RowMapper;

public class LibraryListRowMapper implements RowMapper<LibraryList>
{
    private static AuthorService authorService;

    @Override
    public LibraryList mapRow(final ResultSet resultSet, final int rowNum) throws SQLException
    {
        final Long bookDefinitionId = resultSet.getLong("EBOOK_DEFINITION_ID");
        final String proviewName = resultSet.getString("PROVIEW_DISPLAY_NAME");
        final String titleId = resultSet.getString("TITLE_ID");
        final String isComplete = resultSet.getString("EBOOK_DEFINITION_COMPLETE_FLAG");
        final String isDeleted = resultSet.getString("IS_DELETED_FLAG");
        final Date lastUpdate = resultSet.getTimestamp("LAST_UPDATED");
        final Date lastPublished = resultSet.getTimestamp("PUB_DATE");

        // Add the author information
        final Set<Author> authors = new HashSet<Author>();
        authors.addAll(authorService.findAuthorsByEBookDefnId(bookDefinitionId));

        return new LibraryList(
            bookDefinitionId,
            proviewName,
            titleId,
            isComplete,
            isDeleted,
            lastUpdate,
            lastPublished,
            authors);
    }

    @Required
    public static void setAuthorService(final AuthorService service)
    {
        authorService = service;
    }
}
