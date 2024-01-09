package com.thomsonreuters.uscl.ereader.util;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import org.apache.commons.lang3.StringUtils;

import com.westgroup.publishingservices.uuidgenerator.UUID;
import com.westgroup.publishingservices.uuidgenerator.UUIDFactory;
import org.springframework.stereotype.Service;

/**
 * Utility responsible for generating UUIDs.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a>u0081674
 */
@Service
public class UuidGenerator {
    private static UUIDFactory uuidFactory = initUUIDFactory();

    public String generateUuid() {
        if (uuidFactory != null) {
            final UUID rawUuid = uuidFactory.getUUID();
            return (rawUuid != null && StringUtils.isNotBlank(rawUuid.toString()))
                ? rawUuid.toString() : uuidWithoutDashes();
        }
        return uuidWithoutDashes();
    }

    private static UUIDFactory initUUIDFactory() {
        try {
            return new UUIDFactory();
        } catch (Exception e) {
            throw new EBookException(e);
        }
    }

    private String uuidWithoutDashes() {
        return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }
}
