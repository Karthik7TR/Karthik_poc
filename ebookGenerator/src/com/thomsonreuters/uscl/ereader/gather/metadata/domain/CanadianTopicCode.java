package com.thomsonreuters.uscl.ereader.gather.metadata.domain;

import lombok.Data;

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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@Data
@Entity
@Table(name = "CANADIAN_TOPIC_CODE")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "ebookGenerator/com/thomsonreuters/uscl/ereader/gather/metadata/domain", name = "CanadianTopicCode")
public class CanadianTopicCode implements Serializable {
    @Column(name = "CANADIAN_TOPIC_CODE_ID", nullable = false)
    @Basic(fetch = FetchType.EAGER)
    @Id
    @GeneratedValue(generator = "CanadianTopicCodeSequence")
    @SequenceGenerator(name = "CanadianTopicCodeSequence", sequenceName = "CANADIAN_TOPIC_CODE_ID_SEQ")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "TITLE_ID", referencedColumnName = "TITLE_ID", nullable = false),
            @JoinColumn(name = "JOB_INSTANCE_ID", referencedColumnName = "JOB_INSTANCE_ID", nullable = false),
            @JoinColumn(name = "DOC_UUID", referencedColumnName = "DOC_UUID", nullable = false)
    })
    private DocMetadata docMetadata;

    @Column(name = "TOPIC_KEY", nullable = false, length = 36)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String topicKey;

    @Column(name = "JOB_INSTANCE_ID", nullable = false, insertable = false, updatable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private Long jobInstanceId;

    @Column(name = "DOC_UUID", nullable = false, insertable = false, updatable = false)
    @Basic(fetch = FetchType.EAGER)
    @XmlElement
    private String docUuid;
}
