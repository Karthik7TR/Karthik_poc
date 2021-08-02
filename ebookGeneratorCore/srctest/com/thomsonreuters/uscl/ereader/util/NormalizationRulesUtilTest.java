package com.thomsonreuters.uscl.ereader.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ravi Nandikolla c139353
 *
 */
public final class NormalizationRulesUtilTest {
    @Test
    public void testGetNormalizedCiteForParagraphSymbol() {
        String normalizedCite = "test\u00B6_888";
        normalizedCite = NormalizationRulesUtil.applyCitationNormalizationRules(normalizedCite);
        final String expectedNormalizedCite = "TESTP_888";
        assertTrue(expectedNormalizedCite.equals(normalizedCite));
    }

    @Test
    public void testGetNormalizedCiteForSectionSymbol() {
        String normalizedCite = "test\u00A76666";
        normalizedCite = NormalizationRulesUtil.applyCitationNormalizationRules(normalizedCite);
        final String expectedNormalizedCite = "TESTS6666";
        assertTrue(expectedNormalizedCite.equals(normalizedCite));
    }

    @Test
    public void testGetNormalizedCiteForCaretSymbol() {
        String normalizedCite = "test^999";
        normalizedCite = NormalizationRulesUtil.applyCitationNormalizationRules(normalizedCite);
        final String expectedNormalizedCite = "TEST-999";
        assertTrue(expectedNormalizedCite.equals(normalizedCite));
    }

    @Test
    public void testGetNormalizedCiteForBracketSymbol() {
        String normalizedCite = "teST[39]";
        normalizedCite = NormalizationRulesUtil.applyCitationNormalizationRules(normalizedCite);
        final String expectedNormalizedCite = "TEST(39)";
        assertTrue(expectedNormalizedCite.equals(normalizedCite));
    }

    @Test
    public void testNoSpacesNormalizationRules() {
        String normalizedCite = "te ST [39 ]";
        normalizedCite = NormalizationRulesUtil.pubPageNormalizationRules(normalizedCite);
        final String expectedNormalizedCite = "TEST(39)";
        assertTrue(expectedNormalizedCite.equals(normalizedCite));
    }

    @Test
    public void testTrimNormalizationRules() {
        String normalizedCite = " teST[39] ";
        normalizedCite = NormalizationRulesUtil.pubPageNormalizationRules(normalizedCite);
        final String expectedNormalizedCite = "TEST(39)";
        assertTrue(expectedNormalizedCite.equals(normalizedCite));
    }

    @Test
    public void testSpecialWhiteSpaceNormalizationRules() {
        String label = "This\u2003Space\u2002Label";
        label = NormalizationRulesUtil.whiteSpaceNormalizationRules(label);
        final String expectedLabel = "This Space Label";
        assertTrue(expectedLabel.equals(label));
    }

    @Test
    public void testSpecialHyphenNormalizationRules() {
        String label = "This\u2013Space\u2014Label";
        label = NormalizationRulesUtil.hyphenNormalizationRules(label);
        final String expectedLabel = "This-Space-Label";
        assertTrue(expectedLabel.equals(label));
    }

    @Test
    public void testTOCNormalizationRules() {
        String label = "This\u2003Space\u2014Label";
        label = NormalizationRulesUtil.applyTableOfContentNormalizationRules(label);
        final String expectedLabel = "This Space-Label";
        assertTrue(expectedLabel.equals(label));
    }

    @Test
    public void testNormalizeNoDashesNoWhitespaces() {
        assertEquals("FLETCHERFRMCH1REF", NormalizationRulesUtil.normalizeNoDashesNoWhitespaces("FLETCHER-FRM CH 1 REF"));
    }

    @Test
    public void testNormalizeCiteExtraParagraphSign() {
        assertEquals("KYDAMSSS5:1", NormalizationRulesUtil.normalizeCiteExtra3ParagraphSigns("KYDAM S 5:1"));
    }

