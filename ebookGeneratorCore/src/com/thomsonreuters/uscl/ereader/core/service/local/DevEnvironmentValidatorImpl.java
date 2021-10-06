package com.thomsonreuters.uscl.ereader.core.service.local;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.Table;
import javax.annotation.PostConstruct;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DevEnvironmentValidatorImpl implements DevEnvironmentValidator {
    private static final String JOB_REQUEST = "JOB_REQUEST";
    private static final String WORKSTATION = "workstation";
    private static final String JOB_REQUEST_NOT_ALLOWED_ERROR_MESSAGE = "\n" +
            "JOB_REQUEST is unacceptable JobRequest table name for 'workstation' environment.\n" +
            "Change \n" +
                    "\t@Table(name = \"JOB_REQUEST\")\n" +
            "to\n" +
                    "\t@Table(name = \"JOB_REQUEST_[suffix]\")\n";
    private static final String JOB_REQUEST_REQUIRED_ERROR_MESSAGE = "\n" +
            "JOB_REQUEST is required JobRequest table name for '%s' environment.\n" +
            "Change \n" +
            "\t@Table(name = \"%s\")\n" +
            "to\n" +
            "\t@Table(name = \"JOB_REQUEST\")\n";
    private final String environmentName;

    @Override
    @PostConstruct
    public void validateEnvironment() {
        String jobRequestTableName = getJobRequestTableName();
        if (WORKSTATION.equalsIgnoreCase(environmentName)) {
            if (isJobRequestTableName(jobRequestTableName)) {
                throw new EBookException(JOB_REQUEST_NOT_ALLOWED_ERROR_MESSAGE);
            }
        } else {
            if (!isJobRequestTableName(jobRequestTableName)) {
                throw new EBookException(String.format(JOB_REQUEST_REQUIRED_ERROR_MESSAGE, environmentName, jobRequestTableName));
            }
        }
    }

    private boolean isJobRequestTableName(final String jobRequestTableName) {
        return JOB_REQUEST.equalsIgnoreCase(jobRequestTableName);
    }

    private String getJobRequestTableName() {
        return (JobRequest.class).getAnnotation(Table.class).name();
    }
}
