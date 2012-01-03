package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.dao.DocMetadataDao;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

/**
 * Spring service that handles CRUD requests for DocMetadata entities
 * 
 */

@Service("DocMetadataService")
@Transactional
public class DocMetadataServiceImpl implements DocMetadataService {

	/**
	 * DAO injected by Spring that manages DocMetadata entities
	 * 
	 */
	@Autowired
	private DocMetadataDao docMetadataDAO;

	/**
	 * Instantiates a new DocMetadataServiceImpl.
	 *
	 */
	public DocMetadataServiceImpl() {
	}

	/**
	 * Return all DocMetadata entity
	 * 
	 */
	@Transactional
	public List<DocMetadata> findAllDocMetadatas(Integer startResult, Integer maxRows) {
		return new java.util.ArrayList<DocMetadata>(docMetadataDAO.findAllDocMetadatas(startResult, maxRows));
	}

	/**
	 * Save an existing DocMetadata entity
	 * 
	 */
	@Transactional
	public void saveDocMetadata(DocMetadata docmetadata) {
		DocMetadata existingDocMetadata = docMetadataDAO.findDocMetadataByPrimaryKey(docmetadata.getTitleId(), docmetadata.getJobInstanceId(), docmetadata.getDocUuid());

		if (existingDocMetadata != null) {
			if (existingDocMetadata != docmetadata) {
				existingDocMetadata.setTitleId(docmetadata.getTitleId());
				existingDocMetadata.setJobInstanceId(docmetadata.getJobInstanceId());
				existingDocMetadata.setDocUuid(docmetadata.getDocUuid());
				existingDocMetadata.setDocFamilyUuid(docmetadata.getDocFamilyUuid());
				existingDocMetadata.setDocType(docmetadata.getDocType());
				existingDocMetadata.setNormalizedFirstlineCite(docmetadata.getNormalizedFirstlineCite());
				existingDocMetadata.setFindOrig(docmetadata.getFindOrig());
				existingDocMetadata.setSerialNumber(docmetadata.getSerialNumber());
				existingDocMetadata.setCollectionName(docmetadata.getCollectionName());
				existingDocMetadata.setLastUpdated(docmetadata.getLastUpdated());
			}
			docmetadata = docMetadataDAO.store(existingDocMetadata);
		} else {
			docmetadata = docMetadataDAO.store(docmetadata);
		}
		docMetadataDAO.flush();
	}

	/**
	 * Load an existing DocMetadata entity
	 * 
	 */
	@Transactional
	public Set<DocMetadata> loadDocMetadatas() {
		return docMetadataDAO.findAllDocMetadatas();
	}

	/**
	 * Return a count of all DocMetadata entity
	 * 
	 */
	@Transactional
	public Integer countDocMetadatas() {
		return ((Long) docMetadataDAO.createQuerySingleResult("select count(*) from DocMetadata o").getSingleResult()).intValue();
	}

	/**
	 * Delete an existing DocMetadata entity
	 * 
	 */
	@Transactional
	public void deleteDocMetadata(DocMetadata docmetadata) {
		docMetadataDAO.remove(docmetadata);
		docMetadataDAO.flush();
	}

	/**
	 */
	@Transactional
	public DocMetadata findDocMetadataByPrimaryKey(String titleId, Integer jobInstanceId, String docUuid) {
		return docMetadataDAO.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docUuid);
	}
}
