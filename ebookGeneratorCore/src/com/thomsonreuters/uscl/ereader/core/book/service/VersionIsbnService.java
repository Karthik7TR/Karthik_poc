package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public interface VersionIsbnService {

    void saveIsbn(BookDefinition bookDefinition, String version, String isbn);

    boolean isIsbnExists(String isbn);

    void deleteIsbn(String titleId, String version);

    void modifyIsbn(String titleId, String isbn);

    void resetIsbn(String titleId, String isbn);
}
