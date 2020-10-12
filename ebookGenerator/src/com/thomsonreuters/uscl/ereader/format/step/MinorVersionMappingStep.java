package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.filesystem.AssembleFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.format.domain.MinorVersionCollections;
import com.thomsonreuters.uscl.ereader.format.service.MinorVersionMappingFileSaver;
import com.thomsonreuters.uscl.ereader.format.domain.UnifiedTocElement;
import com.thomsonreuters.uscl.ereader.format.service.MinorVersionMappingService;
import com.thomsonreuters.uscl.ereader.format.service.TitleXmlUnifiedConverter;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.*;

import static com.thomsonreuters.uscl.ereader.format.domain.UnifiedTocElement.UNIFIED_TOC_TAG_NAME;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class MinorVersionMappingStep extends BookStepImpl {
    private static final String NONE = "none";
    @Autowired
    private AssembleFileSystem assembleFileSystem;
    @Autowired
    private ProviewClient proviewClient;
    @Autowired
    private TitleXmlUnifiedConverter titleXmlUnifiedConverter;
    @Autowired
    private MinorVersionMappingService minorVersionMappingService;
    @Autowired
    private MinorVersionMappingFileSaver minorVersionMappingFileSaver;

    @Override
    public ExitStatus executeStep() throws Exception {
        if (getBookDefinition().getVersionWithPreviousDocIds() != null) {
            MinorVersionCollections unifiedTocMaps = buildCollections();
            Map<String, Set<String>> oldIdToNewIds = minorVersionMappingService.createOldIdsToNewIdsMap(unifiedTocMaps);

            if (oldIdToNewIds.isEmpty()) {
                oldIdToNewIds.put(NONE, Collections.singleton(StringUtils.EMPTY));
            }
            saveMapToFile(oldIdToNewIds);
        }
        return ExitStatus.COMPLETED;
    }

    public MinorVersionCollections buildCollections() {
        Map<String, Set<UnifiedTocElement>> nameToTocElementsOld = getNameToTocElementsOldMap();
        Map<String, Set<UnifiedTocElement>> nameToTocElementsNew = getNameToTocElementsNewMap();
        return new MinorVersionCollections(nameToTocElementsOld, nameToTocElementsNew);
    }

    private Map<String, Set<UnifiedTocElement>> getNameToTocElementsOldMap() {
        String oldVersionTitleXml = getOldVersionTitleXml();
        Document unifiedDoc = titleXmlUnifiedConverter.convertDocumentToUnifiedFormat(oldVersionTitleXml);
        return elementsWithTheSameNameMap(unifiedDoc);
    }

    private Map<String, Set<UnifiedTocElement>> getNameToTocElementsNewMap() {
        File newVersionTitleXml = assembleFileSystem.getTitleXml(this);
        Document unifiedDoc = titleXmlUnifiedConverter.convertDocumentToUnifiedFormat(newVersionTitleXml);
        return elementsWithTheSameNameMap(unifiedDoc);
    }

    private String getOldVersionTitleXml() {
        try {
            return proviewClient.getTitleInfo(getBookDefinition().getFullyQualifiedTitleId(), getBookDefinition().getVersionWithPreviousDocIds());
        } catch (ProviewException e) {
            throw new EBookException(e);
        }
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
        minorVersionMappingFileSaver.saveMapToFile(oldIdToNewIds, assembleFileSystem.getMinorVersionMappingXml(this));
    }
}
