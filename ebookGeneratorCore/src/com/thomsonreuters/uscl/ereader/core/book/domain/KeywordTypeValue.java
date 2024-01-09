package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})

@Entity
@Table(name = "KEYWORD_TYPE_VALUES")
public class KeywordTypeValue implements Serializable {
    private static final long serialVersionUID = 8698248929292091625L;

    @Id
    @Column(name = "KEYWORD_TYPE_VALUES_ID", unique = true, nullable = false)
    @SequenceGenerator(name = "keywordTypeValuesIdSequence", sequenceName = "KEYWORD_TYPE_VALUES_ID_SEQ")
    @GeneratedValue(generator = "keywordTypeValuesIdSequence")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "KEYWORD_TYPE_CODES_ID")
    private KeywordTypeCode keywordTypeCode;

    @Column(name = "KEYWORD_TYPE_VALUES_NAME", nullable = false, length = 1024)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdated;

    @Override
    public String toString() {
        final String code = Optional.ofNullable(keywordTypeCode).map(KeywordTypeCode::getName).orElse("null");
        return String.format("keywordTypeCode=[%s] KeywordTypeValue=[%s]", code, name);
    }
}
