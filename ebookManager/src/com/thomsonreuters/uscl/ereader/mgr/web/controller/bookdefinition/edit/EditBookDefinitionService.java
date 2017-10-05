package com.thomsonreuters.uscl.ereader.mgr.web.controller.bookdefinition.edit;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.sap.component.MaterialComponentsResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EditBookDefinitionService {
    List<DocumentTypeCode> getDocumentTypes();

    Map<String, String> getStates();

    Map<String, String> getJurisdictions();

    List<String> getFrontMatterThemes();

    Map<String, String> getPubTypes();

    Map<String, String> getPublishers();

    List<KeywordTypeCode> getKeywordCodes();

    DocumentTypeCode getContentTypeById(Long id);

    List<String> getCodesWorkbenchDirectory(String folder);

    @NotNull
    MaterialComponentsResponse getMaterialBySubNumber(@NotNull String subNumber, @Nullable String titleId);
}
