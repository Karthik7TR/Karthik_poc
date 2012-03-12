/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;

public class EditBookDefinitionService {
	//private static final Logger log = Logger.getLogger(EditBookDefinitionForm.class);
	private CodeService codeService;

	public Map<Long, String> getDocumentTypes() {
		List<DocumentTypeCode> codes = codeService.getAllDocumentTypeCodes();
		Map<Long,String> documentTypes = new LinkedHashMap<Long,String>();
		
		for(DocumentTypeCode code : codes) {
			documentTypes.put(code.getId(), code.getName());
		}

		return documentTypes;
	}
	
	public Map<String, String> getStates() {
		List<StateCode> codes = codeService.getAllStateCodes();
		Map<String ,String> states = new LinkedHashMap<String, String>();
		
		for(StateCode code : codes) {
			states.put(code.getName().toLowerCase(), code.getName());
		}
		
		return states;
	}
	
	public Map<String, String> getJurisdictions() {
		List<JurisTypeCode> codes = codeService.getAllJurisTypeCodes();
		Map<String,String> jurisdictions = new LinkedHashMap<String,String>();
		
		for(JurisTypeCode code : codes) {
			jurisdictions.put(code.getName().toLowerCase(), code.getName());
		}
		
		return jurisdictions;
	}
	
	public Map<String, String> getPubTypes() {
		List<PubTypeCode> codes = codeService.getAllPubTypeCodes();
		Map<String,String> pubTypes = new LinkedHashMap<String,String>();

		for(PubTypeCode code : codes) {
			pubTypes.put(code.getName().toLowerCase(), code.getName());
		}
		
		return pubTypes;
	}
	
	public Map<String, String> getPublishers() {
		List<PublisherCode> codes = codeService.getAllPublisherCodes();
		Map<String,String> publishers = new LinkedHashMap<String,String>();
		
		for(PublisherCode code : codes) {
			publishers.put(code.getName().toLowerCase(), code.getName());
		}
		
		return publishers;
	}
	
	public List<KeywordTypeCode> getKeywordCodes() {
		return codeService.getAllKeywordTypeCodes();
	}
	
	public DocumentTypeCode getContentTypeById(Long id) {
		return codeService.getDocumentTypeCodeById(id);
	}
	
	@Required
	public void setCodeService(CodeService service) {
		codeService = service;
	}
}
