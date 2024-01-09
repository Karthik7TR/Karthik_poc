package com.thomsonreuters.uscl.ereader.gather.metadata.domain;

import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@Data
@Entity
@Table(name = "CANADIAN_DIGEST")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/gather/metadata/domain", name = "CanadianDigest")
public class CanadianDigest implements Serializable {
    @Column(name = "CANADIAN_DIGEST_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @GeneratedValue(generator = "CanadianDigestSequence")
    @SequenceGenerator(name = "CanadianDigestSequence", sequenceName = "CANADIAN_DIGEST_ID_SEQ")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "TITLE_ID", referencedColumnName = "TITLE_ID", nullable = false),
            @JoinColumn(name = "JOB_INSTANCE_ID", referencedColumnName = "JOB_INSTANCE_ID", nullable = false),
            @JoinColumn(name = "DOC_UUID", referencedColumnName = "DOC_UUID", nullable = false)
    })
    private DocMetadata docMetadata;

    @Column(name = "JOB_INSTANCE_ID", nullable = false, insertable = false, updatable = false)
    @Basic(fetch = FetchType.EAGER)
    private Long jobInstanceId;

    @Column(name = "DOC_UUID", nullable = false, insertable = false, updatable = false)
    @Basic(fetch = FetchType.EAGER)
    private String docUuid;

    @Column(name = "CLASSIFNUM", nullable = false, length = 128)
    private String classifnum;

    @Column(name = "CLASSIFICATION", nullable = false, length = 512)
    private String classification;
}