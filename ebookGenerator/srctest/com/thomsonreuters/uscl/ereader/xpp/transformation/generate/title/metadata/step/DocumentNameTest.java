package com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.junit.Test;

/**
 *
 */
public final class DocumentNameTest {
    private static final String DOC_FAMILY_UUID = UUID.randomUUID().toString().replace("-", "");
    private static final int ORDER = 5;

    private static final String BASE_FILE_NAME = "SOME_File-name_1.DIVXML";
    private static final String CORRECT_FILE_NAME = BASE_FILE_NAME + "_" + ORDER + "_" + DOC_FAMILY_UUID + ".html";
    private static final String FILE_NAME_WITH_INVALID_ORDER = BASE_FILE_NAME + "_5five_" + DOC_FAMILY_UUID + ".html";
    private static final String FILE_NAME_WITHOUT_ORDER = BASE_FILE_NAME + "_" + DOC_FAMILY_UUID + ".html";
    private static final String FILE_NAME_WITH_INVALID_UUID =
        BASE_FILE_NAME + "_" + ORDER + "_" + "64723846hgfs-dsfsdfsdf23" + ".html";
    private static final String FILE_NAME_WITHOUT_UUID = BASE_FILE_NAME + "_" + ORDER + "_" + ".html";
    private static final String CORRECT_PART_MAIN_FILE_NAME =
        BASE_FILE_NAME + "_" + ORDER + "_" + "main" + "_" + DOC_FAMILY_UUID + ".part";
    private static final String CORRECT_PART_FOOTNOTES_FILE_NAME =
        BASE_FILE_NAME + "_" + ORDER + "_" + "footnotes" + "_" + DOC_FAMILY_UUID + ".part";
    private static final String CORRECT_PART_MAIN_FILE_NAME_WRONG_TYPE =
        BASE_FILE_NAME + "_" + ORDER + "_" + "wrongType" + "_" + DOC_FAMILY_UUID + ".part";

    @Test
    public void testGetBaseFileName() {
        final DocumentName documentName = new DocumentName(CORRECT_FILE_NAME);
        assertThat(documentName.getBaseName(), equalTo(BASE_FILE_NAME));
    }

    @Test
    public void testGetDocOrder() {
        final DocumentName documentName = new DocumentName(CORRECT_FILE_NAME);
        assertThat(documentName.getOrder(), equalTo(ORDER));
    }

    @Test
    public void testGetDocFamilyUuid() {
        final DocumentName documentName = new DocumentName(CORRECT_FILE_NAME);
        assertThat(documentName.getDocFamilyUuid(), equalTo(DOC_FAMILY_UUID));
    }

    @Test
    public void testGetOriginalFileName() {
        final DocumentName documentName = new DocumentName(CORRECT_FILE_NAME);
        assertThat(documentName.getOriginalFileName(), equalTo(CORRECT_FILE_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileNameWithInvalidOrder() {
        new DocumentName(FILE_NAME_WITH_INVALID_ORDER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileNameWithoutOrder() {
        new DocumentName(FILE_NAME_WITHOUT_ORDER);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileNameWithInvalidUuid() {
        new DocumentName(FILE_NAME_WITH_INVALID_UUID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFileNameWithoutUuid() {
        new DocumentName(FILE_NAME_WITHOUT_UUID);
    }

    @Test
    public void testPartMainFileName() {
        final DocumentName documentName = new DocumentName(CORRECT_PART_MAIN_FILE_NAME);
        assertThat(documentName.getBaseName(), equalTo(BASE_FILE_NAME));
        assertThat(documentName.getOrder(), equalTo(ORDER));
        assertThat(documentName.getPartType(), equalTo(PartType.MAIN));
        assertThat(documentName.getDocFamilyUuid(), equalTo(DOC_FAMILY_UUID));
    }

    @Test
    public void testPartFootnotesFileName() {
        final DocumentName documentName = new DocumentName(CORRECT_PART_FOOTNOTES_FILE_NAME);
        assertThat(documentName.getPartType(), equalTo(PartType.FOOTNOTE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPartFileNameWithWrongType() {
        new DocumentName(CORRECT_PART_MAIN_FILE_NAME_WRONG_TYPE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTypeFromDocumentNameWithoutType() {
        new DocumentName(CORRECT_FILE_NAME).getPartType();
    }
}
