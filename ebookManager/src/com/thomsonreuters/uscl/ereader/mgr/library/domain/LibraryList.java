/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.library.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;

public class LibraryList implements Serializable {
	private static final long serialVersionUID = -4057949125475314670L;
 
	Long bookDefinitionId;
	String fullyQualifiedTitleId;
	String proviewDisplayName;
	String ebookDefinitionCompleteFlag;
	String isDeletedFlag;
	Date lastUpdated;
	Date lastPublishDate;
	Set<Author> authorList;
	
	
	public LibraryList(Long bookDefinitionId, String proviewName,String titleId,
			String isComplete, String isDeleted, Date lastUpdate, Date lastPublished, Set<Author> authors) {
		this.bookDefinitionId = bookDefinitionId;
		this.proviewDisplayName = proviewName;
		this.fullyQualifiedTitleId = titleId;
		this.isDeletedFlag = isDeleted;
		this.lastUpdated = lastUpdate;
		this.ebookDefinitionCompleteFlag = isComplete;
		this.lastPublishDate = lastPublished;
		this.authorList = authors;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LibraryList other = (LibraryList) obj;
		if (authorList == null) {
			if (other.authorList != null)
				return false;
		} else if (!authorList.equals(other.authorList))
			return false;
		if (ebookDefinitionCompleteFlag == null) {
			if (other.ebookDefinitionCompleteFlag != null)
				return false;
		} else if (!ebookDefinitionCompleteFlag
				.equals(other.ebookDefinitionCompleteFlag))
			return false;
		if (bookDefinitionId == null) {
			if (other.bookDefinitionId != null)
				return false;
		} else if (!bookDefinitionId.equals(other.bookDefinitionId))
			return false;
		if (fullyQualifiedTitleId == null) {
			if (other.fullyQualifiedTitleId != null)
				return false;
		} else if (!fullyQualifiedTitleId.equals(other.fullyQualifiedTitleId))
			return false;
		if (isDeletedFlag == null) {
			if (other.isDeletedFlag != null)
				return false;
		} else if (!isDeletedFlag.equals(other.isDeletedFlag))
			return false;
		if (lastPublishDate == null) {
			if (other.lastPublishDate != null)
				return false;
		} else if (!lastPublishDate.equals(other.lastPublishDate))
			return false;
		if (lastUpdated == null) {
			if (other.lastUpdated != null)
				return false;
		} else if (!lastUpdated.equals(other.lastUpdated))
			return false;
		if (proviewDisplayName == null) {
			if (other.proviewDisplayName != null)
				return false;
		} else if (!proviewDisplayName.equals(other.proviewDisplayName))
			return false;
		return true;
	}

	/**
	 */
	public String getAuthorList() {
		StringBuilder buffer = new StringBuilder();
		
		for(Author author : authorList) {
			buffer.append(author.getFullName()).append("<br>");
		}
		return buffer.toString();
	}

	/**
	 */
	public Long getBookDefinitionId() {
		return this.bookDefinitionId;
	}

	/**
	 * Provides the status of the book definition
	 * @return String indicating the status
	 */
	public String getBookStatus() {
		String status;
		if (getIsDeletedFlag()) {
			status = "Deleted";
		} else { 
			if (IsEbookDefinitionCompleteFlag()) {
				status = "Complete";
			} else {
				status = "Incomplete";
			}
		}
		return status;
	}

	/**
	 */
	public String getFullyQualifiedTitleId() {
		return this.fullyQualifiedTitleId;
	}

	/**
	 */
	public boolean getIsDeletedFlag() {
		return( (this.isDeletedFlag.equalsIgnoreCase("Y") ? true : false));
	}



	public Date getLastPublishDate() {
		return lastPublishDate;
	}

	/**
	 */
	public Date getLastUpdated() {
		return this.lastUpdated;
	}


	/**
	 */
	public String getProviewDisplayName() {
		return this.proviewDisplayName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authorList == null) ? 0 : authorList.hashCode());
		result = prime
				* result
				+ ((ebookDefinitionCompleteFlag == null) ? 0
						: ebookDefinitionCompleteFlag.hashCode());
		result = prime
				* result
				+ ((bookDefinitionId == null) ? 0 : bookDefinitionId
						.hashCode());
		result = prime
				* result
				+ ((fullyQualifiedTitleId == null) ? 0 : fullyQualifiedTitleId
						.hashCode());
		result = prime * result
				+ ((isDeletedFlag == null) ? 0 : isDeletedFlag.hashCode());
		result = prime * result
				+ ((lastPublishDate == null) ? 0 : lastPublishDate.hashCode());
		result = prime * result
				+ ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime
				* result
				+ ((proviewDisplayName == null) ? 0 : proviewDisplayName
						.hashCode());
		return result;
	}

	/**
	 */
	public boolean IsEbookDefinitionCompleteFlag() {
		return ((this.ebookDefinitionCompleteFlag.equalsIgnoreCase("Y") ? true : false));
	}

	/**
	 * Returns a textual representation of a bean.
	 *
	 */
	public String toString() {

		StringBuilder buffer = new StringBuilder();

		buffer.append("ebookDefinitionId=[").append(bookDefinitionId).append("] ");
		buffer.append("fullyQualifiedTitleId=[").append(fullyQualifiedTitleId).append("] ");
		buffer.append("proviewDisplayName=[").append(proviewDisplayName).append("] ");
		buffer.append("ebookDefinitionCompleteFlag=[").append(ebookDefinitionCompleteFlag).append("] ");
		buffer.append("isDeletedFlag=[").append(isDeletedFlag).append("] ");
		buffer.append("lastUpdated=[").append(lastUpdated).append("] ");

		return buffer.toString();
	}
}
