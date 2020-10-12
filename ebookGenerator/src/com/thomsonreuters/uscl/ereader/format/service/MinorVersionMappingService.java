package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.format.domain.MinorVersionCollections;
import com.thomsonreuters.uscl.ereader.format.domain.UnifiedTocElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

@Service
public class MinorVersionMappingService {
    public Map<String, Set<String>> createOldIdsToNewIdsMap(final MinorVersionCollections tocMaps) {
        Map<String, Set<String>> oldIdToNewIds = new HashMap<>();
        if (tocMaps.notAllIdsTheSame()) {
            new TreeMap<>(tocMaps.getNameToTocElementsOld()).forEach((oldTocName, oldTocElements) -> {
                boolean isOldTocNameAmbiguous = isNameAmbiguous(oldTocElements);
                oldTocElements.stream()
                        .filter(tocMaps::newNotContains)
                        .forEach(oldTocElement -> findNewId(oldTocElement, tocMaps, oldIdToNewIds, isOldTocNameAmbiguous));
            });
        }
        return removeEmptyValues(oldIdToNewIds);
    }

    private void findNewId(final UnifiedTocElement oldTocElement,
                           final MinorVersionCollections tocMaps,
                           final Map<String, Set<String>> oldIdToNewIds,
                           final boolean isOldTocNameAmbiguous) {
        Set<UnifiedTocElement> newTocElements = tocMaps.getNameToTocElementsNew().get(oldTocElement.getName());
        String newId;
        if (newTocElements == null) {
            newId = StringUtils.EMPTY;
        } else if (hasAmbiguity(isOldTocNameAmbiguous, newTocElements)) {
            newId = ofNullable(performDisambiguation(oldTocElement, newTocElements, tocMaps))
                    .map(UnifiedTocElement::getDocId)
                    .orElse(StringUtils.EMPTY);
        } else {
            newId = newTocElements.iterator().next().getDocId();
        }
        put(oldIdToNewIds, oldTocElement.getDocId(), newId);
    }

    private UnifiedTocElement performDisambiguation(final UnifiedTocElement oldTocElement,
                                                    final Set<UnifiedTocElement> newTocElements,
                                                    final MinorVersionCollections tocMaps) {
        UnifiedTocElement newToc = disambiguationByNeighbourElements(UnifiedTocElement::childNames, oldTocElement, newTocElements, tocMaps);
        if (newToc == null) {
            newToc = disambiguationByNeighbourElements(UnifiedTocElement::siblingNames, oldTocElement, newTocElements, tocMaps);
        }
        if (newToc == null) {
            newToc = disambiguationByNeighbourElements(UnifiedTocElement::parentNames, oldTocElement, newTocElements, tocMaps);
        }
        return newToc;
    }

    private UnifiedTocElement disambiguationByNeighbourElements(final Function<UnifiedTocElement, List<String>> neighbouring,
                                                                final UnifiedTocElement oldTextElement,
                                                                final Set<UnifiedTocElement> tocElementsNew,
                                                                final MinorVersionCollections tocMaps) {
        for (String nameOld : neighbouring.apply(oldTextElement)) {
            if (isUnique(nameOld, tocMaps.getNameToTocElementsOld())) {
                for (UnifiedTocElement tocElementNew : tocElementsNew) {
                    for (String nameNew : neighbouring.apply(tocElementNew)) {
                        if (nameOld.equals(nameNew) && isUnique(nameNew, tocMaps.getNameToTocElementsNew()) && tocMaps.oldNotContains(tocElementNew)) {
                            return tocElementNew;
                        }
                    }
                }
            }
        }
        return null;
    }

    private boolean hasAmbiguity(final boolean isOldTocNameAmbiguous, final Set<UnifiedTocElement> newTocElements) {
        return isNameAmbiguous(newTocElements) || isOldTocNameAmbiguous;
    }

    private boolean isNameAmbiguous(final Set<UnifiedTocElement> nameElements) {
        return nameElements.size() > 1;
    }

    private void put(final Map<String, Set<String>> oldIdToNewIds, final String oldId, final String newId) {
        oldIdToNewIds.computeIfAbsent(oldId, k -> new TreeSet<>()).add(newId);
    }

    private boolean isUnique(final String text, final Map<String, Set<UnifiedTocElement>> textToDocumentElements) {
        return textToDocumentElements.get(text).size() == 1;
    }

    private Map<String, Set<String>> removeEmptyValues(final Map<String, Set<String>> oldIdToNewIds) {
        oldIdToNewIds.forEach((k,v) -> removeEmptyValues(v));
        return oldIdToNewIds;
    }

    private void removeEmptyValues(final Set<String> newIds) {
        if (newIds.size() > 1) newIds.remove(StringUtils.EMPTY);
    }
}
