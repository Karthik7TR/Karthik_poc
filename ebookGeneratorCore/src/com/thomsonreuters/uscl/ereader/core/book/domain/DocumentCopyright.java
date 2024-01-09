package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

/**
 */
@Entity
@Table(name = "DOCUMENT_COPYRIGHT")
@IdClass(DocumentCopyright.DocumentCopyrightPk.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "DocumentCopyright")
public class DocumentCopyright implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private BookDefinition ebookDefinition;

    @Id
    private String copyrightGuid;

    @Column(name = "NEW_TEXT", nullable = false)
    private String newText;

    @Column(name = "NOTE", nullable = false)
    private String note;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdated;

    public BookDefinition getBookDefinition() {
        return ebookDefinition;
    }

    public void setBookDefinition(final BookDefinition bookDefinition) {
        ebookDefinition = bookDefinition;
    }

    public String getCopyrightGuid() {
        return copyrightGuid;
    }

    public void setCopyrightGuid(final String copyrightGuid) {
        this.copyrightGuid = copyrightGuid;
    }

    public String getNewText() {
        return newText;
    }

    public void setNewText(final String newText) {
        this.newText = newText;
    }

    public String getNote() {
        return note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(final Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Transient
    public boolean isEmpty() {
        return StringUtils.isBlank(note) && StringUtils.isBlank(newText) && StringUtils.isBlank(copyrightGuid);
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("DocumentCopyright [");
        if (ebookDefinition != null) {
            buffer.append("bookDefinitionId=").append(ebookDefinition.getEbookDefinitionId()).append(", ");
        }
        buffer.append("copyrightGuid=").append(copyrightGuid).append(", ");
        buffer.append("newText=").append(newText).append(", ");
        buffer.append("note=").append(note).append(", ");
        buffer.append("lastUpdated=").append(lastUpdated).append("]");

        return buffer.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((copyrightGuid == null) ? 0 : copyrightGuid.hashCode());
        result = prime * result + ((newText == null) ? 0 : newText.hashCode());
        result = prime * result + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
        result = prime * result + ((note == null) ? 0 : note.hashCode());
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
        final DocumentCopyright other = (DocumentCopyright) obj;
        if (copyrightGuid == null) {
            if (other.copyrightGuid != null)
                return false;
        } else if (!copyrightGuid.equals(other.copyrightGuid))
            return false;
        if (newText == null) {
            if (other.newText != null)
                return false;
        } else if (!newText.equals(other.newText))
            return false;
        if (lastUpdated == null) {
            if (other.lastUpdated != null)
                return false;
        } else if (!lastUpdated.equals(other.lastUpdated))
            return false;
        if (note == null) {
            if (other.note != null)
                return false;
        } else if (!note.equals(other.note))
            return false;
        return true;
    }

    @Embeddable
    public static class DocumentCopyrightPk implements Serializable {
        private static final long serialVersionUID = 1L;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
            @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false)})
        private BookDefinition ebookDefinition;

        @Column(name = "COPYRIGHT_GUID", nullable = false)
        private String copyrightGuid;

        public DocumentCopyrightPk() {
        }

        public BookDefinition getBookDefinition() {
            return ebookDefinition;
        }

        public void setBookDefinition(final BookDefinition bookDefinition) {
            ebookDefinition = bookDefinition;
        }

        public String getCopyrightGuid() {
            return copyrightGuid;
        }

        public void setCopyrightGuid(final String copyrightGuid) {
            this.copyrightGuid = copyrightGuid;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((copyrightGuid == null) ? 0 : copyrightGuid.hashCode());
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
            final DocumentCopyrightPk other = (DocumentCopyrightPk) obj;
            if (copyrightGuid == null) {
                if (other.copyrightGuid != null)
                    return false;
            } else if (!copyrightGuid.equals(other.copyrightGuid))
                return false;
            return true;
        }
    }
}
