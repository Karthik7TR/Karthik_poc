package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Timestamp;
import java.util.Calendar;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class to run the service as a JUnit test. Each operation in the service is a
 * separate test.
 *
 * @author - Nirupam Chatterjee
 * @author - Ray Cracauer
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class DocMetadataServiceTest {
    private static Logger LOG = LogManager.getLogger(DocMetadataServiceTest.class);
    private Timestamp UPDATE_DATE = getCurrentTimeStamp();
    /**
     * The service being tested, injected by Spring.
     *
     */
    @Autowired
    protected DocMetadataService documentMetadataService;

    protected DocMetadata docmetadata;

    /**
     * Mock up the DAO and the Entity.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // mock up the document metadata

        saveDocMetadata(Integer.valueOf(1));
    }

    /**
     * Operation Unit Test Save an existing DocMetadata entity
     *
     */
    public void saveDocMetadata(final Integer seqNum) {
        docmetadata = new com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata();
        docmetadata.setCollectionName("test_choto_Collection");
        docmetadata.setDocFamilyUuid("1234567890" + seqNum.toString());
        docmetadata.setDocType("codes");
        docmetadata.setDocUuid("1234567890001" + seqNum.toString());
        docmetadata.setFindOrig(null);
        docmetadata.setJobInstanceId(Long.valueOf(99123456));
        docmetadata.setLastUpdated(UPDATE_DATE);
        docmetadata.setNormalizedFirstlineCite("TEST^[89]");
        docmetadata.setSerialNumber(null);
        docmetadata.setTitleId("TL-URB" + seqNum.toString());
        docmetadata.setProviewFamilyUUIDDedup(Integer.valueOf(1));
        documentMetadataService.saveDocMetadata(docmetadata);
    }

    /**
     * Operation Unit Test Delete an existing DocMetadata entity
     *
     */
    @After
    public void deleteDocMetadata() {
        documentMetadataService.deleteDocMetadata(docmetadata);
    }

    /**
     * Operation Unit Test
     */
    @Test
    public void findDocMetadataByPrimaryKey() {
        final String titleId = docmetadata.getTitleId();
        final Long jobInstanceId = docmetadata.getJobInstanceId();
        final String docUuid = docmetadata.getDocUuid();
        DocMetadata response = null;
        response = documentMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid);
        assertTrue(response != null);
    }

    /**
     * Operation Unit Test
     *
     * @author Ray Cracauer
     */
    @Test
    public void findDocMetadataByPrimaryKeyNegativeTitleId() {
        final String titleId = "TEST_TILE";
        final Long jobInstanceId = Long.valueOf(-12345);
        final String docUuid = "123456";

        DocMetadata response = null;
        response = documentMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid);
        assertTrue(response == null);
    }

    /**
     * Operation Unit Test
     *
     * @author Ray Cracauer
     */
    @Test
    public void findDocMetadataByPrimaryKeyAllNulls() {
        final String titleId = null;
        final Long jobInstanceId = null;
        final String docUuid = null;

        DocMetadata response = null;
        response = documentMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid);
        assertTrue(response == null);
    }

    /**
     * Operation Unit Test
     *
     * @author Ray Cracauer
     */
    @Test
    public void findDocMetadataByPrimaryNullTitleId() {
        final String titleId = null;
        final Long jobInstanceId = Long.valueOf(12345);
        final String docUuid = "123456";

        DocMetadata response = null;
        response = documentMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid);
        assertTrue(response == null);
    }

    /**
     * Operation Unit Test
     *
     * @author Ray Cracauer
     */
    @Test
    public void findDocMetadataByPrimaryNullJobInstanceId() {
        final String titleId = "TEST_TILE";
        final Long jobInstanceId = null;
        final String docUuid = "123456";

        DocMetadata response = null;
        response = documentMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid);
        assertTrue(response == null);
    }

    /**
     * Operation Unit Test
     *
     * @author Ray Cracauer
     */
    @Test
    public void findDocMetadataByPrimaryNullDocUuid() {
        final String titleId = "TEST_TILE";
        final Long jobInstanceId = Long.valueOf(12345);
        final String docUuid = null;

        DocMetadata response = null;
        response = documentMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid);
        assertTrue(response == null);
    }

    /**
     * Operation Unit Test
     *
     * @author Ray Cracauer
     */
    @Test
    public void findDocMetadataByPrimaryKeyObjectValues() {
        final String titleId = "TL-URB1";
        final Long jobInstanceId = Long.valueOf(99123456);
        final String docUuid = "12345678900011";

        DocMetadata response = null;
        final DocMetadata expected = new DocMetadata();
        expected.setTitleId(titleId);
        expected.setJobInstanceId(jobInstanceId);
        expected.setDocUuid(docUuid);
        expected.setDocFamilyUuid("12345678901");
        expected.setDocType("codes");
        expected.setNormalizedFirstlineCite("TEST-(89)");
        expected.setFindOrig(null);
        expected.setSerialNumber(null);
        expected.setCollectionName("test_choto_Collection");
        expected.setLastUpdated(UPDATE_DATE);
        expected.setProviewFamilyUUIDDedup(Integer.valueOf(1));

        LOG.debug(" expected " + expected);

        response = documentMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid);
        LOG.debug(" response " + response);

        Assert.assertEquals(response.toString(), expected.toString());
    }

    /**
     * Operation Unit Test Parse DocMetadata xml and persist it
     *
     */
    @Ignore
    public void parseAndStoreDocMetadata() throws Exception {
        final String titleId = "TEST_TILE";
        final Long jobInstanceId = Long.valueOf(12345);
        final String docUuid = "123456";
        documentMetadataService.parseAndStoreDocMetadata(titleId, jobInstanceId, new File(docUuid));
    }

    @Test
    public void testFindAllDocumentMetadataForTitleByJobInstanceId() {
        final Long jobInstanceId = Long.valueOf(99123456);
        saveDocMetadata(2);
        saveDocMetadata(3);
        final DocumentMetadataAuthority documentMetadataAuthority =
            documentMetadataService.findAllDocMetadataForTitleByJobId(jobInstanceId);
        System.out.println(documentMetadataAuthority.toString());
        Assert.assertTrue(documentMetadataAuthority.getAllDocumentMetadata().size() == 3);
    }

    @Test
    public void testFindAllDocumentMetadataForTitleByJobInstanceIdDoesNotReturnNullSet() {
        final DocumentMetadataAuthority documentMetadataAuthority =
            documentMetadataService.findAllDocMetadataForTitleByJobId(0L);
        Assert.assertTrue(documentMetadataAuthority != null);
        Assert.assertTrue(documentMetadataAuthority.getAllDocumentMetadata().size() == 0);
    }

    @Test
    /**
     * This test is here to validate a previously run book's set of document metadata can be retrieved.
     * If job 1804 gets cleaned up, point this at whichever job you like (for your database environment). Change the second assert (and this javadoc) accordingly.
     */
    public void testFindAllDocumentMetadataForTitleIdByJobInstanceIdIntegrationTest() {
        final DocumentMetadataAuthority documentMetadataAuthority =
            documentMetadataService.findAllDocMetadataForTitleByJobId(1804L);
        Assert.assertTrue(documentMetadataAuthority != null);
        Assert.assertTrue(documentMetadataAuthority.getAllDocumentMetadata().size() == 0);
    }

    /**
     * Get the current timestamp
     *
     * @return Timestamp
     */
    private Timestamp getCurrentTimeStamp() {
        // create a java calendar instance
        final Calendar calendar = Calendar.getInstance();
        return new java.sql.Timestamp(calendar.getTime().getTime());
    }
}
