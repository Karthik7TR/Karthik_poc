/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewListFilterForm.FilterCommand;

@Controller
public class ProviewListFilterController {

	@InitBinder(ProviewListFilterForm.FORM_NAME)
	protected void initDataBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));

	}

	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	private List<ProviewTitleInfo> fetchAllLatestProviewTitleInfo(
			HttpSession httpSession) {
		List<ProviewTitleInfo> allLatestProviewTitleInfo = (List<ProviewTitleInfo>) httpSession
				.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES);
		return allLatestProviewTitleInfo;
	}

	/**
	 * 
	 * @param httpSession
	 * @param selectedProviewTitleInfo
	 */
	private void saveSelectedProviewTitleInfo(HttpSession httpSession,
			List<ProviewTitleInfo> selectedProviewTitleInfo) {
		httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES,
				selectedProviewTitleInfo);

	}

	/**
	 * 
	 * @param httpSession
	 * @param filterForm
	 */
	private void saveProviewListFilterForm(HttpSession httpSession,
			ProviewListFilterForm filterForm) {
		httpSession.setAttribute(ProviewListFilterForm.FORM_NAME, filterForm);

	}

	/**
	 * Handle submit/post of a new set of filter criteria.
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_LIST_FILTERED_POST, method = RequestMethod.POST)
	public ModelAndView doFilterPost(
			HttpSession httpSession,
			@ModelAttribute(ProviewListFilterForm.FORM_NAME) ProviewListFilterForm filterForm,
			BindingResult errors, Model model) throws Exception {

		List<ProviewTitleInfo> selectedProviewTitleInfo = new ArrayList<ProviewTitleInfo>();
		List<ProviewTitleInfo> allLatestProviewTitleInfo = fetchAllLatestProviewTitleInfo(httpSession);

		if (FilterCommand.RESET.equals(filterForm.getFilterCommand())) {
			filterForm.initNull();
			selectedProviewTitleInfo = allLatestProviewTitleInfo;
		} else {

			boolean proviewDisplayNameBothWayWildCard = false;
			boolean proviewDisplayNameEndsWithWildCard = false;
			boolean proviewDisplayNameStartsWithWildCard = false;
			boolean titleIdBothWayWildCard = false;
			boolean titleIdEndsWithWildCard = false;
			boolean titleIdStartsWithWildCard = false;
			String proviewDisplayNameSearchTerm = filterForm
					.getProviewDisplayName();
			String titleIdSearchTerm = filterForm.getTitleId();

			if (filterForm.getProviewDisplayName() != null) {
				if (filterForm.getProviewDisplayName().endsWith("%")
						&& filterForm.getProviewDisplayName().startsWith("%")) {
					proviewDisplayNameBothWayWildCard = true;
				} else if (filterForm.getProviewDisplayName().endsWith("%")) {
					proviewDisplayNameStartsWithWildCard = true;

				} else if (filterForm.getProviewDisplayName().startsWith("%")) {
					proviewDisplayNameEndsWithWildCard = true;
				}

				proviewDisplayNameSearchTerm = proviewDisplayNameSearchTerm
						.replaceAll("%", "");
			}

			if (filterForm.getTitleId() != null) {
				if (filterForm.getTitleId().endsWith("%")
						&& filterForm.getTitleId().startsWith("%")) {
					titleIdBothWayWildCard = true;
				} else if (filterForm.getTitleId().endsWith("%")) {
					titleIdStartsWithWildCard = true;

				} else if (filterForm.getTitleId().startsWith("%")) {
					titleIdEndsWithWildCard = true;
				}

				titleIdSearchTerm = titleIdSearchTerm.replaceAll("%", "");
			}

			for (ProviewTitleInfo titleInfo : allLatestProviewTitleInfo) {

				boolean selected = true;

				if (proviewDisplayNameSearchTerm != null) {

					if (titleInfo.getTitle() == null) {
						selected = false;
					} else {
						if (proviewDisplayNameBothWayWildCard) {
							if (!titleInfo.getTitle().contains(
									proviewDisplayNameSearchTerm)) {
								selected = false;
							}
						}

						else if (proviewDisplayNameEndsWithWildCard) {
							if (!titleInfo.getTitle().endsWith(
									proviewDisplayNameSearchTerm)) {
								selected = false;
							}
						} else if (proviewDisplayNameStartsWithWildCard) {
							if (!titleInfo.getTitle().startsWith(
									proviewDisplayNameSearchTerm)) {
								selected = false;
							}
						} else if (!titleInfo.getTitle().equals(
								proviewDisplayNameSearchTerm)) {
							selected = false;
						}

					}

				}

				if (selected) {

					if (titleIdSearchTerm != null) {

						if (titleInfo.getTitleId() == null) {
							selected = false;
						} else {
							if (proviewDisplayNameBothWayWildCard) {
								if (!titleInfo.getTitleId().contains(
										titleIdSearchTerm)) {
									selected = false;
								}
							}

							else if (proviewDisplayNameEndsWithWildCard) {
								if (!titleInfo.getTitleId().endsWith(
										titleIdSearchTerm)) {
									selected = false;
								}
							} else if (proviewDisplayNameStartsWithWildCard) {
								if (!titleInfo.getTitleId().startsWith(
										titleIdSearchTerm)) {
									selected = false;
								}
							} else if (!titleInfo.getTitleId().equals(
									titleIdSearchTerm)) {
								selected = false;
							}

						}

					}

				}

				if (selected) {
					if (!(titleInfo.getTotalNumberOfVersions() >= filterForm
							.getMinVersionsInt())) {
						selected = false;
					}
				}

				if (selected) {
					if (!(titleInfo.getTotalNumberOfVersions() <= filterForm
							.getMaxVersionsInt())) {
						selected = false;
					}
				}

				if (selected) {
					selectedProviewTitleInfo.add(titleInfo);
				}

			}
		}

		saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo);
		saveProviewListFilterForm(httpSession, filterForm);

		model.addAttribute(WebConstants.KEY_PAGINATED_LIST,
				selectedProviewTitleInfo);
		model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE,
				selectedProviewTitleInfo.size());
		model.addAttribute(ProviewListFilterForm.FORM_NAME, filterForm);

		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES);
	}

}
