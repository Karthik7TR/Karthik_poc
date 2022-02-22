package com.thomsonreuters.uscl.ereader.format.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.format.domain.DocsToPagesMap;
import com.thomsonreuters.uscl.ereader.format.domain.PagesToDocsMap;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Service
public class DuplicatedPagebreaksDifferentDocsServiceImpl implements DuplicatedPagebreaksDifferentDocsService {
    private static final String DOCS_TO_PAGES_FILE = "docUuidToPageNumbers.json";
    private static final String PAGES_TO_DOCS_FILE = "pageNumberToDocUuids.json";
    private static final String DUPLICATED_PAGE_NUMBERS_ERROR_MESSAGE = "Duplicated page numbers in different documents.";

    @Override
    public void checkSamePageNumbersInDocs(final PagesToDocsMap pageNumberToDocUuids, final File outputDir, final BookStep step) {
        pageNumberToDocUuids.cleanAndPutInOrder();
        if (!pageNumberToDocUuids.isEmpty()) {
            DocsToPagesMap docUuidToPageNumbers = constructDocIdToPageNumbersMap(pageNumberToDocUuids);
            File pageNumberToDocUuidsFile = new File(outputDir, PAGES_TO_DOCS_FILE);
            File docUuidToPageNumbersFile = new File(outputDir, DOCS_TO_PAGES_FILE);

            saveMaps(pageNumberToDocUuids, docUuidToPageNumbers, pageNumberToDocUuidsFile, docUuidToPageNumbersFile);
            storeFilePathsInContext(step, pageNumberToDocUuidsFile, docUuidToPageNumbersFile);
            throw new EBookException(DUPLICATED_PAGE_NUMBERS_ERROR_MESSAGE);
        }
    }

    private DocsToPagesMap constructDocIdToPageNumbersMap(final PagesToDocsMap pageNumberToDocUuid) {
        DocsToPagesMap docUuidToPageNumbers = new DocsToPagesMap();
        pageNumberToDocUuid.forEach((pageNumber, documentGuids) -> {
            emptyIfNull(documentGuids.getDocumentsWithMainSection()).forEach(uuid -> docUuidToPageNumbers.addDocUuidMainPage(uuid, pageNumber));
            emptyIfNull(documentGuids.getDocumentsWithFootnotesSection()).forEach(uuid -> docUuidToPageNumbers.addDocUuidFootnotePage(uuid, pageNumber));
        });
        return docUuidToPageNumbers;
    }

    private void saveMaps(final PagesToDocsMap pageNumberToDocUuids, final DocsToPagesMap docUuidToPageNumbers,
                          final File pageNumberToDocUuidsFile, final File docUuidToPageNumbersFile) {
        ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
        try {
            writer.writeValue(pageNumberToDocUuidsFile, pageNumberToDocUuids.getMap());
            writer.writeValue(docUuidToPageNumbersFile, docUuidToPageNumbers.getMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void storeFilePathsInContext(final BookStep step, final File pageNumberToDocUuidsFile, final File docUuidToPageNumbersFile) {
        step.setJobExecutionProperty(
                JobExecutionKey.PAGE_NUMBER_TO_DOC_UUIDS_FILE,
                pageNumberToDocUuidsFile.getAbsolutePath());
        step.setJobExecutionProperty(
                JobExecutionKey.DOC_UUID_TO_PAGE_NUMBERS_FILE,
                docUuidToPageNumbersFile.getAbsolutePath());
    }
}
