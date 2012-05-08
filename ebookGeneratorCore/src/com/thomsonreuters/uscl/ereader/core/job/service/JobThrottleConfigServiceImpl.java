package com.thomsonreuters.uscl.ereader.core.job.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.job.dao.AppParameterDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobThrottleConfig.Key;

public class JobThrottleConfigServiceImpl implements JobThrottleConfigService {
	//private static final Logger log = Logger.getLogger(JobThrottleConfigServiceImpl.class);
	private AppParameterDao dao;

	@Override
	@Transactional(readOnly=true)
	public String getConfigValue(JobThrottleConfig.Key key) {
		AppParameter param = (AppParameter) dao.findByPrimaryKey(key.toString());
		return param.getValue();
	}

	@Override
	@Transactional(readOnly=true)
	public JobThrottleConfig getThrottleConfig() {
		int coreThreadPoolSize = Integer.valueOf(getConfigValue(Key.coreThreadPoolSize));
		boolean throttleStepActive = Boolean.valueOf(getConfigValue(Key.throttleStepActive));
		String throttleStepName = getConfigValue(Key.throttleStepName);
		int throtttleStepMaxJobs = Integer.valueOf(getConfigValue(Key.throtttleStepMaxJobs));
		JobThrottleConfig config = new JobThrottleConfig(coreThreadPoolSize, throttleStepActive, throttleStepName, throtttleStepMaxJobs);
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
		parameters.add(new AppParameter(JobThrottleConfig.Key.throttleStepActive.toString(), config.isThrottleStepActive()));
		parameters.add(new AppParameter(JobThrottleConfig.Key.throttleStepName.toString(), config.getThrottleStepName()));
		parameters.add(new AppParameter(JobThrottleConfig.Key.throtttleStepMaxJobs.toString(), config.getThrotttleStepMaxJobs()));
		return parameters;
	}
	
	@Required
	public void setAppParameterDao(AppParameterDao dao) {
		this.dao = dao;
	}
}
