/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.engine.dao;

import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;

public class EngineDaoImpl implements EngineDao {
	//private static final Logger log = Logger.getLogger(EngineDaoImpl.class);
	
	private JdbcTemplate jdbcTemplate;
	private String tablePrefix;
	
	@Override
	public int getRunningJobExecutionCount() {
		String sql = String.format("select count(*) from %sJOB_EXECUTION where (status = '%s') or (status = '%s')",
								tablePrefix, BatchStatus.STARTING.toString(), BatchStatus.STARTED.toString());
		int count = jdbcTemplate.queryForInt(sql);
		return count;
	}
	
	@Required
	public void setJdbcTemplate(JdbcTemplate template) {
		this.jdbcTemplate = template;
	}
	
	/** The prefix to all tables, like "EBOOK_SPRINGBATCH.BATCH_" */
	@Required
	public void setTablePrefix(String prefix) {
		this.tablePrefix = prefix;
	}
}

