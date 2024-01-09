package com.thomsonreuters.uscl.ereader.core.book.model;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public final class PrintComponentVersionTest {
    private static final String VERSION_10 = "10.2.5";
    private static final String VERSION_1 = "1.1.1";
    private static final String VERSION_9 = "9.0.1";

    @Test
    public void testVersionParse() {
        final PrintComponentVersion version = new PrintComponentVersion(VERSION_10);
        assertEquals(VERSION_10, version.toString());
    }

    @Test
    public void testVersionCompare() {
        final List<PrintComponentVersion> versions = Stream.of(
            VERSION_1,
            VERSION_10,
            VERSION_9
            ).map(PrintComponentVersion::new).sorted().collect(Collectors.toList());

        validateVersionOrder(VERSION_1, 0, versions);
        validateVersionOrder(VERSION_9, 1, versions);
        validateVersionOrder(VERSION_10, 2, versions);
    }

    private void validateVersionOrder(final String version, final int position, final List<PrintComponentVersion> versions) {
        assertEquals(version, versions.get(position).toString());
    }

}
