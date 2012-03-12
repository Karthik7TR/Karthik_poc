/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.orchestrate.core.BookDefinition;
import com.thomsonreuters.uscl.ereader.orchestrate.core.service.CoreService;

@Component("editBookDefinitionFormValidator")
public class EditBookDefinitionFormValidator implements Validator {
	private static final Logger log = Logger.getLogger(EditBookDefinitionFormValidator.class);
	private static final int MAXIMUM_TITLE_ID_LENGTH = 40;
	private static final int ISBN_LENGTH = 13;
	private CoreService coreService;
	private CodeService codeService;
	
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (EditBookDefinitionForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	// Do not validate inputs if there were binding errors since you cannot validate garbage 
		// (like "abc" entered instead of a valid integer).
    	if (errors.hasErrors()) {
    		return;
    	}
    	
    	EditBookDefinitionForm form = (EditBookDefinitionForm) obj;
    	
    	// Clear out empty rows in authors, nameLines, and additionalFrontMatters before validation
    	form.removeEmptyRows();
    	log.debug(form);
    	// Set validate error to prevent saving the form
		boolean validateForm = form.isValidateForm();
		if(validateForm) {
			errors.rejectValue("validateForm", "mesg.validate.form");
		}
    	Long contentTypeId = form.getContentTypeId();
		DocumentTypeCode contentType = (contentTypeId != null) ? codeService.getDocumentTypeCodeById(contentTypeId) : null;
    	String titleId = form.getTitleId();
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contentTypeId", "error.required");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "titleId", "error.required");
    	
    	// Validate publication and title ID
    	if (contentType != null && StringUtils.isNotEmpty(titleId)) {
    		// Validate publisher information
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publisher", "error.required");
    		String contentTypeName = contentType.getName();
    		if(contentTypeName.equalsIgnoreCase(WebConstants.KEY_ANALYTICAL)) {
    			// Validate Analytical fields are filled out
    			String pubAbbr = form.getPubAbbr();
        		checkForSpaces(errors, pubAbbr, "pubAbbr", "Pub Abbreviation");
        		checkSpecialCharacters(errors, pubAbbr, "pubAbbr", false);
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubAbbr", "error.required");
        	} else if (contentTypeName.equalsIgnoreCase(WebConstants.KEY_COURT_RULES)) {
        		// Validate Court Rules fields are filled out
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "error.required");
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubType", "error.required");
        	} else if (contentTypeName.equalsIgnoreCase(WebConstants.KEY_SLICE_CODES)) {
        		// Validate Slice Codes fields are filled out
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jurisdiction", "error.required");
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", "error.required");
        	}
    		
    		Long bookDefinitionId = form.getBookdefinitionId();
    		if(bookDefinitionId != null) {
    			// Lookup the book by its primary key
    			BookDefinition bookDef = coreService.findBookDefinitionByEbookDefId(form.getBookdefinitionId());
    			
    			String oldTitleId = bookDef.getTitleId();
				
    			// This is from the book definition edit
    			if(bookDef.IsPublishedOnceFlag()) {
    				// Been published to Proview and set to F
    				if (!oldTitleId.equals(titleId)) {
    					errors.rejectValue("titleId", "error.titleid.changed");
    				}
    			} else {
    				// Check new TitleId is unique if it changed
    				if (!oldTitleId.equals(titleId)) {
    					checkUniqueTitleId(errors, titleId);
    				}
    			}
    		} else {
    			// This is from the book definition create
    			checkUniqueTitleId(errors, titleId);
    		}
    		// Validate Title ID
    		checkMaxLength(errors, MAXIMUM_TITLE_ID_LENGTH, titleId, "titleId", new Object[] {"Title ID", MAXIMUM_TITLE_ID_LENGTH});
    		
    		// Validate Publication Information
    		String pubInfo = form.getPubInfo();
    		checkForSpaces(errors, pubInfo, "pubInfo", "Pub Info");
    		checkSpecialCharacters(errors, pubInfo, "pubInfo", true);
    	}
    	
    	
    	if(form.getIsComplete() || validateForm) {
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "proviewDisplayName", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "copyright", "error.required");
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "materialId", "error.required");
			//TODO: check if length is exactly 18 characters for materialId

			if (form.getIsTOC()) {
				checkGuidFormat(errors, form.getRootTocGuid(), "rootTocGuid");
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rootTocGuid", "error.required");
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tocCollectionName", "error.required");
			} else {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nortDomain", "error.required");
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nortFilterView", "error.required");
			}
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "isbn", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keyCiteToplineFlag", "error.required");
			
			if(form.getNameLines().size() == 0) {
				errors.rejectValue("nameLines", "error.at.least.one", new Object[] {"Name Line"}, "At Least 1 Name Line is required");
			}
			
			Collection<Author> authors = form.getAuthorInfo();
			for(Author author : authors) {
				if(StringUtils.isEmpty(author.getAuthorLastName())) {
					errors.rejectValue("authorInfo", "error.author.last.name");
					break;
				}
			}
			checkDateFormat(errors, form.getPublicationCutoffDate(), "publicationCutoffDate");
			checkIsbnNumber(errors, form.getIsbn(), "isbn");
			
			//TODO: check if cover image is on the server. Need server location.
		}
	}
	
	private void checkUniqueTitleId(Errors errors, String titleId) {
		BookDefinition newBookDef = coreService.findBookDefinitionByTitle(titleId);
		
		if (newBookDef != null) {
			errors.rejectValue("titleId", "error.titleid.exist");
		}
	}
	
	private void checkMaxLength(Errors errors, int maxValue ,String text, String fieldName,  Object[]  args) {
		if (StringUtils.isNotEmpty(text)) {
			if(text.length() > maxValue) {
				errors.rejectValue(fieldName, "error.max.length", args, "Must be maximum of " + maxValue + " characters or under");
			}
		}
	}
	
	private void checkDateFormat(Errors errors, String text, String fieldName) {
		if (StringUtils.isNotEmpty(text)) {
			Pattern pattern = Pattern.compile("^[01]{1}[0-9]{1}/[0-3]{1}[0-9]{1}/[0-9]{4}$");
			Matcher matcher = pattern.matcher(text);
			
			if(!matcher.find()) {
				errors.rejectValue(fieldName, "error.date.format");
			}
		}
	}
	
	private void checkGuidFormat(Errors errors, String text, String fieldName) {
		if (StringUtils.isNotEmpty(text)) {
			try {
				Date date = new SimpleDateFormat("MM/dd/yyyy").parse(text);
			} catch (Exception  e) {
				errors.rejectValue(fieldName, "error.guid.format");
			}
		}
	}
	
	private void checkForSpaces(Errors errors, String text, String fieldName, String arg) {
		if (StringUtils.isNotEmpty(text)) {
			Pattern pattern = Pattern.compile("\\s");
			Matcher matcher = pattern.matcher(text);
			
			if(matcher.find()) {
				errors.rejectValue(fieldName, "error.no.spaces", new Object[]{arg}, "No spaces allowed");
			}
		}
	}
	
	private void checkSpecialCharacters(Errors errors, String text, String fieldName, boolean includeUnderscore) {
		if (StringUtils.isNotEmpty(text)) {
			Pattern pattern = includeUnderscore ? Pattern.compile("[^a-z0-9_ ]", Pattern.CASE_INSENSITIVE):
				Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
			
			Matcher matcher = pattern.matcher(text);
			
			if(matcher.find()) {
				errors.rejectValue(fieldName, "error.special.characters");
			}
		}
	}
	
	private void checkIsbnNumber(Errors errors, String text, String fieldName) {
		if (StringUtils.isNotEmpty(text)) {
			Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{1}-\\d{5}-\\d{1}");
			Matcher matcher = pattern.matcher(text);
			
			if(matcher.find()) {
				String tempIsbn = text.replace("-", "");
				// Validate ISBN number
				if (tempIsbn.length() == ISBN_LENGTH) {
					int checkSum = 0;
					for (int i = 0; i < ISBN_LENGTH; i+=2) {
						String number = tempIsbn.substring(i, i+1);
						checkSum += Integer.parseInt(number);
					}
					for (int i = 1; i < ISBN_LENGTH - 1; i+=2) {
						String number = tempIsbn.substring(i, i+1);
						checkSum += 3 * Integer.parseInt(number);
					}
					
					if(checkSum % 10 != 0) {
						errors.rejectValue(fieldName, "error.isbn.checksum");
					}
				} 
			} else {
				errors.rejectValue(fieldName, "error.isbn.format");
			}
		}
	}
	
	@Required
	public void setCoreService(CoreService service) {
		this.coreService = service;
	}
	
	@Required
	public void setCodeService(CodeService service) {
		this.codeService = service;
	}
}
