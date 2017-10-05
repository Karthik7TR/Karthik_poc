package com.thomsonreuters.uscl.ereader.format.dao;

import com.thomsonreuters.uscl.ereader.format.domain.XSLTMapperEntity;

/**
 * XSLTMapperDao is the interface for accessing the XSLT style sheet entity from the data store.
 *
 * @author Ripu Jain U0115290
 */
public interface XSLTMapperDao {
    /**
     * Retrieves the XSLT style sheet for a given collection and doc-type.
     *
     * @param collection The collection name of the document. Ex: w_codesstaflnvdp
     * @param docType The doc-type of the document. Ex: 6A
     * @return XSLTMapperEntity the XSLT entity object.
     */
    XSLTMapperEntity getXSLT(String collection, String docType);
}
