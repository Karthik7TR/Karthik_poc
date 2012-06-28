/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

@Controller
public class PublishingStatsController {

	private PublishingStatsService publishingStatsService;

	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	protected PublishingStatsForm fetchSavedPublishingStatsForm(
			HttpSession httpSession) {

		PublishingStatsForm form = (PublishingStatsForm) httpSession
				.getAttribute(PublishingStatsForm.FORM_NAME);

		if (form == null) {
			form = new PublishingStatsForm();
		}
		return form;
	}

	/**
	 * 
	 * @param httpSession
	 * @param form
	 */
	private void savePublishingStatsForm(HttpSession httpSession,
			PublishingStatsForm form) {
		httpSession.setAttribute(PublishingStatsForm.FORM_NAME, form);

	}

	/**
	 * 
	 * @param httpSession
	 * @return
	 */
	private List<PublishingStats> fetchPublishingStatsList(
			HttpSession httpSession) {
		List<PublishingStats> publishingStats = (List<PublishingStats>) httpSession
				.getAttribute(WebConstants.KEY_PUBLISHING_STATS_LIST);
		return publishingStats;
	}

	/**
	 * 
	 * @param httpSession
	 * @param publishingStatsList
	 */
	private void savePublishingStatsList(HttpSession httpSession,
			List<PublishingStats> publishingStatsList) {
		httpSession.setAttribute(WebConstants.KEY_PUBLISHING_STATS_LIST,
				publishingStatsList);

	}

	@RequestMapping(value = WebConstants.MVC_STATS, method = RequestMethod.GET)
	public ModelAndView publishingStatsGet(Model model, HttpSession httpSession)
			throws Exception {

		List<PublishingStats> publishingStatsList = fetchPublishingStatsList(httpSession);

		if (publishingStatsList == null) {
			publishingStatsList = publishingStatsService
					.findAllPublishingStats();
			savePublishingStatsList(httpSession, publishingStatsList);
		}

		PublishingStatsForm form = fetchSavedPublishingStatsForm(httpSession);
		if (form.getObjectsPerPage() == null) {
			form.setObjectsPerPage("5");
		}

		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, publishingStatsList);
		model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE,
				publishingStatsList.size());
		model.addAttribute(WebConstants.KEY_PAGE_SIZE, form.getObjectsPerPage());
		model.addAttribute(PublishingStatsForm.FORM_NAME, form);

		return new ModelAndView(WebConstants.VIEW_STATS);
	}

	@RequestMapping(value = WebConstants.MVC_STATS, method = RequestMethod.POST)
	public ModelAndView postSelections(
			@ModelAttribute PublishingStatsForm form, HttpSession httpSession,
			Model model) throws Exception {

		List<PublishingStats> publishingStatsList = fetchPublishingStatsList(httpSession);

		model.addAttribute(WebConstants.KEY_PAGINATED_LIST, publishingStatsList);
		model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE,
				publishingStatsList.size());
		model.addAttribute(WebConstants.KEY_PAGE_SIZE, form.getObjectsPerPage());
		model.addAttribute(PublishingStatsForm.FORM_NAME, form);
		savePublishingStatsForm(httpSession, form);

		return new ModelAndView(WebConstants.VIEW_STATS);
	}

	@Required
	public PublishingStatsService getPublishingStatsService() {
		return publishingStatsService;
	}

	public void setPublishingStatsService(
			PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

}
