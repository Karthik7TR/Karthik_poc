package com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles;

import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import org.jetbrains.annotations.NotNull;

public class PartFilesByUuidIndex {
    private Map<String, PartFilesByTypeIndex> partFilesByUuid = new HashMap<>();

    @NotNull
    public Map<String, PartFilesByTypeIndex> getPartFilesByUuid() {
        return partFilesByUuid;
    }

    public void put(@NotNull final PartType type, @NotNull final DocumentFile documentFile) {
        final String uuid = documentFile.getDocumentName().getDocFamilyUuid();
        if (!partFilesByUuid.containsKey(uuid)) {
            partFilesByUuid.put(uuid, new PartFilesByTypeIndex());
        }
        partFilesByUuid.get(uuid).put(type, documentFile);
    }
}
