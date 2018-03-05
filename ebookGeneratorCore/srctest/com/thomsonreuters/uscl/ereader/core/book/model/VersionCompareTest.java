package com.thomsonreuters.uscl.ereader.core.book.model;

import static com.thomsonreuters.uscl.ereader.core.book.BookMatchers.version;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public final class VersionCompareTest {
    @Test
    public void testVersionCompare() {
        //given
        final List<Version> versions =
            Arrays.asList(version("v1.3"), version("v1.0"), version("v11.34"), version("v7.15"));
        //when
        Collections.sort(versions);
        //then
        assertThat(versions, contains(version("v1.0"), version("v1.3"), version("v7.15"), version("v11.34")));
    }
}
