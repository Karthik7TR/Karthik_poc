package com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.jetbrains.annotations.NotNull;

/**
 * Keeps map materialNumber to files and methods to operate with it.
 */
public class BaseFilesIndex {
    private Map<String, BaseFilesByBaseNameIndex> filesByMaterialNumber = new HashMap<>();

    @NotNull
    public Set<Map.Entry<String, BaseFilesByBaseNameIndex>> getFilesByMaterialNumber() {
        return filesByMaterialNumber.entrySet();
    }

    public void put(
        @NotNull final String materialNumber,
        @NotNull final String baseName,
        @NotNull final PartType type,
        @NotNull final File file) {
        if (!filesByMaterialNumber.containsKey(materialNumber)) {
            filesByMaterialNumber.put(materialNumber, new BaseFilesByBaseNameIndex());
        }
        filesByMaterialNumber.get(materialNumber).put(baseName, type, file);
    }

    @NotNull
    public File get(
        @NotNull final String materialNumber,
        @NotNull final String baseName,
        @NotNull final PartType type) {
        return filesByMaterialNumber.get(materialNumber).getFilesByBaseName(baseName).get(type);
    }
}
