/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.westgroup.publishingservices.uuidgenerator.UUID;
import com.westgroup.publishingservices.uuidgenerator.UUIDFactory;

/**
 * Utility responsible for generating UUIDs.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 */
public class UuidGenerator {
	private static final Logger LOG = Logger.getLogger(UuidGenerator.class);
	private UUIDFactory uuidFactory;

	public String generateUuid() {
		UUID rawUuid = uuidFactory.getUUID();
		return (rawUuid != null && StringUtils.isNotBlank(rawUuid.toString())) ? rawUuid.toString() : "";
	}
}
