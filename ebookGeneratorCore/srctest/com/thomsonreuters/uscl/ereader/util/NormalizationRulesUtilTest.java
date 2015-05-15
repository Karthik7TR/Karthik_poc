/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ravi Nandikolla c139353
 *
 */
public class NormalizationRulesUtilTest 
{
	@Test
	public void testGetNormalizedCiteForParagraphSymbol() throws Exception {
		String normalizedCite = "test\u00B6_888";
		normalizedCite =  NormalizationRulesUtil.applyCitationNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TESTP_888";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
		
	}
	
	@Test
	public void testGetNormalizedCiteForSectionSymbol() throws Exception {
		String normalizedCite = "test\u00A76666";
		normalizedCite =  NormalizationRulesUtil.applyCitationNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TESTS6666";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
		
	}
	
	@Test
	public void testGetNormalizedCiteForCaretSymbol() throws Exception {
		String normalizedCite = "test^999";
		normalizedCite =  NormalizationRulesUtil.applyCitationNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TEST-999";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
		
	}
	
	@Test
	public void testGetNormalizedCiteForBracketSymbol() throws Exception {
		String normalizedCite = "teST[39]";
		normalizedCite =  NormalizationRulesUtil.applyCitationNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TEST(39)";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
		
	}
	
	@Test
	public void testNoSpacesNormalizationRules() throws Exception {
		String normalizedCite = "te ST [39 ]";
		normalizedCite =  NormalizationRulesUtil.pubPageNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TEST(39)";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
	}
	
	@Test
	public void testTrimNormalizationRules() throws Exception {
		String normalizedCite = " teST[39] ";
		normalizedCite =  NormalizationRulesUtil.pubPageNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TEST(39)";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
	}
	
	@Test
	public void testSpecialWhiteSpaceNormalizationRules() {
		String label = "This\u2003Space\u2002Label";
		label = NormalizationRulesUtil.whiteSpaceNormalizationRules(label);
		String expectedLabel = "This Space Label";
		Assert.assertTrue(expectedLabel.equals(label));
	}
	
	@Test
	public void testSpecialHyphenNormalizationRules() {
		String label = "This\u2013Space\u2014Label";
		label = NormalizationRulesUtil.hyphenNormalizationRules(label);
		String expectedLabel = "This-Space-Label";
		Assert.assertTrue(expectedLabel.equals(label));
	}
	
	@Test
	public void testTOCNormalizationRules() {
		String label = "This\u2003Space\u2014Label";
		label = NormalizationRulesUtil.applyTableOfContentNormalizationRules(label);
		String expectedLabel = "This Space-Label";
		Assert.assertTrue(expectedLabel.equals(label));
	}
}
