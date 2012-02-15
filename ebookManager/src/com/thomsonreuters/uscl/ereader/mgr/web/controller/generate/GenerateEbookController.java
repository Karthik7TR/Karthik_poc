package com.thomsonreuters.uscl.ereader.mgr.web.controller.generate;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinitionKey;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;
import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunner;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;


import org.apache.log4j.Logger;

@Controller
public class GenerateEbookController {
	private static final Logger log = Logger
			.getLogger(GenerateEbookController.class);

	private CoreService coreService;
	private String environmentName;
	private JobRunner jobRunner;
	private MessageSourceAccessor messageSourceAccessor;

	@RequestMapping(value = WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW, method = RequestMethod.GET)
	public ModelAndView generateEbookPreview(@RequestParam String titleId,
			@ModelAttribute(GenerateBookForm.FORM_NAME) GenerateBookForm form,
			Model model) throws Exception {

		BookDefinition book = coreService
				.findBookDefinition(new BookDefinitionKey(titleId));

		model.addAttribute(WebConstants.TITLE_ID, titleId);
		model.addAttribute(WebConstants.TITLE, book.getBookName());

		form.setFullyQualifiedTitleId(titleId);

		return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_PREVIEW);
	}

	@RequestMapping(value=WebConstants.MVC_BOOK_SINGLE_GENERATE_PREVIEW, method = RequestMethod.POST)
	public ModelAndView doPost(@ModelAttribute(GenerateBookForm.FORM_NAME) GenerateBookForm form,
			Model model) {
		
		log.debug(form);
		String queuePriorityLabel = form.isHighPriorityJob() ? messageSourceAccessor.getMessage("label.high") :
															   messageSourceAccessor.getMessage("label.normal");
		String userName = null;  // TODO
		String userEmail = null;	// TODO

		BookDefinitionKey bookDefKey = form.getBookDefinitionKey();
		JobRunRequest jobRunRequest = JobRunRequest.create(bookDefKey, userName, userEmail);
		try {
			if (form.isHighPriorityJob()) {
				jobRunner.enqueueHighPriorityJobRunRequest(jobRunRequest);
			} else {
				jobRunner.enqueueNormalPriorityJobRunRequest(jobRunRequest);
			}
			// Report success to user in informational message on page
			Object[] args = { bookDefKey.getFullyQualifiedTitleId(), queuePriorityLabel};
			String infoMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.success", args);
			model.addAttribute(WebConstants.KEY_INFO_MESSAGE, infoMessage);
		} catch (Exception e) {	// Report failure on page in error message area
			Object[] args = { bookDefKey.getFullyQualifiedTitleId(), queuePriorityLabel, e.getMessage()};
			String errMessage = messageSourceAccessor.getMessage("mesg.job.enqueued.fail", args);
			log.error(errMessage, e);
			model.addAttribute(WebConstants.KEY_ERR_MESSAGE, errMessage);
		}
		
		BookDefinition book = coreService
				.findBookDefinition(new BookDefinitionKey(bookDefKey.getFullyQualifiedTitleId()));
		model.addAttribute(WebConstants.TITLE_ID, bookDefKey.getFullyQualifiedTitleId());
		model.addAttribute(WebConstants.TITLE, book.getBookName());

		form.setFullyQualifiedTitleId(bookDefKey.getFullyQualifiedTitleId());
		return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_PREVIEW);
	}
	
	@Required
	public String getEnvironmentName() {
		return environmentName;
	}

	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}

	@Required
	public JobRunner getJobRunner() {
		return jobRunner;
	}

	public void setJobRunner(JobRunner jobRunner) {
		this.jobRunner = jobRunner;
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
	public ModelAndView generateBulkEbookPreview(
			@RequestParam String[] titleId, Model model) throws Exception {

		return new ModelAndView(WebConstants.VIEW_BOOK_GENERATE_BULK_PREVIEW);
	}

	@Required
	public CoreService getCoreService() {
		return coreService;
	}

	@Required
	public void setCoreService(CoreService coreService) {
		this.coreService = coreService;
	}

}
