package com.thomsonreuters.uscl.ereader.gather.step.service.container;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DocsGuidsContainer implements SourceContainer {
    private final List<String> docsGuidsComponents = new ArrayList<>();

    @SneakyThrows
    @Override
    public SourceContainer addSource(final File source, final BookDefinition bookDefinition) {
        final String docsGuidsString = FileUtils.readFileToString(source).trim();
        docsGuidsComponents.add(docsGuidsString);
        return this;
    }

    @Override
    public Collection<String> getSources() {
        return docsGuidsComponents;
    }
}
