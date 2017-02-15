package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.metadata.dao.PaceMetadataDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

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
    @Override
    @Transactional
    public void deletePaceMetadata(final PaceMetadata paceMetadata)
    {
        paceMetadataDao.remove(paceMetadata);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaceMetadata> findAllPaceMetadataForPubCode(final Long pubCode)
    {
        return paceMetadataDao.findPaceMetadataByPubCode(pubCode);
    }

    /**
     *
     */
    @Override
    @Transactional(readOnly = true)
    public PaceMetadata findPaceMetadataByPrimaryKey(final Long pubId)
    {
        return paceMetadataDao.findPaceMetadataByPrimaryKey(pubId);
    }

    /**
     * Save an existing PaceMetadata entity
     */
    @Override
    @Transactional
    public void savePaceMetadata(final PaceMetadata paceMetadata)
    {
        //TODO: Add full set of character encodings here.
        paceMetadataDao.saveMetadata(paceMetadata);
    }

    @Required
    public void setpaceMetadataDAO(final PaceMetadataDao dao)
    {
        paceMetadataDao = dao;
    }

    /**
     * Update an existing PaceMetadata entity
     */
    @Override
    public void updatePaceMetadata(final PaceMetadata paceMetadata)
    {
        paceMetadataDao.updateMetadata(paceMetadata);
    }
}
