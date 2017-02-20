package com.thomsonreuters.uscl.ereader.notification.service;

import org.apache.commons.lang3.StringUtils;

public class DefaultEmailBuilder extends AbstractEmailBuilder
{
    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.notification.AbstractGeneratorEmailBuilder#getAdditionalSubjectPart()
     */
    @Override
    protected String getAdditionalSubjectPart()
    {
        return StringUtils.EMPTY;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.notification.AbstractGeneratorEmailBuilder#getAdditionalBodyPart()
     */
    @Override
    protected String getAdditionalBodyPart()
    {
        return StringUtils.EMPTY;
    }
}
