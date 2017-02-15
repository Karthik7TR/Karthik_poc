package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Collection;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDefinitionDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * Shared service methods used in both the Spring Batch engine and the dashboard web apps.
 */
public class BookDefinitionServiceImpl implements BookDefinitionService
{
    //private static final Logger log = LogManager.getLogger(BookDefinitionServiceImpl.class);
    private BookDefinitionDao bookDefinitionDao;

    @Override
    @Transactional(readOnly = true)
    public BookDefinition findBookDefinitionByTitle(final String titleId)
    {
        return bookDefinitionDao.findBookDefinitionByTitle(titleId);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDefinition findBookDefinitionByEbookDefId(final Long ebookDefId)
    {
        return bookDefinitionDao.findBookDefinitionByEbookDefId(ebookDefId);
    }

    /**
     * Returns all the book definitions based on Keyword Type Code
     * @return a list of BookDefinition
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookDefinition> findAllBookDefinitionsByKeywordCodeId(final Long keywordTypeCodeId)
    {
        return bookDefinitionDao.findAllBookDefinitionsByKeywordCodeId(keywordTypeCodeId);
    }

    /**
     * Returns all the book definitions based on Keyword Type Value
     * @return a list of BookDefinition
     */
    @Override
    @Transactional(readOnly = true)
    public List<BookDefinition> findAllBookDefinitionsByKeywordValueId(final Long keywordTypeValueId)
    {
        return bookDefinitionDao.findAllBookDefinitionsByKeywordValueId(keywordTypeValueId);
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<BookDefinition> findAllBookDefinitions()
    {
        return bookDefinitionDao.findAllBookDefinitions();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDefinition> findBookDefinitions(
        final String sortProperty,
        final boolean isAscending,
        final int pageNumber,
        final int itemsPerPage)
    {
        return bookDefinitionDao.findBookDefinitions(sortProperty, isAscending, pageNumber, itemsPerPage);
    }

    @Required
    public void setBookDefinitionDao(final BookDefinitionDao dao)
    {
        bookDefinitionDao = dao;
    }

    @Override
    @Transactional(readOnly = true)
    public long countNumberOfBookDefinitions()
    {
        return bookDefinitionDao.countNumberOfBookDefinitions();
    }

    /**
     * Instantiates a new DocMetadataServiceImpl.
     *
     */
    /*
     * public DocMetadataServiceImpl() { docMetaXMLParser = new
     * DocMetaDataXMLParser(); }
     */

    /**
     * Update the published status of the book definition
     *
     */
    @Override
    @Transactional
    public void updatePublishedStatus(final Long bookId, final boolean isPublishedOnce)
    {
        final BookDefinition book = findBookDefinitionByEbookDefId(bookId);
        if (book != null)
        {
            book.setPublishedOnceFlag(isPublishedOnce);
            bookDefinitionDao.saveBookDefinition(book);
        }
    }

    /**
     * Update the deleted status of the book definition
     *
     */
    @Override
    @Transactional
    public void updateDeletedStatus(final Long bookId, final boolean isDeleted)
    {
        final BookDefinition book = findBookDefinitionByEbookDefId(bookId);
        if (book != null)
        {
            book.setIsDeletedFlag(isDeleted);
            bookDefinitionDao.saveBookDefinition(book);
        }
    }

    /**
     * Update the book definition
     *
     */
    @Override
    @Transactional
    public void updateSplitNodeInfoSet(final Long bookId, final Collection<SplitNodeInfo> splitNodeInfoList, final String version)
    {
        bookDefinitionDao.saveBookDefinition(bookId, splitNodeInfoList, version);
    }

    /**
     * Save an existing Book Definition
     *
     */
    @Override
    @Transactional
    public BookDefinition saveBookDefinition(BookDefinition eBook)
    {
        eBook = bookDefinitionDao.saveBookDefinition(eBook);

        return eBook;
    }

    /**
     * Delete the book definition
     *
     */
    @Override
    @Transactional
    public void removeBookDefinition(final Long ebookDefId)
    {
        bookDefinitionDao.removeBookDefinition(ebookDefId);
    }

    @Override
    @Transactional
    public BookDefinition saveSplitDocumentsforEBook(final Long bookId, final Collection<SplitDocument> splitDocuments, final int parts)
    {
        return bookDefinitionDao.saveSplitDocuments(bookId, splitDocuments, parts);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getSplitPartsForEbook(final Long bookId)
    {
        return bookDefinitionDao.getSplitPartsForEbook(bookId);
    }

    @Override
    @Transactional
    public void deleteSplitDocuments(final Long bookId)
    {
        bookDefinitionDao.removeSplitDocuments(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SplitDocument> findSplitDocuments(final Long bookId)
    {
        return bookDefinitionDao.getSplitDocumentsforBook(bookId);
    }
}
