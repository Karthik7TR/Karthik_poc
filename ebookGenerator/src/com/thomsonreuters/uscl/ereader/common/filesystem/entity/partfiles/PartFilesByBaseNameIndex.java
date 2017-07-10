package com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles;

import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.jetbrains.annotations.NotNull;

public class PartFilesByBaseNameIndex
{
    private Map<String, PartFilesByUuidIndex> partFilesByBaseName = new HashMap<>();

    @NotNull
    public Map<String, PartFilesByUuidIndex> getPartFilesByBaseName()
    {
        return partFilesByBaseName;
    }

    public void put(@NotNull final String baseName, @NotNull final PartType type, @NotNull final DocumentFile documentFile)
    {
        if (!partFilesByBaseName.containsKey(baseName))
        {
            partFilesByBaseName.put(baseName, new PartFilesByUuidIndex());
        }
        partFilesByBaseName.get(baseName).put(type, documentFile);
    }
}
