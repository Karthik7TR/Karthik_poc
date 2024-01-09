package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.jobthrottleconfig;

import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;

public class JobThrottleConfigForm {
    private JobThrottleConfig jobThrottleConfig = new JobThrottleConfig();

    public static final String FORM_NAME = "jobThrottleForm";

    /**
     * Populate the form from the corresponding domain object.
     */
    public void initialize(final JobThrottleConfig config) {
        setCoreThreadPoolSize(config.getCoreThreadPoolSize());
        setStepThrottleEnabled(config.isStepThrottleEnabled());
        setThrottleStepName(config.getThrottleStepName());
        setThrottleStepNameXppPathway(config.getThrottleStepNameXppPathway());
        setThrottleStepNameXppBundles(config.getThrottleStepNameXppBundles());
        setThrotttleStepMaxJobs(config.getThrottleStepMaxJobs());
    }

    public JobThrottleConfig getJobThrottleConfig() {
        return jobThrottleConfig;
    }

    public int getCoreThreadPoolSize() {
        return jobThrottleConfig.getCoreThreadPoolSize();
    }

    public boolean isStepThrottleEnabled() {
        return jobThrottleConfig.isStepThrottleEnabled();
    }

    public String getThrottleStepName() {
        return jobThrottleConfig.getThrottleStepName();
    }

    public String getThrottleStepNameXppPathway() {
        return jobThrottleConfig.getThrottleStepNameXppPathway();
    }

    public String getThrottleStepNameXppBundles() {
        return jobThrottleConfig.getThrottleStepNameXppBundles();
    }

    public int getThrotttleStepMaxJobs() {
        return jobThrottleConfig.getThrottleStepMaxJobs();
    }

    public void setCoreThreadPoolSize(final int coreThreadPoolSize) {
        jobThrottleConfig.setCoreThreadPoolSize(coreThreadPoolSize);
    }

    public void setStepThrottleEnabled(final boolean enabled) {
        jobThrottleConfig.setStepThrottleEnabled(enabled);
    }

    public void setThrottleStepName(final String throttleStepName) {
        jobThrottleConfig.setThrottleStepName(throttleStepName);
    }

    public void setThrottleStepNameXppPathway(final String throttleStepNameXppPathway) {
        jobThrottleConfig.setThrottleStepNameXppPathway(throttleStepNameXppPathway);
    }

    public void setThrottleStepNameXppBundles(final String throttleStepNameXppBundles) {
        jobThrottleConfig.setThrottleStepNameXppBundles(throttleStepNameXppBundles);
    }

    public void setThrotttleStepMaxJobs(final int throtttleStepMaxJobs) {
        jobThrottleConfig.setThrottleStepMaxJobs(throtttleStepMaxJobs);
    }
}
