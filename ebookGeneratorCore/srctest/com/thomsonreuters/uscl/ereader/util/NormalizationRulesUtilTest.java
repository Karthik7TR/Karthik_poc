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
    public void testNormalizeThirdLineCiteNull() {
        assertNull(NormalizationRulesUtil.normalizeThirdLineCite(null));
    }
}
