package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class IssnValidatorTest {
    private static final String ISSN_VALID = "0317-8471";
    private static final String ISSN_X_VALID = "1050-124X";
    private static final String ISSN_X_FORMAT_ERROR = "202020-3X";
    private static final String ISSN_FORMAT_ERROR_1 = "202020-30";
    private static final String ISSN_FORMAT_ERROR_2 = "202----30";
    private static final String ISSN_FORMAT_ERROR_3 = "ISSN 0317-8471";
    private static final String ISSN_CHECKSUM_ERROR = "2020-3030";
    private static final String ISSN_X_CHECKSUM_ERROR = "2020-303X";

    private IssnValidator issnValidator;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        issnValidator = new IssnValidator();
    }

    @Test
    public void testIssn() {
        issnValidator.validateIssn(ISSN_VALID);
    }

    @Test
    public void testIssnX() {
        issnValidator.validateIssn(ISSN_X_VALID);
    }

    @Test
    public void testIssnFormatErrorX() {
        thrown.expectMessage(IssnValidator.ISSN_FORMAT_ERROR);
        issnValidator.validateIssn(ISSN_X_FORMAT_ERROR);
    }

    @Test
    public void testIssnFormatError1() {
        thrown.expectMessage(IssnValidator.ISSN_FORMAT_ERROR);
        issnValidator.validateIssn(ISSN_FORMAT_ERROR_1);
    }

    @Test
    public void testIssnFormatError2() {
        thrown.expectMessage(IssnValidator.ISSN_FORMAT_ERROR);
        issnValidator.validateIssn(ISSN_FORMAT_ERROR_2);
    }

    @Test
    public void testIssnFormatError3() {
        thrown.expectMessage(IssnValidator.ISSN_FORMAT_ERROR);
        issnValidator.validateIssn(ISSN_FORMAT_ERROR_3);
    }

    @Test
    public void testIssnChecksumError() {
        thrown.expectMessage(IssnValidator.ISSN_CHECKSUM_ERROR);
        issnValidator.validateIssn(ISSN_CHECKSUM_ERROR);
    }

    @Test
    public void testIssnChecksumErrorX() {
        thrown.expectMessage(IssnValidator.ISSN_CHECKSUM_ERROR);
        issnValidator.validateIssn(ISSN_X_CHECKSUM_ERROR);
    }
}
