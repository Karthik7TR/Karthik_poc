package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.dao.AppParameterDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig.Key;

public class JobThrottleConfigServiceImpl implements JobThrottleConfigService {
	//private static final Logger log = Logger.getLogger(JobThrottleConfigServiceImpl.class);
	private static final int DEFAULT_SIZE = 2;
	private AppParameterDao dao;

	@Override
	@Transactional(readOnly=true)
	public String getConfigValue(JobThrottleConfig.Key key) {
		AppParameter param = (AppParameter) dao.findByPrimaryKey(key.toString());
		return (param != null) ? param.getValue() : null;
	}

	@Override
	@Transactional(readOnly=true)
	public JobThrottleConfig getThrottleConfig() {
		String coreThreadPoolSizeString = getConfigValue(Key.coreThreadPoolSize);
		int coreThreadPoolSize = (StringUtils.isNotBlank(coreThreadPoolSizeString)) ? Integer.valueOf(coreThreadPoolSizeString) : DEFAULT_SIZE;
		
		String stepThrottleEnabledString = getConfigValue(Key.stepThrottleEnabled);
		boolean stepThrottleEnabled = (StringUtils.isNotBlank(stepThrottleEnabledString)) ? Boolean.valueOf(stepThrottleEnabledString) : false;
		
		String throttleStepName = getConfigValue(Key.throttleStepName);
		
		String throtttleStepMaxJobsString = getConfigValue(Key.throtttleStepMaxJobs);
		int throtttleStepMaxJobs = (StringUtils.isNotBlank(throtttleStepMaxJobsString)) ? Integer.valueOf(throtttleStepMaxJobsString) : DEFAULT_SIZE;
		
		JobThrottleConfig config = new JobThrottleConfig(coreThreadPoolSize, stepThrottleEnabled, throttleStepName, throtttleStepMaxJobs);
		return config;
	}
	
	@Override
	@Transactional
	public void saveJobThrottleConfig(JobThrottleConfig config) {
		List<AppParameter> params = createThrottleConfigAppParameterList(config);
		for (AppParameter param : params) {
			dao.save(param);
		}
	}
	
	@Override
	@Transactional
	public void deleteJobThrottleConfig(JobThrottleConfig config) {
		List<AppParameter> params = createThrottleConfigAppParameterList(config);
		for (AppParameter param : params) {
			dao.delete(param);
		}
	}

	private List<AppParameter> createThrottleConfigAppParameterList(JobThrottleConfig config) {
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
