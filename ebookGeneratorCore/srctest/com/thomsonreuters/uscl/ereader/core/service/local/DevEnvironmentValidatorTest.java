package com.thomsonreuters.uscl.ereader.core.service.local;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import org.junit.Test;
import javax.persistence.Table;
import static org.junit.Assert.fail;

public class DevEnvironmentValidatorTest {
    private static final String WORKSTATION = "workstation";
    private static final String CICONTENT = "cicontent";
    private static final String JOB_REQUEST = "JOB_REQUEST";

    @Test
    public void testEnvWorkstation() {
        DevEnvironmentValidator service = new DevEnvironmentValidatorImpl(WORKSTATION);

        if (isJobRequestTableName()) {
            expectException(service);
        } else {
            expectSuccess(service);
        }
    }

    @Test
    public void testEnvCicontent() {
        DevEnvironmentValidator service = new DevEnvironmentValidatorImpl(CICONTENT);

        if (isJobRequestTableName()) {
            expectSuccess(service);
        } else {
            expectException(service);
        }
    }

    private void expectSuccess(final DevEnvironmentValidator service) {
        service.validateEnvironment();
    }

    private void expectException(final DevEnvironmentValidator service) {
        try {
            service.validateEnvironment();
        } catch (EBookException e) {
            return;
        }
        fail();
    }

    private boolean isJobRequestTableName() {
        return JOB_REQUEST.equalsIgnoreCase((JobRequest.class).getAnnotation(Table.class).name());
    }
}
