package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test class that connects to the database and test the service
 * class (XSLTMapperService) functionality. The service connects to the EBOOK.XSLT_MAPPER table.
 * @author Ripu Jain U0115290
 * @author Ray Cracauer U0113997
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public final class XSLTMapperServiceIntegrationTest
{
    @Autowired
    protected XSLTMapperService xsltMapperService;

    private static final String COLLECTION = "w_codesstaflnvdp";
    private static final String DOC_TYPE = "6A";
    private static final String XSLT = "CodesStatutes.xsl";

    /**
     * Testing the happy path - return the expected XSLT for a given collection and doc_type.
     */
    @Test
    public void testGetXsltFromDatabase()
    {
        assertEquals(XSLT, xsltMapperService.getXSLT(COLLECTION, DOC_TYPE));
    }

    /**
     * Testing another happy path scenario - return the expected XSLT for given collection and
     * null doc_type.
     */
    @Test
    public void testGetXsltFromDatabaseNullDocType()
    {
        final String COLLECTION = "w_3rd_plirpub";
        final String DOC_TYPE = null;
        final String XSLT = "AnalyticalEaganProducts.xsl";
        assertEquals(XSLT, xsltMapperService.getXSLT(COLLECTION, DOC_TYPE));
    }

    /**
     * Testing another happy path scenario - return the expected XSLT for given collection and
     * empty doc_type.
     */
    @Test
    public void testGetXsltFromDatabaseEmptyDocType()
    {
        final String COLLECTION = "w_3rd_plirpub";
        final String DOC_TYPE = "3E";
        final String XSLT = "AnalyticalEaganProducts.xsl";
        final String hello = xsltMapperService.getXSLT(COLLECTION, DOC_TYPE);
        System.out.println(hello);
        assertEquals(XSLT, xsltMapperService.getXSLT(COLLECTION, DOC_TYPE));
    }

    /**
     * Testing another happy path scenario - return the expected XSLT for given collection and
     * white spaced doc_type.
     */
    @Test
    public void testGetXsltFromDatabaseWhiteSpaceDocType()
    {
        final String COLLECTION = "w_3rd_plirpub";
        final String DOC_TYPE = " ";
        final String XSLT = "AnalyticalEaganProducts.xsl";
        assertEquals(XSLT, xsltMapperService.getXSLT(COLLECTION, DOC_TYPE));
    }

    /**
     * Testing another happy path scenario - return null for a collection and doc_type that are
     * not present in the database.
     */
    @Test
    public void testGetXsltFromDatabaseRowNotPresent()
    {
        final String COLLECTION = "collection not present";
        final String DOC_TYPE = "doc type not present";
        final String XSLT = null;
        assertEquals(XSLT, xsltMapperService.getXSLT(COLLECTION, DOC_TYPE));
    }

    /**
     * Testing bad path scenario - throw an exception when trying to retrieve XSLT for null collection.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetXsltFromDatabaseNullCollection() throws Exception
    {
        final String COLLECTION = null;
        final String DOC_TYPE = "doc type not present";
        xsltMapperService.getXSLT(COLLECTION, DOC_TYPE);
    }
}
