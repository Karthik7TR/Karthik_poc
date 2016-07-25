/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import com.thomsonreuters.uscl.ereader.core.book.service.CodeService;

public class EditBookDefinitionServiceImpl implements EditBookDefinitionService {
	//private static final Logger log = LogManager.getLogger(EditBookDefinitionForm.class);
	private CodeService codeService;
	private File rootCodesWorkbenchLandingStrip;
	private List<String> frontMatterThemes;	

	public List<DocumentTypeCode> getDocumentTypes() {
		return codeService.getAllDocumentTypeCodes();
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
	
	public List<String> getFrontMatterThemes() {
		return frontMatterThemes;
	}
	
	public void setFrontMatterThemes(List<String> frontMatterThemes) {
		this.frontMatterThemes = frontMatterThemes;
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
	
	public List<String> getCodesWorkbenchDirectory(String folder) {
		if(StringUtils.isNotBlank(folder)) {
			File dir = new File(rootCodesWorkbenchLandingStrip, folder);
			if(dir.exists()) {
				List<String> files = Arrays.asList(dir.list());
				Collections.sort(files, String.CASE_INSENSITIVE_ORDER);
				return files;
			} else {
				return null;
			}
		} else {
			List<String> files = Arrays.asList(rootCodesWorkbenchLandingStrip.list());
			Collections.sort(files, String.CASE_INSENSITIVE_ORDER);
			return files;
		}
	}
	
	@Required
	public void setCodeService(CodeService service) {
		codeService = service;
	}
	@Required
	public void setRootCodesWorkbenchLandingStrip(File rootDir) {
		this.rootCodesWorkbenchLandingStrip = rootDir;
	}
}
