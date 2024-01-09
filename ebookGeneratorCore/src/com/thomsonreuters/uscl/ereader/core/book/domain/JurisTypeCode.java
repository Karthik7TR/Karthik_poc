package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;

/**
 * A Jurisdiction Type Code database table entity.
 * Represents all the Jurisdiction Type Code id and names used for Book Definition
 */
@Data
@Entity
@Table(name = "JURIS_TYPE_CODES")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "JurisTypeCode")
public class JurisTypeCode implements Serializable {
    private static final long serialVersionUID = -3118292918077264082L;

    @Id
    @Column(name = "JURIS_TYPE_CODES_ID")
    @SequenceGenerator(name = "jurisTypeCodesIdSequence", sequenceName = "JURIS_TYPE_CODES_ID_SEQ")
    @GeneratedValue(generator = "jurisTypeCodesIdSequence")
    private Long id;

    @Column(name = "JURIS_TYPE_CODES_NAME", nullable = false, length = 1024)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdatedJurisTypeCode;
}
