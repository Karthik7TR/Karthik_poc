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
		String normalizedCite = "test\u00B6_888";
		normalizedCite =  CitationNormalizationRulesUtil.applyNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TESTP_888";
		Assert.assertTrue(expectedNormalizedCite.equals(normalizedCite));
		
	}
	
	@Test
	public void testGetNormalizedCiteForSectionSymbol() throws Exception {
		String normalizedCite = "test\u00A76666";
		normalizedCite =  CitationNormalizationRulesUtil.applyNormalizationRules(normalizedCite);
		String expectedNormalizedCite = "TESTS6666";
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
