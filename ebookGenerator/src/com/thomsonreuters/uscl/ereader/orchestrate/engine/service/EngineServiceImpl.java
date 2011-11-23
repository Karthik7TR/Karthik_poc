package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import org.springframework.batch.core.JobParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.orchestrate.engine.dao.EngineDao;

@Component
public class EngineServiceImpl implements EngineService {

	@Autowired
	private EngineDao dao;
	
	@Override
	@Transactional
	public JobParameters loadJobParameters(String jobName) {
		return dao.loadJobParameters(jobName);
	}
}
