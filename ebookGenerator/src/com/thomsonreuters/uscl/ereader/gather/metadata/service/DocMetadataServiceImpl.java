package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.dao.DocMetadataDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK;
import com.thomsonreuters.uscl.ereader.gather.parsinghandler.DocMetaDataXMLParser;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring service that handles CRUD requests for DocMetadata entities
 * 
 */

@Transactional
public class DocMetadataServiceImpl implements DocMetadataService {

	/**
	 * DAO injected by Spring that manages DocMetadata entities
	 * 
	 */

	private DocMetadataDao docMetadataDAO;

	private DocMetaDataXMLParser docMetaXMLParser;

	/**
	 * Instantiates a new DocMetadataServiceImpl.
	 * 
	 */
	/*
	 * public DocMetadataServiceImpl() { docMetaXMLParser = new
	 * DocMetaDataXMLParser(); }
	 */

	/**
	 * Save an existing DocMetadata entity
	 * 
	 */
	@Transactional
	public void saveDocMetadata(DocMetadata docmetadata) {

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
				existingDocMetadata.setNormalizedFirstlineCite(docmetadata
						.getNormalizedFirstlineCite());
				existingDocMetadata.setFindOrig(docmetadata.getFindOrig());
				existingDocMetadata.setSerialNumber(docmetadata
						.getSerialNumber());
				existingDocMetadata.setCollectionName(docmetadata
						.getCollectionName());
				existingDocMetadata
						.setLastUpdated(docmetadata.getLastUpdated());
				existingDocMetadata
				.setTocSeqNumber(docmetadata.getTocSeqNumber());
				}
			docMetadataDAO.saveMetadata(existingDocMetadata);
		} else {
			docMetadataDAO.saveMetadata(docmetadata);
		}
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
	@Transactional
	public DocMetadata findDocMetadataByPrimaryKey(String titleId,
			Integer jobInstanceId, String docUuid) {
		DocMetadataPK docMetaPk = new DocMetadataPK();
		docMetaPk.setTitleId(titleId);
		docMetaPk.setJobInstanceId(jobInstanceId);
		docMetaPk.setDocUuid(docUuid);
		return docMetadataDAO.findDocMetadataByPrimaryKey(docMetaPk);
	}

	/**
	 */
	@Transactional
	public void parseAndStoreDocMetadata(String titleId, Integer jobInstanceId,
			String collectionName, File metaDataFile, String tocSequenceNumber) {
		saveDocMetadata(docMetaXMLParser.parseDocument(titleId, jobInstanceId,
				collectionName, metaDataFile, tocSequenceNumber));
	}

	/**
	 */
	@Transactional
	public Map<String, String> findDocMetadataByDocUuid(String docUuid) {
		return docMetadataDAO.findDocMetadataMapByDocUuid(docUuid);
	}

	@Required
	public void setdocMetaXMLParser(DocMetaDataXMLParser parser) {
		this.docMetaXMLParser = parser;
	}

	@Required
	public void setdocMetadataDAO(DocMetadataDao dao) {
		this.docMetadataDAO = dao;
	}

	@Override
	public List<DocMetadata> findOrderedDocMetadataByJobId(Integer jobInstanceId) {
		return docMetadataDAO.findOrderedDocMetadataByJobId(jobInstanceId);
	}
}
