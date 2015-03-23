/*
 * Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;

public interface EditBookDefinitionService {

	public List<DocumentTypeCode> getDocumentTypes();
	
	public Map<String, String> getStates();
	
	public Map<String, String> getJurisdictions();
	
	public List<String> getFrontMatterThemes();
	
	public Map<String, String> getPubTypes();
	
	public Map<String, String> getPublishers();
	
	public List<KeywordTypeCode> getKeywordCodes();
	
	public DocumentTypeCode getContentTypeById(Long id);
	
	public List<String> getCodesWorkbenchDirectory(String folder);
}
