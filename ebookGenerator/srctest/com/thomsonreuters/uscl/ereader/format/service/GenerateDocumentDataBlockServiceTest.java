package com.thomsonreuters.uscl.ereader.format.service;

import java.io.InputStream;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *  Tests Document Data block.
 *
 *  @author Mahendra Survase (u0105927)
 */
public final class GenerateDocumentDataBlockServiceTest {
    private GenerateDocumentDataBlockServiceImpl service;
    private DocMetadataService mockDocMetadataService;

    /**
     * Generic setup for all the tests.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
        service = new GenerateDocumentDataBlockServiceImpl();
        service.setDocMetadataService(mockDocMetadataService);
    }

    /**
     * positive test
     *
     */
    @Test
    public void testGetDocumentDataBlockAsStream() {
        // test specific setup.
        final String titleId = "TEST_TITLE_ID";
        final Long jobId = 10L;
        final String docGuid = "TEST_DOC_GUID";

        final DocMetadata docMetaData = new DocMetadata();
        docMetaData.setCollectionName("TEST_COLLECTION");

        EasyMock.expect(mockDocMetadataService.findDocMetadataByPrimaryKey(titleId, jobId, docGuid))
            .andReturn(docMetaData);
        EasyMock.replay(mockDocMetadataService);

        InputStream docBlockStream = null;
        try {
            docBlockStream = service.getDocumentDataBlockAsStream(titleId, jobId, docGuid);
        } catch (final EBookFormatException e) {
            e.printStackTrace();
        }
        EasyMock.verify(mockDocMetadataService);
        Assert.assertNotNull(docBlockStream);
    }
}
