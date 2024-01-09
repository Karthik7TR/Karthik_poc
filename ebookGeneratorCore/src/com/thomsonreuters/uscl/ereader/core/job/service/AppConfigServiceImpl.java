package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.CoreConstants.NovusEnvironment;
import com.thomsonreuters.uscl.ereader.core.job.dao.AppParameterDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;
import org.apache.commons.lang3.StringUtils;
import ch.qos.logback.classic.Level;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class AppConfigServiceImpl implements AppConfigService {
    private static final int DEFAULT_SIZE = 2;
    private AppParameterDao dao;
    private String defaultProviewHostname;
    private NovusEnvironment defaultNovusEnvironment;

    public AppConfigServiceImpl() {
        super();
    }

    @Override
    @Transactional(readOnly = true)
    public JobThrottleConfig loadJobThrottleConfig() {
        final String coreThreadPoolSizeString = getConfigValue(JobThrottleConfig.Key.coreThreadPoolSize.toString());
        final int coreThreadPoolSize = (StringUtils.isNotBlank(coreThreadPoolSizeString))
            ? Integer.valueOf(coreThreadPoolSizeString) : DEFAULT_SIZE;

        final String stepThrottleEnabledString = getConfigValue(JobThrottleConfig.Key.stepThrottleEnabled.toString());
        final boolean stepThrottleEnabled =
            (StringUtils.isNotBlank(stepThrottleEnabledString)) ? Boolean.valueOf(stepThrottleEnabledString) : false;

        final String throttleStepName = getConfigValue(JobThrottleConfig.Key.throttleStepName.toString());

        final String throttleStepNameXppPathway = getConfigValue(JobThrottleConfig.Key.throttleStepNameXppPathway.toString());

        final String throttleStepNameXppBundles = getConfigValue(JobThrottleConfig.Key.throttleStepNameXppBundles.toString());

        final String throtttleStepMaxJobsString = getConfigValue(JobThrottleConfig.Key.throtttleStepMaxJobs.toString());
        final int throtttleStepMaxJobs = (StringUtils.isNotBlank(throtttleStepMaxJobsString))
            ? Integer.valueOf(throtttleStepMaxJobsString) : DEFAULT_SIZE;

        final JobThrottleConfig config =
            new JobThrottleConfig(coreThreadPoolSize, stepThrottleEnabled, throttleStepName,
                throttleStepNameXppPathway, throttleStepNameXppBundles, throtttleStepMaxJobs);

        return config;
    }

    @Override
    @Transactional(readOnly = true)
    public MiscConfig loadMiscConfig() {
        final Level appLogLevel = fetchLogLevel(MiscConfig.Key.appLogLevel.toString(), Level.INFO);
        final Level rootLogLevel = fetchLogLevel(MiscConfig.Key.rootLogLevel.toString(), Level.ERROR);
        String proviewHostname = getConfigValue(MiscConfig.Key.proviewHostname.toString());
        if (StringUtils.isBlank(proviewHostname)) {
            proviewHostname = defaultProviewHostname;
        }

        final int maxSplitParts = Integer.parseInt(getConfigValue(MiscConfig.Key.maxSplitParts.toString()));

        final MiscConfig config =
            new MiscConfig(appLogLevel, rootLogLevel, defaultNovusEnvironment, proviewHostname, maxSplitParts);
        return config;
    }

    @Override
    @Transactional(readOnly = true)
    public String getConfigValue(final String key) {
        final AppParameter param = dao.findOne(key);
        return (param != null) ? param.getValue() : null;
    }

    private Level fetchLogLevel(final String key, final Level defaultLogLevel) {
        final String logLevelString = getConfigValue(key);
        final Level logLevel =
            (StringUtils.isNotBlank(logLevelString)) ? Level.toLevel(logLevelString) : defaultLogLevel;
        return logLevel;
    }

    @Override
    @Transactional
    public void saveJobThrottleConfig(final JobThrottleConfig config) {
        final List<AppParameter> params = createJobThrottleConfigAppParameterList(config);
        for (final AppParameter param : params) {
            dao.save(param);
        }
    }

    @Override
    @Transactional
    public void saveMiscConfig(final MiscConfig config) {
        AppParameter param = new AppParameter(MiscConfig.Key.appLogLevel.toString(), config.getAppLogLevel());
        dao.save(param);
        param = new AppParameter(MiscConfig.Key.rootLogLevel.toString(), config.getRootLogLevel());
        dao.save(param);
        param = new AppParameter(MiscConfig.Key.proviewHostname.toString(), config.getProviewHost().getHostName());
        dao.save(param);
    }

    private List<AppParameter> createJobThrottleConfigAppParameterList(final JobThrottleConfig config) {
        final List<AppParameter> parameters = new ArrayList<>();
        parameters
            .add(new AppParameter(JobThrottleConfig.Key.coreThreadPoolSize.toString(), config.getCoreThreadPoolSize()));
        parameters.add(
            new AppParameter(JobThrottleConfig.Key.stepThrottleEnabled.toString(), config.isStepThrottleEnabled()));
        parameters
            .add(new AppParameter(JobThrottleConfig.Key.throttleStepName.toString(), config.getThrottleStepName()));
        parameters
            .add(new AppParameter(JobThrottleConfig.Key.throttleStepNameXppPathway.toString(), config.getThrottleStepNameXppPathway()));
        parameters
            .add(new AppParameter(JobThrottleConfig.Key.throttleStepNameXppBundles.toString(), config.getThrottleStepNameXppBundles()));
        parameters.add(
            new AppParameter(JobThrottleConfig.Key.throtttleStepMaxJobs.toString(), config.getThrottleStepMaxJobs()));
        return parameters;
    }

    @Required
    public void setAppParameterDao(final AppParameterDao dao) {
        this.dao = dao;
    }

    public void setDefaultProviewHostname(final String hostname) {
        defaultProviewHostname = hostname;
    }

    public void setDefaultNovusEnvironment(final NovusEnvironment env) {
        defaultNovusEnvironment = env;
    }
}
