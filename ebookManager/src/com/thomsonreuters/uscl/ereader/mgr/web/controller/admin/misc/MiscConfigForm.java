package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.misc;

import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MiscConfigForm extends MiscConfig
{
    public static final String FORM_NAME = "miscConfigForm";

    public void initialize(final MiscConfig config)
    {
        setAppLogLevel(config.getAppLogLevel());
        setRootLogLevel(config.getRootLogLevel());
        setNovusEnvironment(config.getNovusEnvironment());
        setProviewHostname(config.getProviewHostname());
        setDisableExistingSingleTitleSplit(config.getDisableExistingSingleTitleSplit());
        setMaxSplitParts(config.getMaxSplitParts());
    }

    public MiscConfig createMiscConfig()
    {
        final MiscConfig config = new MiscConfig();
        config.copy(this);
        return config;
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
