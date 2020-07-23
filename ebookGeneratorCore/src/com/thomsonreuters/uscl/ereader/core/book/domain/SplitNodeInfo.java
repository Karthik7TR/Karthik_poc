package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "SPLIT_NODE_INFO")
@IdClass(SplitNodeInfo.SplitNodeInfoPk.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "SplitNodeInfo")
public class SplitNodeInfo implements Serializable {
    private static final long serialVersionUID = 8698522630203083821L;

    @Id
    private BookDefinition ebookDefinition;

    @Id
    private String splitBookTitleId;

    @Id
    private String bookVersionSubmitted;

    @Column(name = "SPLIT_NODE_GUID", length = 33, nullable = false)
    private String splitNodeGuid;

    public BookDefinition getBookDefinition() {
        return ebookDefinition;
    }

    public void setBookDefinition(final BookDefinition bookDefinition) {
        ebookDefinition = bookDefinition;
    }

    /**
     */
    public void setBookVersionSubmitted(final String bookVersionSubmitted) {
        this.bookVersionSubmitted = bookVersionSubmitted;
    }

    /**
     */
    public String getBookVersionSubmitted() {
        return bookVersionSubmitted;
    }

    public String getSplitBookTitle() {
        return splitBookTitleId;
    }

    public void setSpitBookTitle(final String splitBookTitle) {
        splitBookTitleId = splitBookTitle;
    }

    public String getSplitNodeGuid() {
        return splitNodeGuid;
    }

    public void setSplitNodeGuid(final String splitNodeGuid) {
        this.splitNodeGuid = splitNodeGuid;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("SplitNodeInfo [");
        if (ebookDefinition != null) {
            buffer.append("bookDefinitionId=").append(ebookDefinition.getEbookDefinitionId()).append(", ");
        }
        buffer.append("splitBookTitleId=").append(splitBookTitleId).append(", ");
        buffer.append("bookVersionSubmitted=").append(bookVersionSubmitted).append(", ");
        buffer.append("splitNodeGuid=").append(splitNodeGuid).append("]");

        return buffer.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bookVersionSubmitted == null) ? 0 : bookVersionSubmitted.hashCode());
        result = prime * result
            + ((ebookDefinition.getEbookDefinitionId() == null)
                ? 0 : ebookDefinition.getEbookDefinitionId().hashCode());
        result = prime * result + ((splitBookTitleId == null) ? 0 : splitBookTitleId.hashCode());
        result = prime * result + ((splitNodeGuid == null) ? 0 : splitNodeGuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SplitNodeInfo)) {
            return false;
        }
        final SplitNodeInfo other = (SplitNodeInfo) obj;
        if (!Objects.equals(bookVersionSubmitted, other.bookVersionSubmitted)) {
            return false;
        }
        final Long thisBookDefId = ebookDefinition == null ? null : ebookDefinition.getEbookDefinitionId();
        final Long otherBookDefId = other.ebookDefinition == null ? null : other.ebookDefinition.getEbookDefinitionId();
        if (!Objects.equals(thisBookDefId, otherBookDefId)) {
            return false;
        }
        if (!Objects.equals(splitBookTitleId, other.splitBookTitleId)) {
            return false;
        }
        if (!Objects.equals(splitNodeGuid, other.splitNodeGuid)) {
            return false;
        }
        return true;
    }

    @Embeddable
    public static class SplitNodeInfoPk implements Serializable {
        private static final long serialVersionUID = 3552710801579579685L;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
            @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false)})
        private BookDefinition ebookDefinition;

        @Column(name = "SPLIT_BOOK_TITLE_ID", length = 64)
        @Basic(fetch = FetchType.EAGER)
        private String splitBookTitleId;

        @Column(name = "BOOK_VERSION_SUBMITTED")
        @Basic(fetch = FetchType.EAGER)
        @XmlElement
        private String bookVersionSubmitted;

        public SplitNodeInfoPk() {
        }

        public BookDefinition getBookDefinition() {
            return ebookDefinition;
        }

        public void setBookDefinition(final BookDefinition bookDefinition) {
            ebookDefinition = bookDefinition;
        }

        /**
         */
        public void setBookVersionSubmitted(final String bookVersionSubmitted) {
            this.bookVersionSubmitted = bookVersionSubmitted;
        }

        /**
         */
        public String getBookVersionSubmitted() {
            return bookVersionSubmitted;
        }

        public String getSplitBookTitle() {
            return splitBookTitleId;
        }

        public void setSpitBookTitle(final String splitBookTitle) {
            splitBookTitleId = splitBookTitle;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((bookVersionSubmitted == null) ? 0 : bookVersionSubmitted.hashCode());
            result = prime * result + ((splitBookTitleId == null) ? 0 : splitBookTitleId.hashCode());
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
            final SplitNodeInfoPk other = (SplitNodeInfoPk) obj;
            if (splitBookTitleId == null) {
                if (other.splitBookTitleId != null)
                    return false;
            } else if (!splitBookTitleId.equals(other.splitBookTitleId))
                return false;
            if (bookVersionSubmitted == null) {
                if (other.bookVersionSubmitted != null)
                    return false;
            } else if (!bookVersionSubmitted.equals(other.bookVersionSubmitted))
                return false;
            return true;
        }
    }
}
