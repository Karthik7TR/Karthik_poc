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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.thomsonreuters.uscl.ereader.core.book.domain.common.CopyAware;
import com.thomsonreuters.uscl.ereader.core.book.domain.common.EbookDefinitionAware;
import com.thomsonreuters.uscl.ereader.core.book.domain.common.SequenceNumAware;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 */
@Entity
@Table(name = "NORT_FILE_LOCATION")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "NortFileLocation")
public class NortFileLocation implements Serializable, Comparable<NortFileLocation>, CopyAware<NortFileLocation>, SequenceNumAware, EbookDefinitionAware {
    private static final long serialVersionUID = 249868448548819700L;

    @Column(name = "NORT_FILE_LOCATION_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @GeneratedValue(generator = "NortFileLocationSequence")
    @SequenceGenerator(name = "NortFileLocationSequence", sequenceName = "NORT_FILE_LOCATION_ID_SEQ")
    private Long nortFileLocationId;
    /**
     */

    @Column(name = "LOCATION_NAME", length = 1024)
    @Basic(fetch = FetchType.EAGER)
    private String locationName;
    /**
     */

    @Column(name = "SEQUENCE_NUMBER")
    @Basic(fetch = FetchType.EAGER)
    private Integer sequenceNum;
    /**
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "EBOOK_DEFINITION_ID", referencedColumnName = "EBOOK_DEFINITION_ID", nullable = false)})
    private BookDefinition ebookDefinition;

    public Long getNortFileLocationId() {
        return nortFileLocationId;
    }

    public void setNortFileLocationId(final Long nortFileLocationId) {
        this.nortFileLocationId = nortFileLocationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(final String locationName) {
        this.locationName = locationName;
    }

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    @Override
    public void setSequenceNum(final Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    /**
     */
    @Override
    public void setEbookDefinition(final BookDefinition ebookDefinition) {
        this.ebookDefinition = ebookDefinition;
    }

    /**
     */
    public BookDefinition getEbookDefinition() {
        return ebookDefinition;
    }

    /**
     */
    public NortFileLocation() {
    }

    /**
     * Copies the contents of the specified bean into this bean.
     *
     */
    @Override
    public void copy(final NortFileLocation that) {
        setNortFileLocationId(that.getNortFileLocationId());
        setLocationName(that.getLocationName());
        setSequenceNum(that.getSequenceNum());
        setEbookDefinition(that.getEbookDefinition());
    }

    @Transient
    public boolean isEmpty() {
        return StringUtils.isBlank(locationName);
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("nortFileLocationId=[").append(nortFileLocationId).append("] ");
        buffer.append("locationName=[").append(locationName).append("] ");
        buffer.append("sequenceNum=[").append(sequenceNum).append("] ");

        return buffer.toString();
    }

    /**
     * For sorting the name components into sequence order (1...n).
     */
    @Override
    public int compareTo(final NortFileLocation o) {
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
        if (!(obj instanceof NortFileLocation))
            return false;
        final NortFileLocation other = (NortFileLocation) obj;
        return new EqualsBuilder()
            .append(sequenceNum, other.sequenceNum)
            .isEquals();
    }
}
