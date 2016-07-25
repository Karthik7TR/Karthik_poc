/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist.ProviewTitleForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.proviewaudit.service.ProviewAuditService;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;

@Controller
public class ProviewTitleListController {

	private ProviewClient proviewClient;
	private BookDefinitionService bookDefinitionService;
	private ProviewAuditService proviewAuditService;
	private ManagerService managerService;
	private MessageSourceAccessor messageSourceAccessor;
	private JobRequestService jobRequestService;

	private static final Logger log = LogManager.getLogger(ProviewTitleListController.class);

	/**
	 * @param httpSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ProviewTitleInfo> fetchSelectedProviewTitleInfo(HttpSession httpSession) {
		List<ProviewTitleInfo> allLatestProviewTitleInfo = (List<ProviewTitleInfo>) httpSession
				.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES);
		return allLatestProviewTitleInfo;
	}

	/**
	 * @param httpSession
	 * @param selectedProviewTitleInfo
	 */
	private void saveSelectedProviewTitleInfo(HttpSession httpSession,
			List<ProviewTitleInfo> selectedProviewTitleInfo) {
		httpSession.setAttribute(WebConstants.KEY_SELECTED_PROVIEW_TITLES, selectedProviewTitleInfo);
	}

	/**
	 * @param httpSession
	 * @param allLatestProviewTitleInfo
	 */
	private void saveAllLatestProviewTitleInfo(HttpSession httpSession,
			List<ProviewTitleInfo> allLatestProviewTitleInfo) {
		httpSession.setAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES, allLatestProviewTitleInfo);
	}

	/**
	 * @param httpSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ProviewTitleInfo> fetchAllLatestProviewTitleInfo(HttpSession httpSession) {
		List<ProviewTitleInfo> allLatestProviewTitleInfo = (List<ProviewTitleInfo>) httpSession
				.getAttribute(WebConstants.KEY_ALL_LATEST_PROVIEW_TITLES);
		return allLatestProviewTitleInfo;
	}

	/**
	 * @param httpSession
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, ProviewTitleContainer> fetchAllProviewTitleInfo(HttpSession httpSession) {
		Map<String, ProviewTitleContainer> allProviewTitleInfo = (Map<String, ProviewTitleContainer>) httpSession
				.getAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES);
		return allProviewTitleInfo;
	}

	/**
	 * @param httpSession
	 * @param allProviewTitleInfo
	 */
	private void saveAllProviewTitleInfo(HttpSession httpSession,
			Map<String, ProviewTitleContainer> allProviewTitleInfo) {
		httpSession.setAttribute(WebConstants.KEY_ALL_PROVIEW_TITLES, allProviewTitleInfo);
	}

	/**
	 * @param httpSession
	 * @return
	 */
	protected ProviewListFilterForm fetchSavedProviewListFilterForm(HttpSession httpSession) {
		ProviewListFilterForm form = (ProviewListFilterForm) httpSession.getAttribute(ProviewListFilterForm.FORM_NAME);
		if (form == null) {
			form = new ProviewListFilterForm();
		}
		return form;
	}

	/**
	 * @param httpSession
	 * @return
	 */
	protected ProviewTitleForm fetchSavedProviewTitleForm(HttpSession httpSession) {
		ProviewTitleForm form = (ProviewTitleForm) httpSession.getAttribute(ProviewTitleForm.FORM_NAME);
		return form;
	}

	/**
	 * @param httpSession
	 * @param form
	 */
	private void saveProviewTitleForm(HttpSession httpSession, ProviewTitleForm form) {
		httpSession.setAttribute(ProviewTitleForm.FORM_NAME, form);
	}

	private void sendEmail(String emailAddressString, String subject, String body) {

		ArrayList<InternetAddress> emailAddresses = new ArrayList<InternetAddress>();
		try {
			emailAddresses.add(new InternetAddress(emailAddressString));
			EmailNotification.send(emailAddresses, subject, body);
		} catch (AddressException e) {

		}
	}

	/**
	 * 
	 * @param httpSession
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_TITLES, method = RequestMethod.POST)
	public ModelAndView postSelections(@ModelAttribute ProviewTitleForm form, HttpSession httpSession, Model model)
			throws Exception {

		Command command = form.getCommand();

		switch (command) {

		case REFRESH:

			Map<String, ProviewTitleContainer> allProviewTitleInfo = proviewClient.getAllProviewTitleInfo();
			List<ProviewTitleInfo> allLatestProviewTitleInfo = proviewClient
					.getAllLatestProviewTitleInfo(allProviewTitleInfo);

			saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
			saveAllLatestProviewTitleInfo(httpSession, allLatestProviewTitleInfo);
			saveSelectedProviewTitleInfo(httpSession, allLatestProviewTitleInfo);

			if (allLatestProviewTitleInfo != null) {
				model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allLatestProviewTitleInfo);
				model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, allLatestProviewTitleInfo.size());
			}

			model.addAttribute(ProviewListFilterForm.FORM_NAME, new ProviewListFilterForm());

			ProviewTitleForm proviewTitleForm = fetchSavedProviewTitleForm(httpSession);
			if (proviewTitleForm == null) {
				proviewTitleForm = new ProviewTitleForm();
				proviewTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
				saveProviewTitleForm(httpSession, proviewTitleForm);
			}
			model.addAttribute(ProviewTitleForm.FORM_NAME, proviewTitleForm);
			model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewTitleForm.getObjectsPerPage());
			break;

		case PAGESIZE:
			saveProviewTitleForm(httpSession, form);
			List<ProviewTitleInfo> selectedProviewTitleInfo = fetchSelectedProviewTitleInfo(httpSession);
			model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewTitleInfo);
			model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, selectedProviewTitleInfo.size());
			model.addAttribute(WebConstants.KEY_PAGE_SIZE, form.getObjectsPerPage());
			model.addAttribute(ProviewListFilterForm.FORM_NAME, fetchSavedProviewListFilterForm(httpSession));
			model.addAttribute(ProviewTitleForm.FORM_NAME, form);

			break;
		}

		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES);
	}

	@RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DOWNLOAD, method = RequestMethod.GET)
	public void downloadPublishingStatsExcel(HttpSession httpSession, HttpServletRequest request,
			HttpServletResponse response) {

		ProviewListExcelExportService excelExportService = new ProviewListExcelExportService();
		try {
			Workbook wb = excelExportService.createExcelDocument(httpSession);
			Date date = new Date();
			SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
			String stringDate = s.format(date);
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition",
					"attachment; filename=" + ProviewListExcelExportService.TITLES_NAME + stringDate + ".xls");
			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 
	 * @param httpSession
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_TITLES, method = RequestMethod.GET)
	public ModelAndView allLatestProviewTitleInfo(HttpSession httpSession, Model model) throws Exception {

		List<ProviewTitleInfo> selectedProviewTitleInfo = fetchSelectedProviewTitleInfo(httpSession);

		if (selectedProviewTitleInfo == null) {

			List<ProviewTitleInfo> allLatestProviewTitleInfo = fetchAllLatestProviewTitleInfo(httpSession);
			if (allLatestProviewTitleInfo == null) {

				Map<String, ProviewTitleContainer> allProviewTitleInfo = fetchAllProviewTitleInfo(httpSession);
				try {
					if (allProviewTitleInfo == null) {

						allProviewTitleInfo = proviewClient.getAllProviewTitleInfo();
						saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
					}

					allLatestProviewTitleInfo = proviewClient.getAllLatestProviewTitleInfo(allProviewTitleInfo);
					saveAllLatestProviewTitleInfo(httpSession, allLatestProviewTitleInfo);

					selectedProviewTitleInfo = allLatestProviewTitleInfo;

					saveSelectedProviewTitleInfo(httpSession, selectedProviewTitleInfo);

				} catch (ProviewException e) {
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
							"Proview Exception occured. Please contact your administrator.");

				}
			}

		}

		if (selectedProviewTitleInfo != null) {

			model.addAttribute(WebConstants.KEY_PAGINATED_LIST, selectedProviewTitleInfo);
			model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, selectedProviewTitleInfo.size());
		}

		model.addAttribute(ProviewListFilterForm.FORM_NAME, fetchSavedProviewListFilterForm(httpSession));

		ProviewTitleForm proviewTitleForm = fetchSavedProviewTitleForm(httpSession);
		if (proviewTitleForm == null) {
			proviewTitleForm = new ProviewTitleForm();
			proviewTitleForm.setObjectsPerPage(WebConstants.DEFAULT_PAGE_SIZE);
			saveProviewTitleForm(httpSession, proviewTitleForm);
		}

		model.addAttribute(ProviewTitleForm.FORM_NAME, proviewTitleForm);
		model.addAttribute(WebConstants.KEY_PAGE_SIZE, proviewTitleForm.getObjectsPerPage());

		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLES);
	}

	/**
	 * 
	 * @param titleId
	 * @param httpSession
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_ALL_VERSIONS, method = RequestMethod.GET)
	public ModelAndView singleTitleAllVersions(@RequestParam("titleId") String titleId, HttpSession httpSession,
			Model model) throws Exception {

		Map<String, ProviewTitleContainer> allProviewTitleInfo = fetchAllProviewTitleInfo(httpSession);
		if (allProviewTitleInfo == null) {
			allProviewTitleInfo = proviewClient.getAllProviewTitleInfo();
			saveAllProviewTitleInfo(httpSession, allProviewTitleInfo);
		}

		ProviewTitleContainer proviewTitleContainer = allProviewTitleInfo.get(titleId);

		if (proviewTitleContainer != null) {
			List<ProviewTitleInfo> allTitleVersions = proviewTitleContainer.getProviewTitleInfos();
			if (allTitleVersions != null) {
				model.addAttribute(WebConstants.KEY_PAGINATED_LIST, allTitleVersions);
				model.addAttribute(WebConstants.KEY_TOTAL_BOOK_SIZE, allTitleVersions.size());

				// Check book definition exists in database
				BookDefinition bookDef = bookDefinitionService.findBookDefinitionByTitle(titleId);
				if (bookDef != null) {
					// If it exists, check to see if the book is marked as a
					// pilot book
					model.addAttribute(WebConstants.KEY_PILOT_BOOK_STATUS, bookDef.getPilotBookStatus());
					model.addAttribute(WebConstants.KEY_IS_SPLIT_BOOK, bookDef.isSplitBook());
				}

			}
		}

		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_ALL_VERSIONS);
	}

	/**
	 * 
	 * @param titleId
	 * @param versionNumber
	 * @param status
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DELETE, method = RequestMethod.GET)
	public ModelAndView proviewTitleDelete(@RequestParam("titleId") String titleId,
			@RequestParam("versionNumber") String versionNumber, @RequestParam("status") String status,
			@RequestParam("lastUpdate") String lastUpdate, Model model) throws Exception {

		model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, versionNumber);
		model.addAttribute(WebConstants.KEY_STATUS, status);
		model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM,
				new ProviewTitleForm(titleId, versionNumber, status, lastUpdate));

		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_DELETE);
	}

	/**
	 * 
	 * @param titleId
	 * @param versionNumber
	 * @param status
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_REMOVE, method = RequestMethod.GET)
	public ModelAndView proviewTitleRemove(@RequestParam("titleId") String titleId,
			@RequestParam("versionNumber") String versionNumber, @RequestParam("status") String status,
			@RequestParam("lastUpdate") String lastUpdate, Model model) throws Exception {

		model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, versionNumber);
		model.addAttribute(WebConstants.KEY_STATUS, status);
		model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM,
				new ProviewTitleForm(titleId, versionNumber, status, lastUpdate));
		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_REMOVE);
	}

	/**
	 * 
	 * @param titleId
	 * @param versionNumber
	 * @param status
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_PROMOTE, method = RequestMethod.GET)
	public ModelAndView proviewTitlePromote(@RequestParam("titleId") String titleId,
			@RequestParam("versionNumber") String versionNumber, @RequestParam("status") String status,
			@RequestParam("lastUpdate") String lastUpdate, Model model) throws Exception {

		model.addAttribute(WebConstants.KEY_TITLE_ID, titleId);
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, versionNumber);
		model.addAttribute(WebConstants.KEY_STATUS, status);
		model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM,
				new ProviewTitleForm(titleId, versionNumber, status, lastUpdate));
		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE);
	}

	/**
	 * 
	 * @param model
	 * @param titleId
	 * @param version
	 * @return
	 */
	private boolean isJobRunningForBook(Model model, String titleId, String version) {

		boolean isJobRunning = false;

		if (version.startsWith("v")) {
			version = version.substring(1);
		}
		BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(titleId);
		if (book != null) {

			if (jobRequestService.isBookInJobRequest(book.getEbookDefinitionId())) {
				Object[] args = { book.getFullyQualifiedTitleId(), "", "This book is already in the job queue" };
				String infoMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.fail", args);
				model.addAttribute(WebConstants.KEY_ERR_MESSAGE, infoMessage);
				isJobRunning = true;
			}

			else {
				JobExecution runningJobExecution = managerService.findRunningJob(book.getEbookDefinitionId());

				if (runningJobExecution != null) {
					Object[] args = { book.getFullyQualifiedTitleId(), version,
							runningJobExecution.getId().toString() };
					String infoMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.in.progress", args);
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE, infoMessage);
					isJobRunning = true;
				}
			}
		}
		return isJobRunning;
	}

	/**
	 * 
	 * @param form
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_REMOVE, method = RequestMethod.POST)
	public ModelAndView proviewTitleRemovePost(@ModelAttribute(ProviewTitleForm.FORM_NAME) ProviewTitleForm form,
			Model model) throws Exception {

		model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
		model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
		model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);

		String emailBody = "";
		String emailSubject = "Proview Remove Request Status: ";

		try {
			if (!isJobRunningForBook(model, form.getTitleId(), form.getVersion())) {
				proviewClient.removeTitle(form.getTitleId(), form.getVersion());
				model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: removed from Proview.");
				emailBody = "Title id: " + form.getTitleId() + ", version: " + form.getVersion()
						+ " removed from Proview.";
				emailSubject += "Success";
				sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
				proviewAuditService.save(form.createAudit());
			}
		} catch (Exception e) {
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed to remove from proview. " + e.getMessage());
			emailBody = "Title id: " + form.getTitleId() + ", version: " + form.getVersion()
					+ " could not be removed from Proview.";
			emailSubject += "Unsuccessful";
			sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
		}

		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_REMOVE);
	}

	/**
	 * 
	 * @param form
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_PROMOTE, method = RequestMethod.POST)
	public ModelAndView proviewTitlePromotePost(@ModelAttribute(ProviewTitleForm.FORM_NAME) ProviewTitleForm form,
			Model model) throws Exception {

		model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
		model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
		model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);

		String emailBody = "";
		String emailSubject = "Proview Promote Request Status: ";

		try {
			if (!isJobRunningForBook(model, form.getTitleId(), form.getVersion())) {
				proviewClient.promoteTitle(form.getTitleId(), form.getVersion());
				model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: promoted to Final in Proview.");

				emailBody = "Title id: " + form.getTitleId() + ", version: " + form.getVersion()
						+ " promoted from Proview.";
				emailSubject += "Success";

				sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
				proviewAuditService.save(form.createAudit());

				// TODO: clean up after ProView add notes migration changes for
				// Multi-volume.
				// Lock book so it cannot be split. Book has been promoted as
				// single title.
				BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(form.getTitleId());
				if (book != null && !book.isSplitBook()) {
					book.setIsSplitLock(true);
					bookDefinitionService.saveBookDefinition(book);
				}
			}
		} catch (Exception e) {
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
					"Failed to promote this version in proview. " + e.getMessage());
			emailBody = "Title id: " + form.getTitleId() + ", version: " + form.getVersion()
					+ " could not be promoted from Proview.";
			emailSubject += "Unsuccessful";
			sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
		}

		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_PROMOTE);
	}

	/**
	 * 
	 * @param form
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_PROVIEW_TITLE_DELETE, method = RequestMethod.POST)
	public ModelAndView proviewTitleDeletePost(@ModelAttribute(ProviewTitleForm.FORM_NAME) ProviewTitleForm form,
			Model model) throws Exception {

		model.addAttribute(WebConstants.KEY_TITLE_ID, form.getTitleId());
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, form.getVersion());
		model.addAttribute(WebConstants.KEY_STATUS, form.getStatus());
		model.addAttribute(WebConstants.KEY_PROVIEW_TITLE_INFO_FORM, form);

		String emailBody = "";
		String emailSubject = "Proview Delete Request Status: ";

		try {
			if (!isJobRunningForBook(model, form.getTitleId(), form.getVersion())) {
				proviewClient.deleteTitle(form.getTitleId(), form.getVersion());
				model.addAttribute(WebConstants.KEY_INFO_MESSAGE, "Success: deleted from Proview.");
				emailBody = "Title id: " + form.getTitleId() + ", version: " + form.getVersion()
						+ " deleted from Proview.";
				emailSubject += "Success";
				sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
				proviewAuditService.save(form.createAudit());

			}
		} catch (Exception e) {
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, "Failed to delete from proview. " + e.getMessage());
			emailBody = "Title id: " + form.getTitleId() + ", version: " + form.getVersion()
					+ " could not be deleted from Proview.";
			emailSubject += "Unsuccessful";
			sendEmail(UserUtils.getAuthenticatedUserEmail(), emailSubject, emailBody);
		}

		return new ModelAndView(WebConstants.VIEW_PROVIEW_TITLE_DELETE);
	}

	/**
	 * 
	 * @param proviewClient
	 */
	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}

	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
	}

	@Required
	public void setProviewAuditService(ProviewAuditService service) {
		this.proviewAuditService = service;
	}

	public ManagerService getManagerService() {
		return managerService;
	}

	@Required
	public void setManagerService(ManagerService managerService) {
		this.managerService = managerService;
	}

	public MessageSourceAccessor getMessageSourceAccessor() {
		return messageSourceAccessor;
	}

	@Required
	public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
		this.messageSourceAccessor = messageSourceAccessor;
	}

	public JobRequestService getJobRequestService() {
		return jobRequestService;
	}

	@Required
	public void setJobRequestService(JobRequestService jobRequestService) {
		this.jobRequestService = jobRequestService;
	}

}
