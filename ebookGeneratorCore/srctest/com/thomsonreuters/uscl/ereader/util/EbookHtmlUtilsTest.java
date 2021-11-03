package com.thomsonreuters.uscl.ereader.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EbookHtmlUtilsTest {
    @Test
    public void unescapeHtmlStylingTagsAndRemoveOthers_textWithEscapedStylingTagsIsGiven_stringWithUnescapedStylingTagsReturned() {
        final String escapedText = "text &lt;i&gt;text&lt;/i&gt; test &lt;strong&gt;text&lt;/strong&gt;.";
        final String expectedUnescapedText = "text <i>text</i> test <strong>text</strong>.";

        final String actualUnescapedText = EbookHtmlUtils.unescapeHtmlStylingTagsAndRemoveOthers(escapedText);

        assertEquals(expectedUnescapedText, actualUnescapedText);
    }

    @Test
    public void unescapeHtmlStylingTagsAndRemoveOthers_textWithEscapedTagsIsGiven_stringWithRemovedTagsReturned() {
        final String escapedText = "text &lt;div&gt;text&lt;/div&gt; test &lt;strong&gt;text&lt;/strong&gt;.";
        final String expectedUnescapedText = "text text test <strong>text</strong>.";

        final String actualUnescapedText = EbookHtmlUtils.unescapeHtmlStylingTagsAndRemoveOthers(escapedText);

        assertEquals(expectedUnescapedText, actualUnescapedText);
    }

    @Test
    public void unescapeHtmlStylingTagsAndRemoveOthers_nullIsGiven_emptyStringReturned() {
        final String escapedText = null;
        final String expectedUnescapedText = "";

        final String actualUnescapedText = EbookHtmlUtils.unescapeHtmlStylingTagsAndRemoveOthers(escapedText);

        assertEquals(expectedUnescapedText, actualUnescapedText);
    }

    @Test
    public void unescapeHtmlStylingTagsAndRemoveOthers_brokenTagStructureIsGiven_tagExtracted1() {
        final String escapedText = "&lt;div&gt;&lt;i&gt;broken structure &lt;/div&gt;";
        final String expectedUnescapedText = "<i>broken structure </i>";

        final String actualUnescapedText = EbookHtmlUtils.unescapeHtmlStylingTagsAndRemoveOthers(escapedText);

        assertEquals(expectedUnescapedText, actualUnescapedText);
    }

    @Test
    public void unescapeHtmlStylingTagsAndRemoveOthers_brokenTagStructureIsGiven_tagExtracted2() {
        final String escapedText = "&lt;div&gt;broken structure &lt;/i&gt;&lt;/div&gt;";
        final String expectedUnescapedText = "broken structure ";

        final String actualUnescapedText = EbookHtmlUtils.unescapeHtmlStylingTagsAndRemoveOthers(escapedText);

        assertEquals(expectedUnescapedText, actualUnescapedText);
    }

    @Test
    public void unescapeHtmlStylingTagsAndRemoveOthers_brokenTagStructureIsGiven_tagExtracted3() {
        final String escapedText = "&lt;div&gt;&lt;mark&gt;broken structure &lt;/mark&gt;";
        final String expectedUnescapedText = "<mark>broken structure </mark>";

        final String actualUnescapedText = EbookHtmlUtils.unescapeHtmlStylingTagsAndRemoveOthers(escapedText);

        assertEquals(expectedUnescapedText, actualUnescapedText);
    }

    @Test
    public void unescapeHtmlStylingTagsAndRemoveOthers_brokenTagStructureIsGiven_tagExtracted4() {
        final String escapedText = "&lt;b&gt;broken structure &lt;/b&gt;&lt;/div&gt;";
        final String expectedUnescapedText = "<b>broken structure </b>";

        final String actualUnescapedText = EbookHtmlUtils.unescapeHtmlStylingTagsAndRemoveOthers(escapedText);

        assertEquals(expectedUnescapedText, actualUnescapedText);
    }
}
