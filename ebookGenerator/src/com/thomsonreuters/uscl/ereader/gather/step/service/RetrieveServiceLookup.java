package com.thomsonreuters.uscl.ereader.gather.step.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public interface RetrieveServiceLookup {
    RetrieveService getRetrieveService(BookDefinition.SourceType sourceType);
}
