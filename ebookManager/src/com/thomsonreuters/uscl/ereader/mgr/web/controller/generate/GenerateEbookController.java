package com.thomsonreuters.uscl.ereader.mgr.web.controller.generate;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
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

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.mgr.web.UserUtils;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.view.ViewBookDefinitionForm.Command;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunner;

@Controller
public class GenerateEbookController {
	private static final Logger log = Logger
			.getLogger(GenerateEbookController.class);

	private BookDefinitionService bookDefinitionService;
	private String environmentName;
	private MessageSourceAccessor messageSourceAccessor;
	private ProviewClient proviewClient;
	private JobRequestService jobRequestService;

	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"MM/dd/yyyy");
	String newMajorVersion;
	String newMinorVersion;
	String currentVersion;

	/**
	 * 
	 * @param currentVersion
	 * @param model
	 */
	private void calculateVersionNumbers(Model model) {

		if (currentVersion.equals("Not published")) {
			newMajorVersion = "v1";
			newMinorVersion = "v1";
		} else {
			Double currentVersionDouble = Double.parseDouble(currentVersion
					.substring(1));
			Integer newMajorVersionDouble = (int) (Math
					.floor(currentVersionDouble) + 1);
			Double newMinorVersionDouble = Math.floor(currentVersionDouble) + 0.10;

			newMajorVersion = "v" + newMajorVersionDouble;
			newMinorVersion = "v" + newMinorVersionDouble;

		}
		model.addAttribute(WebConstants.KEY_VERSION_NUMBER, currentVersion);
		model.addAttribute(WebConstants.KEY_NEW_MAJOR_VERSION_NUMBER,
				newMajorVersion);
		model.addAttribute(WebConstants.KEY_NEW_MINOR_VERSION_NUMBER,
				newMinorVersion);

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
	public ModelAndView generateEbookPreview(@RequestParam Long id,
			@ModelAttribute(GenerateBookForm.FORM_NAME) GenerateBookForm form,
			Model model) throws Exception {

		BookDefinition book = bookDefinitionService
				.findBookDefinitionByEbookDefId(id);

		if (book != null) {

			String cutOffDate = null;

			if (book.getPublishCutoffDate() != null) {
				cutOffDate = formatter.format(book.getPublishCutoffDate()
						.getTime());
			}
			ProviewTitleInfo proviewTitleInfo = proviewClient
					.getLatestProviewTitleInfo(book.getFullyQualifiedTitleId());

			model.addAttribute(WebConstants.TITLE, book.getProviewDisplayName());
			model.addAttribute(WebConstants.KEY_ISBN, book.getIsbn());
			model.addAttribute(WebConstants.KEY_MATERIAL_ID,
					book.getMaterialId());
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, book);
			model.addAttribute(WebConstants.KEY_PUBLISHING_CUT_OFF_DATE,
					cutOffDate);
			model.addAttribute(WebConstants.KEY_USE_PUBLISHING_CUT_OFF_DATE,
					book.getDocumentTypeCodes().getUsePublishCutoffDateFlag());

			if (proviewTitleInfo == null) {
				currentVersion = "Not published";

			} else {
				currentVersion = proviewTitleInfo.getVesrion();

			}
			calculateVersionNumbers(model);

		}

		model.addAttribute(WebConstants.KEY_GENERATE_BUTTON_VISIBILITY,
				UserUtils.isSuperUser() ? "" : "disabled=\"disabled\"");

		return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_PREVIEW);
	}

	/**
	 * 
	 * @param form
	 * @param model
	 * @return
	 */
	@RequestMapping(value = WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW, method = RequestMethod.POST)
	public ModelAndView doPost(
			@ModelAttribute(GenerateBookForm.FORM_NAME) GenerateBookForm form,
			Model model) {

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

			String version;
			Integer priority;
			String submittedBy = UserUtils.getAuthenticatedUserName();

			BookDefinition book = bookDefinitionService
					.findBookDefinitionByEbookDefId(form.getId());

			try {
				if (form.isHighPriorityJob()) {
					priority = 10;
				} else {
					priority = 5;
				}

				if (form.isMajorVersion()) {
					version = newMajorVersion;
				} else {
					version = newMinorVersion;
				}

				jobRequestService.saveQueuedJobRequest(form.getId(), version,
						priority, submittedBy);

				// Report success to user in informational message on page
				Object[] args = { book.getTitleId(), queuePriorityLabel };
				String infoMessage = messageSourceAccessor.getMessage(
						"mesg.job.enqueued.success", args);
				model.addAttribute(WebConstants.KEY_INFO_MESSAGE, infoMessage);
			} catch (Exception e) { // Report failure on page in error message
									// area
				Object[] args = { book.getTitleId(), queuePriorityLabel,
						e.getMessage() };
				String errMessage = messageSourceAccessor.getMessage(
						"mesg.job.enqueued.fail", args);
				log.error(errMessage, e);
				model.addAttribute(WebConstants.KEY_ERR_MESSAGE, errMessage);
			}

			model.addAttribute(WebConstants.TITLE_ID, book.getTitleId());
			model.addAttribute(WebConstants.TITLE, book.getProviewDisplayName());
			model.addAttribute(WebConstants.KEY_GENERATE_BUTTON_VISIBILITY,
					"disabled=\"disabled\"");
			model.addAttribute(WebConstants.KEY_BOOK_DEFINITION, book);

			form.setFullyQualifiedTitleId(book.getTitleId());
			mav = new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_PREVIEW);
			break;
		}
		case EDIT: {
			mav = new ModelAndView(new RedirectView(
					WebConstants.MVC_BOOK_DEFINITION_EDIT + queryString));
			break;
		}

		}
		return mav;
	}

	@Required
	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	@Required
	public MessageSourceAccessor getMessageSourceAccessor() {
		return messageSourceAccessor;
	}

	public void setMessageSourceAccessor(
			MessageSourceAccessor messageSourceAccessor) {
		this.messageSourceAccessor = messageSourceAccessor;
	}

	@RequestMapping(value = WebConstants.MVC_BOOK_BULK_GENERATE_PREVIEW, method = RequestMethod.GET)
	public ModelAndView generateBulkEbookPreview(@RequestParam List<Long> id,
			Model model) throws Exception {

		return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_BULK_PREVIEW);
	}

	@Required
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
	}

	@Required
	public ProviewClient getProviewClient() {
		return proviewClient;
	}

	public void setProviewClient(ProviewClient proviewClient) {
		this.proviewClient = proviewClient;
	}

	@Required
	public JobRequestService getJobRequestService() {
		return jobRequestService;
	}

	public void setJobRequestService(JobRequestService jobRequestService) {
		this.jobRequestService = jobRequestService;
	}
}
