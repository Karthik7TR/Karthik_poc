package com.thomsonreuters.uscl.ereader.mgr.web.controller.job.summary;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class JobSummaryForm
{
    public static final String FORM_NAME = "jobSummaryForm";

    public enum DisplayTagSortProperty
    {
        TITLE_ID,
        BOOK_NAME,
        SOURCE_TYPE,
        START_TIME,
        BATCH_STATUS,
        SUBMITTED_BY,
        JOB_INSTANCE_ID,
        JOB_EXECUTION_ID
    }

    public enum JobCommand
    {
        STOP_JOB,
        RESTART_JOB
    };

    private JobCommand command;
    private Long[] jobExecutionIds;
    private PageAndSort<DisplayTagSortProperty> pageAndSort = new PageAndSort<>(); // sort, page, dir, objectsPerPage

    /** Discriminator between which post action is to be performed. */
    public JobCommand getJobCommand()
    {
        return command;
    }

    /** Selected Job execution ID for multi-select. */
    public Long[] getJobExecutionIds()
    {
        return jobExecutionIds;
    }

    public String getDir()
    {
        return (pageAndSort.isAscendingSort()) ? "asc" : "desc";
    }

    public Integer getPage()
    {
        return pageAndSort.getPageNumber();
    }

    public DisplayTagSortProperty getSort()
    {
        return pageAndSort.getSortProperty();
    }

    public Integer getObjectsPerPage()
    {
        return pageAndSort.getObjectsPerPage();
    }

    public boolean isAscendingSort()
    {
        return pageAndSort.isAscendingSort();
    }

    public void setJobCommand(final JobCommand command)
    {
        this.command = command;
    }

    public void setJobExecutionIds(final Long[] ids)
    {
        jobExecutionIds = ids;
    }

    public void setDir(final String direction)
    {
        pageAndSort.setAscendingSort("asc".equals(direction));
    }

    public void setObjectsPerPage(final Integer objectsPerPage)
    {
        pageAndSort.setObjectsPerPage(objectsPerPage);
    }

    public void setPage(final Integer pageNumber)
    {
        pageAndSort.setPageNumber(pageNumber);
    }

    public void setSort(final DisplayTagSortProperty sortProperty)
    {
        pageAndSort.setSortProperty(sortProperty);
    }

    @Override
    public String toString()
    {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
