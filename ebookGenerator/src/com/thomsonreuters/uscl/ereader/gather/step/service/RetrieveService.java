package com.thomsonreuters.uscl.ereader.gather.step.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import org.springframework.batch.core.scope.context.ChunkContext;

import java.io.File;

public interface RetrieveService {
    GatherResponse retrieveToc(BookDefinition bookDefinition, File tocFile, ChunkContext chunkContext) throws Exception;

    GatherResponse retrieveDocsAndMetadata(BookDefinition bookDefinition, File tocFile, File docsGuidsFile, ChunkContext chunkContext) throws Exception;
}
