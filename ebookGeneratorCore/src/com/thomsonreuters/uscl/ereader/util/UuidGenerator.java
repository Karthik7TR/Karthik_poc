package com.thomsonreuters.uscl.ereader.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.westgroup.publishingservices.uuidgenerator.UUID;
import com.westgroup.publishingservices.uuidgenerator.UUIDFactory;

/**
 * Utility responsible for generating UUIDs.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 */
public class UuidGenerator
{
    private static final Logger LOG = LogManager.getLogger(UuidGenerator.class);
    private UUIDFactory uuidFactory;

    public String generateUuid()
    {
        if (uuidFactory != null)
        {
            final UUID rawUuid = uuidFactory.getUUID();
            return (rawUuid != null && StringUtils.isNotBlank(rawUuid.toString())) ? rawUuid.toString() : uuidWithoutDashes();
        }
        return uuidWithoutDashes();
    }

    public void setUuidFactory(final UUIDFactory uuidFactory)
    {
        this.uuidFactory = uuidFactory;
    }

    private String uuidWithoutDashes()
    {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }
}
