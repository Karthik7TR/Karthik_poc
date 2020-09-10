package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;

public interface VersionIsbnService {

    void saveIsbn(String titleId, String version, String isbn);

    void saveIsbn(BookDefinition bookDefinition, String version, String isbn);

    boolean isIsbnExists(String isbn);

    boolean isIsbnChangedFromPreviousGeneration(BookDefinition bookDefinition, String currentProviewVersion);

    String getLastIsbnBeforeVersion(String titleId, Version maxVersion);

    void deleteIsbn(String titleId, String version);

    void modifyIsbn(String titleId, String isbn);

    void resetIsbn(String titleId, String isbn);
}
