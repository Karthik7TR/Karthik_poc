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
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatter;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;

@Component("editBookDefinitionFormValidator")
public class EditBookDefinitionFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionFormValidator.class);
	private static final int MAXIMUM_CHARACTER_40 = 40;
	private static final int MAXIMUM_CHARACTER_64 = 64;
	private static final int MAXIMUM_CHARACTER_1024 = 1024;
	private static final int ISBN_LENGTH = 13;
	private BookDefinitionService bookDefinitionService;
	private CodeService codeService;
	private JobRequestService jobRequestService;
	
	@SuppressWarnings("rawtypes")
	@Override
    public boolean supports(Class clazz) {
		return (EditBookDefinitionForm.class.isAssignableFrom(clazz));
    }

	@Override
    public void validate(Object obj, Errors errors) {
    	
    	EditBookDefinitionForm form = (EditBookDefinitionForm) obj;
    	
    	// Clear out empty rows in authors, nameLines, and additionalFrontMatters before validation
    	form.removeEmptyRows();
    	
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
    	checkForSpaces(errors, form.getTitleId(), "titleId", "Title ID");
    	
    	// Validate publication and title ID
    	if (form.getContentTypeId() != null && StringUtils.isNotEmpty(titleId)) {
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
    			BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(form.getBookdefinitionId());
    			
    			String oldTitleId = bookDef.getFullyQualifiedTitleId();
    			
    			// Check if Book Definition is in JobRequest if set as complete
    			if(bookDef.IsEbookDefinitionCompleteFlag()) {
    				if(jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId())) {
    					errors.rejectValue("validateForm", "error.job.request");
    				}
    			}
				
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

    		// Validate Publication Information
    		String pubInfo = form.getPubInfo();
    		checkForSpaces(errors, pubInfo, "pubInfo", "Pub Info");
    		checkSpecialCharacters(errors, pubInfo, "pubInfo", true);
    	}
    	
		// MaxLength Validations
		checkMaxLength(errors, MAXIMUM_CHARACTER_40, titleId, "titleId", new Object[] {"Title ID", MAXIMUM_CHARACTER_40});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getProviewDisplayName(), "proviewDisplayName", new Object[] {"ProView Display Name", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getCopyright(), "copyright", new Object[] {"Copyright", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getCopyrightPageText(), "copyrightPageText", new Object[] {"Copyright Page Text", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_64, form.getMaterialId(), "materialId", new Object[] {"Material ID", MAXIMUM_CHARACTER_64});
		checkMaxLength(errors, MAXIMUM_CHARACTER_64, form.getRootTocGuid(), "rootTocGuid", new Object[] {"Root TOC Guid", MAXIMUM_CHARACTER_64});
		checkMaxLength(errors, MAXIMUM_CHARACTER_64, form.getTocCollectionName(), "tocCollectionName", new Object[] {"TOC Collection", MAXIMUM_CHARACTER_64});
		checkMaxLength(errors, MAXIMUM_CHARACTER_64, form.getNortDomain(), "nortDomain", new Object[] {"NORT Domain", MAXIMUM_CHARACTER_64});
		checkMaxLength(errors, MAXIMUM_CHARACTER_64, form.getNortFilterView(), "nortFilterView", new Object[] {"NORT Filter View", MAXIMUM_CHARACTER_64});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getPublishDateText(), "publishDateText", new Object[] {"Publish Date Text", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getCurrency(), "currency", new Object[] {"Currentness Message", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getComment(), "comment", new Object[] {"Comment", MAXIMUM_CHARACTER_1024});
		
		int i = 0;
		for(EbookName name: form.getNameLines()) {
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, name.getBookNameText(), "nameLines[" + i +"].bookNameText", new Object[] {"Name Line", MAXIMUM_CHARACTER_1024});
			i++;
		}
		
		i = 0;
		for(FrontMatter frontMatter: form.getAdditionalFrontMatter()) {
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, frontMatter.getAdditionalFrontMatterText(), "additionalFrontMatter["+ i +"].additionalFrontMatterText", new Object[] {"Front Matter", MAXIMUM_CHARACTER_1024});
			i++;
		}
		
		// Require last name to be filled if there are authors
		// Also check max character length for all the fields
    	Collection<Author> authors = form.getAuthorInfo();
    	i = 0;
		for(Author author : authors) {
			if(StringUtils.isEmpty(author.getAuthorLastName())) {
				errors.rejectValue("authorInfo["+ i +"].authorLastName", "error.author.last.name");
			}
			checkMaxLength(errors, MAXIMUM_CHARACTER_40, author.getAuthorNamePrefix(), "authorInfo["+ i +"].authorNamePrefix", new Object[] {"Prefix", MAXIMUM_CHARACTER_40});
			checkMaxLength(errors, MAXIMUM_CHARACTER_40, author.getAuthorNameSuffix(), "authorInfo["+ i +"].authorNameSuffix", new Object[] {"Suffix", MAXIMUM_CHARACTER_40});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, author.getAuthorFirstName(), "authorInfo["+ i +"].authorFirstName", new Object[] {"First name", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, author.getAuthorMiddleName(), "authorInfo["+ i +"].authorMiddleName", new Object[] {"Middle name", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, author.getAuthorLastName(), "authorInfo["+ i +"].authorLastName", new Object[] {"Last name", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, author.getAuthorAddlText(), "authorInfo["+ i +"].authorAddlText", new Object[] {"Additional text", MAXIMUM_CHARACTER_1024});
			i++;
		}
		
		checkDateFormat(errors, form.getPublicationCutoffDate(), "publicationCutoffDate");
		
		
    	if(form.getIsComplete() || validateForm) {
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "proviewDisplayName", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "copyright", "error.required");
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "materialId", "error.required");

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
			
			checkIsbnNumber(errors, form.getIsbn(), "isbn");
			
			//TODO: check if cover image is on the server. Need server location.
		}
	}
	
	private void checkUniqueTitleId(Errors errors, String titleId) {
		BookDefinition newBookDef = bookDefinitionService.findBookDefinitionByTitle(titleId);
		
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
			try {
				@SuppressWarnings("unused")
				Date date = new SimpleDateFormat("MM/dd/yyyy").parse(text);
			} catch (Exception  e) {
				errors.rejectValue(fieldName, "error.date.format");
			}
		}
	}
	
	private void checkGuidFormat(Errors errors, String text, String fieldName) {
		if (StringUtils.isNotEmpty(text)) {
			Pattern pattern = Pattern.compile("^\\w[0-9a-fA-F]{32}$");
			Matcher matcher = pattern.matcher(text);
			
			if(!matcher.find()) {
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
	public void setBookDefinitionService(BookDefinitionService service) {
		this.bookDefinitionService = service;
	}
	
	@Required
	public void setCodeService(CodeService service) {
		this.codeService = service;
	}
	
	@Required
	public void setJobRequestService(JobRequestService service) {
		this.jobRequestService = service;
	}
}
