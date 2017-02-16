package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class CodeDaoImpl implements CodeDao
{
    private SessionFactory sessionFactory;

    public CodeDaoImpl(final SessionFactory sessFactory)
    {
        sessionFactory = sessFactory;
    }

    /**
     * Delete a DocumentType Code in the DOCUMENT_TYPE_CODES table
     * @param documentTypeCode
     * @return
     */
    @Override
    public void deleteDocumentTypeCode(DocumentTypeCode documentTypeCode)
    {
        documentTypeCode = (DocumentTypeCode) sessionFactory.getCurrentSession().merge(documentTypeCode);
        final Session session = sessionFactory.getCurrentSession();
        session.delete(documentTypeCode);
        session.flush();
    }

    /**
     * Delete a JurisType Code in the Juris_TYPE_CODES table
     * @param jurisTypeCode
     * @return
     */
    @Override
    public void deleteJurisTypeCode(JurisTypeCode jurisTypeCode)
    {
        jurisTypeCode = (JurisTypeCode) sessionFactory.getCurrentSession().merge(jurisTypeCode);
        final Session session = sessionFactory.getCurrentSession();
        session.delete(jurisTypeCode);
        session.flush();
    }

    /**
     * Delete a KeywordType Code in the KEYWORD_TYPE_CODES table
     * @param keywordTypeCode
     * @return
     */
    @Override
    public void deleteKeywordTypeCode(KeywordTypeCode keywordTypeCode)
    {
        final Session session = sessionFactory.getCurrentSession();

        // Retrieve all the book definitions that use this keyword value
        final Collection<BookDefinition> books = session.createCriteria(BookDefinition.class, "book")
            .createAlias("book.keywordTypeValues", "keywordValue")
            .createAlias("keywordValue.keywordTypeCode", "keywordCode")
            .add(Restrictions.eq("keywordCode.id", keywordTypeCode.getId()))
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .list();

        // Get Persistent object
        keywordTypeCode = getKeywordTypeCodeById(keywordTypeCode.getId());

        // Remove all KeywordTypeValue references
        final Iterator<KeywordTypeValue> iter = keywordTypeCode.getValues().iterator();
        while (iter.hasNext())
        {
            final KeywordTypeValue value = iter.next();

            //Remove Keyword Value from Book
            for (final BookDefinition book : books)
            {
                book.getKeywordTypeValues().remove(value);
            }
            session.delete(value);
            iter.remove();
        }

        session.delete(keywordTypeCode);
        session.flush();
    }

    /**
     * Delete a KeywordType Value in the KEYWORD_TYPE_VALUES table
     * @param keywordTypeValue
     * @return
     */
    @Override
    public void deleteKeywordTypeValue(KeywordTypeValue keywordTypeValue)
    {
        final Session session = sessionFactory.getCurrentSession();

        // Retrieve all the book definitions that use this keyword value
        final Collection<BookDefinition> books = session.createCriteria(BookDefinition.class, "book")
            .createAlias("book.keywordTypeValues", "keywordValue")
            .add(Restrictions.eq("keywordValue.id", keywordTypeValue.getId()))
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .list();

        // Make keywordTypeValue persist
        keywordTypeValue = (KeywordTypeValue) session.merge(keywordTypeValue);
        final KeywordTypeCode code = keywordTypeValue.getKeywordTypeCode();

        //Remove Keyword Value from Book
        for (final BookDefinition book : books)
        {
            book.getKeywordTypeValues().remove(keywordTypeValue);
        }

        // Remove from Keyword Code collection
        code.getValues().remove(keywordTypeValue);
        session.delete(keywordTypeValue);
        session.flush();
    }

    /**
     * Delete a Publisher Code in the PUBLISHER_TYPE_CODES table
     * @param publisherCode
     * @return
     */
    @Override
    public void deletePublisherCode(PublisherCode publisherCode)
    {
        publisherCode = (PublisherCode) sessionFactory.getCurrentSession().merge(publisherCode);
        final Session session = sessionFactory.getCurrentSession();
        session.delete(publisherCode);
        session.flush();
    }

    /**
     * Delete a PubType Code in the PUB_TYPE_CODES table
     * @param PubTypeCode
     * @return
     */
    @Override
    public void deletePubTypeCode(PubTypeCode pubTypeCode)
    {
        pubTypeCode = (PubTypeCode) sessionFactory.getCurrentSession().merge(pubTypeCode);
        final Session session = sessionFactory.getCurrentSession();
        session.delete(pubTypeCode);
        session.flush();
    }

    /**
     * Get all the DocumentType codes from the DOCUMENT_TYPE_CODES table
     * @return a list of DocumentTypeCode objects
     */
    @Override
    public List<DocumentTypeCode> getAllDocumentTypeCodes()
    {
        final Criteria criteria =
            sessionFactory.getCurrentSession().createCriteria(DocumentTypeCode.class).addOrder(Order.asc("name"));
        return criteria.list();
    }

    /**
     * Get all the JurisType codes from the Juris_TYPE_CODES table
     * @return a list of JurisTypeCode objects
     */
    @Override
    public List<JurisTypeCode> getAllJurisTypeCodes()
    {
        final Criteria criteria =
            sessionFactory.getCurrentSession().createCriteria(JurisTypeCode.class).addOrder(Order.asc("name"));
        return criteria.list();
    }

    /**
     * Get all the KeywordType codes from the KEYWORD_TYPE_CODES table
     * @return a list of KeywordTypeCode objects
     */
    @Override
    public List<KeywordTypeCode> getAllKeywordTypeCodes()
    {
        final Criteria criteria = sessionFactory.getCurrentSession().createCriteria(KeywordTypeCode.class);
        final List<KeywordTypeCode> codes = criteria.list();

        // Sort KeywordTypeCodes
        Collections.sort(codes);

        // Sort values in each KeywordTypeCode
        for (final KeywordTypeCode code : codes)
        {
            final List<KeywordTypeValue> values = new ArrayList<>();
            values.addAll(code.getValues());
            Collections.sort(values);

            code.setValues(values);
        }
        return codes;
    }

    /**
     * Get all the KeywordType codes from the KEYWORD_TYPE_VALUES table
     * @return a list of KeywordTypeValue objects
     */
    @Override
    public List<KeywordTypeValue> getAllKeywordTypeValues()
    {
        final Criteria criteria =
            sessionFactory.getCurrentSession().createCriteria(KeywordTypeValue.class).addOrder(Order.asc("name"));
        return criteria.list();
    }

    /**
     * Get all the KeywordTypeValue codes from the KEYWORD_TYPE_VALUES table
     * that has keywordTypeCodeId
     * @return a list of KeywordTypeValue objects
     */
    @Override
    public List<KeywordTypeValue> getAllKeywordTypeValues(final Long keywordTypeCodeId)
    {
        final Criteria criteria = sessionFactory.getCurrentSession()
            .createCriteria(KeywordTypeValue.class)
            .add(Restrictions.eq("keywordTypeCode.id", keywordTypeCodeId))
            .addOrder(Order.asc("name"));
        return criteria.list();
    }

    /**
     * Get all the Publisher codes from the PUBLISHER_TYPE_CODES table
     * @return a list of PublisherCode objects
     */
    @Override
    public List<PublisherCode> getAllPublisherCodes()
    {
        final Criteria criteria =
            sessionFactory.getCurrentSession().createCriteria(PublisherCode.class).addOrder(Order.asc("name"));
        return criteria.list();
    }

    /**
     * Get all the PubType codes from the PUB_TYPE_CODES table
     * @return a list of PubTypeCode objects
     */
    @Override
    public List<PubTypeCode> getAllPubTypeCodes()
    {
        final Criteria criteria =
            sessionFactory.getCurrentSession().createCriteria(PubTypeCode.class).addOrder(Order.asc("name"));
        return criteria.list();
    }

    /**
     * Get a DocumentType Code from the DOCUMENT_TYPE_CODES table that match DOCUMENT_TYPE_CODES_ID
     * @param documentTypeCodeId
     * @return
     */
    @Override
    public DocumentTypeCode getDocumentTypeCodeById(final Long documentTypeCodeId)
    {
        return (DocumentTypeCode) sessionFactory.getCurrentSession().get(DocumentTypeCode.class, documentTypeCodeId);
    }

    /**
     * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_ID
     * @param jurisTypeCodeId
     * @return
     */
    @Override
    public JurisTypeCode getJurisTypeCodeById(final Long jurisTypeCodeId)
    {
        return (JurisTypeCode) sessionFactory.getCurrentSession().get(JurisTypeCode.class, jurisTypeCodeId);
    }

    /**
     * Get a JurisType Code from the Juris_TYPE_CODES table that match Juris_TYPE_CODES_NAME.
     * This search is case insensitive.
     * @param JurisTypeCodeName
     * @return
     */
    @Override
    public JurisTypeCode getJurisTypeCodeByName(final String jurisTypeCodeName)
    {
        return (JurisTypeCode) sessionFactory.getCurrentSession()
            .createCriteria(JurisTypeCode.class)
            .add(Restrictions.eq("name", jurisTypeCodeName).ignoreCase())
            .uniqueResult();
    }

    /**
     * Get a KeywordType Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_ID
     * @param keywordTypeCodeId
     * @return
     */
    @Override
    public KeywordTypeCode getKeywordTypeCodeById(final Long keywordTypeCodeId)
    {
        return (KeywordTypeCode) sessionFactory.getCurrentSession().get(KeywordTypeCode.class, keywordTypeCodeId);
    }

    /**
     * Get a KeywordTypeCode Code from the KEYWORD_TYPE_CODES table that match KEYWORD_TYPE_CODES_NAME
     * @param keywordTypeCodeName
     * @return
     */
    @Override
    public KeywordTypeCode getKeywordTypeCodeByName(final String keywordTypeCodeName)
    {
        return (KeywordTypeCode) sessionFactory.getCurrentSession()
            .createCriteria(KeywordTypeCode.class)
            .add(Restrictions.eq("name", keywordTypeCodeName).ignoreCase())
            .uniqueResult();
    }

    /**
     * Get a KeywordType Value from the KEYWORD_TYPE_VALUES table that match KEYWORD_TYPE_VALUES_ID
     * @param keywordTypeValueId
     * @return
     */
    @Override
    public KeywordTypeValue getKeywordTypeValueById(final Long keywordTypeValueId)
    {
        return (KeywordTypeValue) sessionFactory.getCurrentSession().get(KeywordTypeValue.class, keywordTypeValueId);
    }

    /**
     * Get a Publisher Code from the PUBLISHER_TYPE_CODES table that match PUBLISHER_TYPE_CODES_ID
     * @param publisherCodeId
     * @return
     */
    @Override
    public PublisherCode getPublisherCodeById(final Long publisherCodeId)
    {
        return (PublisherCode) sessionFactory.getCurrentSession().get(PublisherCode.class, publisherCodeId);
    }

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_ID
     * @param pubTypeCodeId
     * @return
     */
    @Override
    public PubTypeCode getPubTypeCodeById(final Long pubTypeCodeId)
    {
        return (PubTypeCode) sessionFactory.getCurrentSession().get(PubTypeCode.class, pubTypeCodeId);
    }

    /**
     * Get a PubType Code from the PUB_TYPE_CODES table that match PUB_TYPE_CODES_NAME.
     * This search is case insensitive.
     * @param pubTypeCodeName
     * @return
     */
    @Override
    public PubTypeCode getPubTypeCodeByName(final String pubTypeCodeName)
    {
        return (PubTypeCode) sessionFactory.getCurrentSession()
            .createCriteria(PubTypeCode.class)
            .add(Restrictions.eq("name", pubTypeCodeName).ignoreCase())
            .uniqueResult();
    }

    /**
     * Create or Update a DocumentType Code to the DOCUMENT_TYPE_CODES table
     * @param documentTypeCode
     * @return
     */
    @Override
    public void saveDocumentTypeCode(final DocumentTypeCode documentTypeCode)
    {
        documentTypeCode.setLastUpdated(new Date());

        final Session session = sessionFactory.getCurrentSession();

        // Determine if it a new object
        if (documentTypeCode.getId() != null)
        {
            session.merge(documentTypeCode);
        }
        else
        {
            session.save(documentTypeCode);
        }

        session.flush();
    }

    /**
     * Create or Update a JurisType Code to the Juris_TYPE_CODES table
     * @param jurisTypeCode
     * @return
     */
    @Override
    public void saveJurisTypeCode(final JurisTypeCode jurisTypeCode)
    {
        jurisTypeCode.setLastUpdated(new Date());

        final Session session = sessionFactory.getCurrentSession();

        // Determine if it a new object
        if (jurisTypeCode.getId() != null)
        {
            session.merge(jurisTypeCode);
        }
        else
        {
            session.save(jurisTypeCode);
        }

        session.flush();
    }

    /**
     * Create or Update a KeywordType Code to the KEYWORD_TYPE_CODES table
     * @param keywordTypeCode
     * @return
     */
    @Override
    public void saveKeywordTypeCode(final KeywordTypeCode keywordTypeCode)
    {
        keywordTypeCode.setLastUpdated(new Date());

        final Session session = sessionFactory.getCurrentSession();

        // Determine if it a new object
        if (keywordTypeCode.getId() != null)
        {
            session.merge(keywordTypeCode);
        }
        else
        {
            session.save(keywordTypeCode);
        }
        session.flush();
    }

    /**
     * Create or Update a KeywordType Value to the KEYWORD_TYPE_VALUES table
     * @param keywordTypeValue
     * @return
     */
    @Override
    public void saveKeywordTypeValue(final KeywordTypeValue keywordTypeValue)
    {
        keywordTypeValue.setLastUpdated(new Date());

        final Session session = sessionFactory.getCurrentSession();

        // Determine if it a new object
        if (keywordTypeValue.getId() != null)
        {
            session.merge(keywordTypeValue);
        }
        else
        {
            session.save(keywordTypeValue);
        }
        session.flush();
    }

    /**
     * Create or Update a Publisher Code to the PUBLISHER_TYPE_CODES table
     * @param publisherCode
     * @return
     */
    @Override
    public void savePublisherCode(final PublisherCode publisherCode)
    {
        publisherCode.setLastUpdated(new Date());

        final Session session = sessionFactory.getCurrentSession();

        // Determine if it a new object
        if (publisherCode.getId() != null)
        {
            session.merge(publisherCode);
        }
        else
        {
            session.save(publisherCode);
        }
        session.flush();
    }

    /**
     * Create or Update a PubType Code to the PUB_TYPE_CODES table
     * @param PubTypeCode
     * @return
     */
    @Override
    public void savePubTypeCode(final PubTypeCode pubTypeCode)
    {
        pubTypeCode.setLastUpdated(new Date());

        final Session session = sessionFactory.getCurrentSession();

        // Determine if it a new object
        if (pubTypeCode.getId() != null)
        {
            session.merge(pubTypeCode);
        }
        else
        {
            session.save(pubTypeCode);
        }
        session.flush();
    }
}
