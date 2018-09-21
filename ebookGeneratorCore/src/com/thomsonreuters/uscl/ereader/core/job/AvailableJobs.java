package com.thomsonreuters.uscl.ereader.core.job;

import java.util.stream.Stream;

public enum AvailableJobs {
    EBOOK_GENERATOR_XPP_JOB("ebookGeneratorXppJob"),
    EBOOK_GENERATOR_JOB("ebookGeneratorJob"),
    EBOOK_BUNDLE_JOB("ebookBundleJob");

    private final String jobName;

    AvailableJobs(final String jobName) {
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }

    public static AvailableJobs getByJobName(final String jobName) {
        return Stream.of(values())
            .filter(value -> value.jobName.equals(jobName))
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException(String.format("Job %s is not supported", jobName)));
    }
}
