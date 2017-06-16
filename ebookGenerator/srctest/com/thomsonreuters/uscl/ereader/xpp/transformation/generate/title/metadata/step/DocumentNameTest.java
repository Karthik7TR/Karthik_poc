package com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.junit.Test;

/**
 *
 */
public final class DocumentNameTest
{
    private static final String DOC_FAMILY_UUID = UUID.randomUUID().toString().replace("-", "");
    private static final int ORDER = 5;
    private static final String CORRECT_FILE_NAME = "SOME_File-name_1.DIVXML_" + ORDER + "_" + DOC_FAMILY_UUID + ".html";
    private static final String FILE_NAME_WITH_INVALID_ORDER = "SOME_File-name_1.DIVXML_5five_" + DOC_FAMILY_UUID + ".html";
    private static final String FILE_NAME_WITHOUT_ORDER = "SOME_File-name_1.DIVXML_" + DOC_FAMILY_UUID + ".html";
    private static final String FILE_NAME_WITH_INVALID_UUID = "SOME_File-name_1.DIVXML_" + ORDER + "_" + "64723846hgfs-dsfsdfsdf23" + ".html";
    private static final String FILE_NAME_WITHOUT_UUID = "SOME_File-name_1.DIVXML_" + ORDER + "_" + ".html";

    @Test
    public void testGetDocOrder()
    {
        final DocumentName documentName = new DocumentName(CORRECT_FILE_NAME);
        assertThat(documentName.getOrder(), equalTo(ORDER));
    }

    @Test
    public void testGetDocFamilyGuid()
    {
        final DocumentName documentName = new DocumentName(CORRECT_FILE_NAME);
        assertThat(documentName.getDocFamilyGuid(), equalTo(DOC_FAMILY_UUID));
    }

    @Test
    public void testGetOriginalFileName()
    {
        final DocumentName documentName = new DocumentName(CORRECT_FILE_NAME);
        assertThat(documentName.getOriginalFileName(), equalTo(CORRECT_FILE_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileNameWithInvalidOrder()
    {
        new DocumentName(FILE_NAME_WITH_INVALID_ORDER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileNameWithoutOrder()
    {
        new DocumentName(FILE_NAME_WITHOUT_ORDER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileNameWithInvalidUuid()
    {
        new DocumentName(FILE_NAME_WITH_INVALID_UUID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileNameWithoutUuid()
    {
        new DocumentName(FILE_NAME_WITHOUT_UUID);
    }
}
