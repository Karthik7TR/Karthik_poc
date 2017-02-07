package com.thomsonreuters.uscl.ereader.gather.parser;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.gather.exception.NortLabelParseException;

public class NortLabelParserTest
{
    private NortLabelParser parser = null;

    @Before
    public void setUp()
    {
        parser = new NortLabelParser();
    }

    @Test
    public void successTest() throws NortLabelParseException
    {
        String text = "<heading>Test 123 text</heading><section>should not be in there</section>";
        String expected = "Test 123 text";

        String result = parser.parse(text);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void levelOneTest() throws NortLabelParseException
    {
        String text = "One <heading>Test 123 text</heading><section>should not be in there</section> One";
        String expected = "One Test 123 text One";

        String result = parser.parse(text);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void levelTwoTest() throws NortLabelParseException
    {
        String text =
            "Two <heading>Test 123 text</heading><section><cite.query>should not</cite.query> be in there</section> Two";
        String expected = "Two Test 123 text Two";

        String result = parser.parse(text);
        Assert.assertEquals(expected, result);
    }
}
