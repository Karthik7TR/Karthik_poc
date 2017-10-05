package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.core.book.domain.RenameTocEntry;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

public interface TocService {
    GatherResponse findTableOfContents(
        String guid,
        String collectionName,
        File tocFile,
        List<ExcludeDocument> excludeDocuments,
        List<RenameTocEntry> renameTocEntries,
        boolean isFinalStage,
        List<String> splitTocGuidList,
        int thresholdValue) throws GatherException;
}
