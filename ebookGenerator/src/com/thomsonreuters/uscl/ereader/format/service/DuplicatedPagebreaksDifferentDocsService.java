package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.format.domain.PagesToDocsMap;
import java.io.File;

public interface DuplicatedPagebreaksDifferentDocsService {
    void checkSamePageNumbersInDocs(PagesToDocsMap pageNumberToDocUuids, File outputDir, BookStep step);
}
