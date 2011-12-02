package com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.jobsummary;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.displaytag.pagination.PaginatedList;
import org.displaytag.properties.SortOrderEnum;
import org.springframework.batch.core.JobParameter;

import com.thomsonreuters.uscl.ereader.orchestrate.core.engine.EngineConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.WebConstants.SortProperty;
import com.thomsonreuters.uscl.ereader.orchestrate.dashboard.web.controller.JobExecutionVdo;

/**
 * A DisplayTag PaginatedList implementation for paging through the part lists of job executions.
 * Note that the JobExecution business object is wrappered by a JobExecutionVdo to allow for the exposing
 * of presentation related properties.
 */
public class JobExecutionPaginatedList implements PaginatedList {
	//private static final Logger log = Logger.getLogger(JobExecutionPaginatedList.class);
	private Comparator<JobExecutionVdo> startTimeComparator = new StartTimeComparator();
	private Map<SortProperty, Comparator<JobExecutionVdo>> comparatorMap = createComparatorMap(startTimeComparator);
	
	private List<JobExecutionVdo> partialList;
	private int pageNumber;		// Which page number of data is this
	private int fullListSize;   // The size of the entire population of elements that are to be displayed in a paginated fashion
	private int itemsPerPage;	// How many rows are to be shown on each page
	private SortProperty sortProperty;	// Indicated the JobExecution property that we want to sort by
	private boolean ascending;	// True if the list is sorted in the ascending direction
	
	/**
	 * Create the PaginatedList used for paging and sorting operations by DisplayTag.
	 * None the parameters may be null.
	 */
	public JobExecutionPaginatedList(List<JobExecutionVdo> partialList, int fullListSize, 
									 int pageNumber, int itemsPerPage,
									 SortProperty property, boolean ascending) {
		this.partialList = partialList;
		this.fullListSize = fullListSize;
		this.pageNumber = pageNumber;
		this.itemsPerPage = itemsPerPage;
		this.sortProperty = property;
		this.ascending = ascending;
		this.sortList();
	}
	
	@Override
	public int getFullListSize() {
		return fullListSize;
	}

	@Override
	public List<JobExecutionVdo> getList() {
		return partialList;
	}

	@Override
	public int getObjectsPerPage() {
		return itemsPerPage;
	}

	@Override
	public int getPageNumber() {
		return pageNumber;
	}

	@Override
	public String getSearchId() {
		return null;
	}

	@Override
	/** Returns of of the SortProperty values */
	public String getSortCriterion() {
		return sortProperty.toString();
	}

	@Override
	public SortOrderEnum getSortDirection() {
		return (ascending) ? SortOrderEnum.ASCENDING : SortOrderEnum.DESCENDING;
	}
	public boolean isAscendingSort() {
		return ascending;
	}
	
	public void sortList() {
		sortList(sortProperty, ascending);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void sortList(SortProperty property, boolean ascendingSort) {
		this.sortProperty = property;
		this.ascending = ascendingSort;
		Comparator comparator = comparatorMap.get(sortProperty);
		Collections.sort(partialList, comparator);
	}
	
	/**
	 * Creates a map of the sort property key to the comparator value used to sort the partialList by that property.
	 */
	private Map<SortProperty, Comparator<JobExecutionVdo>> createComparatorMap(Comparator<JobExecutionVdo> startTimeComparator) {
		Map<SortProperty, Comparator<JobExecutionVdo>> map = new HashMap<SortProperty, Comparator<JobExecutionVdo>>();
		map.put(SortProperty.BOOK, new BookComparator());
		map.put(SortProperty.INSTANCE_ID, new InstanceIdComparator());
		map.put(SortProperty.BATCH_STATUS, new BatchStatusComparator());
		map.put(SortProperty.START_TIME, startTimeComparator);
		map.put(SortProperty.EXECUTION_TIME, new ExecutionTimeComparator());
		return map;
	}
	
//	class JobNameComparator implements Comparator<JobExecutionVdo> {
//		public int compare(JobExecutionVdo je1, JobExecutionVdo je2) {
//			int result = 0;
//			if (je1.getJobExecution().getJobInstance().getJobName() != null) {
//				result = je1.getJobExecution().getJobInstance().getJobName().compareTo(je2.getJobExecution().getJobInstance().getJobName());
//			}
//			return ((ascending) ? result : -result);
//		}
//	}
	
	
	class BookComparator implements Comparator<JobExecutionVdo> {
		public int compare(JobExecutionVdo je1, JobExecutionVdo je2) {
			int result = 0;
			JobParameter book1Param = je1.getJobExecution().getJobInstance().getJobParameters().getParameters().get(EngineConstants.JOB_PARAM_BOOK_CODE);
			if (book1Param != null) {
				JobParameter book2Param = je2.getJobExecution().getJobInstance().getJobParameters().getParameters().get(EngineConstants.JOB_PARAM_BOOK_CODE);
				if (book2Param != null) {
					String book1Code = (String) book1Param.getValue();
					String book2Code = (String) book2Param.getValue();
					if (book1Code != null) {
						if (book2Code != null) {
							result = book1Code.compareTo(book2Code);
						} else {
							result = 1;
						}
					} else {
						result = -1;
					}
				} else {
					result = 1;
				}
			} else {
				result = -1;
			}
			return ((ascending) ? result : -result);
		}
	}
	
	class InstanceIdComparator implements Comparator<JobExecutionVdo> {
		public int compare(JobExecutionVdo je1, JobExecutionVdo je2) {
			int result = 0;
			if (je1.getJobExecution().getJobInstance().getId() != null) {
				result = je1.getJobExecution().getJobInstance().getId().compareTo(je2.getJobExecution().getJobInstance().getId());
			}
			return ((ascending) ? result : -result);
		}
	}

	class BatchStatusComparator implements Comparator<JobExecutionVdo> {
		public int compare(JobExecutionVdo je1, JobExecutionVdo je2) {
			int result = 0;
			if (je1.getJobExecution().getStartTime() != null) {
				result = je1.getJobExecution().getStatus().toString().compareTo(je2.getJobExecution().getStatus().toString());
			}
			return ((ascending) ? result : -result);
		}
	}
	
	class StartTimeComparator implements Comparator<JobExecutionVdo> {
		public int compare(JobExecutionVdo je1, JobExecutionVdo je2) {
			int result = 0;
			if (je1.getJobExecution().getStartTime() != null) {
				result = je1.getJobExecution().getStartTime().compareTo(je2.getJobExecution().getStartTime());
			}
			return ((ascending) ? result : -result);
		}
	}
	
	class ExecutionTimeComparator implements Comparator<JobExecutionVdo> {
		public int compare(JobExecutionVdo je1, JobExecutionVdo je2) {
			int result = 0;
			if (je1.getExecutionDurationMs() > -1) {
				result = (int) (je1.getExecutionDurationMs() - je2.getExecutionDurationMs());
			}
			return ((ascending) ? result : -result);
		}
	}
}	
