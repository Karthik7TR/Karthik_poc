package com.thomsonreuters.uscl.ereader.format.text;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class DocumentExtensionAdapterTest {

	DocumentExtensionAdapter adapter;
	
	@Before
	public void setup() throws Exception {
		adapter = new DocumentExtensionAdapter();
	}
	
	@Test
	public void RetrieveContextValueTest() {
		String context = "StaticText";
		String key = "key";

		String expected = "Default value";
		String actual = adapter.RetrieveContextValue(context, key, expected);
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void GenerateSponsorHashTest()
	{
		String actual = adapter.GenerateSponsorHash("sponsorId", "documentGuid");
		Assert.assertEquals("99F96D3F6C62C0017509B67B0C1111DCC5181956BCA4C15B64A1A9D0C703D194", actual);
	}
	
	@Test
	public void IsMatchTest()
	{
		// Null input returns false
		boolean actual = adapter.IsMatch(null, "\\d+");
		Assert.assertEquals(false, actual);
		
		// Empty input returns false
		actual = adapter.IsMatch("", "\\d+");
		Assert.assertEquals(false, actual);
		
		// Match returns true
		actual = adapter.IsMatch("123", "\\d+");
		Assert.assertEquals(true, actual);
		
		// Non-match returns false
		actual = adapter.IsMatch("abc", "\\d+");
		Assert.assertEquals(false, actual);
	}
	
	@Test
	public void ToXmlEncodedStringTest() {
		// Null input
		String actual = adapter.ToXmlEncodedString(null);
		Assert.assertEquals(null, actual);
		
		// Replace LATIN CAPITAL LETTER A WITH CIRCUMFLEX
		actual = adapter.ToXmlEncodedString("a\u00C2b");
		Assert.assertEquals("ab", actual);
		
		// Replace LATIN SMALL LETTER A WITH CIRCUMFLEX
		actual = adapter.ToXmlEncodedString("a\u00E2b");
		Assert.assertEquals("ab", actual);
		
		// Replace EURO SIGN
		actual = adapter.ToXmlEncodedString("a\u20ACb");
		Assert.assertEquals("ab", actual);
		
		// Replace SINGLE LOW-9 QUOTATION MARK
		actual = adapter.ToXmlEncodedString("a\u201Ab");
		Assert.assertEquals("a b", actual);
		
		// Replace EN SPACE
		actual = adapter.ToXmlEncodedString("a\u2002b");
		Assert.assertEquals("a b", actual);
		
		// no replace
		actual = adapter.ToXmlEncodedString("ab");
		Assert.assertEquals("ab", actual);	
	}
}
