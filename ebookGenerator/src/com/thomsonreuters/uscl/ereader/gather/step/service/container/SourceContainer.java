package com.thomsonreuters.uscl.ereader.gather.step.service.container;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

import java.io.File;
import java.util.Collection;

public interface SourceContainer {
    SourceContainer addSource(final File source, final BookDefinition bookDefinition);
    Collection<String> getSources();
}
