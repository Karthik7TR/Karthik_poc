package com.thomsonreuters.uscl.ereader.format.dao;

import com.thomsonreuters.uscl.ereader.format.domain.XSLTMapperEntity;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Repository;

/**
 * XSLTMapperDaoImpl is a DAO to retrieve the XSLTMapperEntity.
 *
 * @author Ripu Jain U0115290
 */
@Repository("xsltMapperDao")
public class XSLTMapperDaoImpl implements XSLTMapperDao {
    private SessionFactory sessionFactory;

    /**
     * Retrieves the XSLT style sheet for a given collection and doc-type.
     *
     * @param collection The collection name of the document. Ex: w_codesstaflnvdp
     * @param docType The doc-type of the document. Ex: 6A
     * @return XSLTMapperEntity the XSLT entity object.
     */
    @Override
    public XSLTMapperEntity getXSLT(final String collection, final String docType) {
        if (StringUtils.isBlank(collection))
            throw new IllegalArgumentException(
                "Failed to builed the query to retrieve XSLT. " + "Collection name can not be null.");

        final Query query;
        if (StringUtils.isNotBlank(docType)) {
            query = sessionFactory.getCurrentSession().getNamedQuery("getXSLT");
            query.setString("collection", collection);
            query.setString("doc_type", docType);
        } else {
            query = sessionFactory.getCurrentSession().getNamedQuery("getXSLTWhereDocTypeIsNull");
            query.setString("collection", collection);
        }
        final Object queryResult = query.uniqueResult();
        if (queryResult == null)
            return null;
        return (XSLTMapperEntity) queryResult;
    }

    @Required
    public void setSessionFactory(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
