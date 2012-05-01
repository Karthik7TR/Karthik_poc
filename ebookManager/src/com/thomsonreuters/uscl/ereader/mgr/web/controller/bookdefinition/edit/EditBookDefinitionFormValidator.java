/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.core.job.service.JobRequestService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

@Component("editBookDefinitionFormValidator")
public class EditBookDefinitionFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionFormValidator.class);
	private static final int MAXIMUM_CHARACTER_40 = 40;
	private static final int MAXIMUM_CHARACTER_64 = 64;
	private static final int MAXIMUM_CHARACTER_1024 = 1024;
	private static final int MAXIMUM_CHARACTER_2048 = 2048;
	private static final int ISBN_TOTAL_CHARACTER_LENGTH = 17;
	private static final int ISBN_NUMBER_LENGTH = 13;
	private BookDefinitionService bookDefinitionService;
	private CodeService codeService;
	private JobRequestService jobRequestService;
	private String environmentName;
	
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
		
    	Long contentTypeId = form.getContentTypeId();
		DocumentTypeCode contentType = (contentTypeId != null) ? codeService.getDocumentTypeCodeById(contentTypeId) : null;
    	String titleId = form.getTitleId();
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contentTypeId", "error.required");
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "titleId", "error.required");
    	checkForSpaces(errors, titleId, "titleId", "Title ID");
    	
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
        	} else {
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", "error.required");
        	}
    		
    		Long bookDefinitionId = form.getBookdefinitionId();
    		if(bookDefinitionId != null) {
    			// Lookup the book by its primary key
    			BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(form.getBookdefinitionId());
    			
    			String oldTitleId = bookDef.getFullyQualifiedTitleId();
    			
    			// Check if Book Definition is deleted
    	    	if(bookDef.isDeletedFlag()) {
    				errors.rejectValue("validateForm", "mesg.book.deleted");
    			}
    			
    			// Check if Book Definition is in JobRequest if set as complete
    			if(bookDef.getEbookDefinitionCompleteFlag()) {
    				if(jobRequestService.isBookInJobRequest(bookDef.getEbookDefinitionId())) {
    					errors.rejectValue("validateForm", "error.job.request");
    				}
    			}
				
    			// This is from the book definition edit
    			if(bookDef.getPublishedOnceFlag()) {
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
		checkMaxLength(errors, MAXIMUM_CHARACTER_2048, form.getCopyright(), "copyright", new Object[] {"Copyright", MAXIMUM_CHARACTER_2048});
		checkMaxLength(errors, MAXIMUM_CHARACTER_2048, form.getCopyrightPageText(), "copyrightPageText", new Object[] {"Copyright Page Text", MAXIMUM_CHARACTER_2048});
		checkMaxLength(errors, MAXIMUM_CHARACTER_64, form.getMaterialId(), "materialId", new Object[] {"Material ID", MAXIMUM_CHARACTER_64});
		checkMaxLength(errors, MAXIMUM_CHARACTER_64, form.getRootTocGuid(), "rootTocGuid", new Object[] {"Root TOC Guid", MAXIMUM_CHARACTER_64});
		checkMaxLength(errors, MAXIMUM_CHARACTER_64, form.getTocCollectionName(), "tocCollectionName", new Object[] {"TOC Collection", MAXIMUM_CHARACTER_64});
		checkMaxLength(errors, MAXIMUM_CHARACTER_64, form.getNortDomain(), "nortDomain", new Object[] {"NORT Domain", MAXIMUM_CHARACTER_64});
		checkMaxLength(errors, MAXIMUM_CHARACTER_64, form.getNortFilterView(), "nortFilterView", new Object[] {"NORT Filter View", MAXIMUM_CHARACTER_64});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getPublishDateText(), "publishDateText", new Object[] {"Publish Date Text", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_2048, form.getCurrency(), "currency", new Object[] {"Currentness Message", MAXIMUM_CHARACTER_2048});
		checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getComment(), "comment", new Object[] {"Comment", MAXIMUM_CHARACTER_1024});
		checkMaxLength(errors, MAXIMUM_CHARACTER_2048, form.getAdditionalTrademarkInfo(), "currency", new Object[] {"Additional Trademark/Patent Info", MAXIMUM_CHARACTER_2048});
		checkMaxLength(errors, MAXIMUM_CHARACTER_2048, form.getFrontMatterTitle().getBookNameText(), "frontMatterTitle.bookNameText", new Object[] {"Main Title", MAXIMUM_CHARACTER_2048});
		checkMaxLength(errors, MAXIMUM_CHARACTER_2048, form.getFrontMatterSubtitle().getBookNameText(), "frontMatterSubtitle.bookNameText", new Object[] {"Sub Title", MAXIMUM_CHARACTER_2048});
		checkMaxLength(errors, MAXIMUM_CHARACTER_2048, form.getFrontMatterSeries().getBookNameText(), "frontMatterSeries.bookNameText", new Object[] {"Series", MAXIMUM_CHARACTER_2048});
		

		// Require last name to be filled if there are authors
		// Also check max character length for all the fields
    	Collection<Author> authors = form.getAuthorInfo();
    	List<Integer> authorSequenceChecker = new ArrayList<Integer>();
    	int i = 0;
		for(Author author : authors) {
			if(StringUtils.isEmpty(author.getAuthorLastName())) {
				errors.rejectValue("authorInfo["+ i +"].authorLastName", "error.author.last.name");
			}

			checkMaxLength(errors, MAXIMUM_CHARACTER_40, author.getAuthorNamePrefix(), "authorInfo["+ i +"].authorNamePrefix", new Object[] {"Prefix", MAXIMUM_CHARACTER_40});
			checkMaxLength(errors, MAXIMUM_CHARACTER_40, author.getAuthorNameSuffix(), "authorInfo["+ i +"].authorNameSuffix", new Object[] {"Suffix", MAXIMUM_CHARACTER_40});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, author.getAuthorFirstName(), "authorInfo["+ i +"].authorFirstName", new Object[] {"First name", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, author.getAuthorMiddleName(), "authorInfo["+ i +"].authorMiddleName", new Object[] {"Middle name", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, author.getAuthorLastName(), "authorInfo["+ i +"].authorLastName", new Object[] {"Last name", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_2048, author.getAuthorAddlText(), "authorInfo["+ i +"].authorAddlText", new Object[] {"Additional text", MAXIMUM_CHARACTER_2048});
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "authorInfo["+ i +"].sequenceNum", "error.required.field", new Object[] {"Sequence Number"});
			// Check duplicate sequence numbers exist
			checkDuplicateSequenceNumber(errors, author.getSequenceNum(), "authorInfo["+ i +"].sequenceNum", authorSequenceChecker);
			i++;
		}
		
		// Check max character and required fields for Front Matter
		i=0;
		List<Integer> pageSequenceChecker = new ArrayList<Integer>();
		for(FrontMatterPage page: form.getFrontMatters()) {				
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, page.getPageTocLabel(), "frontMatters["+ i +"].pageTocLabel", new Object[] {"Page TOC Label", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, page.getPageHeadingLabel(), "frontMatters["+ i +"].pageHeadingLabel", new Object[] {"Page Heading Label", MAXIMUM_CHARACTER_1024});
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatters["+ i +"].sequenceNum", "error.required.field", new Object[] {"Sequence Number"});
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatters["+ i +"].pageTocLabel", "error.required.field", new Object[] {"Page TOC Label"});
			// Check duplicate sequence numbers exist
			checkDuplicateSequenceNumber(errors, page.getSequenceNum(), "frontMatters["+ i +"].sequenceNum", pageSequenceChecker);
						
			// Check Front Matter sections for max characters and required fields
			int j = 0;
			List<Integer> sectionSequenceChecker = new ArrayList<Integer>();
			for(FrontMatterSection section : page.getFrontMatterSections()) {
				checkMaxLength(errors, MAXIMUM_CHARACTER_1024, section.getSectionHeading(), "frontMatters["+ i +"].frontMatterSections["+ j +"].sectionHeading", new Object[] {"Section Heading", MAXIMUM_CHARACTER_1024});
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatters["+ i +"].frontMatterSections["+ j +"].sequenceNum", "error.required.field", new Object[] {"Sequence Number"});
				// Check duplicate sequence numbers exist
				checkDuplicateSequenceNumber(errors, section.getSequenceNum(), "frontMatters["+ i +"].frontMatterSections["+ j +"].sequenceNum", sectionSequenceChecker);
				
				// Check Front Matter Pdf for max characters and required fields
				int k = 0;
				List<Integer> pdfSequenceChecker = new ArrayList<Integer>();
				for (FrontMatterPdf pdf : section.getPdfs()) {
					checkMaxLength(errors, MAXIMUM_CHARACTER_1024, pdf.getPdfFilename(), "frontMatters["+ i +"].frontMatterSections["+ j +"].pdfs["+ k +"].pdfFilename", new Object[] {"PDF Filename", MAXIMUM_CHARACTER_1024});
					checkMaxLength(errors, MAXIMUM_CHARACTER_1024, pdf.getPdfLinkText(), "frontMatters["+ i +"].frontMatterSections["+ j +"].pdfs["+ k +"].pdfLinkText", new Object[] {"PDF Link Text", MAXIMUM_CHARACTER_1024});
					ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatters["+ i +"].frontMatterSections["+ j +"].pdfs["+ k +"].sequenceNum", "error.required.field", new Object[] {"Sequence Number"});
					// Check duplicate sequence numbers exist
					checkDuplicateSequenceNumber(errors, pdf.getSequenceNum(), "frontMatters["+ i +"].frontMatterSections["+ j +"].pdfs["+ k +"].sequenceNum", pdfSequenceChecker);
					
					// Check both fields of PDF is filled 
					if(StringUtils.isBlank(pdf.getPdfFilename()) || StringUtils.isBlank(pdf.getPdfLinkText())) {
						errors.rejectValue("frontMatters["+ i +"].frontMatterSections["+ j +"].pdfs["+ k +"].pdfFilename", "error.required.pdf");
					}
					k++;
				}
				j++;
			}
			i++;
		}
		
		if(form.isPublicationCutoffDateUsed()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publicationCutoffDate", "error.publication.cutoff.date");
		}
		checkDateFormat(errors, form.getPublicationCutoffDate(), "publicationCutoffDate");

		// Only run these validation when Validate Button or Book Definition is set as Complete.
    	if(form.getIsComplete() || validateForm) {
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "proviewDisplayName", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "copyright", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "materialId", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatterTocLabel", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatterTitle.bookNameText", "error.required");

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
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatterTitle", "error.required");
			
			checkIsbnNumber(errors, form.getIsbn(), "isbn");
			
			// Validate that required keyword are selected
			// getAllKeywordTypeCodes must be in sorted order by the name because
			// form.getKeywords returns a String Collection of KeywordTypeValues placed
			// in the order of KeywordTypeCodes.
			List<KeywordTypeCode> keywordCodes = codeService.getAllKeywordTypeCodes();
			i = 0;
			for(KeywordTypeCode code : keywordCodes) {
				// Check that user has selected a keyword if that KeywordTypeCode is required
				if(code.getIsRequired()) {
					ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keywords["+ i +"]", "error.required");
				}
				i++;
			}
			
			// Check if pdf file and cover image exists on NAS location when on prod server
			if(environmentName.equalsIgnoreCase("prod")) {
				// Check cover image exists
				if(StringUtils.isNotBlank(titleId)) {
					fileExist(errors, form.createCoverImageName(), WebConstants.KEY_COVER_IMAGE_LOCATION, "validateForm");
				}
				// Check all pdfs on Front Matter
				i=0;
				for(FrontMatterPage page: form.getFrontMatters()) {
					int j = 0;
					for(FrontMatterSection section : page.getFrontMatterSections()) {
						int k = 0;
						for (FrontMatterPdf pdf : section.getPdfs()) {
							String filename = pdf.getPdfFilename();
							if(StringUtils.isNotBlank(filename)) {
								fileExist(errors, filename, WebConstants.KEY_PDF_LOCATION, "frontMatters["+ i +"].frontMatterSections["+ j +"].pdfs["+ k +"].pdfFilename");
							}
							k++;
						}
						j++;
					}
					i++;
				}
			}
		}
    	
    	// Adding error message if any validation fails
    	if(errors.hasErrors()) {
			errors.rejectValue("validateForm", "mesg.errors.form");
		}
    	
    	// Adding validation message if Validation button was pressed.
    	if(validateForm) {
			errors.rejectValue("validateForm", "mesg.validate.form");
		}
	}
	
	private void fileExist(Errors errors, String filename, String location, String fieldName) {
		File file = new File(location, filename);
		if(!file.isFile()) {
			errors.rejectValue(fieldName, "error.not.exist", new Object[] {filename, location}, "File does not exist on server.");
		}
	}
	
	private void checkUniqueTitleId(Errors errors, String titleId) {
		BookDefinition newBookDef = bookDefinitionService.findBookDefinitionByTitle(titleId);
		
		if (newBookDef != null) {
			errors.rejectValue("titleId", "error.titleid.exist");
		}
	}
	
	private void checkDuplicateSequenceNumber(Errors errors, Integer sequenceNumber, String fieldName, List<Integer> sequenceChecker) {
		// Check duplicate sequence numbers exist
		if(sequenceNumber != null) {
			if(sequenceChecker.contains(sequenceNumber)) {
				errors.rejectValue(fieldName, "error.sequence.number");
			} else {
				sequenceChecker.add(sequenceNumber);
			}
		}
	}
	
	private void checkIsbnNumber(Errors errors, String text, String fieldName) {
		if (StringUtils.isNotEmpty(text)) {
			if(text.length() == ISBN_TOTAL_CHARACTER_LENGTH) {
				String tempIsbn = text.replace("-", "");
				Pattern pattern = Pattern.compile("^\\d{13}$");
				Matcher matcher = pattern.matcher(tempIsbn);
				// Validate ISBN number
				if (matcher.find()) {
					int checkSum = 0;
					for (int i = 0; i < ISBN_NUMBER_LENGTH; i+=2) {
						String number = tempIsbn.substring(i, i+1);
						checkSum += Integer.parseInt(number);
					}
					for (int i = 1; i < ISBN_NUMBER_LENGTH - 1; i+=2) {
						String number = tempIsbn.substring(i, i+1);
						checkSum += 3 * Integer.parseInt(number);
					}
					
					if(checkSum % 10 != 0) {
						errors.rejectValue(fieldName, "error.isbn.checksum");
					}
				} else {
					errors.rejectValue(fieldName, "error.isbn.format");
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
	@Required
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
}
