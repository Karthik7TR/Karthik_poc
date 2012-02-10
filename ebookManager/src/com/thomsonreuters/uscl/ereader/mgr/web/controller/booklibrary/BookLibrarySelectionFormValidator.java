package com.thomsonreuters.uscl.ereader.mgr.web.controller.booklibrary;


import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component("bookLibrarySelectionFormValidator")
public class BookLibrarySelectionFormValidator implements Validator {
	
	//private static final Logger log = Logger.getLogger(BookLibrarySelectionFormValidator.class);
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (BookLibrarySelectionForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	// Do not validate inputs if there were binding errors since you cannot validate garbage (like "abc" entered instead of a valid integer).
    	if (errors.hasErrors()) {
    		return;
    	}
    	BookLibrarySelectionForm form = (BookLibrarySelectionForm) obj;
    	
    	String[] keys = form.getSelectedEbookKeys();
    	if (keys == null) {
    		String[] args = { "Selection" };
			errors.reject("error.required.field", args, "Selection of book definition required");
    	} else if (!(form.getSelectedEbookKeys().length > 0)) {
			String[] args = { "Selection" };
			errors.reject("error.required.field", args, "Selection of book definition required");
		}
	}
}
