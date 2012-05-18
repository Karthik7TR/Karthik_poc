package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.dao.AppParameterDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.MiscConfig;

public class AppConfigServiceImpl implements AppConfigService {
	private static final int DEFAULT_SIZE = 2;
	private AppParameterDao dao;
	
	public AppConfigServiceImpl() {
		super();
	}
	
	@Override
	@Transactional(readOnly=true)
	public JobThrottleConfig loadJobThrottleConfig() {
		String coreThreadPoolSizeString = getConfigValue(JobThrottleConfig.Key.coreThreadPoolSize.toString());
		int coreThreadPoolSize = (StringUtils.isNotBlank(coreThreadPoolSizeString)) ? Integer.valueOf(coreThreadPoolSizeString) : DEFAULT_SIZE;
		
		String stepThrottleEnabledString = getConfigValue(JobThrottleConfig.Key.stepThrottleEnabled.toString());
		boolean stepThrottleEnabled = (StringUtils.isNotBlank(stepThrottleEnabledString)) ? Boolean.valueOf(stepThrottleEnabledString) : false;
		
		String throttleStepName = getConfigValue(JobThrottleConfig.Key.throttleStepName.toString());
		
		String throtttleStepMaxJobsString = getConfigValue(JobThrottleConfig.Key.throtttleStepMaxJobs.toString());
		int throtttleStepMaxJobs = (StringUtils.isNotBlank(throtttleStepMaxJobsString)) ? Integer.valueOf(throtttleStepMaxJobsString) : DEFAULT_SIZE;
		
		JobThrottleConfig config = new JobThrottleConfig(coreThreadPoolSize, stepThrottleEnabled, throttleStepName, throtttleStepMaxJobs);

		return config;
	}
	@Override
	@Transactional(readOnly=true)
	public MiscConfig loadMiscConfig() {
		Level appLogLevel = fetchLogLevel(MiscConfig.Key.appLogLevel.toString(), Level.INFO);
		Level rootLogLevel = fetchLogLevel(MiscConfig.Key.rootLogLevel.toString(), Level.ERROR);
		
		MiscConfig config = new MiscConfig(appLogLevel, rootLogLevel);
		return config;
	}

	@Override
	@Transactional(readOnly=true)
	public String getConfigValue(String key) {
		AppParameter param = (AppParameter) dao.findByPrimaryKey(key);
		return (param != null) ? param.getValue() : null;
	}
	
	private Level fetchLogLevel(String key, Level defaultLogLevel) {
		String logLevelString = getConfigValue(key);
		Level logLevel = (StringUtils.isNotBlank(logLevelString)) ? Level.toLevel(logLevelString) : defaultLogLevel;
		return logLevel;
	}
	
	@Override
	@Transactional
	public void saveJobThrottleConfig(JobThrottleConfig config) {
		List<AppParameter> params = createJobThrottleConfigAppParameterList(config);
		for (AppParameter param : params) {
			dao.save(param);
		}
	}

	@Override
	@Transactional
	public void saveMiscConfig(MiscConfig config) {
		AppParameter param = new AppParameter(MiscConfig.Key.appLogLevel.toString(), config.getAppLogLevel());
		dao.save(param);
		param = new AppParameter(MiscConfig.Key.rootLogLevel.toString(), config.getRootLogLevel());
		dao.save(param);
	}	
	
	private List<AppParameter> createJobThrottleConfigAppParameterList(JobThrottleConfig config) {
		List<AppParameter> parameters = new ArrayList<AppParameter>();
		parameters.add(new AppParameter(JobThrottleConfig.Key.coreThreadPoolSize.toString(), config.getCoreThreadPoolSize()));
		parameters.add(new AppParameter(JobThrottleConfig.Key.stepThrottleEnabled.toString(), config.isStepThrottleEnabled()));
		parameters.add(new AppParameter(JobThrottleConfig.Key.throttleStepName.toString(), config.getThrottleStepName()));
		parameters.add(new AppParameter(JobThrottleConfig.Key.throtttleStepMaxJobs.toString(), config.getThrotttleStepMaxJobs()));
		return parameters;
	}
	
	@Required
	public void setAppParameterDao(AppParameterDao dao) {
		this.dao = dao;
	}
}