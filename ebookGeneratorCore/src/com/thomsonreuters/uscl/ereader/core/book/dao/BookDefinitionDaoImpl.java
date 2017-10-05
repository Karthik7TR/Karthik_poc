package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class BookDefinitionDaoImpl implements BookDefinitionDao {
    // private static final Logger log =
    // LogManager.getLogger(BookDefinitionDaoImpl.class);
    private SessionFactory sessionFactory;

    public BookDefinitionDaoImpl(final SessionFactory sessFactory) {
        sessionFactory = sessFactory;
    }

    @Override
    public BookDefinition findBookDefinitionByTitle(final String titleId) {
        final BookDefinition bookDef = (BookDefinition) sessionFactory.getCurrentSession()
            .createCriteria(BookDefinition.class)
            .add(Restrictions.eq("fullyQualifiedTitleId", titleId))
            .uniqueResult();

        return bookDef;
    }

    @Override
    public BookDefinition findBookDefinitionByEbookDefId(final Long ebookDefId) {
        return (BookDefinition) sessionFactory.getCurrentSession().get(BookDefinition.class, ebookDefId);
    }

    @Override
    @Deprecated
    public List<BookDefinition> findAllBookDefinitions() {
        final String namedQuery = "findBookDefnBySearchCriterion";
        final String bookDefnQuery = sessionFactory.getCurrentSession().getNamedQuery(namedQuery).getQueryString();
        final Query query = sessionFactory.getCurrentSession().createQuery(bookDefnQuery);
        return query.list();
    }

    @Override
    public List<BookDefinition> findBookDefinitions(
        final String sortProperty,
        final boolean isAscending,
        final int pageNumber,
        final int itemsPerPage) {
        final String namedQuery = "findBookDefnBySearchCriterion";
        final String bookDefnQuery =
            sessionFactory.getCurrentSession().getNamedQuery(namedQuery).getQueryString() + "order by ?";

        final Query query = sessionFactory.getCurrentSession().createQuery(bookDefnQuery);
        query.setParameter(0, (isAscending) ? " asc" : " desc");
        query.setFirstResult((pageNumber - 1) * (itemsPerPage));
        query.setMaxResults(itemsPerPage);
        return query.list();
    }

    @Override
    public List<BookDefinition> findBookDefinitions(
        final String sortProperty,
        final boolean isAscending,
        final int pageNumber,
        final int itemsPerPage,
        final String proviewDisplayName,
        final String fullyQualifiedTitleId,
        final String isbn,
        final String materialId,
        final Date to,
        final Date from,
        final String status) {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(BookDefinition.class);

        // Publish end time stamp comes from different domain. We don't sort it
        // in book definition.
        if (sortProperty != null && !sortProperty.equals("publishEndTimestamp")) {
            criteria.addOrder(isAscending ? Order.asc(sortProperty) : Order.desc(sortProperty));
        }

        if (proviewDisplayName != null && !proviewDisplayName.equals("")) {
            criteria.add(Restrictions.like("proviewDisplayName", proviewDisplayName));
        }

        if (fullyQualifiedTitleId != null && !fullyQualifiedTitleId.equals("")) {
            criteria.add(Restrictions.like("fullyQualifiedTitleId", fullyQualifiedTitleId));
        }

        if (isbn != null && !isbn.equals("")) {
            criteria.add(Restrictions.like("isbn", isbn));
        }

        if (materialId != null && !materialId.equals("")) {
            criteria.add(Restrictions.like("materialId", materialId));
        }

        if (to != null) {
            criteria.add(Restrictions.le("lastUpdated", to));
        }

        if (from != null) {
            criteria.add(Restrictions.ge("lastUpdated", from));
        }

        if (status != null && !status.equals("")) {
            criteria.add(Restrictions.eq("ebookDefinitionCompleteFlag", status));
        }

        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    /**
     * Returns all the book definitions based on Keyword Type Code
     *
     * @return a list of BookDefinition
     */
    @Override
    public List<BookDefinition> findAllBookDefinitionsByKeywordCodeId(final Long keywordTypeCodeId) {
        final Criteria c = sessionFactory.getCurrentSession()
            .createCriteria(BookDefinition.class, "book")
            .createAlias("book.keywordTypeValues", "keywordValues")
            .createAlias("keywordValues.keywordTypeCode", "keywordCode")
            .add(Restrictions.eq("keywordCode.id", keywordTypeCodeId))
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return c.list();
    }

    /**
     * Returns all the book definitions based on Keyword Type Value
     *
     * @return a list of BookDefinition
     */
    @Override
    public List<BookDefinition> findAllBookDefinitionsByKeywordValueId(final Long keywordTypeValueId) {
        final Criteria c = sessionFactory.getCurrentSession()
            .createCriteria(BookDefinition.class, "book")
            .createAlias("book.keywordTypeValues", "keywordValues")
            .add(Restrictions.eq("keywordValues.id", keywordTypeValueId))
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return c.list();
    }

    @Override
    public long countNumberOfBookDefinitions() {
        final Query query = sessionFactory.getCurrentSession().getNamedQuery("countBookDefinitions");
        return (Long) query.uniqueResult();
    }

    @Override
    public void removeBookDefinition(final Long bookDefId) {
        final Session session = sessionFactory.getCurrentSession();
        session.delete(findBookDefinitionByEbookDefId(bookDefId));
        session.flush();
    }

    @Override
    public BookDefinition saveBookDefinition(BookDefinition eBook) {
        final Session session = sessionFactory.getCurrentSession();

        eBook.setLastUpdated(new Date());

        // Attach Publisher Code
        eBook.setPublisherCodes(
            (PublisherCode) session.createCriteria(PublisherCode.class)
                .add(Restrictions.eq("name", eBook.getPublisherCodes().getName()))
                .uniqueResult());

        // Save if book is new
        if (eBook.getEbookDefinitionId() == null) {
            session.save(eBook);
        }

        // attach child objects to book definition
        eBook = (BookDefinition) session.merge(eBook);
        session.flush();

        return eBook;
    }

    @Override
    public BookDefinition saveSplitDocuments(
        final Long bookId,
        final Collection<SplitDocument> splitDocuments,
        final int parts) {
        final Session session = sessionFactory.getCurrentSession();

        BookDefinition eBook = (BookDefinition) session.createCriteria(BookDefinition.class)
            .add(Restrictions.eq("ebookDefinitionId", bookId))
            .uniqueResult();
        eBook.getSplitDocuments().clear();
        eBook.getSplitDocuments().addAll(splitDocuments);
        eBook.setSplitEBookParts(Integer.valueOf(parts));
        // attach child objects to book definition
        eBook = (BookDefinition) session.merge(eBook);
        session.flush();
        return eBook;
    }

    @Override
    public void removeSplitDocuments(final Long bookId) {
        final Session session = sessionFactory.getCurrentSession();

        BookDefinition eBook = (BookDefinition) session.createCriteria(BookDefinition.class)
            .add(Restrictions.eq("ebookDefinitionId", bookId))
            .uniqueResult();
        eBook.getSplitDocuments().clear();
        // attach child objects to book definition
        eBook = (BookDefinition) session.merge(eBook);
        session.flush();
    }

    @Override
    public List<SplitDocument> getSplitDocumentsforBook(final Long ebookDefinitionId) {
        final BookDefinition bookDef = (BookDefinition) sessionFactory.getCurrentSession()
            .createCriteria(BookDefinition.class)
            .add(Restrictions.eq("ebookDefinitionId", ebookDefinitionId))
            .uniqueResult();

        return bookDef.getSplitDocumentsAsList();
    }

    @Override
    public Integer getSplitPartsForEbook(final Long ebookDefinitionId) {
        final BookDefinition bookDef = (BookDefinition) sessionFactory.getCurrentSession()
            .createCriteria(BookDefinition.class)
            .add(Restrictions.eq("ebookDefinitionId", ebookDefinitionId))
            .uniqueResult();

        return bookDef.getSplitEBookParts();
    }

    @Override
    public BookDefinition saveBookDefinition(
        final Long bookId,
        final Collection<SplitNodeInfo> newSplitNodeInfoList,
        final String newVersion) {
        final Session session = sessionFactory.getCurrentSession();

        BookDefinition eBook = (BookDefinition) session.createCriteria(BookDefinition.class)
            .add(Restrictions.eq("ebookDefinitionId", bookId))
            .uniqueResult();

        eBook.setLastUpdated(new Date());

        //Remove persisted rows if the version is same
        final List<SplitNodeInfo> listTobeRemoved = new ArrayList<>();
        for (final SplitNodeInfo splitNodeInfo : eBook.getSplitNodes()) {
            if (splitNodeInfo.getBookVersionSubmitted().equalsIgnoreCase(newVersion)) {
                listTobeRemoved.add(splitNodeInfo);
            }
        }

        if (listTobeRemoved.size() > 0) {
            for (final SplitNodeInfo splitNodeInfo : listTobeRemoved) {
                if (eBook.getSplitNodes().contains(splitNodeInfo)) {
                    eBook.getSplitNodes().remove(splitNodeInfo);
                }
            }
        }

        eBook.getSplitNodes().addAll(newSplitNodeInfoList);

        // attach child objects to book definition
        eBook = (BookDefinition) session.merge(eBook);

        session.flush();

        return eBook;
    }
}
