package com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles;

import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.jetbrains.annotations.NotNull;

public class PartFilesIndex {
    private Map<String, PartFilesByBaseNameIndex> partFilesByMaterialNumber = new HashMap<>();

    @NotNull
    public Map<String, PartFilesByBaseNameIndex> getPartFilesByMaterialNumber() {
        return partFilesByMaterialNumber;
    }

    public void put(
        @NotNull final String materialNumber,
        @NotNull final String baseName,
        @NotNull final PartType type,
        @NotNull final DocumentFile documentFile) {
        if (!partFilesByMaterialNumber.containsKey(materialNumber)) {
            partFilesByMaterialNumber.put(materialNumber, new PartFilesByBaseNameIndex());
        }
        partFilesByMaterialNumber.get(materialNumber).put(baseName, type, documentFile);
    }

    @NotNull
    public DocumentFile get(
        @NotNull final String materialNumber,
        @NotNull final String baseName,
        @NotNull final String uuid,
        @NotNull final PartType type) {
        return partFilesByMaterialNumber.get(materialNumber)
            .getPartFilesByBaseName()
            .get(baseName)
            .getPartFilesByUuid()
            .get(uuid)
            .getPartFilesByType()
            .get(type);
    }
}
