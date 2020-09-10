package com.thomsonreuters.uscl.ereader.deliver.service;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.service.VersionIsbnService;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewRuntimeException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SupersededProviewHandlerHelper {
    private static final String FINAL_STATUS = "Final";

    @Autowired
    private VersionIsbnService versionIsbnService;
    @Autowired
    private ProviewClient proviewClient;

    public void markTitleSuperseded(final String fullyQualifiedTitleId, final Map<String, ProviewTitleContainer> allPublishedTitles) {
        final List<ProviewTitleContainer> splitBookTitles = groupTitlesBySplitBooks(allPublishedTitles).get(new TitleId(fullyQualifiedTitleId).getHeadTitleId());
        final List<BigInteger> lowerMajorVersions = getLowerFinalMajorVersions(splitBookTitles);
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

    private void markTitleVersionAsSuperseded(final List<ProviewTitleContainer> splitBookTitles, final BigInteger majorVersion) {
        final BigInteger maxFinalMajorVersion = splitBookTitles.stream()
            .flatMap(titleContainer -> titleContainer.getProviewTitleInfos().stream())
            .filter(info -> FINAL_STATUS.equalsIgnoreCase(info.getStatus()))
            .map(ProviewTitleInfo::getMajorVersion)
            .distinct()
            .max(Comparator.naturalOrder()).orElse(BigInteger.ZERO);

        splitBookTitles.forEach(titleContainer -> markPreviousFinalVersionAsSuperseded(titleContainer, majorVersion, maxFinalMajorVersion));
    }

    private Map<String, List<ProviewTitleContainer>> groupTitlesBySplitBooks(final Map<String, ProviewTitleContainer> allPublishedTitles) {
        return allPublishedTitles.values().stream()
            .collect(Collectors.groupingBy(titleContainer ->
                new TitleId(titleContainer.getLatestVersion().getTitleId()).getHeadTitleId()
            ));
    }

    private void markPreviousFinalVersionAsSuperseded(final ProviewTitleContainer titleContainer,
        final BigInteger newFinalMajorVersion, final BigInteger maxFinalMajorVersion) {
        final BigInteger majorVersionToMarkSuperseded = getFinalMajorVersionToMarkSuperseded(titleContainer, newFinalMajorVersion, maxFinalMajorVersion);

        titleContainer.getProviewTitleInfos().stream()
            .filter(info -> info.getMajorVersion().equals(majorVersionToMarkSuperseded))
            .max(Comparator.comparing(ProviewTitleInfo::getMinorVersion))
            .ifPresent(info -> markVersionAsSuperseededAndPromoteToFinal(info.getTitleId(),
                    info.getMajorVersion(), info.getMinorVersion().add(BigInteger.ONE)));
    }

    private BigInteger getFinalMajorVersionToMarkSuperseded(final ProviewTitleContainer titleContainer,
        final BigInteger newFinalMajorVersion, final BigInteger maxFinalMajorVersion) {
        return titleContainer.getProviewTitleInfos().stream()
            .filter(info -> FINAL_STATUS.equalsIgnoreCase(info.getStatus()))
            .map(ProviewTitleInfo::getMajorVersion)
            .filter(majorVersion -> majorVersion.compareTo(maxFinalMajorVersion) < 0)
            .filter(majorVersion -> majorVersion.compareTo(newFinalMajorVersion) <= 0)
            .max(Comparator.naturalOrder())
            .orElse(BigInteger.ZERO);
    }

    private List<BigInteger> getLowerFinalMajorVersions(final List<ProviewTitleContainer> splitBookTitles) {
        final List<BigInteger> allMajorVersions = splitBookTitles.stream().flatMap(title -> title.getFinalMajorVersions().stream())
            .distinct().sorted().collect(Collectors.toList());
        return allMajorVersions.subList(0, Math.max(allMajorVersions.size() - 1, 0));
    }

    @SneakyThrows
    private void markVersionAsSuperseededAndPromoteToFinal(final String fullyQualifiedTitleId,
        final BigInteger majorVersion, final BigInteger minorVersion) {
        final String version = new Version(majorVersion, minorVersion).getFullVersion();
        try {
            HttpStatus status = proviewClient.changeTitleVersionToSuperseded(fullyQualifiedTitleId, version);
            if (HttpStatus.OK.equals(status)) {
                status = proviewClient.promoteTitle(fullyQualifiedTitleId, version);
                if (HttpStatus.OK.equals(status)) {
                    saveIsbnOfSupersededVersion(fullyQualifiedTitleId, new Version(version));
                }
            }
        } catch (final ProviewRuntimeException e) {
            log.warn("{}/{} {}", fullyQualifiedTitleId, version, e.getMessage());
        }
    }

    private void saveIsbnOfSupersededVersion(final String fullyQualifiedTitleId, final Version version) {
        String isbn = versionIsbnService.getLastIsbnBeforeVersion(fullyQualifiedTitleId, version);
        if (StringUtils.isNotEmpty(isbn)) {
            versionIsbnService.saveIsbn(fullyQualifiedTitleId, version.getVersionWithoutPrefix(), isbn);
        }
    }
}
