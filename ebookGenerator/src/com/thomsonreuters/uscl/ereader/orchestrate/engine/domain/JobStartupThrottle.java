package com.thomsonreuters.uscl.ereader.orchestrate.engine.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 * Domain objects mapped to table JOB_STARTUP_THROTTLE in schema EBOOK_CONFIG
 *
 * @author Mahendra Survase (u0105927)
 *
 */
@Deprecated // Deprecated in favor of the JobThrottleConfig
//@Entity
//@Table(name="JOB_STARTUP_THROTTLE")
public class JobStartupThrottle implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "THROTTLE_TIME_ZONE")
    private String throttleTimeZone;

    @Id
    @Column(name = "THROTTLE_STEP_NAME")
    private String throttleStepName;

    @Column(name = "THROTTLE_LIMIT")
    private int throttleLimit;

    public JobStartupThrottle() {
        super();
    }

    public int getThrottleLimit() {
        return throttleLimit;
    }

    public void setThrottleLimit(final int throttleLimit) {
        this.throttleLimit = throttleLimit;
    }

    public String getThrottleTimeZone() {
        return throttleTimeZone;
    }

    public void setThrottleTimeZone(final String throttleTimeZone) {
        this.throttleTimeZone = throttleTimeZone;
    }

    public String getThrottleStepName() {
        return throttleStepName;
    }

    public void setThrottleStepName(final String throttleStepName) {
        this.throttleStepName = throttleStepName;
    }
}
