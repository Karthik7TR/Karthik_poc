package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.gather.metadata.dao.DocMetadataDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.parsinghandler.DocMetaDataXMLParser;

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

	@Required
	public void setdocMetadataDAO(DocMetadataDao dao) {
		this.docMetadataDAO = dao;
	}

	public Map<String, String> findDistinctFamilyGuidsByJobId(Long jobInstanceId) {
		return docMetadataDAO.findDistinctFamilyGuidsByJobId(jobInstanceId);
	}
}
