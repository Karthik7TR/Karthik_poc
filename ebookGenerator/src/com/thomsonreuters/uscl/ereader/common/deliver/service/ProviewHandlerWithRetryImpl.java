package com.thomsonreuters.uscl.ereader.common.deliver.service;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.proview.ProviewRetry;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;

public class ProviewHandlerWithRetryImpl implements ProviewHandlerWithRetry
{
    @Resource(name = "proviewHandler")
    private ProviewHandler proviewHandler;

    @ProviewRetry
    @Override
    public void removeTitle(final String splitTitle, final Version version) throws ProviewException
    {
        final String response = proviewHandler.removeTitle(splitTitle, version);
        if (response.contains("200"))
        {
            proviewHandler.deleteTitle(splitTitle, version);
        }
    }
}
