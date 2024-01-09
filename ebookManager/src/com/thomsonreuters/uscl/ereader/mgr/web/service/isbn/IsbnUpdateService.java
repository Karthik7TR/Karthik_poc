package com.thomsonreuters.uscl.ereader.mgr.web.service.isbn;

import com.thomsonreuters.uscl.ereader.common.filesystem.NasFileSystem;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class IsbnUpdateService {
    private static final String LIBRARY = "library";
    private static final String JSON = ".json";
    private static final String TITLE_ID = "titleId";
    private static final String TITLE_VERSION = "titleVersion";
    private static final String ISBN = "isbn";
    private static final String ARCHIVED_FILE_DATE_PATTERN = "yyyyMMddHHmmss";
    private static final String ISBN_INFO_MESSAGE = "Isbn of title %s with version %s: %s";
    private static final String FILE_ARCHIVED_MESSAGE = "File %s was archived";

    @Autowired
    private VersionIsbnService versionIsbnService;
    @Autowired
    private BookDefinitionService bookDefinitionService;
    @Autowired
    private NasFileSystem nasFileSystem;

    public void updateVersionIsbn() throws IOException, ParseException {
        String library = nasFileSystem.getIsbnFileDir() + File.separator + LIBRARY + JSON;
        try (Reader reader = new FileReader(library)) {
            updateIsbns(reader);
        }
        archiveFile(library);
    }

    private void updateIsbns(final Reader reader) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);
        for (Object object : jsonArray) {
            updateSingleIsbn((JSONObject) object);
        }
    }

    private void updateSingleIsbn(final JSONObject jsonObject) {
        if (isObjectContainsTitleInformation(jsonObject)) {
            String titleId = (String) jsonObject.get(TITLE_ID);
            String titleVersion = getTitleVersion(jsonObject);
            String isbn = (String) jsonObject.get(ISBN);
            log.info(String.format(ISBN_INFO_MESSAGE, titleId, titleVersion, isbn));
            BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByTitle(titleId);
            if (bookDefinition != null) {
                versionIsbnService.saveIsbn(bookDefinition, titleVersion, isbn);
            }
        }
    }

    private boolean isObjectContainsTitleInformation(final JSONObject jsonObject) {
        return jsonObject.containsKey(TITLE_ID) && jsonObject.containsKey(TITLE_VERSION)
                && jsonObject.containsKey(ISBN);
    }

    private String getTitleVersion(final JSONObject jsonObject) {
        String titleVersion = (String) jsonObject.get(TITLE_VERSION);
        return new Version(titleVersion).getVersionWithoutPrefix();
    }

    private void archiveFile(final String filename) throws IOException {
        String date = new SimpleDateFormat(ARCHIVED_FILE_DATE_PATTERN).format(new Date());
        String newFilename = nasFileSystem.getIsbnFileArchiveDir() + File.separator + LIBRARY + date + JSON;
        Files.move(Paths.get(filename), Paths.get(newFilename));
        log.info(String.format(FILE_ARCHIVED_MESSAGE, newFilename));
    }
}
