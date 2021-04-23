package com.thomsonreuters.uscl.ereader.format.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.thomsonreuters.uscl.ereader.core.service.DateProvider;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class GenerateDocumentDataBlockServiceTest {
    private static final String COLLECTION_NAME = "TEST_COLLECTION";
    private static final String TITLE_ID = "TEST_TITLE_ID";
    private static final String DOC_GUID = "TEST_DOC_GUID";
    private static final long JOB_INSTANCE_ID = 1L;
    private static final String DATA_BLOCK = "<document-data><collection>TEST_COLLECTION</collection><datetime>20210423120000</datetime><versioned>False</versioned><doc-type></doc-type><cite></cite></document-data>";

    @InjectMocks
    private GenerateDocumentDataBlockServiceImpl service;
    @Mock
    private DocMetadataService mockDocMetadataService;
    @Mock
    private DateProvider dateProvider;

    @Before
    public void setUp() {
        when(dateProvider.getDate()).thenReturn(getDate());
        when(mockDocMetadataService.findDocMetadataByPrimaryKey(any(), any(), any())).thenReturn(getDocMetadata());
    }

    @Test
    public void testGetDocumentDataBlockAsStream() throws IOException, EBookFormatException {
        try (InputStream docBlockStream = service.getDocumentDataBlockAsStream(TITLE_ID, JOB_INSTANCE_ID, DOC_GUID)) {
            verifyDataBlock(docBlockStream);
        }
    }

    private Date getDate() {
        return new GregorianCalendar(2021, Calendar.APRIL, 23).getTime();
    }

    private DocMetadata getDocMetadata() {
        final DocMetadata docMetaData = new DocMetadata();
        docMetaData.setCollectionName(COLLECTION_NAME);
        return docMetaData;
    }

    private void verifyDataBlock(final InputStream docBlockStream) throws IOException {
        String dataBlock = IOUtils.toString(docBlockStream);
        assertEquals(DATA_BLOCK, dataBlock);
    }
}
