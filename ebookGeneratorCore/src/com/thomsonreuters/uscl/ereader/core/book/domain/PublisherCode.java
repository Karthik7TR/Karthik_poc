package com.thomsonreuters.uscl.ereader.core.book.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import lombok.Data;
import org.hibernate.annotations.Cascade;

/**
 * A publisher code database table entity.
 * Represents all the publisher code id and names used for Book Definition
 */
@Data
@Entity
@Table(name = "PUBLISHER_CODES")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/core/book/domain", name = "PublisherCode")
public class PublisherCode implements Serializable {
    private static final long serialVersionUID = -2270804278406061488L;

    @Id
    @Column(name = "PUBLISHER_CODES_ID")
    @SequenceGenerator(name = "publisherCodesIdSequence", sequenceName = "PUBLISHER_CODES_ID_SEQ")
    @GeneratedValue(generator = "publisherCodesIdSequence")
    private Long id;

    @Column(name = "PUBLISHER_NAME", nullable = false, length = 1024)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATED", nullable = false)
    private Date lastUpdatedTimeStampForPubCode;

    @OneToMany(mappedBy = "publisherCode", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<DocumentTypeCode> documentTypeCodes;
}
