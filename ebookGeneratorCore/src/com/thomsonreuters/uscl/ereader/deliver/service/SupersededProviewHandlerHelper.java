package com.thomsonreuters.uscl.ereader.deliver.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SupersededProviewHandlerHelper {
    private static final String FINAL_STATUS = "Final";

    @Autowired
    private ProviewClient proviewClient;

    public void markTitleSuperseded(final String fullyQualifiedTitleId, final Map<String, ProviewTitleContainer> allPublishedTitles) {
        final List<ProviewTitleContainer> splitBookTitles = groupTitlesBySplitBooks(allPublishedTitles).get(new TitleId(fullyQualifiedTitleId).getHeadTitleId());
        final List<Integer> lowerMajorVersions = getLowerFinalMajorVersions(splitBookTitles);
        lowerMajorVersions.forEach(majorVersion -> markTitleVersionAsSuperseded(splitBookTitles, majorVersion));
        log.debug("Successfully marked as Superseded old major versions {} of {}", lowerMajorVersions, fullyQualifiedTitleId);
    }

    public void markTitleVersionAsSupersededInThread(final String fullyQualifiedTitleId, final Version version, final Map<String, ProviewTitleContainer> allPublishedTitles) {
        Executors.newSingleThreadExecutor().execute(() -> markTitleVersionAsSuperseded(fullyQualifiedTitleId, version, allPublishedTitles));
    }

    //package view is for testing
    void markTitleVersionAsSuperseded(final String fullyQualifiedTitleId, final Version version, final Map<String, ProviewTitleContainer> allPublishedTitles) {
        final List<ProviewTitleContainer> splitBookTitles = groupTitlesBySplitBooks(allPublishedTitles).get(new TitleId(fullyQualifiedTitleId).getHeadTitleId());
        markTitleVersionAsSuperseded(splitBookTitles, version.getMajorNumber());
    }

    private void markTitleVersionAsSuperseded(final List<ProviewTitleContainer> splitBookTitles, final int majorVersion) {
        final int maxFinalMajorVersion = splitBookTitles.stream().flatMap(titleContainer -> titleContainer.getProviewTitleInfos().stream())
            .filter(info -> FINAL_STATUS.equalsIgnoreCase(info.getStatus()))
            .map(ProviewTitleInfo::getMajorVersion)
            .distinct()
            .max(Comparator.naturalOrder()).orElse(0);

        splitBookTitles.forEach(titleContainer -> markPreviousFinalVersionAsSuperseded(titleContainer, majorVersion, maxFinalMajorVersion));
    }

    private Map<String, List<ProviewTitleContainer>> groupTitlesBySplitBooks(final Map<String, ProviewTitleContainer> allPublishedTitles) {
        return allPublishedTitles.values().stream()
            .collect(Collectors.groupingBy(titleContainer ->
                new TitleId(titleContainer.getLatestVersion().getTitleId()).getHeadTitleId()
            ));
    }

    private void markPreviousFinalVersionAsSuperseded(final ProviewTitleContainer titleContainer, final int newFinalMajorVersion, final int maxFinalMajorVersion) {
        final int majorVersionToMarkSuperseded = getFinalMajorVersionToMarkSuperseded(titleContainer, newFinalMajorVersion, maxFinalMajorVersion);

        titleContainer.getProviewTitleInfos().stream()
            .filter(info -> info.getMajorVersion() == majorVersionToMarkSuperseded)
            .max(Comparator.comparingInt(ProviewTitleInfo::getMinorVersion))
            .ifPresent(info -> markVersionAsSuperseededAndPromoteToFinal(info.getTitleId(), info.getMajorVersion(), info.getMinorVersion() + 1));
    }

    private int getFinalMajorVersionToMarkSuperseded(final ProviewTitleContainer titleContainer, final int newFinalMajorVersion, final int maxFinalMajorVersion) {
        return titleContainer.getProviewTitleInfos().stream()
            .filter(info -> FINAL_STATUS.equalsIgnoreCase(info.getStatus()))
            .map(ProviewTitleInfo::getMajorVersion)
            .filter(majorVersion -> majorVersion < maxFinalMajorVersion)
            .filter(majorVersion -> majorVersion <= newFinalMajorVersion)
            .max(Comparator.naturalOrder())
            .orElse(0);
    }

    private List<Integer> getLowerFinalMajorVersions(final List<ProviewTitleContainer> splitBookTitles) {
        final List<Integer> allMajorVersions = splitBookTitles.stream().flatMap(title -> title.getFinalMajorVersions().stream())
            .distinct().sorted().collect(Collectors.toList());
        return allMajorVersions.subList(0, Math.max(allMajorVersions.size() - 1, 0));
    }

    @SneakyThrows
    private void markVersionAsSuperseededAndPromoteToFinal(final String fullyQualifiedTitleId, final Integer majorVersion, final Integer minorVersion) {
        final String version = new Version(majorVersion, minorVersion).getFullVersion();
        try {
            final HttpStatus status = proviewClient.changeTitleVersionToSuperseded(fullyQualifiedTitleId, version);
            if (HttpStatus.OK.equals(status)) {
                proviewClient.promoteTitle(fullyQualifiedTitleId, version);
            }
        } catch (final ProviewRuntimeException e) {
            log.warn("{}/{} {}", fullyQualifiedTitleId, version, e.getMessage());
        }
    }
}
