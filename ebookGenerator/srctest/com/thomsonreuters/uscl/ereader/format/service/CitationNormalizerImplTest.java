package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public final class CitationNormalizerImplTest {
    private CitationNormalizer sut = new CitationNormalizerImpl();
    private static final String EXPECTED_RESULT = "NJ ST 9:9-1";

    @Test
    public void shouldGetFirstCitationFromBothRepealedRange() {
        test("NJ ST 9:9-1 REPEALED TO 9:9-3 REPEALED", EXPECTED_RESULT);
    }

    @Test
    public void shouldNotSplitWordsStartingWithTo() {
        test("NJ ST 9:9-1 TOMATO REPEALED", EXPECTED_RESULT + " TOMATO");
    }

    @Test
    public void shouldGetFirstCitationFromRepealedRange() {
        test("NJ ST 9:9-1 TO 9:9-3 REPEALED", EXPECTED_RESULT);
    }

    @Test
    public void shouldGetFirstCitationFromRange() {
        test("NJ ST 9:9-1 TO 9:9-3", EXPECTED_RESULT);
    }

    @Test
    public void shouldNotChangeSingleCitation() {
        test("NJ ST 9:9-1", EXPECTED_RESULT);
    }

    @Test
    public void shouldRemoveRepealedWithSpace() {
        test("NJ ST 9:9-1 REPEALED", EXPECTED_RESULT);
    }

    @Test
    public void shouldRemoveRepealedWithoutSpace() {
        test("NJ ST 9:9-1REPEALED", EXPECTED_RESULT);
    }

    @Test
    public void shouldNotBreakNotMatchingCitation() {
        test("NJ st$ 9:9-1", "NJ ST$ 9:9-1");
    }

    private void test( String input, String expected) {
        //when
        final String result = sut.normalizeCitation(input);
        //then
        assertEquals(expected, result);
    }
}
