package com.thomsonreuters.uscl.ereader.format.domain;

import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class MinorVersionCollections {
    private final Map<String, Set<UnifiedTocElement>> nameToTocElementsOld;
    private final Map<String, Set<UnifiedTocElement>> nameToTocElementsNew;
    private final Set<String> oldDocIds;
    private final Set<String> newDocIds;

    public MinorVersionCollections(final Map<String, Set<UnifiedTocElement>> nameToTocElementsOld,
                                   final Map<String, Set<UnifiedTocElement>> nameToTocElementsNew) {
        this.nameToTocElementsOld = nameToTocElementsOld;
        this.nameToTocElementsNew = nameToTocElementsNew;
        oldDocIds = getDocIds(nameToTocElementsOld);
        newDocIds = getDocIds(nameToTocElementsNew);
    }

    public boolean notAllIdsTheSame() {
        return !oldDocIds.equals(newDocIds);
    }

    public boolean newNotContains(final UnifiedTocElement tocElementOld) {
        return !newDocIds.contains(tocElementOld.getDocId());
    }

    public boolean oldNotContains(final UnifiedTocElement tocElementNew) {
        return !oldDocIds.contains(tocElementNew.getDocId());
    }

    private Set<String> getDocIds(final Map<String, Set<UnifiedTocElement>> nameToTocElementsOld) {
        return nameToTocElementsOld.values().stream()
                .flatMap(Collection::stream)
                .map(UnifiedTocElement::getDocId)
                .collect(Collectors.toSet());
    }
}
