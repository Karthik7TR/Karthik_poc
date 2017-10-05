package com.thomsonreuters.uscl.ereader.core.book.model;

import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class Version {
    public static final String VERSION_PREFIX = "v";
    private static final Pattern VERSION_PATTREN = Pattern.compile("v\\d+\\.\\d+");

    private int majorVersion;
    private int minorVersion;

    public Version(@NotNull final String version) {
        Assert.notNull(version);
        Assert.isTrue(
            VERSION_PATTREN.matcher(version).matches(),
            "Version should match pattern: v<major_version>.<minor_version>");

        final int indexOfDot = version.indexOf('.');
        majorVersion = Integer.valueOf(version.substring(1, indexOfDot));
        minorVersion = Integer.valueOf(version.substring(indexOfDot + 1));
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
        result = prime * result + majorVersion;
        result = prime * result + minorVersion;
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
        if (majorVersion != other.majorVersion)
            return false;
        if (minorVersion != other.minorVersion)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return getFullVersion();
    }
}
