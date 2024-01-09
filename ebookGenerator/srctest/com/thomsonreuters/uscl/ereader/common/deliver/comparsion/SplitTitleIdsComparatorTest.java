package com.thomsonreuters.uscl.ereader.common.deliver.comparsion;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

public final class SplitTitleIdsComparatorTest {
    private static final String ROOT_TITLE_ID = "uscl/an/some_title";
    private static final String PART_TWO = ROOT_TITLE_ID + "_pt2";
    private static final String PART_THREE = ROOT_TITLE_ID + "_pt3";

    private Comparator<String> comparator;

    @Before
    public void onTestSetUp() {
        comparator = new SplitTitleIdsComparator(ROOT_TITLE_ID);
    }

    @Test
    public void shouldReturnLessThenZeroValue() {
        //given
        //when
        final int result = comparator.compare(PART_THREE, PART_TWO);
        //then
        assertThat(result, lessThan(0));
    }

    @Test
    public void shouldReturnGreaterThenZeroValue() {
        //given
        //when
        final int result = comparator.compare(ROOT_TITLE_ID, PART_TWO);
        //then
        assertThat(result, greaterThan(0));
    }

    @Test
    public void shouldReturnZeroValue() {
        //given
        //when
        final int result = comparator.compare(PART_TWO, PART_TWO);
        //then
        assertThat(result, equalTo(0));
    }
}
