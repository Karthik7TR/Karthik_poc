package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public abstract class BaseProviewGroupListController {

	protected List<ProviewGroup> filterProviewGroupList(ProviewGroupListFilterForm filterForm,
			List<ProviewGroup> allLatestProviewGroups) {

		List<ProviewGroup> selectedProviewGroupList = new ArrayList<ProviewGroup>();

		boolean groupNameBothWayWildCard = false;
		boolean groupNameEndsWithWildCard = false;
		boolean groupNameStartsWithWildCard = false;
		boolean groupIdBothWayWildCard = false;
		boolean groupIdEndsWithWildCard = false;
		boolean groupIdStartsWithWildCard = false;
		String groupNameSearchTerm = filterForm.getGroupName();
		String groupIdSearchTerm = filterForm.getProviewGroupID();

		if (groupNameSearchTerm != null && groupNameSearchTerm.length() > 0) {
			if (groupNameSearchTerm.endsWith("%") && groupNameSearchTerm.startsWith("%")) {
				groupNameBothWayWildCard = true;

			} else if (groupNameSearchTerm.endsWith("%")) {
				groupNameStartsWithWildCard = true;

			} else if (groupNameSearchTerm.startsWith("%")) {
				groupNameEndsWithWildCard = true;
			}

			groupNameSearchTerm = groupNameSearchTerm.replaceAll("%", "");
		}

		if (groupIdSearchTerm != null && groupIdSearchTerm.length() > 0) {
			if (groupIdSearchTerm.endsWith("%") && groupIdSearchTerm.startsWith("%")) {
				groupIdBothWayWildCard = true;

			} else if (groupIdSearchTerm.endsWith("%")) {
				groupIdStartsWithWildCard = true;

			} else if (groupIdSearchTerm.startsWith("%")) {
				groupIdEndsWithWildCard = true;
			}

			groupIdSearchTerm = groupIdSearchTerm.replaceAll("%", "");
		}

		for (ProviewGroup proviewGroup : allLatestProviewGroups) {

			boolean selected = true;

			if (groupNameSearchTerm != null && groupNameSearchTerm.length() > 0) {
				if (proviewGroup.getGroupName() == null) {
					selected = false;
				} else {
					if (groupNameBothWayWildCard) {
						if (!proviewGroup.getGroupName().contains(groupNameSearchTerm)) {
							selected = false;
						}
					} else if (groupNameEndsWithWildCard) {
						if (!proviewGroup.getGroupName().endsWith(groupNameSearchTerm)) {
							selected = false;
						}
					} else if (groupNameStartsWithWildCard) {
						if (!proviewGroup.getGroupName().startsWith(groupNameSearchTerm)) {
							selected = false;
						}
					} else if (!proviewGroup.getGroupName().equals(groupNameSearchTerm)) {
						selected = false;
					}
				}
			}
			if (selected) {
				if (groupIdSearchTerm != null && groupIdSearchTerm.length() > 0) {
					if (proviewGroup.getGroupId() == null) {
						selected = false;
					} else {

						if (groupIdBothWayWildCard) {
							if (!proviewGroup.getGroupId().contains(groupIdSearchTerm)) {
								selected = false;
							}
						} else if (groupIdEndsWithWildCard) {
							if (!proviewGroup.getGroupId().endsWith(groupIdSearchTerm)) {
								selected = false;
							}
						} else if (groupIdStartsWithWildCard) {
							if (!proviewGroup.getGroupId().startsWith(groupIdSearchTerm)) {
								selected = false;
							}
						} else if (!proviewGroup.getGroupId().equals(groupIdSearchTerm)) {
							selected = false;
						}
					}
				}
			}
			if (selected) {
				selectedProviewGroupList.add(proviewGroup);
			}
		}
		return selectedProviewGroupList;
	}

	/**
	 * @param httpSession
	 * @param filterForm
	 */
	protected void saveProviewGroupListFilterForm(HttpSession httpSession, ProviewGroupListFilterForm filterForm) {
		httpSession.setAttribute(ProviewGroupListFilterForm.FORM_NAME, filterForm);
	}

	/**
	 * @param httpSession
	 * @return
	 */
	protected ProviewGroupListFilterForm fetchProviewGroupListFilterForm(HttpSession httpSession) {
		ProviewGroupListFilterForm form = (ProviewGroupListFilterForm) httpSession
				.getAttribute(ProviewGroupListFilterForm.FORM_NAME);
		if (form == null) {
			form = new ProviewGroupListFilterForm();
		}
		return form;
	}

	/**
	 * @param httpSession
	 * @param allProviewGroups
	 */
	protected void saveAllProviewGroups(HttpSession httpSession, Map<String, ProviewGroupContainer> allProviewGroups) {
		httpSession.setAttribute(WebConstants.KEY_ALL_PROVIEW_GROUPS, allProviewGroups);
	}

	/**
	 * @param httpSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, ProviewGroupContainer> fetchAllProviewGroups(HttpSession httpSession) {
		Map<String, ProviewGroupContainer> allProviewGroups = (Map<String, ProviewGroupContainer>) httpSession
				.getAttribute(WebConstants.KEY_ALL_PROVIEW_GROUPS);
		return allProviewGroups;
	}

	/**
	 * @param httpSession
	 * @param selectedProviewGroupList
	 */
	protected void saveSelectedProviewGroups(HttpSession httpSession, List<ProviewGroup> selectedProviewGroupList) {
		httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS, selectedProviewGroupList);
	}

	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<ProviewGroup> fetchSelectedProviewGroups(HttpSession httpSession) {
		return (List<ProviewGroup>) httpSession.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS);
	}

	protected void savePaginatedList(HttpSession httpSession, List<GroupDetails> groupDetailsList) {
		httpSession.setAttribute(WebConstants.KEY_PAGINATED_LIST, groupDetailsList);
	}

	@SuppressWarnings("unchecked")
	protected List<GroupDetails> fetchPaginatedList(HttpSession httpSession) {
		return (List<GroupDetails>) httpSession.getAttribute(WebConstants.KEY_PAGINATED_LIST);
	}

	/**
	 * @param httpSession
	 * @param form
	 */
	protected void saveProviewGroupForm(HttpSession httpSession, ProviewGroupForm form) {
		httpSession.setAttribute(ProviewGroupForm.FORM_NAME, form);
	}

	/**
	 * @param httpSession
	 * @return
	 */
	protected ProviewGroupForm fetchProviewGroupForm(HttpSession httpSession) {
		ProviewGroupForm form = (ProviewGroupForm) httpSession.getAttribute(ProviewGroupForm.FORM_NAME);
		if (form == null) {
			form = new ProviewGroupForm();
		}
		return form;
	}

	/**
	 * @param httpSession
	 * @param allLatestProviewGroups
	 */
	protected void saveAllLatestProviewGroups(HttpSession httpSession, List<ProviewGroup> allLatestProviewGroups) {
		httpSession.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS, allLatestProviewGroups);
	}

	/**
	 * @param httpSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<ProviewGroup> fetchAllLatestProviewGroups(HttpSession httpSession) {
		List<ProviewGroup> allLatestProviewGroupList = (List<ProviewGroup>) httpSession
				.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS);
		return allLatestProviewGroupList;
	}
}
