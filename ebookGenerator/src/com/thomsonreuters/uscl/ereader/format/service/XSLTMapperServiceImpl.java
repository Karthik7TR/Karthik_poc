package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.format.dao.XSLTMapperDao;
import com.thomsonreuters.uscl.ereader.format.domain.XSLTMapperEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring service that handles requests to retrieve XSLTMapperEntity
 * @author Ripu Jain U0115290
 */
@Service
public class XSLTMapperServiceImpl implements XSLTMapperService
{
    private XSLTMapperDao xsltMapperDao;

    /**
     * Return XSLT style sheet name for a given document collection and doc-type.
     * @param collection The collection name of the document. Ex: w_codesstaflnvdp
     * @param docType The doc-type of the document. Ex: 6A
     * @return XSLT style sheet name.
     */
    @Override
    @Transactional(readOnly = true)
    public String getXSLT(final String collection, final String docType)
    {
        if (xsltMapperDao == null)
        {
            throw new IllegalArgumentException(
                "xsltMapperDao was not injected "
                    + "into XSLTMapperService! This is a programming error. "
                    + "Check the Spring configuration!");
        }
        final XSLTMapperEntity xslt = xsltMapperDao.getXSLT(collection, docType);
        if (xslt == null)
            return null;
        return xslt.getXSLT();
    }

    public void setXsltMapperDao(final XSLTMapperDao xsltMapperDao)
    {
        this.xsltMapperDao = xsltMapperDao;
    }
}
