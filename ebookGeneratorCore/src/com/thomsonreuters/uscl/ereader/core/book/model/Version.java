/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.model;

import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class Version {
	private static String VERSION_PREFIX = "v";
	private static Pattern VERSION_PATTREN = Pattern.compile("v\\d\\.\\d");

	private int majorVersion;
	private int minorVersion;

	public Version(@NotNull String version) {
		Assert.notNull(version);
		Assert.isTrue(VERSION_PATTREN.matcher(version).matches(),
				"Version should match pattarn: v<major_version>.<minor_version>");

		int indexOfDot = version.indexOf(".");
		majorVersion = Integer.valueOf(version.substring(1, indexOfDot));
		minorVersion = Integer.valueOf(version.substring(indexOfDot + 1));
	}

	public Version(int majorVersion, int minorVersion) {
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

	@SuppressWarnings("null")
	@NotNull
	public String getFullVersion() {
		return new StringBuilder(VERSION_PREFIX).append(majorVersion).append(".").append(minorVersion).toString();
	}

	@SuppressWarnings("null")
	@NotNull
	public String getVersionWithoutPrefix() {
		return new StringBuilder().append(majorVersion).append(".").append(minorVersion).toString();
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Version other = (Version) obj;
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
