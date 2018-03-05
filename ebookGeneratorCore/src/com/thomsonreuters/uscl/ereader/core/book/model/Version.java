package com.thomsonreuters.uscl.ereader.core.book.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class Version implements Comparable<Version> {
    public static final String VERSION_PREFIX = "v";
    private static final Pattern VERSION_PATTREN = Pattern.compile("v(\\d+)\\.(\\d+)");

    private Integer majorVersion;
    private Integer minorVersion;

    public Version(@NotNull final String version) {
        Assert.notNull(version);
        final Matcher matcher = VERSION_PATTREN.matcher(version);
        Assert.isTrue(matcher.matches(), "Version should match pattern: v<major_version>.<minor_version>");

        majorVersion = Integer.valueOf(matcher.group(1));
        minorVersion = Integer.valueOf(matcher.group(2));
    }

    public Version(final int majorVersion, final int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public int getMajorNumber() {
        return majorVersion;
    }

    public int getMinorNumber() {
        return minorVersion;
    }

    @NotNull
    public String getMajorVersion() {
        return VERSION_PREFIX + majorVersion;
    }

    @NotNull
    public String getFullVersion() {
        return new StringBuilder(VERSION_PREFIX).append(majorVersion).append(".").append(minorVersion).toString();
    }

    @NotNull
    public String getVersionWithoutPrefix() {
        return new StringBuilder().append(majorVersion).append(".").append(minorVersion).toString();
    }

    @NotNull
    public String getVersionForFilePattern() {
        return new StringBuilder().append("_").append(majorVersion).append("_").append(minorVersion).toString();
    }

    public boolean isNewMajorVersion() {
        return minorVersion == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((majorVersion == null) ? 0 : majorVersion.hashCode());
        result = prime * result + ((minorVersion == null) ? 0 : minorVersion.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Version other = (Version) obj;
        if (majorVersion == null) {
            if (other.majorVersion != null)
                return false;
        } else if (!majorVersion.equals(other.majorVersion))
            return false;
        if (minorVersion == null) {
            if (other.minorVersion != null)
                return false;
        } else if (!minorVersion.equals(other.minorVersion))
            return false;
        return true;
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
