package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "SPLIT_DOCUMENT")
@IdClass(SplitDocument.SplitDocumentPk.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "SplitDocument")
public class SplitDocument implements Serializable {
    private static final long serialVersionUID = 8698522630203083821L;

    @Id
    private BookDefinition ebookDefinition;

    @Id
    private String tocGuid;

    @Column(name = "NOTE", length = 512, nullable = false)
    private String note;

    public BookDefinition getBookDefinition() {
        return ebookDefinition;
    }

    public void setBookDefinition(final BookDefinition bookDefinition) {
        ebookDefinition = bookDefinition;
    }

    public String getTocGuid() {
        return tocGuid;
    }

    public void setTocGuid(final String tocGuid) {
        this.tocGuid = tocGuid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(final String note) {
        this.note = note;
    }

    @Transient
    public boolean isEmpty() {
        return StringUtils.isBlank(note) && StringUtils.isBlank(tocGuid);
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("SplitDocument [");
        if (ebookDefinition != null) {
            buffer.append("bookDefinitionId=").append(ebookDefinition.getEbookDefinitionId()).append(", ");
        }
        buffer.append("tocGuid=").append(tocGuid).append(", ");
        buffer.append("note=").append(note).append("]");

        return buffer.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tocGuid == null) ? 0 : tocGuid.hashCode());
        result = prime * result
            + ((ebookDefinition.getEbookDefinitionId() == null)
                ? 0 : ebookDefinition.getEbookDefinitionId().hashCode());
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
        final SplitDocument other = (SplitDocument) obj;
        if (tocGuid == null) {
            if (other.tocGuid != null)
                return false;
        } else if (!tocGuid.equals(other.tocGuid))
            return false;
        if (ebookDefinition == null) {
            if (other.ebookDefinition != null)
                return false;
        } else if (!ebookDefinition.getEbookDefinitionId().equals(other.ebookDefinition.getEbookDefinitionId()))
            return false;
        if (note == null) {
            if (other.note != null)
                return false;
        } else if (!note.equals(other.note))
            return false;
        return true;
    }

    @Embeddable
    public static class SplitDocumentPk implements Serializable {
        private static final long serialVersionUID = 3552710801579579685L;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
            @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false)})
        private BookDefinition ebookDefinition;

        @Column(name = "TOC_GUID", length = 33, nullable = false)
        private String tocGuid;

        public SplitDocumentPk() {
        }

        public BookDefinition getBookDefinition() {
            return ebookDefinition;
        }

        public void setBookDefinition(final BookDefinition bookDefinition) {
            ebookDefinition = bookDefinition;
        }

        public String getTocGuid() {
            return tocGuid;
        }

        public void setTocGuid(final String tocGuid) {
            this.tocGuid = tocGuid;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((tocGuid == null) ? 0 : tocGuid.hashCode());
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
            final SplitDocumentPk other = (SplitDocumentPk) obj;
            if (tocGuid == null) {
                if (other.tocGuid != null)
                    return false;
            } else if (!tocGuid.equals(other.tocGuid))
                return false;
            return true;
        }
    }
}
