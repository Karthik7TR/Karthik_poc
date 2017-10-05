package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Collection;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;

/**
 * Service methods that are common to both the Spring Batch generator engine and manager web applications.
 */
public interface BookDefinitionService {
    /**
     * Returns all the current book definitions.
     * @return a list of BookDefinition
     */
    @Deprecated // Remove once the dashboard Create Book functionality is retired in favor of launching books from the manager application.
    List<BookDefinition> findAllBookDefinitions();

    /**
     * Find a book definition by its primary key.
     * @param key the primary key of the definition
     * @return the found entity, or null if not found.
     */
    BookDefinition findBookDefinitionByTitle(String titleId);

    BookDefinition findBookDefinitionByEbookDefId(Long ebookDefId);

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
     * Returns all the current book definitions based on the search criterion
     *
     * @return a list of BookDefinitions
     */

    List<BookDefinition> findBookDefinitions(
        String sortProperty,
        boolean isAscending,
        int pageNumber,
        int itemsPerPage);

    /**
     * Returns a count of all current book definitions.
     * @return an integer
     */
    long countNumberOfBookDefinitions();

    /**
     * Update the published status of the book definition
     *
     */
    void updatePublishedStatus(Long bookId, boolean isPublishedOnce);

    /**
     * Update Node Guids with split book titles of book definition
     *
     */
    void updateSplitNodeInfoSet(Long bookId, Collection<SplitNodeInfo> splitNodeInfoSet, String version);

    /**
     * Update the deleted status of the book definition
     *
     */
    void updateDeletedStatus(Long bookId, boolean isDeleted);

    /**
     * Save or update the book definition
     *
     */
    BookDefinition saveBookDefinition(BookDefinition eBook);

    /**
     * Delete the book definition
     *
     */
    void removeBookDefinition(Long ebookDefId);

    BookDefinition saveSplitDocumentsforEBook(Long bookId, Collection<SplitDocument> splitDocuments, int parts);

    Integer getSplitPartsForEbook(Long bookId);

    void deleteSplitDocuments(Long bookId);

    List<SplitDocument> findSplitDocuments(Long bookId);
}
