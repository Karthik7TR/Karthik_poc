package com.thomsonreuters.uscl.ereader.core.job.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobFilter;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSort.SortProperty;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.BatchStatus;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public class JobDaoImpl implements JobDao {
    private static final JobSummaryRowMapper JOB_SUMMARY_ROW_MAPPER = new JobSummaryRowMapper();
    private static final JobExecutionIdRowMapper JOB_EXECUTION_ID_ROW_MAPPER = new JobExecutionIdRowMapper();
    private JdbcTemplate jdbcTemplate;

    @Override
    public int getStartedJobCount() {
        final String sql = String.format(
            "select count(*) from BATCH_JOB_EXECUTION where (status = '%s') or (status = '%s')",
            BatchStatus.STARTING.toString(),
            BatchStatus.STARTED.toString());
        final int count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count;
    }

    @Override
    public List<JobSummary> findJobSummary(final List<Long> jobExecutionIds) {
        final List<JobSummary> list = new ArrayList<>(jobExecutionIds.size());
        for (final long jobExecutionId : jobExecutionIds) {
            final StringBuilder sql = new StringBuilder(
                "select auditTable.EBOOK_DEFINITION_ID, stats.COMB_BOOK_DEFN_ID, auditTable.PROVIEW_DISPLAY_NAME, auditTable.SOURCE_TYPE, auditTable.TITLE_ID, execution.JOB_INSTANCE_ID, ");
            sql.append(
                "execution.JOB_EXECUTION_ID, execution.STATUS, execution.START_TIME, execution.END_TIME, stats.JOB_SUBMITTER_NAME , CASE WHEN userProfile.USER_LN IS NOT NULL AND userProfile.USER_FN  IS NOT NULL THEN userProfile.USER_LN || ', ' || userProfile.USER_FN end AS JOB_SUBMITTER_USER_NAME from \n ");
            sql.append("BATCH_JOB_EXECUTION execution, PUBLISHING_STATS stats, EBOOK_AUDIT auditTable, USER_PROFILE userProfile ");
            sql.append("where ");
            sql.append(String.format("(execution.JOB_EXECUTION_ID = %d) and ", jobExecutionId));
            sql.append("(execution.JOB_INSTANCE_ID = stats.JOB_INSTANCE_ID(+)) and ");
            sql.append("(stats.AUDIT_ID = auditTable.AUDIT_ID(+)) and ");
            sql.append("(stats.JOB_SUBMITTER_NAME=userProfile.USER_ID(+)) ");
            // sql.append("(auditTable.PROVIEW_DISPLAY_NAME is not null)");  // Do not fetch rows that appear to be garbage
            final List<JobSummary> rows = jdbcTemplate.query(sql.toString(), JOB_SUMMARY_ROW_MAPPER);
            if (rows.isEmpty()) {
                log.debug(String.format("Job Execution ID %d was not found", jobExecutionId));
            } else if (rows.size() == 1) {
                final JobSummary summary = rows.get(0);
                list.add(summary);
            } else {
                log.error(String.format("%d rows were unexpectedly returned!", rows.size()));
            }
        }
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findJobExecutions(final JobFilter filter, final JobSort sort) {
        List<Long> jobExecutionIds = null;
        final StringBuffer sql = new StringBuffer("select execution.JOB_EXECUTION_ID from ");
        sql.append("BATCH_JOB_EXECUTION execution ");
        sql.append(", BATCH_JOB_INSTANCE instance ");
        if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
            sql.append(", PUBLISHING_STATS stats, EBOOK_AUDIT auditTable ");
        }
        sql.append("where ");
        sql.append("(execution.JOB_INSTANCE_ID = instance.JOB_INSTANCE_ID) and ");
        sql.append("(instance.JOB_NAME != '" + JobParameterKey.JOB_NAME_PROCESS_BUNDLE + "') and ");
        sql.append(addFiltersToQuery(filter, sort));

        final String orderByColumn = getOrderByColumnName(sort.getSortProperty());
        sql.append(String.format("order by %s %s", orderByColumn, sort.getSortDirection()));

        //log.debug("SQL: " + sql.toString());
        final Object[] args = argumentsAddToFilter(filter, sort);

        jobExecutionIds = jdbcTemplate.query(sql.toString(), JOB_EXECUTION_ID_ROW_MAPPER, args);

        return jobExecutionIds;
    }

    /**
     * Generate sql query that matches the filters user inputs
     * @param filter
     * @return
     */
    private String addFiltersToQuery(final JobFilter filter, final JobSort sort) {
        final StringBuffer sql = new StringBuffer();

        if (filter.getFrom() != null) {
            /* 	We want to show jobs that are in the STARTING status even if they have no start time because
            	these are restarted jobs that are waiting for a thread (from the pool) to run and have not yet
            	began to actually execute.  They will remain in the STARTING status until another job completes
            	so the task can come off the ThreadPoolTaskExecutor blocking queue and be assigned a thread
            	from the pool when a thread frees up. */
            sql.append(
                String.format(
                    "((execution.STATUS = '%s') or (execution.START_TIME >= ?)) and ",
                    BatchStatus.STARTING.toString()));
        }
        if (filter.getTo() != null) {
            sql.append("(execution.START_TIME < ?) and ");
        }
        if (filter.getBatchStatus() != null && filter.getBatchStatus().length > 0) {
            final StringBuffer csvStatus = new StringBuffer();
            boolean firstTime = true;
            for (final BatchStatus status : filter.getBatchStatus()) {
                if (!firstTime) {
                    csvStatus.append(",");
                }
                firstTime = false;
                csvStatus.append(String.format("'%s'", status.toString()));
            }
            sql.append(String.format("(execution.STATUS in (%s)) and ", csvStatus));
        }
        if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
            sql.append("(execution.JOB_INSTANCE_ID = stats.JOB_INSTANCE_ID(+)) and ");
            sql.append("(stats.AUDIT_ID = auditTable.AUDIT_ID(+)) and ");

            if (StringUtils.isNotBlank(filter.getBookName())) {
                sql.append("(UPPER(auditTable.PROVIEW_DISPLAY_NAME) like UPPER(?)) and ");
            }
            if (StringUtils.isNotBlank(filter.getTitleId())) {
                sql.append("(UPPER(auditTable.TITLE_ID) like UPPER(?)) and ");
            }
            if (StringUtils.isNotBlank(filter.getSubmittedBy())) {
                sql.append("(UPPER(stats.JOB_SUBMITTER_NAME) like UPPER(?)) and ");
            }
        }
        sql.append("(1=1) "); // end of WHERE clause, ensure proper SQL syntax

        return sql.toString();
    }

    /**
     * Creates the arguments list to use in jdbcTemplate.query.
     * The order of the arguements being added needs to match the filter order in
     * @param filter
     * @return
     */
    private Object[] argumentsAddToFilter(final JobFilter filter, final JobSort sort) {
        final List<Object> args = new ArrayList<>();

        if (filter.getFrom() != null) {
            args.add(filter.getFrom());
        }
        if (filter.getTo() != null) {
            args.add(filter.getTo());
        }
        if (filter.hasAnyBookProperties() || sort.isSortingOnBookProperty()) {
            if (StringUtils.isNotBlank(filter.getBookName())) {
                args.add(filter.getBookName());
            }
            if (StringUtils.isNotBlank(filter.getTitleId())) {
                args.add(filter.getTitleId());
            }
            if (StringUtils.isNotBlank(filter.getSubmittedBy())) {
                args.add(filter.getSubmittedBy());
            }
        }
        return args.toArray();
    }

    /**
     * Map the sort column enumeration into the actual column identifier used in the HQL query.
     * @param sortProperty enumerated value that reflects the database table sort column to sort on.
     */
    private String getOrderByColumnName(final SortProperty sortProperty) {
        switch (sortProperty) {


        case JOB_EXECUTION_ID:
            return "execution.JOB_EXECUTION_ID";
        case JOB_INSTANCE_ID:
            return "execution.JOB_INSTANCE_ID";
        case BATCH_STATUS:
            return "execution.STATUS";
        case START_TIME:
            return "execution.START_TIME";
        case BOOK_NAME:
            return "auditTable.PROVIEW_DISPLAY_NAME";
        case TITLE_ID:
            return "auditTable.TITLE_ID";
        case SOURCE_TYPE:
            return "auditTable.SOURCE_TYPE";
        case SUBMITTED_BY:
            case USER_NAME:
                return "stats.JOB_SUBMITTER_NAME";

        default:
            throw new IllegalArgumentException("Unexpected sort property: " + sortProperty);
        }
    }

    @Required
    public void setJdbcTemplate(final JdbcTemplate template) {
        jdbcTemplate = template;
    }
}

