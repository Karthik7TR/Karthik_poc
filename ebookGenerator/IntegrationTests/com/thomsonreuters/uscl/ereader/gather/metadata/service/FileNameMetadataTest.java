package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.FileNameMetadata;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileNameMetadataTest {
    private static final String METADATA_FILE_NAME_FULL = "4-w_codesstaaznvdp-N5D8487409E3411E183F7C076EF385880-001305.xml";
    private static final String METADATA_FILE_NAME_EMPTY = "6-w_codesstaaznvdp-Ic27dc047746611ec91e2c71719220aed.xml";
    private static final String COLLECTION_NAME = "w_codesstaaznvdp";
    private static final String DEFAULT_COLLECTION_NAME = "defaultCollectionName";
    private static final String DOC_UUID_SHORT = "Ic27dc047746611ec91e2c71719220aed";
    private static final String DOC_UUID_LONG_305 = "N5D8487409E3411E183F7C076EF385880-001305";
    private static final String DOC_NAME = "Ic27dc047746611ec91e2c71719220aed.xml";
    private static final String DOC_UUID = "Ic27dc047746611ec91e2c71719220aed";

    @Test
    public void extractDocCollectionNameAndDocUuidLong() {
        testCollectionAndDocUuidExtraction(METADATA_FILE_NAME_FULL, COLLECTION_NAME, DOC_UUID_LONG_305);
    }

    @Test
    public void extractDocCollectionNameAndDocUuidShort() {
        testCollectionAndDocUuidExtraction(METADATA_FILE_NAME_EMPTY, COLLECTION_NAME, DOC_UUID_SHORT);
    }

    @Test
    public void extractDocUuidNoCollectionName() {
        testCollectionAndDocUuidExtraction(DOC_NAME, DEFAULT_COLLECTION_NAME, DOC_UUID);
    }

    private void testCollectionAndDocUuidExtraction(final String fileName, final String expectedCollectionName, final String expectedDocUuid) {
        File metaDataFile = mock(File.class);
        when(metaDataFile.getName()).thenReturn(fileName);
        FileNameMetadata fileNameMetadata = new FileNameMetadata(metaDataFile);

        assertEquals(expectedCollectionName, fileNameMetadata.getCollectionName());
        assertEquals(expectedDocUuid, fileNameMetadata.getDocUuid());
    }
}
