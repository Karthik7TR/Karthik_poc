/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class TitleId {
	private static final String _PT = "_pt";
	
	@NotNull
	private String headTitleId;
	private int partNumber;

	public TitleId(@NotNull String titleId) {
		Assert.notNull(titleId);
		
		int indexOfPt = titleId.lastIndexOf(_PT);
		if (indexOfPt < 0) {
			headTitleId = titleId;
			partNumber = 1;
		} else {
			try {
				headTitleId = titleId.substring(0, indexOfPt);
				partNumber = Integer.valueOf(titleId.substring(indexOfPt + _PT.length()));
			} catch (NumberFormatException e) {
				headTitleId = titleId;
				partNumber = 1;
			}
		}
	}

	@NotNull
	public String getHeadTitleId() {
		return headTitleId;
	}

	public int getPartNumber() {
		return partNumber;
	}

	public String getTitleId() {
		if (partNumber == 1)
			return headTitleId;
		return headTitleId + _PT + partNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((headTitleId == null) ? 0 : headTitleId.hashCode());
		result = prime * result + partNumber;
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
		TitleId other = (TitleId) obj;
		if (headTitleId == null) {
			if (other.headTitleId != null)
				return false;
		} else if (!headTitleId.equals(other.headTitleId))
			return false;
		if (partNumber != other.partNumber)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TitleId [headTitleId=" + headTitleId + ", partNumber=" + partNumber + "]";
	}

}
