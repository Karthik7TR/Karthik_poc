package com.thomsonreuters.uscl.ereader.gather.restclient.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherDocRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherNortRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherTocRequest;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public final class GatherRestServiceIntegrationTest {
    private static Logger log = LogManager.getLogger(GatherRestServiceIntegrationTest.class);
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Autowired
    private GatherService gatherService;

    /**
     * Fetch a TOC by invoking the Gather REST service.
     */
    @Test
    public void testGetToc() {
        final String TOC_COLLECTION_NAME = "w_an_rcc_cajur_toc"; // Client
        final String ROOT_TOC_GUID_IMPH = "I0900caf0675c11da90ebf04471783734";
//		String ROOT_TOC_GUID_FSLP = "I4fa9eea0b36011dab270c8080cf3148a";
        final File tempDir = temporaryFolder.getRoot();
        final File tocFile = new File(tempDir, "toc.xml");
        Assert.assertTrue(tempDir.canWrite());
        final GatherTocRequest gatherTocRequest = new GatherTocRequest(
            ROOT_TOC_GUID_IMPH,
            TOC_COLLECTION_NAME,
            tocFile,
            null,
            null,
            true,
            new ArrayList<String>(),
            0);

        final GatherResponse gatherResponse = gatherService.getToc(gatherTocRequest);
        log.debug(gatherResponse);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(0, gatherResponse.getErrorCode());
        Assert.assertEquals(10, gatherResponse.getDocCount());
        Assert.assertEquals(13, gatherResponse.getNodeCount());
        Assert.assertTrue(tocFile.exists());
        Assert.assertTrue(tocFile.length() > 0);
    }

    /**
     * Fetch a TOC by invoking the Gather REST service.
     */
    @Test
    public void testGetTocNort() {
        final String NORT_DOMAIN_NAME = "w_uscl_eBook"; // Client
        final String NORT_FILTER_EXPRESSION = "Test1";
        final File tempDir = temporaryFolder.getRoot();
        final File tocFile = new File(tempDir, "toc.xml");
        Assert.assertTrue(tempDir.canWrite());
        final GatherNortRequest gatherNortRequest = new GatherNortRequest(
            NORT_DOMAIN_NAME,
            NORT_FILTER_EXPRESSION,
            tocFile,
            new Date(),
            null,
            null,
            true,
            true,
            new ArrayList<String>(),
            0);

        final GatherResponse gatherResponse = gatherService.getNort(gatherNortRequest);
        log.debug(gatherResponse);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(3, gatherResponse.getErrorCode());
        Assert.assertTrue(tocFile.exists());
        Assert.assertTrue(tocFile.length() > 0);
    }

    /**
     * Fetch a single DOC from Novus by invoking the Gather REST service.
     * Prod DOC GUIDS and collection name from S. Alic (1/20/12)
     *	I2e91cd8ba11611d9ad0a81db1eb1d417 - w_an_rcc_cajur
     *	I6df3a45ac31e11dab3bee1090045c758 - w_an_ea_texts
     *	NE7EFCB407E2611DA8F1DA64F3D0F013D - w_codesstaflnvdp
     *	N978755A0297B11E096CDBA6364A6FDC3 - w_codesstausnvdp
     *	NE7EFCB407E2611DA8F1DA64F3D0F013D - w_codesstaflnvdp  (Client)
     */
    @Test
    public void testGetDoc() {
        final String DOC_COLLECTION_NAME_CLIENT1 = "w_an_rcc_cajur"; // w_an_rcc_texts in prod
        final String DOC_GUID_CLIENT1 = "Iff5a5a987c8f11da9de6e47d6d5aa7a5";
//		String DOC_COLLECTION_NAME_CLIENT1 = "w_codesstaflnvdu";
//		String DOC_GUID_CLIENT1 = "NE7EFCB407E2611DA8F1DA64F3D0F013D";

        final String collectionName = DOC_COLLECTION_NAME_CLIENT1;
        final String docGuid = DOC_GUID_CLIENT1;

        //File tempDir = temporaryFolder.getRoot();
        final File tempDir = new File(System.getProperty("java.io.tmpdir")); // Use if you want to see the files that were created

        final File contentDir = new File(tempDir, "junit_content");
        final File metadataDir = new File(tempDir, "junit_metadata");
        contentDir.mkdirs();
        metadataDir.mkdirs();
        final File contentFile = new File(contentDir, docGuid + ".xml");
        final File metadataFile = new File(metadataDir, "1-" + DOC_COLLECTION_NAME_CLIENT1 + "-" + docGuid + ".xml");
        final Collection<String> guids = new ArrayList<>();
        guids.add(docGuid);
        final GatherDocRequest docRequest =
            new GatherDocRequest(guids, collectionName, contentDir, metadataDir, true, true);

        final GatherResponse gatherResponse = gatherService.getDoc(docRequest);

        log.debug(gatherResponse);
        Assert.assertNotNull(gatherResponse);
        Assert.assertEquals(0, gatherResponse.getErrorCode());
        Assert.assertTrue(contentFile.exists());
        Assert.assertTrue(contentFile.length() > 0);
        Assert.assertTrue(metadataFile.exists());
        Assert.assertTrue(metadataFile.length() > 0);
    }
}
