package com.thomsonreuters.uscl.ereader.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Ravi Nandikolla c139353
 *
 */
public class CitationNormalizationRulesUtilTest 
{
	@Test
	public void testGetNormalizedCiteForParagraphSymbol() throws Exception {
		String normalizedCite = "test�_888";
		normalizedCite =  CitationNormalizationRulesUtil.applyNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TESTp_888";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
		
	}
	
	@Test
	public void testGetNormalizedCiteForSectionSymbol() throws Exception {
		String normalizedCite = "test�6666";
		normalizedCite =  CitationNormalizationRulesUtil.applyNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TESTs6666";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
		
	}
	
	@Test
	public void testGetNormalizedCiteForCaretSymbol() throws Exception {
		String normalizedCite = "test^999";
		normalizedCite =  CitationNormalizationRulesUtil.applyNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TEST-999";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
		
	}
	
	@Test
	public void testGetNormalizedCiteForBracketSymbol() throws Exception {
		String normalizedCite = "teST[39]";
		normalizedCite =  CitationNormalizationRulesUtil.applyNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TEST(39)";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
		
	}
}
