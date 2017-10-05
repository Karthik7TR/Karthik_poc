package com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.jetbrains.annotations.NotNull;

public class BaseFilesByTypeIndex {
    private Map<PartType, File> filesByType = new EnumMap<>(PartType.class);

    @NotNull
    public File get(@NotNull final PartType type) {
        return filesByType.get(type);
    }

    @NotNull
    public Map<PartType, File> getFilesByType() {
        return filesByType;
    }

    public void put(@NotNull final PartType type, @NotNull final File file) {
        filesByType.put(type, file);
    }
}
