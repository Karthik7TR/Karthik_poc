package com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles;

import java.util.EnumMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.jetbrains.annotations.NotNull;

public class PartFilesByTypeIndex {
    private Map<PartType, DocumentFile> partFilesByType = new EnumMap<>(PartType.class);

    @NotNull
    public Map<PartType, DocumentFile> getPartFilesByType() {
        return partFilesByType;
    }

    public void put(@NotNull final PartType type, @NotNull final DocumentFile documentFile) {
        partFilesByType.put(type, documentFile);
    }
}
