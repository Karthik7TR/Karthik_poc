package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.format.service.MinorVersionMappingFileSaver;
import com.thomsonreuters.uscl.ereader.format.service.TitleXmlUnifiedConverter;
import com.thomsonreuters.uscl.ereader.format.service.TocXmlUnifiedConverter;
import com.thomsonreuters.uscl.ereader.format.domain.UnifiedTocElement;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.thomsonreuters.uscl.ereader.format.domain.UnifiedTocElement.UNIFIED_TOC_TAG_NAME;
import static java.util.Optional.ofNullable;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class BuildPreviousDocumentIdsMappingStep extends BookStepImpl {
    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;
    @Autowired
    private DocMetadataService docMetadataService;
    @Autowired
    private ProviewClient proviewClient;
    @Autowired
    private TitleXmlUnifiedConverter titleXmlUnifiedConverter;
    @Autowired
    private TocXmlUnifiedConverter tocXmlUnifiedConverter;
    @Autowired
    private MinorVersionMappingFileSaver minorVersionMappingFileSaver;

    @Override
    public ExitStatus executeStep() throws Exception {
        if (getBookDefinition().getVersionWithPreviousDocIds() != null) {
            Map<String, Set<UnifiedTocElement>> nameToTocElementsOld = getNameToTocElementsOldMap();
            Map<String, Set<UnifiedTocElement>> nameToTocElementsNew = getNameToTocElementsNewMap();
            Map<String, Set<String>> oldIdToNewIds = createOldIdsToNewIdsMap(nameToTocElementsOld, nameToTocElementsNew);

            if (!oldIdToNewIds.isEmpty()) {
                saveMapToFile(oldIdToNewIds);
                setJobExecutionProperty(JobExecutionKey.WITH_PREVIOUS_DOCUMENT_IDS, Boolean.TRUE);
            }
        }
        return ExitStatus.COMPLETED;
    }

    private Map<String, Set<UnifiedTocElement>> getNameToTocElementsOldMap() throws ProviewException {
        String oldVersionTitleXml = getOldVersionTitleXml();
        Document unifiedDoc = titleXmlUnifiedConverter.convertDocumentToUnifiedFormat(oldVersionTitleXml);
        return elementsWithTheSameNameMap(unifiedDoc);
    }

    private String getOldVersionTitleXml() throws ProviewException {
        return proviewClient.getTitleInfo(getBookDefinition().getFullyQualifiedTitleId(), getBookDefinition().getVersionWithPreviousDocIds());
    }

    private Map<String, Set<UnifiedTocElement>> getNameToTocElementsNewMap() {
        final File tocXmlFile = formatFileSystem.getTransformedToc(this);
        final Map<String, String> familyGuidMap = docMetadataService.findDistinctProViewFamGuidsByJobId(getJobInstanceId());
        Document unifiedDoc = tocXmlUnifiedConverter.convertDocumentToUnifiedFormat(tocXmlFile, familyGuidMap);
        return elementsWithTheSameNameMap(unifiedDoc);
    }

    private Map<String, Set<UnifiedTocElement>> elementsWithTheSameNameMap(final Document unifiedDoc) {
        Map<String, Set<UnifiedTocElement>> elementsWithTheSameName = new HashMap<>();
        unifiedDoc.getElementsByTag(UNIFIED_TOC_TAG_NAME).stream()
                .map(element -> (UnifiedTocElement) element)
                .forEach(element -> {
                    String text = element.getName();
                    elementsWithTheSameName.putIfAbsent(text, new TreeSet<>(Comparator.comparing(UnifiedTocElement::getUniqueKey)));
                    elementsWithTheSameName.get(text).add(element);
                });
        return elementsWithTheSameName;
    }

    private void saveMapToFile(final Map<String, Set<String>> oldIdToNewIds) {
        minorVersionMappingFileSaver.saveMapToFile(oldIdToNewIds,
                formatFileSystem.getFormatDirectory(this),
                formatFileSystem.getOldToNewDocumentIdMappingXml(this).getName());
    }

    private Map<String, Set<String>> createOldIdsToNewIdsMap(final Map<String, Set<UnifiedTocElement>> nameToTocElementsOld,
                                                             final Map<String, Set<UnifiedTocElement>> nameToTocElementsNew) {
        if (allIdsTheSame(nameToTocElementsOld, nameToTocElementsNew)) return Collections.emptyMap();
        Map<String, Set<String>> oldIdToNewIds = new HashMap<>();
        new TreeMap<>(nameToTocElementsOld).forEach((oldTocName, oldTocElements) -> {
            boolean isOldTocNameAmbiguous = isNameAmbiguous(oldTocElements);
            oldTocElements.forEach(oldTocElement -> findNewId(oldTocElement,
                    nameToTocElementsOld, nameToTocElementsNew,
                    oldIdToNewIds, isOldTocNameAmbiguous));
        });
        return removeEmptyValues(oldIdToNewIds);
    }

    private void findNewId(final UnifiedTocElement oldTocElement,
                           final Map<String, Set<UnifiedTocElement>> nameToTocElementsOld,
                           final Map<String, Set<UnifiedTocElement>> nameToTocElementsNew,
                           final Map<String, Set<String>> oldIdToNewIds,
                           final boolean isOldTocNameAmbiguous) {
        Set<UnifiedTocElement> newTocElements = nameToTocElementsNew.get(oldTocElement.getName());
        String newId;
        if (newTocElements == null) {
            newId = StringUtils.EMPTY;
        } else if (hasAmbiguity(isOldTocNameAmbiguous, newTocElements)) {
            newId = ofNullable(performDisambiguation(oldTocElement, newTocElements, nameToTocElementsOld, nameToTocElementsNew))
                    .map(UnifiedTocElement::getDocId)
                    .orElse(StringUtils.EMPTY);
        } else {
            newId = newTocElements.iterator().next().getDocId();
        }
        put(oldIdToNewIds, oldTocElement.getDocId(), newId);
    }

    private UnifiedTocElement performDisambiguation(final UnifiedTocElement oldTocElement,
                                         final Set<UnifiedTocElement> newTocElements,
                                         final Map<String, Set<UnifiedTocElement>> nameToTocElementsOld,
                                         final Map<String, Set<UnifiedTocElement>> nameToTocElementsNew) {
        UnifiedTocElement newToc = disambiguationByNeighbourElements(UnifiedTocElement::childNames, oldTocElement, newTocElements, nameToTocElementsOld, nameToTocElementsNew);
        if (newToc == null) {
            newToc = disambiguationByNeighbourElements(UnifiedTocElement::siblingNames, oldTocElement, newTocElements, nameToTocElementsOld, nameToTocElementsNew);
        }
        if (newToc == null) {
            newToc = disambiguationByNeighbourElements(UnifiedTocElement::parentNames, oldTocElement, newTocElements, nameToTocElementsOld, nameToTocElementsNew);
        }
        return newToc;
    }

    private UnifiedTocElement disambiguationByNeighbourElements(final Function<UnifiedTocElement, List<String>> neighbouring,
                                                       final UnifiedTocElement oldTextElement,
                                                       final Set<UnifiedTocElement> tocElementsNew,
                                                       final Map<String, Set<UnifiedTocElement>> nameToTocElementsOld,
                                                       final Map<String, Set<UnifiedTocElement>> nameToTocElementsNew) {
        for (String nameOld : neighbouring.apply(oldTextElement)) {
            if (isUnique(nameOld, nameToTocElementsOld)) {
                for (UnifiedTocElement tocElementNew : tocElementsNew) {
                    for (String nameNew : neighbouring.apply(tocElementNew)) {
                        if (nameOld.equals(nameNew) && isUnique(nameNew, nameToTocElementsNew)) {
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

    private boolean allIdsTheSame(final Map<String, Set<UnifiedTocElement>> nameToTocElementsOld, final Map<String, Set<UnifiedTocElement>> nameToTocElementsNew) {
        return getDocIds(nameToTocElementsOld).equals(getDocIds(nameToTocElementsNew));
    }

    private Set<String> getDocIds(final Map<String, Set<UnifiedTocElement>> nameToTocElementsOld) {
        return nameToTocElementsOld.values().stream()
                .flatMap(Collection::stream)
                .map(UnifiedTocElement::getDocId)
                .collect(Collectors.toSet());
    }

    private Map<String, Set<String>> removeEmptyValues(final Map<String, Set<String>> oldIdToNewIds) {
        oldIdToNewIds.forEach((k,v) -> removeEmptyValues(v));
        return oldIdToNewIds;
    }

    private void removeEmptyValues(final Set<String> newIds) {
        if (newIds.size() > 1) newIds.remove(StringUtils.EMPTY);
    }
}
