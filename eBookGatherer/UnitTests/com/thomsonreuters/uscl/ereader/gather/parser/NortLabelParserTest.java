package com.thomsonreuters.uscl.ereader.gather.parser;

import com.thomsonreuters.uscl.ereader.gather.exception.NortLabelParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class NortLabelParserTest {
    private NortLabelParser parser;

    @Before
    public void setUp() {
        parser = new NortLabelParser();
    }

    @Test
    public void successTest() throws NortLabelParseException {
        final String text = "<heading>Test 123 text</heading><section>should not be in there</section>";
        final String expected = "Test 123 text";

        final String result = parser.parse(text);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void levelOneTest() throws NortLabelParseException {
        final String text = "One <heading>Test 123 text</heading><section>should not be in there</section> One";
        final String expected = "One Test 123 text One";

        final String result = parser.parse(text);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void levelTwoTest() throws NortLabelParseException {
        final String text =
            "Two <heading>Test 123 text</heading><section><cite.query>should not</cite.query> be in there</section> Two";
        final String expected = "Two Test 123 text Two";

        final String result = parser.parse(text);
        Assert.assertEquals(expected, result);
    }
}
