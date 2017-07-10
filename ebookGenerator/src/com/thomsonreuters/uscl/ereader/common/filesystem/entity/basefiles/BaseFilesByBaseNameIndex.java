package com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.jetbrains.annotations.NotNull;

public class BaseFilesByBaseNameIndex
{
    private Map<String, BaseFilesByTypeIndex> filesByBaseName = new HashMap<>();

    @NotNull
    public Set<Map.Entry<String, BaseFilesByTypeIndex>> filesByBaseName()
    {
        return filesByBaseName.entrySet();
    }

    @NotNull
    public BaseFilesByTypeIndex getFilesByBaseName(@NotNull final String baseName)
    {
        return filesByBaseName.get(baseName);
    }

    public void put(@NotNull final String baseName, @NotNull final PartType type, @NotNull final File file)
    {
        if (!filesByBaseName.containsKey(baseName))
        {
            filesByBaseName.put(baseName, new BaseFilesByTypeIndex());
        }
        filesByBaseName.get(baseName).put(type, file);
    }
}
