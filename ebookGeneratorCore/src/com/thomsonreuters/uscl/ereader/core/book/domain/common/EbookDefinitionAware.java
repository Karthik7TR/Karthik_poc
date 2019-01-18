package com.thomsonreuters.uscl.ereader.core.book.domain.common;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public interface EbookDefinitionAware {
    void setEbookDefinition(BookDefinition bookDefinition);
}
