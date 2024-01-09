package com.thomsonreuters.uscl.ereader.core.job.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.thomsonreuters.uscl.ereader.core.job.AvailableJobs;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "jobThrottleConfig")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
public class JobThrottleConfig {
    /** Typesafe representation of the keys used to represent the throttling configuration */
    public enum Key {
        coreThreadPoolSize,
        stepThrottleEnabled,
        throttleStepName,
        throttleStepNameXppPathway,
        throttleStepNameXppBundles,
        throtttleStepMaxJobs
    };

    /** The task executor core thread pool size */
    @XmlElement(name = "coreThreadPoolSize", required = true)
    private int coreThreadPoolSize = 2;
    /** is step-level throttling active */
    @XmlElement(name = "stepThrottleEnabled", required = true)
    private boolean stepThrottleEnabled;
    /** The step name at which the throttle is applied for Novus pathway*/
    @XmlElement(name = "throttleStepName", required = true)
    private String throttleStepName;
    /** The step name at which the throttle is applied for XPP pathway*/
    @XmlElement(name = "throttleStepNameXppPathway", required = true)
    private String throttleStepNameXppPathway;
    /** The step name at which the throttle is applied XPP bundles*/
    @XmlElement(name = "throttleStepNameXppBundles", required = true)
    private String throttleStepNameXppBundles;
    /** The limit of jobs up to the specified throttle step name */
    @XmlElement(name = "throttleStepMaxJobs", required = true)
    private int throttleStepMaxJobs = 2;

    /**
     * Full constructor.
     */
    public JobThrottleConfig(
        final int coreThreadPoolSize,
        final boolean stepThrottleEnabled,
        final String throttleStepName,
        final String throttleStepNameXppPathway,
        final String throttleStepNameXppBundles,
        final int throtttleStepMaxJobs) {
        setAllProperties(coreThreadPoolSize, stepThrottleEnabled, throttleStepName,
            throttleStepNameXppPathway, throttleStepNameXppBundles, throtttleStepMaxJobs);
    }

    /**
     * Copy the property values from one object to this one.
     * @param config the source object to copy property values from.
     */
    public void copy(final JobThrottleConfig config) {
        setAllProperties(
            config.getCoreThreadPoolSize(),
            config.isStepThrottleEnabled(),
            config.getThrottleStepName(),
            config.getThrottleStepNameXppPathway(),
            config.getThrottleStepNameXppBundles(),
            config.getThrottleStepMaxJobs());
    }

    private synchronized void setAllProperties(
        final int coreThreadPoolSize,
        final boolean stepThrottleEnabled,
        final String throttleStepName,
        final String throttleStepNameXppPathway,
        final String throttleStepNameXppBundles,
        final int throtttleStepMaxJobs) {
        setCoreThreadPoolSize(coreThreadPoolSize);
        setStepThrottleEnabled(stepThrottleEnabled);
        setThrottleStepName(throttleStepName);
        setThrottleStepNameXppPathway(throttleStepNameXppPathway);
        setThrottleStepNameXppBundles(throttleStepNameXppBundles);
        setThrottleStepMaxJobs(throtttleStepMaxJobs);
    }

    public String getThrottleStepName(final String jobName) {
        final String stepName;
        switch (AvailableJobs.getByJobName(jobName)) {
            case EBOOK_BUNDLE_JOB:
                stepName = getThrottleStepNameXppBundles();
                break;
            case EBOOK_GENERATOR_JOB:
                stepName = getThrottleStepName();
                break;
            case EBOOK_GENERATOR_XPP_JOB:
                stepName = getThrottleStepNameXppPathway();
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return stepName;
    }
}
