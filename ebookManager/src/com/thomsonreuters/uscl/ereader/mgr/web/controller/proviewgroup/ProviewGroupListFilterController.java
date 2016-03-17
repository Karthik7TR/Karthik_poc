package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup.ProviewGroupListFilterForm.FilterCommand;

@Controller
public class ProviewGroupListFilterController {


	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ProviewGroup> fetchAllLatestProviewGroups(
			HttpSession httpSession) {
		List<ProviewGroup> allLatestProviewGroupList = (List<ProviewGroup>) httpSession
				.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_GROUPS);
		return allLatestProviewGroupList;
	}

	/**
	 * 
	 * @param httpSession
	 * @param selectedProviewGroupList
	 */
	private void saveSelectedProviewGroups(HttpSession httpSession,
			List<ProviewGroup> selectedProviewGroupList) {
		httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS,
				selectedProviewGroupList);

	}

	/**
	 * 
	 * @param httpSession
	 * @param filterForm
	 */
	private void saveProviewGroupListFilterForm(HttpSession httpSession,
			ProviewGroupListFilterForm filterForm) {
		httpSession.setAttribute(ProviewGroupListFilterForm.FORM_NAME, filterForm);

	}

	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	protected ProviewGroupForm fetchSavedProviewGroupForm(
			HttpSession httpSession) {
		ProviewGroupForm form = (ProviewGroupForm) httpSession
				.getAttribute(ProviewGroupForm.FORM_NAME);
		if (form == null) {
			form = new ProviewGroupForm();
		}
		return form;
	}

	/**
	 * 
	 * @param httpSession
	 * @param form
	 */
	private void saveProviewGroupForm(HttpSession httpSession,
			ProviewGroupForm form) {
		httpSession.setAttribute(ProviewGroupForm.FORM_NAME, form);

	}

	/**
	 * Handle submit/post of a new set of filter criteria.
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_GROUP_LIST_FILTERED_POST, method = RequestMethod.POST)
	public ModelAndView doFilterPost(
			HttpSession httpSession,
			@ModelAttribute(ProviewGroupListFilterForm.FORM_NAME) ProviewGroupListFilterForm filterForm,
			BindingResult errors, Model model) throws Exception {

		List<ProviewGroup> selectedProviewGroupList = new ArrayList<ProviewGroup>();
		List<ProviewGroup> allLatestProviewGroupList = fetchAllLatestProviewGroups(httpSession);

		if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
			filterForm.initNull();
			selectedProviewGroupList = allLatestProviewGroupList;
		} else {

			boolean groupNameBothWayWildCard = false;
			boolean groupNameEndsWithWildCard = false;
			boolean groupNameStartsWithWildCard = false;
			boolean groupIdBothWayWildCard = false;
			boolean groupIdEndsWithWildCard = false;
			boolean groupIdStartsWithWildCard = false;
			String  groupNameSearchTerm = filterForm.getGroupName();
			String groupIdSearchTerm = filterForm.getProviewGroupID();

			if (filterForm.getGroupName() != null && filterForm.getGroupName().length() > 0 ) {
				if (filterForm.getGroupName().endsWith("%")
						&& filterForm.getGroupName().startsWith("%")) {
					groupNameBothWayWildCard = true;
				} else if (filterForm.getGroupName().endsWith("%")) {
					groupNameStartsWithWildCard  = true;

				} else if (filterForm.getGroupName().startsWith("%")) {
					groupNameEndsWithWildCard = true;
				}

				groupNameSearchTerm = groupNameSearchTerm
						.replaceAll("%", "");
			}

			if (filterForm.getProviewGroupID() != null && filterForm.getProviewGroupID().length() > 0) {
				if (filterForm.getProviewGroupID().endsWith("%")
						&& filterForm.getProviewGroupID().startsWith("%")) {
					groupIdBothWayWildCard = true;
				} else if (filterForm.getProviewGroupID().endsWith("%")) {
					groupIdStartsWithWildCard = true;

				} else if (filterForm.getProviewGroupID().startsWith("%")) {
					groupIdEndsWithWildCard = true;
				}

				groupIdSearchTerm = groupIdSearchTerm.replaceAll("%", "");
			}

			for (ProviewGroup proviewGroup : allLatestProviewGroupList) {

				boolean selected = true;

				if (groupNameSearchTerm != null && groupNameSearchTerm.length() > 0) {

					if (proviewGroup.getGroupName() == null) {
						selected = false;
					} else {
						if (groupNameBothWayWildCard) {
							if (!proviewGroup.getGroupName().contains(
									groupNameSearchTerm)) {
								selected = false;
							}
						}

						else if (groupNameEndsWithWildCard) {
							if (!proviewGroup.getGroupName().endsWith(
									groupNameSearchTerm)) {
								selected = false;
							}
						} else if (groupNameStartsWithWildCard) {
							if (!proviewGroup.getGroupName().startsWith(
									groupNameSearchTerm)) {
								selected = false;
							}
						} else if (!proviewGroup.getGroupName().equals(
								groupNameSearchTerm)) {
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
								if (!proviewGroup.getGroupId().contains(
										groupIdSearchTerm)) {
									selected = false;
								}
							}

							else if (groupIdEndsWithWildCard) {
								if (!proviewGroup.getGroupId().endsWith(
										groupIdSearchTerm)) {
									selected = false;
								}
							} else if (groupIdStartsWithWildCard) {
								if (!proviewGroup.getGroupId().startsWith(
										groupIdSearchTerm)) {
									selected = false;
								}
							} else if (!proviewGroup.getGroupId().equals(
									groupIdSearchTerm)) {
								selected = false;
							}

						}

					}

				}				

				if (selected) {
					selectedProviewGroupList.add(proviewGroup);
				}

			}
		}

		saveSelectedProviewGroups(httpSession, selectedProviewGroupList);
		saveProviewGroupListFilterForm(httpSession, filterForm);

		ProviewGroupForm proviewGroupForm = fetchSavedProviewGroupForm(httpSession);
		if (proviewGroupForm == null) {
			proviewGroupForm = new ProviewGroupForm();
			proviewGroupForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
			saveProviewGroupForm(httpSession, proviewGroupForm);
		}
		model.addAttribute(ProviewGroupForm.FORM_NAME, proviewGroupForm);
		model.addAttribute(WebConstants.KEY_PAGE_SIZE,
				proviewGroupForm.getObjectsPerPage());

		model.addAttribute(WebConstants.KEY_PAGINATED_LIST,
				selectedProviewGroupList);
		model.addAttribute(WebConstants.KEY_TOTAL_GROUP_SIZE,
				selectedProviewGroupList.size());
		model.addAttribute(ProviewGroupListFilterForm.FORM_NAME, filterForm);

		return new ModelAndView(WebConstants.VIEW_PROVIEW_GROUPS);
	}

}
