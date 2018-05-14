package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.thomsonreuters.uscl.ereader.StringBool;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "lastUpdatedTimeStampForKeyWordCode"})
@ToString

@Entity
@Table(name = "KEYWORD_TYPE_CODES")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "KeywordTypeCode")
public class KeywordTypeCode implements Serializable {
    private static final long serialVersionUID = -6883749966331206015L;

    @Id
    @Column(name = "KEYWORD_TYPE_CODES_ID", unique = true, nullable = false)
    @SequenceGenerator(name = "keywordTypeCodesIdSequence", sequenceName = "KEYWORD_TYPE_CODES_ID_SEQ")
    @GeneratedValue(generator = "keywordTypeCodesIdSequence")
    private Long id;

    @Column(name = "KEYWORD_TYPE_CODES_NAME", nullable = false, length = 1024)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdatedTimeStampForKeyWordCode;

    @Column(name = "IS_REQUIRED", nullable = false, length = 1)
    private String isRequired;

    @OneToMany(mappedBy = "keywordTypeCode", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    @OrderBy("name")
    private List<KeywordTypeValue> values;

    public KeywordTypeCode() {
        values = new ArrayList<>();
    }

    public boolean getIsRequired() {
        return StringBool.toBool(isRequired);
    }

    public void setIsRequired(final boolean isRequired) {
        this.isRequired = StringBool.toString(isRequired);
    }
}
