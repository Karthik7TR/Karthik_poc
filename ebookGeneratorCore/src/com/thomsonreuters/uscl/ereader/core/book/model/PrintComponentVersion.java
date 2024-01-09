package com.thomsonreuters.uscl.ereader.core.book.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class PrintComponentVersion implements Comparable<PrintComponentVersion> {
    private static final String MAJOR_VERSION = "majorVersion";
    private static final String MINOR_VERSION = "minorVersion";
    private static final String PRINT_COMPONENT_VERSION = "printComponentVersion";
    private static final Pattern VERSION_PATTERN = Pattern.compile(String.format("(?<%s>\\d+)\\.(?<%s>\\d+)\\.(?<%s>\\d+)", MAJOR_VERSION, MINOR_VERSION, PRINT_COMPONENT_VERSION));

    private Integer majorVersion;
    private Integer minorVersion;
    private Integer printComponentVersion;

    public PrintComponentVersion(@NotNull final String version) {
        final Matcher matcher = VERSION_PATTERN.matcher(version);
        Assert.isTrue(matcher.matches(), "Version should match pattern: v<major_version>.<minor_version>");

        majorVersion = Integer.valueOf(matcher.group(MAJOR_VERSION));
        minorVersion = Integer.valueOf(matcher.group(MINOR_VERSION));
        printComponentVersion = Integer.valueOf(matcher.group(PRINT_COMPONENT_VERSION));
    }

    @Override
    public int compareTo(final PrintComponentVersion o) {
        final int majorCompare = majorVersion.compareTo(o.majorVersion);
        if (majorCompare == 0) {
            final int minorCompare = minorVersion.compareTo(o.minorVersion);
            return (minorCompare == 0) ? printComponentVersion.compareTo(o.printComponentVersion) : minorCompare;
        }
        return majorCompare;
    }

    @Override
    public String toString() {
        return majorVersion + "." + minorVersion + "." + printComponentVersion;
    }
}
