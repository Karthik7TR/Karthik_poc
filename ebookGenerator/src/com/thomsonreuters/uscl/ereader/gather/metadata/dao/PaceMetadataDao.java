/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.metadata.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.PaceMetadata;


/**
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 *
 */
public interface PaceMetadataDao
{
    /**
     * Query - findPaceMetadataByPrimaryKey
     */
    public PaceMetadata findPaceMetadataByPrimaryKey(Long publicationId)
        throws DataAccessException;

    public void remove(PaceMetadata toRemove) throws DataAccessException;

    public void saveMetadata(PaceMetadata metadata);

    public void updateMetadata(PaceMetadata metadata);

     /**
         * Query - findPaceMetadataByPubCode
         * 
         */
    List<PaceMetadata> findPaceMetadataByPubCode(Long pubCode)
        throws DataAccessException;
}
