/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.image.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;

public interface ImageDao {
	
	public List<ImageMetadataEntity> findImageMetadata(long jobInstanceId);
	
	public void saveImageMetadata(ImageMetadataEntity metadata);
}
