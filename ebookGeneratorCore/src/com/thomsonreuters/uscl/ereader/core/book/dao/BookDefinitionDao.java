package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import org.springframework.dao.DataAccessException;

public interface BookDefinitionDao {
    /**
     * Returns all the current book definitions.
     * @return a list of BookDefinition
     * @deprecated
     */
    @Deprecated
    List<BookDefinition> findAllBookDefinitions();

    /**
     * Find a book definition by its primary key.
     * @param key the primary key of the definition
     * @return the found entity, or null if not found.
     */
    BookDefinition findBookDefinitionByTitle(String fullyQualifiedTitleId);

    /**
     * Find a book definition by its primary key.
     * @param key the primary key of the definition
     * @return the found entity, or null if not found.
     */
    BookDefinition findBookDefinitionByEbookDefId(Long ebookDefId);

    /**
     * Returns all the current book definitions based on the search criterion
     *
     * @return a list of BookDefinition
     */
    List<BookDefinition> findBookDefinitions(
        String sortProperty,
        boolean isAscending,
        int pageNumber,
        int itemsPerPage);

    /**
     * Returns all the book definitions based on Keyword Type Code
     * @return a list of BookDefinition
     */
    List<BookDefinition> findAllBookDefinitionsByKeywordCodeId(Long keywordTypeCodeId);

    /**
     * Returns all the book definitions based on Keyword Type Value
     * @return a list of BookDefinition
     */
    List<BookDefinition> findAllBookDefinitionsByKeywordValueId(Long keywordTypeValueId);

    /**
     * Returns a count of the current book definitions.
     * @return an integer
     */
    long countNumberOfBookDefinitions();

    /**
     * Removes a book definition.
     * @param key the primary key of the definition
     * @return void
     */
    void removeBookDefinition(Long bookDefId) throws DataAccessException;

    /**
     * Saves a book definitions.
     * @param a Book definition
     * @return BookDefinition
     */

    BookDefinition saveBookDefinition(BookDefinition eBook);

    BookDefinition saveSplitDocuments(Long ebookDefinitionId, Collection<SplitDocument> splitDocuments, int parts);

    List<BookDefinition> findBookDefinitions(
        String sortProperty,
        boolean isAscending,
        int pageNumber,
        int itemsPerPage,
        String proviewDisplayName,
        String fullyQualifiedTitleId,
        String isbn,
        String materialId,
        Date to,
        Date from,
        String status);

    BookDefinition saveBookDefinition(
        Long ebookDefinitionId,
        Collection<SplitNodeInfo> splitNodeInfoList,
        String newVersion);

    Integer getSplitPartsForEbook(Long ebookDefinitionId);

    void removeSplitDocuments(Long bookId);

    Collection<SplitDocument> getSplitDocumentsforBook(Long bookId);
}
