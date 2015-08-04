/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.gather.metadata.dao.DocMetadataDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.parsinghandler.DocMetaDataXMLParser;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;

/**
 * Spring service that handles CRUD requests for DocMetadata entities
 * 
 */
public class DocMetadataServiceImpl implements DocMetadataService {

	/**
	 * DAO injected by Spring that manages DocMetadata entities
	 */

	private DocMetadataDao docMetadataDAO;

	/**
	 * Save an existing DocMetadata entity
	 * 
	 */
	@Transactional
	public void saveDocMetadata(DocMetadata docmetadata) {
		//TODO: Add full set of character encodings here.
		String cite = docmetadata.getNormalizedFirstlineCite();
		String normalizedCite = NormalizationRulesUtil.applyCitationNormalizationRules(cite);
		docmetadata.setNormalizedFirstlineCite(normalizedCite);

		DocMetadataPK existingDocPk = new DocMetadataPK();
		existingDocPk.setDocUuid(docmetadata.getDocUuid());
		existingDocPk.setJobInstanceId(docmetadata.getJobInstanceId());
		existingDocPk.setTitleId(docmetadata.getTitleId());

		DocMetadata existingDocMetadata = docMetadataDAO
				.findDocMetadataByPrimaryKey(existingDocPk);

		if (existingDocMetadata != null) {
			if (existingDocMetadata != docmetadata) {
				existingDocMetadata.setTitleId(docmetadata.getTitleId());
				existingDocMetadata.setJobInstanceId(docmetadata
						.getJobInstanceId());
				existingDocMetadata.setDocUuid(docmetadata.getDocUuid());
				existingDocMetadata.setDocFamilyUuid(docmetadata
						.getDocFamilyUuid());
				existingDocMetadata.setDocType(docmetadata.getDocType());
				existingDocMetadata.setNormalizedFirstlineCite(docmetadata.getNormalizedFirstlineCite());
				existingDocMetadata.setFindOrig(docmetadata.getFindOrig());
				existingDocMetadata.setSerialNumber(docmetadata
						.getSerialNumber());
				existingDocMetadata.setCollectionName(docmetadata
						.getCollectionName());
				existingDocMetadata
						.setLastUpdated(docmetadata.getLastUpdated());
				}
			docMetadataDAO.saveMetadata(existingDocMetadata);
		} else {
			docMetadataDAO.saveMetadata(docmetadata);
		}
	}
	

	/**
	 * Update an existing DocMetadata entity
	 * 
	 */
	public void updateDocMetadata(DocMetadata docmetadata)
	{
		docMetadataDAO.updateMetadata(docmetadata);
	}

	/**
	 * Delete an existing DocMetadata entity
	 * 
	 */
	@Transactional
	public void deleteDocMetadata(DocMetadata docmetadata) {
		docMetadataDAO.remove(docmetadata);
	}

	/**
	 */
	@Transactional(readOnly = true)
	public DocMetadata findDocMetadataByPrimaryKey(String titleId,
			Long jobInstanceId, String docUuid) {
		DocMetadataPK docMetaPk = new DocMetadataPK();
		docMetaPk.setTitleId(titleId);
		docMetaPk.setJobInstanceId(jobInstanceId);
		docMetaPk.setDocUuid(docUuid);
		return docMetadataDAO.findDocMetadataByPrimaryKey(docMetaPk);
	}
	
	@Transactional(readOnly = true)
	public List<String> findDistinctSplitTitlesByJobId(Long jobInstanceId){
		return docMetadataDAO.findDistinctSplitTitlesByJobId(jobInstanceId);
	}

	@Transactional(readOnly = true)
	public DocumentMetadataAuthority findAllDocMetadataForTitleByJobId(Long jobInstanceId){
		return docMetadataDAO.findAllDocMetadataForTitleByJobId(jobInstanceId);
	}
	
	/**
	 */
	@Transactional
	public void parseAndStoreDocMetadata(String titleId, Long jobInstanceId, String collectionName, File metaDataFile) throws Exception {
		/* Instantiate a SAX parser instance. 
		   Note that this cannot be a Spring context singleton due to the state maintained within the 
		   class instance and we need thread safety because this is ultimately used in a Spring Batch job step. */
		DocMetaDataXMLParser xmlParser = DocMetaDataXMLParser.create();
		DocMetadata docMetaData = xmlParser.parseDocument(titleId, jobInstanceId, collectionName, metaDataFile);
		saveDocMetadata(docMetaData);
	}
	
	/**
	 */
	@Transactional
	public int updateProviewFamilyUUIDDedupFields(Long jobInstanceId) throws Exception {

		//Dedupe the document family records.
		int duplicateDocCounter =0; 
		DocumentMetadataAuthority docAuthority = findAllDocMetadataForTitleByJobId(jobInstanceId);
		Map<String, Integer> familyAuth = new HashMap<String, Integer>();
		for (DocMetadata docMeta : docAuthority.getAllDocumentMetadata())
		{
			if (docMeta.getDocFamilyUuid() != null && familyAuth.containsKey(docMeta.getDocFamilyUuid()))
			{
				Integer dedupValue = familyAuth.get(docMeta.getDocFamilyUuid()) + 1;
				docMeta.setProviewFamilyUUIDDedup(dedupValue);
				updateDocMetadata(docMeta);
				familyAuth.put(docMeta.getDocFamilyUuid(), dedupValue);
				duplicateDocCounter++;
			}
			else
			{
				familyAuth.put(docMeta.getDocFamilyUuid(), 0);
			}
		}
		return duplicateDocCounter;
	}
	

	/**
	 */
	@Transactional
	public void updateSplitBookFields(Long jobInstanceId, Map<String, DocumentInfo> documentInfoMap) throws Exception {

		DocumentMetadataAuthority docAuthority = findAllDocMetadataForTitleByJobId(jobInstanceId);
		for (DocMetadata docMeta : docAuthority.getAllDocumentMetadata()) {
			String key = docMeta.getDocUuid();
			if (documentInfoMap.containsKey(key)) {
				DocumentInfo documentInfo = documentInfoMap.get(key);
				docMeta.setDocSize(documentInfo.getDocSize());
				docMeta.setSpitBookTitle(documentInfo.getSplitTitleId());
				updateDocMetadata(docMeta);
			}
		}
	}
	
	@Required
	public void setdocMetadataDAO(DocMetadataDao dao) {
		this.docMetadataDAO = dao;
	}
	
	public Map<String, String> findDistinctProViewFamGuidsByJobId(Long jobInstanceId)
	{
		DocumentMetadataAuthority docAuthority = findAllDocMetadataForTitleByJobId(jobInstanceId);
		Map<String, String> mapping = new HashMap<String, String>();
		for (DocMetadata docMeta : docAuthority.getAllDocumentMetadata())
		{
			if (docMeta.getDocFamilyUuid() != null)
			{
				mapping.put(docMeta.getDocUuid(), docMeta.getProViewId());
			}
		}
		
		return mapping;
	}


	@Transactional(readOnly = true)
	public DocMetadata findDocMetadataMapByPartialCiteMatchAndJobId(Long jobInstanceId, String cite){
		return docMetadataDAO.findDocMetadataMapByPartialCiteMatchAndJobId(jobInstanceId, cite);
	}
}
