package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("transformationUtil")
public class TransformationUtil
{
    private static final Logger LOG = LogManager.getLogger(TransformationUtil.class);

    @Value("${xpp.sample.xpp.directory}")
    private File xppDirectory;

    public boolean shouldSkip(final BookStep step)
    {
        final boolean skip = !xppDirectory.exists();
        if (skip)
        {
            LOG.debug(String.format("%s skipped, because sample xppTemp directory not found", step.getStepName()));
        }
        return skip;
    }
}
