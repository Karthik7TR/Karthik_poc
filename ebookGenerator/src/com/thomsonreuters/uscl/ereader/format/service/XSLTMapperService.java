package com.thomsonreuters.uscl.ereader.format.service;

/**
 * Spring service that handles requests to retrieve XSLTMapperEntity
 * @author Ripu Jain U0115290
 */
public interface XSLTMapperService
{
    /**
     * Return XSLT style sheet name for a given document collection and doc-type.
     * @param collection The collection name of the document. Ex: w_codesstaflnvdp
     * @param docType The doc-type of the document. Ex: 6A
     * @return XSLT style sheet name.
     */
    String getXSLT(String collection, String docType);
}
