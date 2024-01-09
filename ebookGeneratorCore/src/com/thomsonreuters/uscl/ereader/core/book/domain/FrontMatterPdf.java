package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
@Table(name = "FRONT_MATTER_PDF")
public class FrontMatterPdf implements Serializable, Comparable<FrontMatterPdf> {
    private static final long serialVersionUID = -8713934748505263533L;

    @Column(name = "FRONT_MATTER_PDF_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @GeneratedValue(generator = "FrontMatterPdfSequence")
    @SequenceGenerator(name = "FrontMatterPdfSequence", sequenceName = "FRONT_MATTER_PDF_ID_SEQ")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumns({
        @JoinColumn(
            name = "FRONT_MATTER_SECTION_ID",
            referencedColumnName = "FRONT_MATTER_SECTION_ID",
            nullable = false)})
    private FrontMatterSection section;

    @Column(name = "PDF_LINK_TEXT", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    private String pdfLinkText;

    @Column(name = "PDF_FILENAME", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    private String pdfFilename;

    @Column(name = "SEQUENCE_NUMBER", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    private Integer sequenceNum;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public FrontMatterSection getSection() {
        return section;
    }

    public void setSection(final FrontMatterSection section) {
        this.section = section;
    }

    public String getPdfLinkText() {
        return pdfLinkText;
    }

    public void setPdfLinkText(final String pdfLinkText) {
        this.pdfLinkText = pdfLinkText;
    }

    public String getPdfFilename() {
        return pdfFilename;
    }

    public void setPdfFilename(final String pdfFilename) {
        this.pdfFilename = pdfFilename;
    }

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(final Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    @Transient
    public boolean isEmpty() {
        return (StringUtils.isBlank(pdfFilename) && StringUtils.isBlank(pdfLinkText) && (sequenceNum == null));
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("FrontMatterPdf [pdfLinkText=").append(pdfLinkText).append(", ");
        buffer.append("pdfFilename=").append(pdfFilename).append(", ");
        buffer.append("]");

        return buffer.toString();
    }

    /**
     * For sorting the name components into sequence order (1...n).
     */
    @Override
    public int compareTo(final FrontMatterPdf o) {
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(sequenceNum)
            .toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof FrontMatterPdf))
            return false;
        final FrontMatterPdf other = (FrontMatterPdf) obj;
        return new EqualsBuilder()
            .append(sequenceNum, other.sequenceNum)
            .isEquals();
    }
}