class JobSummaryRowMapper implements RowMapper<JobSummary> {
    @Override
    public JobSummary mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        final Long bookDefinitionId = resultSet.getLong("EBOOK_DEFINITION_ID");
        final Long combinedBookDefinitionId = resultSet.getLong("COMB_BOOK_DEFN_ID");
        final String bookName = resultSet.getString("PROVIEW_DISPLAY_NAME");
        final String titleId = resultSet.getString("TITLE_ID");
        final String sourceType = resultSet.getString("SOURCE_TYPE");
        final Long jobExecutionId = resultSet.getLong("JOB_EXECUTION_ID");
        final Long jobInstanceId = resultSet.getLong("JOB_INSTANCE_ID");
        final BatchStatus batchStatus = BatchStatus.valueOf(resultSet.getString("STATUS"));
        final String userName = resultSet.getString("JOB_SUBMITTER_USER_NAME"); //UserName
        final String submittedBy = resultSet.getString("JOB_SUBMITTER_NAME"); // UserID of user who started the job
        final Date startTime = resultSet.getTimestamp("START_TIME");
        final Date endTime = resultSet.getTimestamp("END_TIME");
        final JobSummary js = new JobSummary(
            bookDefinitionId,
            combinedBookDefinitionId,
            bookName,
            titleId,
            sourceType,
            jobInstanceId,
            jobExecutionId,
            batchStatus,
            submittedBy,
                userName,
            startTime,
            endTime);
        return js;
    }
}

class JobExecutionIdRowMapper implements RowMapper<Long> {
    @Override
    public Long mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        final Long id = resultSet.getLong("JOB_EXECUTION_ID");
        return id;
    }
}
