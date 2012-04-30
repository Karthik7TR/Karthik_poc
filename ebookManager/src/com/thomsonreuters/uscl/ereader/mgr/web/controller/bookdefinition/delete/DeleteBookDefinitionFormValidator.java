package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionLockService;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

public class DeleteBookDefinitionFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(DeleteBookDefinitionFormValidator.class);
	private JobRequestService jobRequestService;
	private BookDefinitionLockService bookLockService;

	@Override
    public boolean supports(Class<?> clazz) {
		return (DeleteBookDefinitionForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
		
		DeleteBookDefinitionForm form = (DeleteBookDefinitionForm) obj;
		
		Long bookDefinitionId = form.getId();
		
		if(bookDefinitionId != null) {
			
			if(form.getAction() != DeleteBookDefinitionForm.Action.RESTORE) {
				// Check if book is scheduled or in queue
				JobRequest request = jobRequestService.findJobRequestByBookDefinitionId(bookDefinitionId);
				if(request != null) {
					errors.rejectValue("id", "error.job.request");	
				}
				
				// Check book is not being edited
				BookDefinition book = new BookDefinition();
				book.setEbookDefinitionId(bookDefinitionId);
				BookDefinitionLock lock = bookLockService.findBookLockByBookDefinition(book);
				
				if(lock != null) {
					errors.rejectValue("id", "error.book.locked", new Object[] {lock.getFullName(), lock.getUsername()}, "Book Definition is currently being edited");	
				}
	
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "error.required");
				
				if(StringUtils.isNotBlank(form.getCode()) && !form.getCode().equals(WebConstants.KEY_DELETE_BOOK)) {
					errors.rejectValue("code", "error.invalid", new Object[] {"Code"}, "Invalid code");
				}
			
			}
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "error.required");
		} else {
			errors.rejectValue("id", "error.invalid", new Object[] {"eBook Definition"}, "Invalid eBook Definition");
		}
	
	}
	
	@Required
	public void setJobRequestService(JobRequestService service) {
		this.jobRequestService = service;
	}
	
	@Required
	public void setBookDefinitionLockService(BookDefinitionLockService service) {
		this.bookLockService = service;
	}

}
