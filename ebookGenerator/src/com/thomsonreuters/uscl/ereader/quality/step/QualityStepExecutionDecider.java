package com.thomsonreuters.uscl.ereader.quality.step;

import com.thomsonreuters.uscl.ereader.core.quality.service.QualityReportsAdminService;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.beans.factory.annotation.Autowired;

public class QualityStepExecutionDecider implements JobExecutionDecider {
    @Autowired
    private QualityReportsAdminService qualityReportsAdminService;

    @Override
    public FlowExecutionStatus decide(final JobExecution arg0, final StepExecution arg1) {
        final FlowExecutionStatus status;
        if (qualityReportsAdminService.getParams().isQualityStepEnabled()) {
            status = new FlowExecutionStatus("ENABLED");
        } else {
            status = new FlowExecutionStatus("DISABLED");
        }
        return status;
    }
}
