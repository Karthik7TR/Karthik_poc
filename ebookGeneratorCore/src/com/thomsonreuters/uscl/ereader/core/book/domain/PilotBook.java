package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

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
import javax.xml.bind.annotation.XmlType;

import com.thomsonreuters.uscl.ereader.core.book.domain.common.CopyAware;
import com.thomsonreuters.uscl.ereader.core.book.domain.common.EbookDefinitionAware;
import com.thomsonreuters.uscl.ereader.core.book.domain.common.SequenceNumAware;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(of = {"pilotBookTitleId", "ebookDefinition"})
@ToString(of = {"pilotBookTitleId", "ebookDefinition", "note"})

@Entity
@Table(name = "PILOT_BOOK")
@IdClass(PilotBook.PilotBookPk.class)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "PilotBook")
public class PilotBook implements Serializable, Comparable<PilotBook>, CopyAware<PilotBook>, SequenceNumAware, EbookDefinitionAware {
    private static final long serialVersionUID = 7962657038385328632L;

    @Id
    private String pilotBookTitleId;

    @Id
    private BookDefinition ebookDefinition;

    @Column(name = "SEQUENCE_NUMBER")
    @Basic(fetch = FetchType.EAGER)
    private Integer sequenceNum;

    @Column(name = "NOTE", length = 512)
    private String note;

    /**
     * Copies the contents of the specified bean into this bean.
     *
     */
    @Override
    public void copy(final PilotBook that) {
        setPilotBookTitleId(that.getPilotBookTitleId());
        setSequenceNum(that.getSequenceNum());
        setNote(that.getNote());
        setEbookDefinition(that.getEbookDefinition());
    }

    public boolean isEmpty() {
        return pilotBookTitleId == null || pilotBookTitleId.equals("");
    }

    /**
     * For sorting the name components into sequence order (1...n).
     */
    @Override
    public int compareTo(final PilotBook o) {
        int result = 0;
        if (sequenceNum != null) {
            if (o != null) {
                final Integer i = o.getSequenceNum();
                result = (i != null) ? sequenceNum.compareTo(i) : 1;
            } else {
                result = 1;
            }
        } else { // int1 is null
            result = (o != null) ? -1 : 0;
        }
        return result;
    }

    @Data
    @Embeddable
    public static class PilotBookPk implements Serializable {
        private static final long serialVersionUID = 3552710801579579685L;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
            @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false)})
        private BookDefinition ebookDefinition;
        @Column(name = "PILOT_BOOK_TITLE_ID", nullable = false)
        private String pilotBookTitleId;
    }
}
