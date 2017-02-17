package com.thomsonreuters.uscl.ereader.common.outage.step;

import org.springframework.batch.core.step.tasklet.Tasklet;

public interface OutageAwareStep extends Tasklet
{
    // Marker interface to use in aspect
}
