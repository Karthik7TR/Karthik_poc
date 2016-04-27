/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.generate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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
import org.springframework.web.servlet.view.RedirectView;

import com.thomsonreuters.uscl.ereader.GroupDefinition;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.core.outage.service.OutageService;
import com.thomsonreuters.uscl.ereader.core.service.MiscConfigSyncService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils.SecurityRole;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.generate.GenerateBookForm.Command;
import com.thomsonreuters.uscl.ereader.mgr.web.service.ManagerService;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

@Controller
public class GenerateEbookController {
	private static final Logger log = Logger
			.getLogger(GenerateEbookController.class);
	
	private static String REMOVE_GROUP_WARNING_MESSAGE = "Groups will be removed from ProView for %s";

	private BookDefinitionService bookDefinitionService;
	private String environmentName;
	private MessageSourceAccessor messageSourceAccessor;
	private ProviewClient proviewClient;
	private GroupService groupService;
	private JobRequestService jobRequestService;
	private PublishingStatsService publishingStatsService;
	private ManagerService managerService;
	private OutageService outageService;
	private MiscConfigSyncService miscConfigService;
	private static final String REVIEW_STATUS = "Review";

	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			CoreConstants.DATE_FORMAT_PATTERN);

	/**
	 * 
	 * @param model
	 * @param form
	 * @param currentVersion
	 * @param status
	 */
	private void calculateVersionNumbers(Model model, GenerateBookForm form,
			String currentVersion, String status) {

		String newMajorVersion;
		String newMinorVersion;
		String newOverwriteVersion;
		String majorPart;
		String minorPart;
		Integer newMajorPartInteger;
		Integer newMinorPartInteger;

		if (currentVersion.equals("Not published")) {
			newMajorVersion = "1.0";
			newMinorVersion = "1.0";
			newOverwriteVersion = "1.0";
		} else {
			if (currentVersion.startsWith("v")) {
				currentVersion = currentVersion.substring(1);
			}

			if (currentVersion.contains(".")) {
				majorPart = currentVersion.substring(0,
						currentVersion.indexOf("."));
				minorPart = currentVersion.substring(currentVersion
						.indexOf(".") + 1);

				newMajorPartInteger = Integer.parseInt(majorPart) + 1;
				newMinorPartInteger = Integer.parseInt(minorPart) + 1;

			} else {
				majorPart = currentVersion;

				newMajorPartInteger = Integer.parseInt(majorPart) + 1;
				newMinorPartInteger = 1;
			}

			newMajorVersion = newMajorPartInteger.toString() + ".0";
			newMinorVersion = majorPart + "." + newMinorPartInteger.toString();
			newOverwriteVersion = currentVersion;

		}

		form.setCurrentVersion(currentVersion);
		form.setNewOverwriteVersion(newOverwriteVersion);
		form.setNewMajorVersion(newMajorVersion);
		form.setNewMinorVersion(newMinorVersion);

		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, currentVersion);
		model.addAttribute(WebConstants.KEY_NEW_OVERWRITE_VERSION_NUMBER,
				newOverwriteVersion);
		model.addAttribute(WebConstants.KEY_NEW_MAJOR_VERSION_NUMBER,
				newMajorVersion);
		model.addAttribute(WebConstants.KEY_NEW_MINOR_VERSION_NUMBER,
				newMinorVersion);
		model.addAttribute(WebConstants.BOOK_STATUS_IN_PROVIEW, status);
		model.addAttribute(WebConstants.KEY_OVERWRITE_ALLOWED,
				REVIEW_STATUS.equals(status) ? "Y" : "N");

		model.addAttribute(GenerateBookForm.FORM_NAME, form);

	}

	/**
	 * 
	 * @param model
	 * @param form
	 * @param titleId
	 * @throws Exception
	 */
	private void setModelVersion(Model model, GenerateBookForm form,
			String titleId) throws Exception {

		String currentVersion;
		String status;

		try {

			ProviewTitleInfo proviewTitleInfo = proviewClient
					.getLatestProviewTitleInfo(titleId);

			if (proviewTitleInfo == null) {
				currentVersion = "Not published";
				status = null;

			} else {
				currentVersion = proviewTitleInfo.getVersion();
				status = proviewTitleInfo.getStatus();

			}
			form.setCurrentVersion(currentVersion);
			calculateVersionNumbers(model, form, currentVersion, status);

		} catch (ProviewException e) {
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
					"Proview Exception occured. Please contact your administrator.");
			log.debug(e);
		}
	}
	
	private Integer getMajorVersion(String versionStr) {
		Double valueDouble = Double.valueOf(versionStr);
		return valueDouble.intValue();
	}
	
	private void setModelGroup(Long bookDefinitionId, BookDefinition book, Model model,
			GenerateBookForm form) {
		Integer currentVersion = getMajorVersion(form.getNewMinorVersion());
		Integer nextVersion = getMajorVersion(form.getNewMajorVersion());

		GroupDefinition currentGroup = null;
		GroupDefinition nextGroup = null;
		try {
			// Setup next groups
			if(StringUtils.isNotBlank(book.getGroupName())) {
				List<String> splitTitles = createSplitTitles(book, currentVersion);
				currentGroup = groupService.createGroupDefinition(book, GroupDefinition.VERSION_NUMBER_PREFIX + currentVersion, splitTitles);
				splitTitles = createSplitTitles(book, nextVersion);
				nextGroup = groupService.createGroupDefinition(book, GroupDefinition.VERSION_NUMBER_PREFIX + nextVersion, splitTitles);
			}
			
			GroupDefinition lastGroupDefinition = groupService.getLastGroupDefinition(book);
			if(lastGroupDefinition != null) {
				
				if(lastGroupDefinition.subgroupExists()) {
					if(StringUtils.isNotBlank(book.getGroupName()) &&
							StringUtils.isBlank(book.getSubGroupHeading())) {
						model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, "Previous group in ProView had subgroup(s). Currently, book definition is setup to have no subgroup.");
					} else if(StringUtils.isBlank(book.getGroupName())) {
						model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, 
								String.format(REMOVE_GROUP_WARNING_MESSAGE, book.getFullyQualifiedTitleId()));
					}
				} else {
					if(StringUtils.isBlank(book.getGroupName())) {
						model.addAttribute(WebConstants.KEY_WARNING_MESSAGE, 
								String.format(REMOVE_GROUP_WARNING_MESSAGE, book.getFullyQualifiedTitleId()));
					}
				}
			}
		} catch (ProviewException e) {
			String errorMessage = e.getMessage();
			if(errorMessage.equalsIgnoreCase(CoreConstants.SUBGROUP_SPLIT_ERROR_MESSAGE)) {
				// If publised, check for SubGroup Heading change 
				model.addAttribute(WebConstants.KEY_GROUP_NEXT_ERROR, e.getMessage());
			} else if(errorMessage.equalsIgnoreCase(CoreConstants.SUBGROUP_ERROR_MESSAGE)) {
				if(currentGroup != null && nextGroup == null) {
					model.addAttribute(WebConstants.KEY_GROUP_NEXT_ERROR, e.getMessage());
				} else {
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE, e.getMessage());
				}
			}
			log.debug(e);
		} catch (Exception e) {
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, e.getMessage());
			log.debug(e);
		}
		
		model.addAttribute(WebConstants.KEY_GROUP_CURRENT_PREVIEW, currentGroup);
		model.addAttribute(WebConstants.KEY_GROUP_NEXT_PREVIEW, nextGroup);
	}
	
	private List<String> createSplitTitles(BookDefinition book, Integer version) {
		List<String> splitTitles = null;
		if(book.isSplitBook()) {
			splitTitles = new ArrayList<>();
			if(book.isSplitTypeAuto()) {
				splitTitles.add("Auto Split");
			} else {
				int count = book.getSplitDocuments().size();
				String titleId = book.getFullyQualifiedTitleId();
				splitTitles.add(titleId + "/v" + version);
				for(int i = 0; i < count; i++) {
					int part = i + 2;
					String nextTitleId = titleId + "_pt" + part + "/v" + version;
					splitTitles.add(nextTitleId);
				}
			}
		}
		
		return splitTitles;
	}

	/**
	 * 
	 * @param bookDefinitionId
	 * @param book
	 * @param model
	 */
	private void setModelIsbn(Long bookDefinitionId,
			BookDefinition book, Model model) {

		boolean isPublished = publishingStatsService.hasIsbnBeenPublished(book.getIsbn(), book.getFullyQualifiedTitleId());

		// If publised, ISBN is not new
		model.addAttribute(WebConstants.KEY_IS_NEW_ISBN, isPublished ? "N": "Y");
	}

	/**
	 * 
	 * @param cutOffDate
	 * @return
	 */
	private boolean isCutOffDateGreaterOrEqualToday(Date cutOffDate) {

		boolean cutOffDateGreaterOrEqualToday = false;
		Date today = new DateTime().toDateMidnight().toDate();

		if (cutOffDate != null) {
			cutOffDateGreaterOrEqualToday = (cutOffDate == today)
					|| cutOffDate.after(today);
		}

		return cutOffDateGreaterOrEqualToday;
	}

	/**
	 * 
	 * @param titleId
	 * @param form
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW, method = RequestMethod.GET)
	public ModelAndView generateEbookPreview(@RequestParam("id") Long id,
			@ModelAttribute(GenerateBookForm.FORM_NAME) GenerateBookForm form,
			Model model) throws Exception {

		BookDefinition book = bookDefinitionService
				.findBookDefinitionByEbookDefId(id);

		if (book != null) {

			// Redirect to error page if book is marked as deleted
			if (book.isDeletedFlag()) {
				return new ModelAndView(new RedirectView(
						WebConstants.MVC_ERROR_BOOK_DELETED));
			}

			String cutOffDate = null;

			if (book.getPublishCutoffDate() != null) {
				cutOffDate = formatter.format(book.getPublishCutoffDate()
						.getTime());
			}

			model.addAttribute(WebConstants.TITLE, book.getProviewDisplayName());
			model.addAttribute(WebConstants.KEY_ISBN, book.getIsbn());
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, book);
			model.addAttribute(WebConstants.KEY_PUBLISHING_CUT_OFF_DATE,
					cutOffDate);
			model.addAttribute(
					WebConstants.KEY_USE_PUBLISHING_CUT_OFF_DATE,
					book.getDocumentTypeCodes().getUsePublishCutoffDateFlag() ? "Y"
							: "N");
			model.addAttribute(
					WebConstants.KEY_PUBLISHING_CUTOFF_DATE_EQUAL_OR_GREATER_THAN_TODAY,
					isCutOffDateGreaterOrEqualToday(book.getPublishCutoffDate()) ? "Y"
							: "N");

			model.addAttribute(WebConstants.KEY_IS_COMPLETE,
					book.getEbookDefinitionCompleteFlag());
			model.addAttribute(WebConstants.KEY_PILOT_BOOK_STATUS,
					book.getPilotBookStatus());
			model.addAttribute(WebConstants.KEY_IS_SPLIT_BOOK, book.isSplitBook());
			model.addAttribute(WebConstants.KEY_DISABLE_TITLE_FROM_SPLIT, miscConfigService.getMiscConfig().getDisableExistingSingleTitleSplit());
			
			form.setFullyQualifiedTitleId(book.getFullyQualifiedTitleId());
			setModelVersion(model, form, book.getFullyQualifiedTitleId());
			
			if(StringUtils.isNotBlank(form.getNewMajorVersion())) {
				setModelGroup(id, book, model, form);
			}
			setModelIsbn(id, book, model);

		}

		SecurityRole[] roles = { SecurityRole.ROLE_PUBLISHER,
				SecurityRole.ROLE_SUPERUSER, SecurityRole.ROLE_PUBLISHER_PLUS };
		model.addAttribute(WebConstants.KEY_SUPER_PUBLISHER_PUBLISHERPLUS,
				UserUtils.isUserInRole(roles) ? "" : "disabled=\"disabled\"");
		model.addAttribute(WebConstants.KEY_SUPER_USER_ONLY,
				UserUtils.isUserInRole(SecurityRole.ROLE_SUPERUSER) ? "" : "disabled=\"disabled\"");
		
		

		model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE,
				outageService.getAllPlannedOutagesToDisplay());
		return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_PREVIEW);
	}

	/**
	 * 
	 * @param form
	 * @param model
	 * @param session
	 * @return
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW, method = RequestMethod.POST)
	public ModelAndView doPost(
			@ModelAttribute(GenerateBookForm.FORM_NAME) GenerateBookForm form,
			Model model, HttpSession session) {

		log.debug(form);

		ModelAndView mav = null;
		String queryString = String.format("?%s=%s", WebConstants.KEY_ID,
				form.getId());
		Command command = form.getCommand();

		switch (command) {

		case GENERATE: {

			String queuePriorityLabel = form.isHighPriorityJob() ? messageSourceAccessor
					.getMessage("label.high") : messageSourceAccessor
					.getMessage("label.normal");

			String version = "";

			if (GenerateBookForm.Version.MAJOR.equals(form.getNewVersion())) {
				version = form.getNewMajorVersion();
			} else if (GenerateBookForm.Version.MINOR.equals(form
					.getNewVersion())) {
				version = form.getNewMinorVersion();
			} else if (GenerateBookForm.Version.OVERWRITE.equals(form
					.getNewVersion())) {
				version = form.getNewOverwriteVersion();
			}

			Integer priority;
			String submittedBy = UserUtils.getAuthenticatedUserName();

			BookDefinition book = bookDefinitionService
					.findBookDefinitionByEbookDefId(form.getId());

			boolean jobAlreadyQueued = jobRequestService
					.isBookInJobRequest(book.getEbookDefinitionId());

			if (jobAlreadyQueued) {
				Object[] args = { book.getFullyQualifiedTitleId(),
						queuePriorityLabel,
						"This book is already in the job queue" };
				String errMessage = messageSourceAccessor.getMessage(
						"mesg.job.enqueued.fail", args);
				model.addAttribute(WebConstants.KEY_ERR_MESSAGE, errMessage);
				log.error(errMessage);
			} else if (book.isDeletedFlag()) {

				String errMessage = messageSourceAccessor
						.getMessage("mesg.book.deleted");
				model.addAttribute(WebConstants.KEY_ERR_MESSAGE, errMessage);
				log.error(errMessage);
			} else {
				JobExecution runningJobExecution = managerService
						.findRunningJob(book.getEbookDefinitionId());
				if (runningJobExecution != null) {
					Object[] args = { book.getFullyQualifiedTitleId(), version,
							runningJobExecution.getId().toString() };
					String infoMessage = messageSourceAccessor.getMessage(
							"mesg.job.enqueued.in.progress", args);
					model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
							infoMessage);
				} else {

					try {
						if (form.isHighPriorityJob()) {
							priority = 10;
						} else {
							priority = 5;
						}

						jobRequestService.saveQueuedJobRequest(book, version,
								priority, submittedBy);

						// Report success to user in informational message on
						// page
						Object[] args = { book.getFullyQualifiedTitleId(),
								queuePriorityLabel };
						String infoMessage = messageSourceAccessor.getMessage(
								"mesg.job.enqueued.success", args);
						model.addAttribute(WebConstants.KEY_INFO_MESSAGE,
								infoMessage);

						// Set Published Once Flag to prevent user from editing
						// Book
						// Title ID
						if (!book.getPublishedOnceFlag()) {
							bookDefinitionService.updatePublishedStatus(
									book.getEbookDefinitionId(), true);
						}
					} catch (Exception e) { // Report failure on page in error
											// message
											// area
						Object[] args = { book.getFullyQualifiedTitleId(),
								queuePriorityLabel, e.getMessage() };
						String errMessage = messageSourceAccessor.getMessage(
								"mesg.job.enqueued.fail", args);
						log.error(errMessage, e);
						model.addAttribute(WebConstants.KEY_ERR_MESSAGE,
								errMessage);
					}
				}
			}
			model.addAttribute(WebConstants.TITLE_ID, book.getTitleId());
			model.addAttribute(WebConstants.TITLE, book.getProviewDisplayName());
			model.addAttribute(WebConstants.KEY_SUPER_PUBLISHER_PUBLISHERPLUS,
					"disabled=\"disabled\"");
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, book);

			form.setFullyQualifiedTitleId(book.getTitleId());

			model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE,
					outageService.getAllPlannedOutagesToDisplay());
			mav = new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_PREVIEW);

			break;
		}
		case EDIT: {
			mav = new ModelAndView(new RedirectView(
					WebConstants.MVC_BOOK_DEFINITION_EDIT + queryString));
			break;
		}
		case CANCEL: {
			session.setAttribute(WebConstants.KEY_BOOK_GENERATE_CANCEL,
					"Book generation cancelled");
			mav = new ModelAndView(new RedirectView(
					WebConstants.MVC_BOOK_DEFINITION_VIEW_GET + queryString));
			break;
		}
		case GROUP: {
			mav = new ModelAndView(new RedirectView(
					WebConstants.MVC_GROUP_DEFINITION_EDIT + queryString));
			break;
		}

		}
		return mav;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	@Required
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	public MessageSourceAccessor getMessageSourceAccessor() {
		return messageSourceAccessor;
	}

	@Required
	public void setMessageSourceAccessor(
			MessageSourceAccessor messageSourceAccessor) {
		this.messageSourceAccessor = messageSourceAccessor;
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW, method = RequestMethod.GET)
	public ModelAndView generateBulkEbookPreview(@RequestParam("id") List<Long> id,
			Model model) throws Exception {

		List<GenerateBulkBooksContainer> booksToGenerate = new ArrayList<GenerateBulkBooksContainer>();

		for (Long bookId : id) {
			BookDefinition book = bookDefinitionService
					.findBookDefinitionByEbookDefId(bookId);

			if (book != null) {
				GenerateBulkBooksContainer bookToGenerate = new GenerateBulkBooksContainer();
				bookToGenerate.setBookId(bookId);
				bookToGenerate.setFullyQualifiedTitleId(book
						.getFullyQualifiedTitleId());
				bookToGenerate.setProviewDisplayName(book
						.getProviewDisplayName());
				bookToGenerate.setDeleted(book.isDeletedFlag());

				booksToGenerate.add(bookToGenerate);

			}

		}

		model.addAttribute(WebConstants.KEY_BULK_PUBLISH_LIST, booksToGenerate);
		model.addAttribute(WebConstants.KEY_BULK_PUBLISH_SIZE,
				booksToGenerate.size());
		model.addAttribute(WebConstants.KEY_DISPLAY_OUTAGE,
				outageService.getAllPlannedOutagesToDisplay());
		return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_BULK_PREVIEW);
	}

	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
	}

	public ProviewClient getProviewClient() {
		return proviewClient;
	}

	@Required
	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}
	
	@Required
	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public JobRequestService getJobRequestService() {
		return jobRequestService;
	}

	@Required
	public void setJobRequestService(JobRequestService jobRequestService) {
		this.jobRequestService = jobRequestService;
	}

	public PublishingStatsService getPublishingStatsService() {
		return publishingStatsService;
	}

	@Required
	public void setPublishingStatsService(
			PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

	@Required
	public void setManagerService(ManagerService service) {
		this.managerService = service;
	}

	@Required
	public void setOutageService(OutageService service) {
		this.outageService = service;
	}
	
	@Required
	public void setMiscConfigSyncService(MiscConfigSyncService miscConfigService) {
		this.miscConfigService = miscConfigService;
	}
}