    @Test
    public void testNormalizeCiteTrailingDot() {
        assertEquals("KYDAMs5:4.", NormalizationRulesUtil.normalizeCiteTrailingDot("KYDAM s 5:4"));
    }

    @Test
    public void testNormalizeCiteTrailingDot2() {
        assertEquals("KYDAMs5:5..", NormalizationRulesUtil.normalizeCiteTrailingDot2("KYDAM s 5:5"));
    }

    @Test
    public void testCiteExtra2ParagraphSignsTrailingDot() {
        assertEquals("KYDAMSS12:24.", NormalizationRulesUtil.normalizeCiteExtra2ParagraphSignsTrailingDot("KYDAM S 12:24"));
    }

    @Test
    public void testNormalizeNoDashesNoWhitespacesNull() {
        assertNull(NormalizationRulesUtil.normalizeNoDashesNoWhitespaces(null));
    }

    @Test
    public void testNormalizeThirdLineCite() {
        String wNormalizedCite = "WESTSFEDFORMSBANKRUPTCYCOURTSs1:9";
        assertEquals(NormalizationRulesUtil.applyCitationNormalizationRules(wNormalizedCite),
                NormalizationRulesUtil.normalizeThirdLineCite("West&apos;s Fed. Forms, Bankruptcy Courts § 1:9 (5th ed.)"));
    }

    @Test
    public void testNormalizeThirdLineCite2() {
        String wNormalizedCite = "WESTSFEDFORMSCOURTSOFAPPEALSs2:41";
        assertEquals(NormalizationRulesUtil.applyCitationNormalizationRules(wNormalizedCite),
                NormalizationRulesUtil.normalizeThirdLineCite("West&apos;s Fed. Forms, Courts of Appeals § 2:41"));
    }

    @Test
    public void testNormalizeThirdLineCiteKeepingDecimalDot() {
        String wNormalizedCite = "CONNPRACTRIALPRACTICEs1.29";
        assertEquals(NormalizationRulesUtil.applyCitationNormalizationRules(wNormalizedCite),
                NormalizationRulesUtil.normalizeThirdLineCiteKeepingDecimalDot("Conn. Prac., Trial Practice § 1.29 (2d ed.)"));
    }

    @Test
    public void testNormalizeThirdLineCiteKeepingDecimalDot2() {
        String wNormalizedCite = "CONNPRACCIVILPRACTICEFORMSForm504.9";
        assertEquals(NormalizationRulesUtil.applyCitationNormalizationRules(wNormalizedCite),
                NormalizationRulesUtil.normalizeThirdLineCiteKeepingDecimalDot("Conn. Prac., Civil Practice Forms Form 504.9 (4th ed.)"));
    }

    @Test
    public void testNormalizeThirdLineCiteKeepingDecimalDot3() {
        String wNormalizedCite = "CONNPRACCIVILPRACTICEFORMSForm504.1-FF";
        assertEquals(NormalizationRulesUtil.applyCitationNormalizationRules(wNormalizedCite),
                NormalizationRulesUtil.normalizeThirdLineCiteKeepingDecimalDot("Conn. Prac., Civil Practice Forms Form 504.1-FF (4th ed.)"));
    }

    @Test
    public void testNormalizeThirdLineCiteKeepingDecimalDot4() {
        String wNormalizedCite = "CONNPRACCIVILPRACTICEFORMSForm504.1-DD-1";
        assertEquals(NormalizationRulesUtil.applyCitationNormalizationRules(wNormalizedCite),
                NormalizationRulesUtil.normalizeThirdLineCiteKeepingDecimalDot("Conn. Prac., Civil Practice Forms Form 504.1-DD-1 (4th ed.)"));
    }

    @Test
    public void testNormalizeThirdLineCiteNull() {
        assertNull(NormalizationRulesUtil.normalizeThirdLineCite(null));
    }
}
