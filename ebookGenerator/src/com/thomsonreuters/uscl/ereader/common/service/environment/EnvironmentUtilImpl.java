package com.thomsonreuters.uscl.ereader.common.service.environment;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.core.CoreConstants;

public class EnvironmentUtilImpl implements EnvironmentUtil {
    @Resource(name = "environmentName")
    private String environmentName;

    @Override
    public boolean isProd() {
        return CoreConstants.PROD_ENVIRONMENT_NAME.equalsIgnoreCase(environmentName);
    }
}
