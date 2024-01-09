package com.thomsonreuters.uscl.ereader.core.book.model;

import java.math.BigInteger;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

@EqualsAndHashCode(of = {"majorVersion", "minorVersion"})
public class Version implements Comparable<Version> {
    public static final String VERSION_PREFIX = "v";
    private static final String MAJOR = "majorV";
    private static final String MINOR = "minorV";
    private static final Pattern VERSION_PATTERN = Pattern.compile(String.format("v(?<%s>\\d+)(\\.(?<%s>\\d+))?", MAJOR, MINOR));

    @Getter
    private String version;
    private BigInteger majorVersion;
    private BigInteger minorVersion;

    @SneakyThrows
    public Version(@NotNull final String version) {
        Assert.notNull(version);
        this.version = version;
        final Matcher matcher = VERSION_PATTERN.matcher(version);
        Assert.isTrue(matcher.matches(), "Version should match pattern: v<major_version>.[<minor_version>]");
        majorVersion = new BigInteger(matcher.group(MAJOR));
        minorVersion = Optional.ofNullable(matcher.group(MINOR))
            .map(BigInteger::new)
            .orElse(BigInteger.ZERO);
    }

    public Version(final BigInteger majorVersion, final BigInteger minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.version = getFullVersion();
    }

    public BigInteger getMajorNumber() {
        return majorVersion;
    }

    public BigInteger getMinorNumber() {
        return minorVersion;
    }

    @NotNull
    public String getMajorVersion() {
        return VERSION_PREFIX + majorVersion;
    }

    @NotNull
    public String getFullVersion() {
        return VERSION_PREFIX + majorVersion + "." + minorVersion;
    }

    @NotNull
    public String getVersionWithoutPrefix() {
        return majorVersion + "." + minorVersion;
    }

    @NotNull
    public String getVersionForFilePattern() {
        return "_" + majorVersion + "_" + minorVersion;
    }

    public boolean isNewMajorVersion() {
        return minorVersion.equals(BigInteger.ZERO);
    }

    @Override
    public String toString() {
        return getFullVersion();
    }

    @Override
    public int compareTo(final Version o) {
        final int majorCompare = majorVersion.compareTo(o.majorVersion);
        return (majorCompare == 0) ? minorVersion.compareTo(o.minorVersion) : majorCompare;
    }
}
