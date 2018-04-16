package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.delete;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinitionLock;
import com.thomsonreuters.uscl.ereader.core.job.domain.JobRequest;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.service.book.BookDefinitionLockService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("deleteBookDefinitionFormValidator")
public class DeleteBookDefinitionFormValidator implements Validator {
    private final JobRequestService jobRequestService;
    private final BookDefinitionLockService bookLockService;

    @Autowired
    public DeleteBookDefinitionFormValidator(
        final JobRequestService jobRequestService,
        final BookDefinitionLockService bookLockService) {
        this.jobRequestService = jobRequestService;
        this.bookLockService = bookLockService;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return (DeleteBookDefinitionForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors) {
        final DeleteBookDefinitionForm form = (DeleteBookDefinitionForm) obj;

        final Long bookDefinitionId = form.getId();

        if (bookDefinitionId != null) {
            if (form.getAction() != DeleteBookDefinitionForm.Action.RESTORE) {
                // Check if book is scheduled or in queue
                final JobRequest request = jobRequestService.findJobRequestByBookDefinitionId(bookDefinitionId);
                if (request != null) {
                    errors.rejectValue("id", "error.job.request");
                }

                // Check book is not being edited
                final BookDefinition book = new BookDefinition();
                book.setEbookDefinitionId(bookDefinitionId);
                final BookDefinitionLock lock = bookLockService.findActiveBookLock(book);

                if (lock != null) {
                    errors.rejectValue(
                        "id",
                        "error.book.locked",
                        new Object[] {lock.getFullName(), lock.getUsername()},
                        "Book Definition is currently being edited");
                }

                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "error.required");

                if (StringUtils.isNotBlank(form.getCode())
                    && !form.getCode().equals(WebConstants.CONFIRM_CODE_DELETE_BOOK)) {
                    errors.rejectValue("code", "error.invalid", new Object[] {"Code"}, "Invalid code");
                }
            }

            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "error.required");
        } else {
            errors.rejectValue("id", "error.invalid", new Object[] {"eBook Definition"}, "Invalid eBook Definition");
        }
    }
}
