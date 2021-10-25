package com.thomsonreuters.uscl.ereader.core.book.model;

import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class TitleIdTest {
    private static final String TITLE_ID = "uscl/an/title";
    private static final String PART_01 = TITLE_ID;
    private static final String PART_02 = TITLE_ID + "_pt2";
    private static final String PART_03 = TITLE_ID + "_pt3";
    private static final String PART_20 = TITLE_ID + "_pt20";

    @Test
    public void testTitleIdSorting() {
        List<TitleId> splitTitleIds = Stream.of(PART_03, PART_20, PART_01, PART_02)
                .map(TitleId::new)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(PART_01, splitTitleIds.get(0).getTitleId());
        assertEquals(PART_02, splitTitleIds.get(1).getTitleId());
        assertEquals(PART_03, splitTitleIds.get(2).getTitleId());
        assertEquals(PART_20, splitTitleIds.get(3).getTitleId());
    }
}
