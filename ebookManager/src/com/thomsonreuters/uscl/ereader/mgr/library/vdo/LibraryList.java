package com.thomsonreuters.uscl.ereader.mgr.library.vdo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import lombok.Getter;
import lombok.Setter;

public class LibraryList implements Serializable {
    private static final long serialVersionUID = -4057949125475314670L;

    private Long bookDefinitionId;
    @Getter @Setter
    private String sourceType;
    private String fullyQualifiedTitleId;
    private String proviewDisplayName;
    private String ebookDefinitionCompleteFlag;
    private String isDeletedFlag;
    private Date lastUpdated;
    private Date lastPublishDate;
    private Long combBookDefnId;
    private String isCombBookFlag;
    private Set<Author> authorList;

    public LibraryList(
        final Long bookDefinitionId,
        final String sourceType,
        final String proviewName,
        final String titleId,
        final String isComplete,
        final String isDeleted,
        final Date lastUpdate,
        final Date lastPublished,
        final Set<Author> authors) {
        this.bookDefinitionId = bookDefinitionId;
        this.sourceType = sourceType;
        proviewDisplayName = proviewName;
        fullyQualifiedTitleId = titleId;
        isDeletedFlag = isDeleted;
        lastUpdated = lastUpdate;
        ebookDefinitionCompleteFlag = isComplete;
        lastPublishDate = lastPublished;
        authorList = authors;
    }

    public LibraryList(
            final Long bookDefinitionId,
            final String sourceType,
            final String proviewName,
            final String titleId,
            final String isComplete,
            final String isDeleted,
            final Date lastUpdate,
            final Date lastPublished,
            final Long combBookDefnId,
            final String isCombBookFlag,
            final Set<Author> authors) {
        this.bookDefinitionId = bookDefinitionId;
        this.sourceType = sourceType;
        proviewDisplayName = proviewName;
        fullyQualifiedTitleId = titleId;
        isDeletedFlag = isDeleted;
        lastUpdated = lastUpdate;
        ebookDefinitionCompleteFlag = isComplete;
        lastPublishDate = lastPublished;
        authorList = authors;
        this.combBookDefnId = combBookDefnId;
        this.isCombBookFlag = isCombBookFlag;
    }

    public Long getCombBookDefnId() {
        return combBookDefnId;
    }

    public String getIsCombBookFlag() {
        return isCombBookFlag;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LibraryList other = (LibraryList) obj;
        if (authorList == null) {
            if (other.authorList != null)
                return false;
        } else if (!authorList.equals(other.authorList))
            return false;
        if (ebookDefinitionCompleteFlag == null) {
            if (other.ebookDefinitionCompleteFlag != null)
                return false;
        } else if (!ebookDefinitionCompleteFlag.equals(other.ebookDefinitionCompleteFlag))
            return false;
        if (bookDefinitionId == null) {
            if (other.bookDefinitionId != null)
                return false;
        } else if (!bookDefinitionId.equals(other.bookDefinitionId))
            return false;
        if (combBookDefnId == null) {
            if (other.combBookDefnId != null)
                return false;
        } else if (!combBookDefnId.equals(other.combBookDefnId))
            return false;

        if (!Objects.equals(sourceType, other.sourceType)) {
            return false;
        }
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
        if (isCombBookFlag == null) {
            if (other.isCombBookFlag != null)
                return false;
        } else if (!isCombBookFlag.equals(other.isCombBookFlag))
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
        final StringBuilder buffer = new StringBuilder();

        for (final Author author : authorList) {
            buffer.append(author.getFullName()).append("<br>");
        }
        return buffer.toString();
    }

    /**
     */
    public Long getBookDefinitionId() {
        return bookDefinitionId;
    }

    /**
     * Provides the status of the book definition
     * @return String indicating the status
     */
    public String getBookStatus() {
        final String status;
        if (getIsDeletedFlag()) {
            status = "Deleted";
        } else {
            if (IsEbookDefinitionCompleteFlag()) {
                status = "Ready";
            } else {
                status = "Incomplete";
            }
        }
        return status;
    }

    /**
     */
    public String getFullyQualifiedTitleId() {
        return fullyQualifiedTitleId;
    }

    /**
     */
    public boolean getIsDeletedFlag() {
        return "Y".equalsIgnoreCase(isDeletedFlag);
    }

    public Date getLastPublishDate() {
        return lastPublishDate;
    }

    /**
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     */
    public String getProviewDisplayName() {
        return proviewDisplayName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((authorList == null) ? 0 : authorList.hashCode());
        result = prime * result + ((ebookDefinitionCompleteFlag == null) ? 0 : ebookDefinitionCompleteFlag.hashCode());
        result = prime * result + ((bookDefinitionId == null) ? 0 : bookDefinitionId.hashCode());
        result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
        result = prime * result + ((fullyQualifiedTitleId == null) ? 0 : fullyQualifiedTitleId.hashCode());
        result = prime * result + ((isDeletedFlag == null) ? 0 : isDeletedFlag.hashCode());
        result = prime * result + ((lastPublishDate == null) ? 0 : lastPublishDate.hashCode());
        result = prime * result + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
        result = prime * result + ((proviewDisplayName == null) ? 0 : proviewDisplayName.hashCode());
        result = prime * result + ((combBookDefnId == null) ? 0 : combBookDefnId.hashCode());
        result = prime * result + ((isCombBookFlag == null) ? 0 : isCombBookFlag.hashCode());
        return result;
    }

    /**
     */
    public boolean IsEbookDefinitionCompleteFlag() {
        return "Y".equalsIgnoreCase(ebookDefinitionCompleteFlag);
    }

    /**
     * Returns a textual representation of a bean.
     *
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();

        buffer.append("ebookDefinitionId=[").append(bookDefinitionId).append("] ");
        buffer.append("sourceType=[").append(sourceType).append("] ");
        buffer.append("fullyQualifiedTitleId=[").append(fullyQualifiedTitleId).append("] ");
        buffer.append("proviewDisplayName=[").append(proviewDisplayName).append("] ");
        buffer.append("ebookDefinitionCompleteFlag=[").append(ebookDefinitionCompleteFlag).append("] ");
        buffer.append("isDeletedFlag=[").append(isDeletedFlag).append("] ");
        buffer.append("lastUpdated=[").append(lastUpdated).append("] ");

        return buffer.toString();
    }

}
