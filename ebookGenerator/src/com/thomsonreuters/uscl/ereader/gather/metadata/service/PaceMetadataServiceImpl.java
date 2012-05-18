package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.gather.metadata.dao.PaceMetadataDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;


/**
 * Spring service that handles CRUD requests for DocMetadata entities
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 */
public class PaceMetadataServiceImpl implements PaceMetadataService
{
    /** DAO injected by Spring that manages PaceMetadataDao entities */
    private PaceMetadataDao paceMetadataDao;

    /**
     * Delete an existing DocMetadata entity
     */
    @Transactional
    public void deletePaceMetadata(PaceMetadata paceMetadata)
    {
        paceMetadataDao.remove(paceMetadata);
    }

    @Transactional(readOnly = true)
    public List<PaceMetadata> findAllPaceMetadataForPubCode(Long pubCode)
    {
        return paceMetadataDao.findPaceMetadataByPubCode(pubCode);
    }

    /**
     * 
     */
    @Transactional(readOnly = true)
    public PaceMetadata findPaceMetadataByPrimaryKey(Long pubId)
    {
        return paceMetadataDao.findPaceMetadataByPrimaryKey(pubId);
    }

    /**
     * Save an existing PaceMetadata entity
     */
    @Transactional
    public void savePaceMetadata(PaceMetadata paceMetadata)
    {
        //TODO: Add full set of character encodings here.
        paceMetadataDao.saveMetadata(paceMetadata);
    }

    @Required
    public void setpaceMetadataDAO(PaceMetadataDao dao)
    {
        this.paceMetadataDao = dao;
    }

    /**
     * Update an existing PaceMetadata entity
     */
    public void updatePaceMetadata(PaceMetadata paceMetadata)
    {
        paceMetadataDao.updateMetadata(paceMetadata);
    }
	
}
