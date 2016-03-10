/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.PilotBookStatus;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPdf;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterSection;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.TableViewer;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.controller.BaseFormValidator;

@Component("editBookDefinitionFormValidator")
public class EditBookDefinitionFormValidator extends BaseFormValidator implements Validator {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionFormValidator.class);
	private static final int MAXIMUM_CHARACTER_40 = 40;
	private static final int MAXIMUM_CHARACTER_64 = 64;
	private static final int MAXIMUM_CHARACTER_512 = 512;
	private static final int MAXIMUM_CHARACTER_1024 = 1024;
	private static final int MAXIMUM_CHARACTER_2048 = 2048;
	private static final int ISBN_TOTAL_CHARACTER_LENGTH = 17;
	private static final int ISBN_NUMBER_LENGTH = 13;
	private BookDefinitionService bookDefinitionService;
	private CodeService codeService;
	private String environmentName;
	private File rootCodesWorkbenchLandingStrip;
	
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
		
    	validateTitleId(form, errors);
    	
		// MaxLength Validations
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

		validateAuthors(form, errors);
		validateExcludeDocuments(form, errors);
		validateRenameTocEntries(form, errors);
		validateTableViewers(form, errors);
		validateAdditionalFrontMatter(form, errors);
		validateDocumentCopyrights(form, errors);
		validateDocumentCurrencies(form, errors);
		
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

			if(form.getSourceType().equals(SourceType.TOC)) {
				checkGuidFormat(errors, form.getRootTocGuid(), "rootTocGuid");
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rootTocGuid", "error.required");
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tocCollectionName", "error.required");
			} else if(form.getSourceType().equals(SourceType.NORT)) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nortDomain", "error.required");
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nortFilterView", "error.required");
			} else {
				checkMaxLength(errors, MAXIMUM_CHARACTER_1024, form.getCodesWorkbenchBookName(), "codesWorkbenchBookName", new Object[] {"CWB Book Name", MAXIMUM_CHARACTER_1024});
				validateNortFileLocations(form, errors);
				
			}
			
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "isbn", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keyCiteToplineFlag", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatterTitle", "error.required");			
			
			checkIsbnNumber(errors, form.getIsbn(), "isbn");
			
			validateProviewKeywords(errors);
			validateProdOnlyRequirements(form, errors);
			
			if(form.isSplitBook()){
        		if(!form.isGroupsEnabled()) {
        			errors.rejectValue("groupsEnabled", "error.required");
        		}
        	}
		}
    	
    	if(form.isGroupsEnabled()) {
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "groupName", "error.required");
    		if(form.isSplitBook()){
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subGroupHeading", "error.required");
        	}
    	}
    	
		if(!form.isSplitTypeAuto()){
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "splitEBookParts", "error.required");
			if(form.getSplitEBookParts() != null && form.getSplitEBookParts() > 0){
				validateSplitDocuments(form, errors);			
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
	
	// All the validations to verify that the Title ID is formed with all requirements
	private void validateTitleId(EditBookDefinitionForm form, Errors errors) {

    	String titleId = form.getTitleId();
    	ValidationUtils.rejectIfEmptyOrWhitespace(errors, "titleId", "error.required");
    	checkForSpaces(errors, titleId, "titleId", "Title ID");
		checkMaxLength(errors, MAXIMUM_CHARACTER_40, titleId, "titleId", new Object[] {"Title ID", MAXIMUM_CHARACTER_40});
		
		// Validate publisher information
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "publisher", "error.required");
    	
		// Validate publication and title ID
    	if (StringUtils.isNotEmpty(titleId)) {
    		Long contentTypeId = form.getContentTypeId();
    		String publisher = form.getPublisher();
    		DocumentTypeCode contentType = (contentTypeId != null) ? codeService.getDocumentTypeCodeById(contentTypeId) : null;

    		if("uscl".equalsIgnoreCase(publisher)) {
	    		if(contentType != null && WebConstants.DOCUMENT_TYPE_ANALYTICAL.equalsIgnoreCase(contentType.getName())) {
	    			// Validate Analytical fields are filled out
	    			String pubAbbr = form.getPubAbbr();
	        		checkForSpaces(errors, pubAbbr, "pubAbbr", "Pub Abbreviation");
	        		checkSpecialCharacters(errors, pubAbbr, "pubAbbr", false);
	        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubAbbr", "error.required");
	        	} else if (contentType != null &&WebConstants.DOCUMENT_TYPE_COURT_RULES.equalsIgnoreCase(contentType.getName())) {
	        		// Validate Court Rules fields are filled out
	        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "state", "error.required");
	        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubType", "error.required");
	        	} else if (contentType != null && WebConstants.DOCUMENT_TYPE_SLICE_CODES.equalsIgnoreCase(contentType.getName())) {
	        		// Validate Slice Codes fields are filled out
	        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jurisdiction", "error.required");
	        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", "error.required");
	        	} else {
	        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", "error.required");
	        	}
	    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contentTypeId", "error.required");
	    		
    		}else {
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", "error.required");
        		
        		// Validate Product Code
        		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "productCode", "error.required");
        		String productCode = form.getProductCode();
        		checkForSpaces(errors, productCode, "productCode", "Product Code");
        		checkSpecialCharacters(errors, productCode, "productCode", true);

        	}
    		
    		Long bookDefinitionId = form.getBookdefinitionId();
    		if(bookDefinitionId != null) {
    			// Lookup the book by its primary key
    			BookDefinition bookDef = bookDefinitionService.findBookDefinitionByEbookDefId(form.getBookdefinitionId());
    			
    			// Bug 297047: Super user deletes Book Definition while another user is editing the Book Definition.
    			if(bookDef == null) {
    				// Let controller redirect the user to error: Book Definition Deleted
    				return;
    			}
    			
	    		String oldTitleId = bookDef.getFullyQualifiedTitleId();
	    			
    			// Check if Book Definition is deleted
    	    	if(bookDef.isDeletedFlag()) {
    				errors.rejectValue("validateForm", "mesg.book.deleted");
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
    	} else {
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jurisdiction", "error.required");
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "pubInfo", "error.required");
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "productCode", "error.required");
    		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contentTypeId", "error.required");
    	}
	}
	
	private void validateAuthors(EditBookDefinitionForm form, Errors errors) {
		// Require last name to be filled if there are authors
		// Also check max character length for all the fields
    	List<Author> authors = form.getAuthorInfo();
    	// Sort the authors before validations
		Collections.sort(authors);
		form.setAuthorInfo(authors);
		List<Integer> authorSequenceChecker = new ArrayList<Integer>();
    	int i = 0;
		for(Author author : authors) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "authorInfo["+ i +"].sequenceNum", "error.required.field", new Object[] {"Sequence Number"});
			checkMaxLength(errors, MAXIMUM_CHARACTER_40, author.getAuthorNamePrefix(), "authorInfo["+ i +"].authorNamePrefix", new Object[] {"Prefix", MAXIMUM_CHARACTER_40});
			checkMaxLength(errors, MAXIMUM_CHARACTER_40, author.getAuthorNameSuffix(), "authorInfo["+ i +"].authorNameSuffix", new Object[] {"Suffix", MAXIMUM_CHARACTER_40});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, author.getAuthorFirstName(), "authorInfo["+ i +"].authorFirstName", new Object[] {"First name", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, author.getAuthorMiddleName(), "authorInfo["+ i +"].authorMiddleName", new Object[] {"Middle name", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, author.getAuthorLastName(), "authorInfo["+ i +"].authorLastName", new Object[] {"Last name", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_2048, author.getAuthorAddlText(), "authorInfo["+ i +"].authorAddlText", new Object[] {"Additional text", MAXIMUM_CHARACTER_2048});
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "authorInfo["+ i +"].authorLastName", "error.author.last.name");
			// Check duplicate sequence numbers exist
			checkDuplicateSequenceNumber(errors, author.getSequenceNum(), "authorInfo["+ i +"].sequenceNum", authorSequenceChecker);
			i++;
		}
	}
	
	private void validateNortFileLocations(EditBookDefinitionForm form, Errors errors) {
		// Require at least one file location
		// Also check max character length for all the fields
    	List<NortFileLocation> nortFileLocations = form.getNortFileLocations();
    	// Sort the list before validations
		Collections.sort(nortFileLocations);
		form.setNortFileLocations(nortFileLocations);
		List<Integer> sequenceChecker = new ArrayList<Integer>();
		
		// Check if book Folder exists
		String bookFolderName = form.getCodesWorkbenchBookName();
		
		if(validateFileExists(errors, "codesWorkbenchBookName", this.rootCodesWorkbenchLandingStrip, bookFolderName)) {
			File bookDirectory = new File(this.rootCodesWorkbenchLandingStrip, bookFolderName);
	    	int i = 0;
			for(NortFileLocation fileLocation : nortFileLocations) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nortFileLocations["+ i +"].sequenceNum", "error.required.field", new Object[] {"Sequence Number"});
				checkMaxLength(errors, MAXIMUM_CHARACTER_1024, fileLocation.getLocationName(), "nortFileLocations["+ i +"].locationName", new Object[] {"Name", MAXIMUM_CHARACTER_1024});
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nortFileLocations["+ i +"].locationName", "error.required.field");
				validateFileExists(errors, "nortFileLocations["+ i +"].locationName", bookDirectory, fileLocation.getLocationName());
				
				// Check duplicate sequence numbers exist
				checkDuplicateSequenceNumber(errors, fileLocation.getSequenceNum(), "nortFileLocations["+ i +"].sequenceNum", sequenceChecker);
				i++;
			}
			
			if(i == 0) {
				errors.rejectValue("nortFileLocations", "error.at.least.one", new Object[]{"Content Set"}, "At Least 1 Content Set is required");
			}
		}
	}
	
	private boolean validateFileExists(Errors errors, String fieldName, File directory, String fileName) {
		if(StringUtils.isBlank(fileName)) {
			errors.rejectValue(fieldName, "error.required");
			return false;
		} else {
			File file = new File(directory, fileName);
			if(!file.exists()) {
				errors.rejectValue(fieldName, "error.not.exist", new Object[]{fileName, directory.getPath()}, "File/Directory does not exist in " + directory.getPath());
				return false;
			}
		}
		return true;
	}
	
	private void validateSplitDocuments(EditBookDefinitionForm form, Errors errors){
		int i=0;
		List<String> tocGuids = new ArrayList<String>();
		for(SplitDocument document: form.getSplitDocuments()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "splitDocuments["+ i +"].tocGuid", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "splitDocuments["+ i +"].note", "error.required");		
				
			String tocGuid = null;
			// Check if there are duplicate guids
			if (document != null && !document.isEmpty()) {
					tocGuid = document.getTocGuid();
			}
			if(StringUtils.isNotBlank(tocGuid)) {
				checkMaxLength(errors, MAXIMUM_CHARACTER_512, document.getNote(), "splitDocuments["+ i +"].note", new Object[] {"Note", MAXIMUM_CHARACTER_512});
				if(tocGuids.contains(tocGuid)) {
					errors.rejectValue("splitDocuments["+ i +"].tocGuid", "error.duplicate", new Object[] {"TOC/NORT GUID"}, "Duplicate Toc Guid");
				} else {
					checkGuidFormat(errors, tocGuid, "splitDocuments["+ i +"].tocGuid");
					tocGuids.add(tocGuid);
				}
			}
			i++;
		}
		
	}
	
	private void validateExcludeDocuments(EditBookDefinitionForm form, Errors errors) {
		// Validate Exclude Documents has all required fields
		int i=0;
		List<String> documentGuids = new ArrayList<String>();
		for(ExcludeDocument document: form.getExcludeDocuments()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "excludeDocuments["+ i +"].documentGuid", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "excludeDocuments["+ i +"].note", "error.required");
			checkMaxLength(errors, MAXIMUM_CHARACTER_512, document.getNote(), "excludeDocuments["+ i +"].note", new Object[] {"Note", MAXIMUM_CHARACTER_512});
			
			// Check if there are duplicate guids
			String documentGuid = document.getDocumentGuid();
			if(StringUtils.isNotBlank(documentGuid)) {
				if(documentGuids.contains(documentGuid)) {
					errors.rejectValue("excludeDocuments["+ i +"].documentGuid", "error.duplicate", new Object[] {"Document Guid"}, "Duplicate Document Guid");
				} else {
					checkGuidFormat(errors, documentGuid, "excludeDocuments["+ i +"].documentGuid");
					documentGuids.add(documentGuid);
				}
			}
			i++;
		}
		if(form.isExcludeDocumentsUsed()) {
			if(form.getExcludeDocuments().size() == 0) {
				errors.rejectValue("excludeDocuments","error.used.selected", new Object[] {"Exclude Documents"}, "Please select 'No' if Exclude Documents will not be used.");
			}
		}
	}
	
	private void validateRenameTocEntries(EditBookDefinitionForm form, Errors errors) {
		// Validate RenameTocEntry has all required fields
		int i=0;
		List<String> tocGuids = new ArrayList<String>();
		for(RenameTocEntry label: form.getRenameTocEntries()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "renameTocEntries["+ i +"].tocGuid", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "renameTocEntries["+ i +"].oldLabel", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "renameTocEntries["+ i +"].newLabel", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "renameTocEntries["+ i +"].note", "error.required");
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, label.getOldLabel(), "renameTocEntries["+ i +"].oldLabel", new Object[] {"Old Label", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, label.getNewLabel(), "renameTocEntries["+ i +"].newLabel", new Object[] {"New Label", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_512, label.getNote(), "renameTocEntries["+ i +"].note", new Object[] {"Note", MAXIMUM_CHARACTER_512});
			
			// Check if there are duplicate guids
			String tocGuid = label.getTocGuid();
			if(StringUtils.isNotBlank(tocGuid)) {
				if(tocGuids.contains(tocGuid)) {
					errors.rejectValue("renameTocEntries["+ i +"].tocGuid", "error.duplicate", new Object[] {"Guid"}, "Duplicate Guid");
				} else {
					checkGuidFormat(errors, tocGuid, "renameTocEntries["+ i +"].tocGuid");
					tocGuids.add(tocGuid);
				}
			}
			i++;
		}
		
		if(form.isRenameTocEntriesUsed()) {
			if(form.getRenameTocEntries().size() == 0) {
				errors.rejectValue("renameTocEntries","error.used.selected", new Object[] {"Rename TOC Labels"}, "Please select 'No' if Rename TOC Labels will not be used.");
			}
		}
	}
	
	private void validateTableViewers(EditBookDefinitionForm form, Errors errors) {
		// Validate Table Viewers has all required fields
		int i=0;
		List<String> documentGuids = new ArrayList<String>();
		for(TableViewer document: form.getTableViewers()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tableViewers["+ i +"].documentGuid", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tableViewers["+ i +"].note", "error.required");
			checkMaxLength(errors, MAXIMUM_CHARACTER_512, document.getNote(), "tableViewers["+ i +"].note", new Object[] {"Note", MAXIMUM_CHARACTER_512});
			
			// Check if there are duplicate guids
			String documentGuid = document.getDocumentGuid();
			if(StringUtils.isNotBlank(documentGuid)) {
				if(documentGuids.contains(documentGuid)) {
					errors.rejectValue("tableViewers["+ i +"].documentGuid", "error.duplicate", new Object[] {"Document Guid"}, "Duplicate Document Guid");
				} else {
					checkGuidFormat(errors, documentGuid, "tableViewers["+ i +"].documentGuid");
					documentGuids.add(documentGuid);
				}
			}
			i++;
		}
		
		if(form.isTableViewersUsed()) {
			if(form.getTableViewers().size() == 0) {
				errors.rejectValue("tableViewers","error.used.selected", new Object[] {"Table Viewer"}, "Please select 'No' if Table Viewer will not be used.");
			}
		}
	}
	
	private void validateDocumentCopyrights(EditBookDefinitionForm form, Errors errors) {
		// Validate document copyright has all required fields
		int i=0;
		List<String> copyrightGuids = new ArrayList<String>();
		for(DocumentCopyright documentCopyright: form.getDocumentCopyrights()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "documentCopyrights["+ i +"].copyrightGuid", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "documentCopyrights["+ i +"].newText", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "documentCopyrights["+ i +"].note", "error.required");
			checkMaxLength(errors, MAXIMUM_CHARACTER_512, documentCopyright.getNote(), "documentCopyrights["+ i +"].note", new Object[] {"Note", MAXIMUM_CHARACTER_512});
			checkMaxLength(errors, MAXIMUM_CHARACTER_512, documentCopyright.getNewText(), "documentCopyrights["+ i +"].newText", new Object[] {"Note", MAXIMUM_CHARACTER_512});
			
			// Check if there are duplicate guids
			String documentGuid = documentCopyright.getCopyrightGuid();
			if(StringUtils.isNotBlank(documentGuid)) {
				if(copyrightGuids.contains(documentGuid)) {
					errors.rejectValue("documentCopyrights["+ i +"].copyrightGuid", "error.duplicate", new Object[] {"Copyright Guid"}, "Duplicate Copyright Guid");
				} else {
					checkGuidFormat(errors, documentGuid, "documentCopyrights["+ i +"].copyrightGuid");
					copyrightGuids.add(documentGuid);
				}
			}
			i++;
		}
	}
	
	private void validateDocumentCurrencies(EditBookDefinitionForm form, Errors errors) {
		// Validate document currency has all required fields
		int i=0;
		List<String> currencyGuids = new ArrayList<String>();
		for(DocumentCurrency documentCurrency: form.getDocumentCurrencies()) {
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "documentCurrencies["+ i +"].currencyGuid", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "documentCurrencies["+ i +"].newText", "error.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "documentCurrencies["+ i +"].note", "error.required");
			checkMaxLength(errors, MAXIMUM_CHARACTER_512, documentCurrency.getNote(), "documentCurrencies["+ i +"].note", new Object[] {"Note", MAXIMUM_CHARACTER_512});
			checkMaxLength(errors, MAXIMUM_CHARACTER_512, documentCurrency.getNewText(), "documentCurrencies["+ i +"].newText", new Object[] {"Note", MAXIMUM_CHARACTER_512});
			
			// Check if there are duplicate guids
			String documentGuid = documentCurrency.getCurrencyGuid();
			if(StringUtils.isNotBlank(documentGuid)) {
				if(currencyGuids.contains(documentGuid)) {
					errors.rejectValue("documentCurrencies["+ i +"].currencyGuid", "error.duplicate", new Object[] {"Currency Guid"}, "Duplicate Currency Guid");
				} else {
					checkGuidFormat(errors, documentGuid, "documentCurrencies["+ i +"].currencyGuid");
					currencyGuids.add(documentGuid);
				}
			}
			i++;
		}
	}
	
	private void validateAdditionalFrontMatter(EditBookDefinitionForm form, Errors errors) {
		// Sort the list before validations
		List<FrontMatterPage> pages = form.getFrontMatters();
		Collections.sort(pages);
		form.setFrontMatters(pages);
		
		// Check max character and required fields for Front Matter
		int i=0;
		List<Integer> pageSequenceChecker = new ArrayList<Integer>();
		for(FrontMatterPage page: pages) {				
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, page.getPageTocLabel(), "frontMatters["+ i +"].pageTocLabel", new Object[] {"Page TOC Label", MAXIMUM_CHARACTER_1024});
			checkMaxLength(errors, MAXIMUM_CHARACTER_1024, page.getPageHeadingLabel(), "frontMatters["+ i +"].pageHeadingLabel", new Object[] {"Page Heading Label", MAXIMUM_CHARACTER_1024});
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatters["+ i +"].sequenceNum", "error.required.field", new Object[] {"Sequence Number"});
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatters["+ i +"].pageTocLabel", "error.required.field", new Object[] {"Page TOC Label"});
			// Check duplicate sequence numbers exist
			checkDuplicateSequenceNumber(errors, page.getSequenceNum(), "frontMatters["+ i +"].sequenceNum", pageSequenceChecker);
						
			// Check Front Matter sections for max characters and required fields
			int j = 0;
			List<Integer> sectionSequenceChecker = new ArrayList<Integer>();
			
			// Sort the list before validations
			List<FrontMatterSection> sections = page.getFrontMatterSections();
			Collections.sort(sections);
			page.setFrontMatterSections(sections);
			for(FrontMatterSection section : sections) {
				checkMaxLength(errors, MAXIMUM_CHARACTER_1024, section.getSectionHeading(), "frontMatters["+ i +"].frontMatterSections["+ j +"].sectionHeading", new Object[] {"Section Heading", MAXIMUM_CHARACTER_1024});
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "frontMatters["+ i +"].frontMatterSections["+ j +"].sequenceNum", "error.required.field", new Object[] {"Sequence Number"});
				// Check duplicate sequence numbers exist
				checkDuplicateSequenceNumber(errors, section.getSequenceNum(), "frontMatters["+ i +"].frontMatterSections["+ j +"].sequenceNum", sectionSequenceChecker);
				
				// Check Front Matter Pdf for max characters and required fields
				int k = 0;
				List<Integer> pdfSequenceChecker = new ArrayList<Integer>();
				
				// Sort the list before validations
				List<FrontMatterPdf> pdfs = section.getPdfs();
				Collections.sort(pdfs);
				section.setPdfs(pdfs);
				for (FrontMatterPdf pdf : pdfs) {
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
	}
	
	private void validateProviewKeywords(Errors errors) {
		// Validate that required keyword are selected
		// getAllKeywordTypeCodes must be in sorted order by the name because
		// form.getKeywords returns a String Collection of KeywordTypeValues placed
		// in the order of KeywordTypeCodes.
		List<KeywordTypeCode> keywordCodes = codeService.getAllKeywordTypeCodes();
		int i = 0;
		for(KeywordTypeCode code : keywordCodes) {
			// Check that user has selected a keyword if that KeywordTypeCode is required
			if(code.getIsRequired()) {
				ValidationUtils.rejectIfEmptyOrWhitespace(errors, "keywords["+ i +"]", "error.required");
			}
			i++;
		}
	}
	
	private void validateProdOnlyRequirements(EditBookDefinitionForm form, Errors errors) {
		// Check if pdf file and cover image exists on NAS location when on prod server
		if(environmentName.equalsIgnoreCase(CoreConstants.PROD_ENVIRONMENT_NAME)) {
			// Check cover image exists
			if(StringUtils.isNotBlank(form.getTitleId())) {
				fileExist(errors, form.createCoverImageName(), WebConstants.LOCATION_COVER_IMAGE, "validateForm", "error.not.exist");
				if(form.getPilotBook() == PilotBookStatus.TRUE) {
					fileExist(errors, form.createPilotBookCsvName(), WebConstants.LOCATION_PILOT_BOOK_CSV, "pilotBook", "error.pilot.boo.file.not.exist");
				}
			}
			// Check all pdfs on Front Matter
			int i=0;
			for(FrontMatterPage page: form.getFrontMatters()) {
				int j = 0;
				for(FrontMatterSection section : page.getFrontMatterSections()) {
					int k = 0;
					for (FrontMatterPdf pdf : section.getPdfs()) {
						String filename = pdf.getPdfFilename();
						if(StringUtils.isNotBlank(filename)) {
							fileExist(errors, filename, WebConstants.LOCATION_PDF, "frontMatters["+ i +"].frontMatterSections["+ j +"].pdfs["+ k +"].pdfFilename", "error.not.exist");
						}
						k++;
					}
					j++;
				}
				i++;
			}
		}
	}
	
	private void fileExist(Errors errors, String filename, String location, String fieldName, String errorMessage) {
		File file = new File(location, filename);
		if(!file.isFile()) {
			errors.rejectValue(fieldName, errorMessage, new Object[] {filename, location}, "File does not exist on server.");
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
	public void setEnvironmentName(String environmentName) {
		this.environmentName = environmentName;
	}
	@Required
	public void setRootCodesWorkbenchLandingStrip(File rootDir) {
		this.rootCodesWorkbenchLandingStrip = rootDir;
	}
}
