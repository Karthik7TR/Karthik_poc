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
@Table(name = "DOCUMENT_CURRENCY")
@IdClass(DocumentCurrency.DocumentCurrencyPk.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "DocumentCurrency")
public class DocumentCurrency implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Id
    private BookDefinition ebookDefinition;

    @Id
    private String currencyGuid;

    @Column(name = "NEW_TEXT", nullable = false)
    private String newText;

    @Column(name = "NOTE", nullable = false)
    private String note;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdated;

    public BookDefinition getBookDefinition()
    {
        return ebookDefinition;
    }

    public void setBookDefinition(final BookDefinition bookDefinition)
    {
        ebookDefinition = bookDefinition;
    }

    public String getCurrencyGuid()
    {
        return currencyGuid;
    }

    public void setCurrencyGuid(final String currencyGuid)
    {
        this.currencyGuid = currencyGuid;
    }

    public String getNewText()
    {
        return newText;
    }

    public void setNewText(final String newText)
    {
        this.newText = newText;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(final String note)
    {
        this.note = note;
    }

    public Date getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated(final Date lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }

    @Transient
    public boolean isEmpty()
    {
        return StringUtils.isBlank(note)
            && StringUtils.isBlank(newText)
            && StringUtils.isBlank(currencyGuid);
    }

    @Override
    public String toString()
    {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("DocumentCurrency [");
        if (ebookDefinition != null)
        {
            buffer.append("bookDefinitionId=").append(ebookDefinition.getEbookDefinitionId()).append(", ");
        }
        buffer.append("currencyGuid=").append(currencyGuid).append(", ");
        buffer.append("newText=").append(newText).append(", ");
        buffer.append("note=").append(note).append(", ");
        buffer.append("lastUpdated=").append(lastUpdated).append("]");

        return buffer.toString();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((currencyGuid == null) ? 0 : currencyGuid.hashCode());
        result = prime * result + ((newText == null) ? 0 : newText.hashCode());
        result = prime * result + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
        result = prime * result + ((note == null) ? 0 : note.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DocumentCurrency other = (DocumentCurrency) obj;
        if (currencyGuid == null)
        {
            if (other.currencyGuid != null)
                return false;
        }
        else if (!currencyGuid.equals(other.currencyGuid))
            return false;
        if (newText == null)
        {
            if (other.newText != null)
                return false;
        }
        else if (!newText.equals(other.newText))
            return false;
        if (lastUpdated == null)
        {
            if (other.lastUpdated != null)
                return false;
        }
        else if (!lastUpdated.equals(other.lastUpdated))
            return false;
        if (note == null)
        {
            if (other.note != null)
                return false;
        }
        else if (!note.equals(other.note))
            return false;
        return true;
    }

    @Embeddable
    public static class DocumentCurrencyPk implements Serializable
    {
        private static final long serialVersionUID = 1L;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
            @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false)})
        private BookDefinition ebookDefinition;

        @Column(name = "CURRENCY_GUID", nullable = false)
        private String currencyGuid;

        public DocumentCurrencyPk()
        {
        }

        public BookDefinition getBookDefinition()
        {
            return ebookDefinition;
        }

        public void setBookDefinition(final BookDefinition bookDefinition)
        {
            ebookDefinition = bookDefinition;
        }

        public String getCurrencyGuid()
        {
            return currencyGuid;
        }

        public void setCurrencyGuid(final String currencyGuid)
        {
            this.currencyGuid = currencyGuid;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((currencyGuid == null) ? 0 : currencyGuid.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final DocumentCurrencyPk other = (DocumentCurrencyPk) obj;
            if (currencyGuid == null)
            {
                if (other.currencyGuid != null)
                    return false;
            }
            else if (!currencyGuid.equals(other.currencyGuid))
                return false;
            return true;
        }
    }
}
