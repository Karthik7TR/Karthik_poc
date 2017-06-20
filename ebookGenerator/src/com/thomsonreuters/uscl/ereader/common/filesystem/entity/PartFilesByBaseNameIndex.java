package com.thomsonreuters.uscl.ereader.common.filesystem.entity;

import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.jetbrains.annotations.NotNull;

public class PartFilesByBaseNameIndex
{
    private Map<String, PartFilesByOrderIndex> partFilesByBaseName = new HashMap<>();

    @NotNull
    public Map<String, PartFilesByOrderIndex> getPartFilesByBaseName()
    {
        return partFilesByBaseName;
    }

    public void put(@NotNull final String baseName, @NotNull final Integer order, @NotNull final PartType type, @NotNull final DocumentFile documentFile)
    {
        if (!partFilesByBaseName.containsKey(baseName))
        {
            partFilesByBaseName.put(baseName, new PartFilesByOrderIndex());
        }
        partFilesByBaseName.get(baseName).put(order, type, documentFile);
    }
}
