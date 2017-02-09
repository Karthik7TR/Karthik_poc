package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("bookLibrarySelectionFormValidator")
public class BookLibrarySelectionFormValidator implements Validator
{
    //private static final Logger log = LogManager.getLogger(BookLibrarySelectionFormValidator.class);
    @Override
    public boolean supports(final Class<?> clazz)
    {
        return (BookLibrarySelectionForm.class.isAssignableFrom(clazz));
    }

    @Override
    public void validate(final Object obj, final Errors errors)
    {
        // Do not validate inputs if there were binding errors since you cannot validate garbage (like "abc" entered instead of a valid integer).
        if (errors.hasErrors())
        {
            return;
        }
        final BookLibrarySelectionForm form = (BookLibrarySelectionForm) obj;

        if (form.getCommand() == BookLibrarySelectionForm.Command.GENERATE
            || form.getCommand() == BookLibrarySelectionForm.Command.EXPORT
            || form.getCommand() == BookLibrarySelectionForm.Command.IMPORT
            || form.getCommand() == BookLibrarySelectionForm.Command.PROMOTE)
        {
            final String[] keys = form.getSelectedEbookKeys();
            if (keys == null || keys.length == 0)
            {
                errors.reject("error.required.bookselection");
            }
        }
    }
}
