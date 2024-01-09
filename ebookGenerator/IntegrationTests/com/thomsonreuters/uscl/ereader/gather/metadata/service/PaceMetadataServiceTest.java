package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import org.junit.After;
import org.junit.Before;
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
 * @author - Ravi Nandikolla
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class PaceMetadataServiceTest{
    /**
     * The service being tested, injected by Spring.
     *
     */

    protected PaceMetadata paceMetadata;

    /**
     * The service being tested, injected by Spring.
     *
     */
    @Autowired
    protected PaceMetadataService paceMetadataService;

    /**
     * Mock up the DAO and the Entity.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // mock up the document metadata
        paceMetadata = new com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata();
        paceMetadata.setActive("H");
        paceMetadata.setPublicationId(Long.valueOf(123456789));
        paceMetadata.setPublicationCode(Long.valueOf(126977));
        paceMetadata.setStdPubName("AAA");
        paceMetadata.setPublicationName("A.A.A");
        paceMetadata.setType("H");
        paceMetadataService.savePaceMetadata(paceMetadata);
    }

    /**
     * Operation Unit Test
     */
    @Test
    public void findPaceMetadataByPrimaryKey() {
        final Long titleId = paceMetadata.getPublicationId();
        PaceMetadata response = null;
        response = paceMetadataService.findPaceMetadataByPrimaryKey(titleId);
        assertTrue(response != null);
    }

    /**
     * Operation Unit Test
     */
    @Test
    public void findPaceMetadataByPublicationCode() {
        final Long titleId = paceMetadata.getPublicationCode();
        List<PaceMetadata> response = new ArrayList<>();
        response = paceMetadataService.findAllPaceMetadataForPubCode(titleId);
        System.out.println(response.get(0).getStdPubName());
        assertTrue(response != null);
    }

    /**
     * Operation Unit Test Delete an existing DocMetadata entity
     *
     */
    @After
    public void deleteDocMetadata() {
        paceMetadataService.deletePaceMetadata(paceMetadata);
    }
}
