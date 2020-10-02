package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public interface VersionIsbnService {

    String getIsbnOfTitleVersion(String titleId, String version);

    void saveIsbn(String titleId, String version, String isbn);

    void saveIsbn(BookDefinition bookDefinition, String version, String isbn);

    boolean isIsbnExists(String isbn);

    boolean isIsbnChangedFromPreviousGeneration(BookDefinition bookDefinition, String currentProviewVersion);

    void deleteIsbn(String titleId, String version);

    void modifyIsbn(String titleId, String isbn);

    void resetIsbn(String titleId, String isbn);
}
