package com.thomsonreuters.uscl.ereader.core.book.service;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;

/**
 * Spring service that handles CRUD requests for EBookAudit entities
 * 
 */

@Transactional
public class EBookAuditServiceImpl implements EBookAuditService {

	/**
	 * DAO injected by Spring that manages EBookAudit entities
	 * 
	 */

	private EbookAuditDao eBookAuditDAO;


	/**
	 * Save an existing eBookAudit entity
	 * 
	 */
	@Transactional
	public void saveEBookAudit(EbookAudit eBookAudit) {


		EbookAudit existingAudit = eBookAuditDAO.findEbookAuditByPrimaryKey(eBookAudit.getAuditId());

		if (existingAudit != null) {
			if (existingAudit != eBookAudit) {
				existingAudit.setAuditId(eBookAudit.getAuditId());
				existingAudit.setAuditNote(eBookAudit.getAuditNote());
				existingAudit.setAuditType(eBookAudit.getAuditType());
				existingAudit.setAuthorNamesConcat(eBookAudit.getAuthorNamesConcat());
				existingAudit.setAutoUpdateSupportFlag(eBookAudit.getAutoUpdateSupportFlag());
				existingAudit.setBookNamesConcat(eBookAudit.getBookNamesConcat());
				existingAudit.setCopyright(eBookAudit.getCopyright());
				existingAudit.setCopyrightPageText(eBookAudit.getCopyrightPageText());
				existingAudit.setCoverImage(eBookAudit.getCoverImage());
				existingAudit.setCurrency(eBookAudit.getCurrency());
				existingAudit.setDocCollectionName(eBookAudit.getDocCollectionName());
				existingAudit.setDocumentTypeCodesId(eBookAudit.getDocumentTypeCodesId());
				existingAudit.setFrontMatterConcat(eBookAudit.getFrontMatterConcat());
				existingAudit.setIsbn(eBookAudit.getIsbn());
				existingAudit.setIsTocFlag(eBookAudit.getIsTocFlag());
				existingAudit.setKeyciteToplineFlag(eBookAudit.getKeyciteToplineFlag());
				existingAudit.setKeywordsConcat(eBookAudit.getKeywordsConcat());
				existingAudit.setMaterialId(eBookAudit.getMaterialId());
				existingAudit.setNortDomain(eBookAudit.getNortDomain());
				existingAudit.setNortFilterView(eBookAudit.getNortFilterView());
				existingAudit.setOnePassSsoLinkFlag(eBookAudit.getOnePassSsoLinkFlag());
				existingAudit.setPublishDateText(eBookAudit.getPublishDateText());
				existingAudit.setPublishedOnceFlag(eBookAudit.getPublishedOnceFlag());
				existingAudit.setPublisherCodesId(eBookAudit.getPublisherCodesId());
				existingAudit.setRootTocGuid(eBookAudit.getRootTocGuid());
				existingAudit.setSearchIndexFlag(eBookAudit.getSearchIndexFlag());
				existingAudit.setLastUpdated(eBookAudit.getLastUpdated());
				existingAudit.setTitleId(eBookAudit.getTitleId());
				existingAudit.setTocCollectionName(eBookAudit.getTocCollectionName());
				existingAudit.setUpdatedBy(eBookAudit.getUpdatedBy());
				}
			eBookAuditDAO.saveAudit(existingAudit);
		} else {
			eBookAuditDAO.saveAudit(eBookAudit);
		}
	}

	/**
	 * Delete an existing Author entity
	 * 
	 */
	@Transactional
	public void deleteEBookAudit(EbookAudit eBookAudit) {
		eBookAuditDAO.remove(eBookAudit);
	}

	@Override
	public EbookAudit findEBookAuditByPrimaryKey(Long auditId) {
		return eBookAuditDAO.findEbookAuditByPrimaryKey(auditId);
	}

	@Required
	/**
	 * @param eBookAuditDAO the eBookAuditDAO to set
	 */
	public void seteBookAuditDAO(EbookAuditDao eBookAuditDAO) {
		this.eBookAuditDAO = eBookAuditDAO;
	}

	/**
	 * @return the eBookAuditDAO
	 */
	public EbookAuditDao geteBookAuditDAO() {
		return eBookAuditDAO;
	}
}
