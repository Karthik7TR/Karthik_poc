package com.thomsonreuters.uscl.ereader.orchestrate.engine.dao;

import org.apache.log4j.Logger;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class EngineDaoImpl implements EngineDao {
	private static final Logger log = Logger.getLogger(EngineDaoImpl.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public int getRunningJobExecutionCount() {
		String sql = String.format("select count(*) from BATCH_JOB_EXECUTION where (status = '%s') or (status = '%s')",
								BatchStatus.STARTING.toString(), BatchStatus.STARTED.toString());
		int count = jdbcTemplate.queryForInt(sql);
		return count;
	}
}

