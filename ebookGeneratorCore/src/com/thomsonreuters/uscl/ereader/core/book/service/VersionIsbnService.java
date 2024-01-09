package com.thomsonreuters.uscl.ereader.core.book.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.VersionIsbn;

import java.util.List;

public interface VersionIsbnService {

    String getIsbnOfTitleVersion(String titleId, String version);

    void saveIsbn(String titleId, String version, String isbn);

    void saveIsbn(BookDefinition bookDefinition, String version, String isbn);

    boolean isIsbnExists(String isbn);

    boolean isIsbnChangedFromPreviousGeneration(BookDefinition bookDefinition, String currentProviewVersion);

    void deleteIsbn(String titleId, String version);

    void modifyIsbn(String titleId, String isbn);

    void resetIsbn(String titleId, String isbn);

    String getMaterialIdOfTitleVersion(final String titleId, final String version);

    void saveMaterialId(String titleId, String version, String materialId);

    List<VersionIsbn> getAllVersionIsbnEbookDefinition();
}
