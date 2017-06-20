package com.thomsonreuters.uscl.ereader.common.filesystem.entity;

import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.jetbrains.annotations.NotNull;

public class PartFilesByOrderIndex
{
    private Map<Integer, PartFilesByTypeIndex> partFilesByOrder = new HashMap<>();

    @NotNull
    public Map<Integer, PartFilesByTypeIndex> getPartFilesByOrder()
    {
        return partFilesByOrder;
    }

    public void put(@NotNull final Integer order, @NotNull final PartType type, @NotNull final DocumentFile documentFile)
    {
        if (!partFilesByOrder.containsKey(order))
        {
            partFilesByOrder.put(order, new PartFilesByTypeIndex());
        }
        partFilesByOrder.get(order).put(type, documentFile);
    }
}
