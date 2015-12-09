package com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.displaytag.pagination.PaginatedList;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.Model;

import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroup;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroupSort;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookGroupSort.SortProperty;
import com.thomsonreuters.uscl.ereader.mgr.group.service.EbookGroupService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.PageAndSort;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.group.list.GroupForm.DisplayGroupSortProperty;

public abstract class AbstractGroupController {

	protected EbookGroupService ebookGroupService;

	public EbookGroupService getEbookGroupService() {
		return ebookGroupService;
	}

	@Required
	public void setEbookGroupService(EbookGroupService ebookGroupService) {
		this.ebookGroupService = ebookGroupService;
	}

	protected static final String PAGE_AND_SORT_NAME = "groupPageAndSort";

	protected GroupListFilterForm fetchSavedGroupListForm(HttpSession httpSession) {
		GroupListFilterForm form = (GroupListFilterForm) httpSession.getAttribute(GroupListFilterForm.FORM_NAME);
		if (form == null) {
			form = new GroupListFilterForm();
		}
		return form;
	}

	@SuppressWarnings("unchecked")
	protected PageAndSort<DisplayGroupSortProperty> fetchSavedPageAndSort(HttpSession httpSession) {
		PageAndSort<DisplayGroupSortProperty> pageAndSort = (PageAndSort<DisplayGroupSortProperty>) httpSession
				.getAttribute(PAGE_AND_SORT_NAME);
		if (pageAndSort == null) {
			pageAndSort = new PageAndSort<DisplayGroupSortProperty>(1, DisplayGroupSortProperty.GROUP_NAME, false);
		}
		return pageAndSort;
	}

	/**
	 * Handles the current paging and sorting state and creates the DisplayTag
	 * PaginatedList object for use by the DisplayTag custom tag in the JSP.
	 * 
	 * @param pageAndSortForm
	 *            paging/sorting/direction/display count
	 * @param jobExecutionIds
	 *            list of
	 * @param httpSession
	 * @param model
	 */
	protected void setUpModel(GroupListFilterForm groupListForm, PageAndSort<DisplayGroupSortProperty> pageAndSort,
			HttpSession httpSession, Model model) {

		// Save filter and paging state in the session
		httpSession.setAttribute(GroupListFilterForm.FORM_NAME, groupListForm);
		httpSession.setAttribute(PAGE_AND_SORT_NAME, pageAndSort);

		model.addAttribute(GroupListFilterForm.FORM_NAME, groupListForm);

		// Create the DisplayTag VDO object - the PaginatedList which wrappers
		// the job execution partial list
		PaginatedList paginatedList = createPaginatedList(pageAndSort, groupListForm);
		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, paginatedList);
	}

	/**
	 * Create the partial paginated list used by DisplayTag to render to current
	 * page number of list list of objects.
	 * 
	 * @param pageAndSort
	 *            current page number, sort column, and sort direction
	 *            (asc/desc).
	 * @return an implemented DisplayTag paginated list interface
	 */
	private PaginatedList createPaginatedList(PageAndSort<DisplayGroupSortProperty> pageAndSort,
			GroupListFilterForm groupListForm) {
		EbookGroup ebookGroup = new EbookGroup(groupListForm.getTitleId(), groupListForm.getProviewDisplayName(),
				groupListForm.getGroupName(), groupListForm.getBookDefinitionId(), groupListForm.getVersion());
		EbookGroupSort bookGroupSort = createBookGroupSort(pageAndSort);

		// Lookup all the EbookAudit objects by their primary key
		List<EbookGroup> ebookGroups = ebookGroupService.findEbookGroups(ebookGroup, bookGroupSort);

		int numberOfGroups = ebookGroupService.totalEbookGroups(ebookGroup);
		// Instantiate the object used by DisplayTag to render a partial list
		GroupPaginatedList paginatedList = new GroupPaginatedList(ebookGroups, numberOfGroups,
				pageAndSort.getPageNumber(), pageAndSort.getObjectsPerPage(),
				(DisplayGroupSortProperty) pageAndSort.getSortProperty(), pageAndSort.isAscendingSort());
		return paginatedList;
	}

	/**
	 * Map the sort property name returned by display tag to the business object
	 * property name for sort used in the service. I.e. map a
	 * PageAndSortForm.DisplayTagSortProperty to a EbookGroupSort.SortProperty
	 * 
	 * @param dtSortProperty
	 *            display tag sort property key from the JSP
	 * @param ascendingSort
	 *            true to sort in ascending order
	 * @return a ebookAudit sort business object used by the service to fetch
	 *         the audit entities.
	 */
	protected static EbookGroupSort createBookGroupSort(PageAndSort<DisplayGroupSortProperty> pageAndSort) {
		return new EbookGroupSort(SortProperty.valueOf(pageAndSort.getSortProperty().toString()),
				pageAndSort.isAscendingSort(), pageAndSort.getPageNumber(), pageAndSort.getObjectsPerPage());
	}
}
