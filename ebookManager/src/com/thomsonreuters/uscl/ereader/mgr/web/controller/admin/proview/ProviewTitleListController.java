/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.proview;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.hibernate.envers.RevisionEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.library.domain.LibraryList;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary.BookLibraryFilterForm;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm;

@Controller
public class ProviewTitleListController {

	private ProviewClient proviewClient;

	private List<ProviewTitleInfo> fetchAllLatestProviewTitleInfo(
			HttpSession httpSession) {
		List<ProviewTitleInfo> allLatestProviewTitleInfo = (List<ProviewTitleInfo>) httpSession
				.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES);
		return allLatestProviewTitleInfo;
	}

	private void saveAllLatestProviewTitleInfo(HttpSession httpSession,
			List<ProviewTitleInfo> allLatestProviewTitleInfo) {
		httpSession.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES,
				allLatestProviewTitleInfo);

	}

	private Map<String, ProviewTitleContainer> fetchAllProviewTitleInfo(
			HttpSession httpSession) {
		Map<String, ProviewTitleContainer> allProviewTitleInfo = (Map<String, ProviewTitleContainer>) httpSession
				.getAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES);
		return allProviewTitleInfo;
	}

	private void saveAllProviewTitleInfo(HttpSession httpSession,
			Map<String, ProviewTitleContainer> allProviewTitleInfo) {
		httpSession.setAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES,
				allProviewTitleInfo);

	}

	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLES, method = RequestMethod.GET)
	public ModelAndView allLatestProviewTitleInfo(HttpSession httpSession,
			Model model) throws Exception {

		List<ProviewTitleInfo> allLatestProviewTitleInfo = fetchAllLatestProviewTitleInfo(httpSession);
		if (allLatestProviewTitleInfo == null) {

			Map<String, ProviewTitleContainer> allProviewTitleInfo = fetchAllProviewTitleInfo(httpSession);
			if (allProviewTitleInfo == null) {
				allProviewTitleInfo = proviewClient.getAllProviewTitleInfo();
				saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
			}

			allLatestProviewTitleInfo = proviewClient
					.getAllLatestProviewTitleInfo(allProviewTitleInfo);
			saveAllLatestProviewTitleInfo(httpSession,
					allLatestProviewTitleInfo);
		}

		if (allLatestProviewTitleInfo != null) {
			model.addAttribute(WebConstants.KEY_PAGINATED_LIST,
					allLatestProviewTitleInfo);
			model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE,
					allLatestProviewTitleInfo.size());
		}

		return new ModelAndView(WebConstants.VIEW_ADMIN_KEYWORD_PROVIEW_TITLES);
	}

	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLE_ALL_VERSIONS, method = RequestMethod.GET)
	public ModelAndView singleTitleAllVersions(@RequestParam String titleId,
			HttpSession httpSession, Model model) throws Exception {

		Map<String, ProviewTitleContainer> allProviewTitleInfo = fetchAllProviewTitleInfo(httpSession);
		if (allProviewTitleInfo == null) {
			allProviewTitleInfo = proviewClient.getAllProviewTitleInfo();
			saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
		}

		ProviewTitleContainer proviewTitleContainer = allProviewTitleInfo
				.get(titleId);

		if (proviewTitleContainer != null) {
			List<ProviewTitleInfo> allTitleVersions = proviewTitleContainer
					.getProviewTitleInfos();
			if (allTitleVersions != null) {
				model.addAttribute(WebConstants.KEY_PAGINATED_LIST,
						allTitleVersions);
				model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE,
						allTitleVersions.size());
			}
		}

		return new ModelAndView(
				WebConstants.VIEW_ADMIN_KEYWORD_PROVIEW_TITLE_ALL_VERSIONS);
	}

	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLE_DELETE, method = RequestMethod.GET)
	public ModelAndView proviewTitleDelete(@RequestParam String titleId,
			@RequestParam String versionNumber, @RequestParam String status,
			Model model) throws Exception {

		model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, versionNumber);
		model.addAttribute(WebConstants.KEY_STATUS, status);
		model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM,
				new ProviewTitleForm(titleId, versionNumber, status));

		return new ModelAndView(
				WebConstants.VIEW_ADMIN_KEYWORD_PROVIEW_TITLE_DELETE);
	}

	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLE_REMOVE, method = RequestMethod.GET)
	public ModelAndView allLatestProviewTitleRemove(
			@RequestParam String titleId, @RequestParam String versionNumber,
			@RequestParam String status, Model model) throws Exception {

		model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, versionNumber);
		model.addAttribute(WebConstants.KEY_STATUS, status);
		model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM,
				new ProviewTitleForm(titleId, versionNumber, status));
		return new ModelAndView(
				WebConstants.VIEW_ADMIN_KEYWORD_PROVIEW_TITLE_REMOVE);
	}

	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLE_REMOVE, method = RequestMethod.POST)
	public ModelAndView proviewTitleRemovePost(
			@ModelAttribute(ProviewTitleForm.FORM_NAME) ProviewTitleForm form,
			Model model) throws Exception {

		model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
		model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
		model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);

		try {
			proviewClient.removeTitle(form.getTitleId(), form.getVersion());
			model.addAttribute(WebConstants.KEY_INFO_MESSAGE,
					"Success removed from Proview.");

		} catch (Exception e) {
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
					"Failed to remove from proview. " + e.getMessage());
		}

		return new ModelAndView(
				WebConstants.VIEW_ADMIN_KEYWORD_PROVIEW_TITLE_REMOVE);
	}

	@RequestMapping(value = WebConstants.MVC_ADMIN_KEYWORD_PROVIEW_TITLE_DELETE, method = RequestMethod.POST)
	public ModelAndView proviewTitleDeletePost(
			@ModelAttribute(ProviewTitleForm.FORM_NAME) ProviewTitleForm form,
			Model model) throws Exception {

		model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
		model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
		model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);

		try {
			proviewClient.deleteTitle(form.getTitleId(), form.getVersion());
			model.addAttribute(WebConstants.KEY_INFO_MESSAGE,
					"Success deleted from Proview.");

		} catch (Exception e) {
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
					"Failed to delete from proview. " + e.getMessage());
		}

		return new ModelAndView(
				WebConstants.VIEW_ADMIN_KEYWORD_PROVIEW_TITLE_DELETE);
	}

	public ProviewClient getProviewClient() {
		return proviewClient;
	}

	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
}
