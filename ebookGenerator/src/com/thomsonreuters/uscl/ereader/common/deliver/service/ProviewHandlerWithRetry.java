package com.thomsonreuters.uscl.ereader.common.deliver.service;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;

public interface ProviewHandlerWithRetry {
    void removeTitle(String splitTitle, Version version) throws ProviewException;
}
