package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;

public class GroupForm {
	
	public static final String FORM_NAME = "groupForm";
	public enum DisplayGroupSortProperty { TITLE_ID, PROVIEW_DISPLAY_NAME, GROUP_NAME }
	
	private PageAndSort<DisplayGroupSortProperty> pageAndSort = new PageAndSort<DisplayGroupSortProperty>();	// sort, page, dir, objectsPerPage
	
	public GroupForm() {
		super();
	}
	public String getDir() {
		return (pageAndSort.isAscendingSort()) ? "asc" : "desc";
	}
	public Integer getPage() {
		return pageAndSort.getPageNumber();
	}
	public DisplayGroupSortProperty getSort() {
		return pageAndSort.getSortProperty();
	}
	public Integer getObjectsPerPage() {
		return pageAndSort.getObjectsPerPage();
	}
	public boolean isAscendingSort() {
		return pageAndSort.isAscendingSort();
	}
	public void setDir(String direction) {
		pageAndSort.setAscendingSort("asc".equals(direction));
	}
	public void setObjectsPerPage(Integer objectsPerPage) {
		pageAndSort.setObjectsPerPage(objectsPerPage);
	}
	public void setPage(Integer pageNumber) {
		pageAndSort.setPageNumber(pageNumber);
	}
	public void setSort(DisplayGroupSortProperty sortProperty) {
		pageAndSort.setSortProperty(sortProperty);
	}
	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}
}

