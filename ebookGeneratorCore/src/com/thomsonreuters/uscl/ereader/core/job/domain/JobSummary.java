package com.thomsonreuters.uscl.ereader.core.job.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.Period;
import org.springframework.batch.core.BatchStatus;

/**
 * A subset of book and job execution data used to present the Job Summary view.
 */
@Getter
@AllArgsConstructor
public class JobSummary {
    private final Long bookDefinitionId;
    private final Long combinedBookDefinitionId;
    private final String bookName;
    private final String titleId;
    private final String sourceType;
    private final Long jobInstanceId;
    private final Long jobExecutionId;
    private final BatchStatus batchStatus;
    private final String submittedBy;
    private final String userName;
    private final Date startTime;
    private final Date endTime;

    public String getDuration() {
        return JobSummary.getExecutionDuration(getExecutionDuration());
    }

    private Long getExecutionDuration() {
        return getExecutionDuration(startTime, endTime);
    }

    public static long getExecutionDuration(final Date start, Date end) {
        long duration = 0;
        if (start != null) {
            if (end == null) {
                end = new Date();
            }
            duration = end.getTime() - start.getTime();
        }
        return duration;
    }

    public static String getExecutionDuration(final Long durationMilliseconds) {
        if (durationMilliseconds == null) {
            return null;
        }
        final long durationMs = durationMilliseconds.longValue();
        final StringBuffer periodString = new StringBuffer();
        if (durationMs > -1) {
            final Period period = new Period(durationMs);
            periodString.append((period.getHours() < 10) ? "0" : "");
            periodString.append(period.getHours());
            periodString.append(":");
            periodString.append((period.getMinutes() < 10) ? "0" : "");
            periodString.append(period.getMinutes());
            periodString.append(":");
            periodString.append((period.getSeconds() < 10) ? "0" : "");
            periodString.append(period.getSeconds());
            periodString.append(".");
            periodString.append((period.getMillis() < 10) ? "0" : "");
            periodString.append((period.getMillis() < 100) ? "0" : "");
            periodString.append(period.getMillis());
        }
        return periodString.toString();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
