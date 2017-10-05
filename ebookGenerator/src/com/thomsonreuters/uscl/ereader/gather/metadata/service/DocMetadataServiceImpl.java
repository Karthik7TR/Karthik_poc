package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.gather.metadata.dao.DocMetadataDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadataPK;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.parsinghandler.DocMetaDataXMLParser;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

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
    @Override
    @Transactional
    public void saveDocMetadata(final DocMetadata docmetadata) {
        //TODO: Add full set of character encodings here.
        final String cite = docmetadata.getNormalizedFirstlineCite();
        final String normalizedCite = NormalizationRulesUtil.applyCitationNormalizationRules(cite);
        docmetadata.setNormalizedFirstlineCite(normalizedCite);

        final DocMetadataPK existingDocPk = new DocMetadataPK();
        existingDocPk.setDocUuid(docmetadata.getDocUuid());
        existingDocPk.setJobInstanceId(docmetadata.getJobInstanceId());
        existingDocPk.setTitleId(docmetadata.getTitleId());

        final DocMetadata existingDocMetadata = docMetadataDAO.findDocMetadataByPrimaryKey(existingDocPk);

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
            docMetadataDAO.saveMetadata(existingDocMetadata);
        } else {
            docMetadataDAO.saveMetadata(docmetadata);
        }
    }

    /**
     * Update an existing DocMetadata entity
     *
     */
    @Override
    public void updateDocMetadata(final DocMetadata docmetadata) {
        docMetadataDAO.updateMetadata(docmetadata);
    }

    /**
     * Delete an existing DocMetadata entity
     *
     */
    @Override
    @Transactional
    public void deleteDocMetadata(final DocMetadata docmetadata) {
        docMetadataDAO.remove(docmetadata);
    }

    /**
     */
    @Override
    @Transactional(readOnly = true)
    public DocMetadata findDocMetadataByPrimaryKey(
        final String titleId,
        final Long jobInstanceId,
        final String docUuid) {
        final DocMetadataPK docMetaPk = new DocMetadataPK();
        docMetaPk.setTitleId(titleId);
        docMetaPk.setJobInstanceId(jobInstanceId);
        docMetaPk.setDocUuid(docUuid);
        return docMetadataDAO.findDocMetadataByPrimaryKey(docMetaPk);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findDistinctSplitTitlesByJobId(final Long jobInstanceId) {
        return docMetadataDAO.findDistinctSplitTitlesByJobId(jobInstanceId);
    }

    @Override
    @Transactional(readOnly = true)
    public DocumentMetadataAuthority findAllDocMetadataForTitleByJobId(final Long jobInstanceId) {
        return docMetadataDAO.findAllDocMetadataForTitleByJobId(jobInstanceId);
    }

    /**
     */
    @Override
    @Transactional
    public void parseAndStoreDocMetadata(
        final String titleId,
        final Long jobInstanceId,
        final String collectionName,
        final File metaDataFile) throws Exception {
        /* Instantiate a SAX parser instance.
           Note that this cannot be a Spring context singleton due to the state maintained within the
           class instance and we need thread safety because this is ultimately used in a Spring Batch job step. */
        final DocMetaDataXMLParser xmlParser = DocMetaDataXMLParser.create();
        final DocMetadata docMetaData = xmlParser.parseDocument(titleId, jobInstanceId, collectionName, metaDataFile);
        saveDocMetadata(docMetaData);
    }

    /**
     */
    @Override
    @Transactional
    public int updateProviewFamilyUUIDDedupFields(final Long jobInstanceId) throws Exception {
        //Dedupe the document family records.
        int duplicateDocCounter = 0;
        final DocumentMetadataAuthority docAuthority = findAllDocMetadataForTitleByJobId(jobInstanceId);
        final Map<String, Integer> familyAuth = new HashMap<>();
        for (final DocMetadata docMeta : docAuthority.getAllDocumentMetadata()) {
            if (docMeta.getDocFamilyUuid() != null && familyAuth.containsKey(docMeta.getDocFamilyUuid())) {
                final Integer dedupValue = familyAuth.get(docMeta.getDocFamilyUuid()) + 1;
                docMeta.setProviewFamilyUUIDDedup(dedupValue);
                updateDocMetadata(docMeta);
                familyAuth.put(docMeta.getDocFamilyUuid(), dedupValue);
                duplicateDocCounter++;
            } else {
                familyAuth.put(docMeta.getDocFamilyUuid(), 0);
            }
        }
        return duplicateDocCounter;
    }

    /**
     */
    @Override
    @Transactional
    public void updateSplitBookFields(final Long jobInstanceId, final Map<String, DocumentInfo> documentInfoMap)
        throws Exception {
        final DocumentMetadataAuthority docAuthority = findAllDocMetadataForTitleByJobId(jobInstanceId);
        for (final DocMetadata docMeta : docAuthority.getAllDocumentMetadata()) {
            final String key = docMeta.getDocUuid();
            if (documentInfoMap.containsKey(key)) {
                final DocumentInfo documentInfo = documentInfoMap.get(key);
                docMeta.setDocSize(documentInfo.getDocSize());
                docMeta.setSpitBookTitle(documentInfo.getSplitTitleId());
                updateDocMetadata(docMeta);
            }
        }
    }

    @Required
    public void setdocMetadataDAO(final DocMetadataDao dao) {
        docMetadataDAO = dao;
    }

    @Override
    public Map<String, String> findDistinctProViewFamGuidsByJobId(final Long jobInstanceId) {
        final DocumentMetadataAuthority docAuthority = findAllDocMetadataForTitleByJobId(jobInstanceId);
        final Map<String, String> mapping = new HashMap<>();
        for (final DocMetadata docMeta : docAuthority.getAllDocumentMetadata()) {
            if (docMeta.getDocFamilyUuid() != null) {
                mapping.put(docMeta.getDocUuid(), docMeta.getProViewId());
            }
        }

        return mapping;
    }

    @Override
    @Transactional(readOnly = true)
    public DocMetadata findDocMetadataMapByPartialCiteMatchAndJobId(final Long jobInstanceId, final String cite) {
        return docMetadataDAO.findDocMetadataMapByPartialCiteMatchAndJobId(jobInstanceId, cite);
    }
}
