/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class BookTitleId implements Comparable<BookTitleId> {
	@NotNull
	private String titleId;
	@NotNull
	private Version version;

	public BookTitleId(@NotNull String titleId, @NotNull Version version) {
		Assert.notNull(titleId);
		Assert.notNull(version);

		this.titleId = titleId;
		this.version = version;
	}

	@NotNull
	public String getTitleId() {
		return titleId;
	}

	@NotNull
	public Version getVersion() {
		return version;
	}

	@SuppressWarnings("null")
	@NotNull
	public String getTitleIdWithMajorVersion() {
		return new StringBuilder().append(titleId).append("/").append(version.getMajorVersion()).toString();
	}

	@SuppressWarnings("null")
	@NotNull
	public String getTitleIdWithVersion() {
		return new StringBuilder().append(titleId).append("/").append(version.getFullVersion()).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((titleId == null) ? 0 : titleId.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookTitleId other = (BookTitleId) obj;
		if (titleId == null) {
			if (other.titleId != null)
				return false;
		} else if (!titleId.equals(other.titleId))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("BookTitleId [titleID=").append(titleId).append("; version=").append(version)
				.append("]").toString();
	}

	@Override
	public int compareTo(BookTitleId o) {
		return titleId.compareTo(o.titleId);
	}

}
