package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.Assert;

/**
 * A key/value pair table used for misc application configuration.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "APP_PARAMETER")
@EntityListeners(AuditingEntityListener.class)
public class AppParameter {
    @Id
    @Column(name = "PARAMETER_KEY")
    @NonNull
    private String key;
    @Column(name = "PARAMETER_VALUE")
    private String value;
    @Column(name = "LAST_UPDATED", nullable = false)
    @LastModifiedDate
    @NonNull
    private Date lastUpdated;

    public AppParameter() {
        super();
    }

    public AppParameter(final String key, final Object value) {
        Assert.notNull(key);
        setKey(key);
        setValue(value.toString());
    }
}
