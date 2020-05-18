package com.thomsonreuters.uscl.ereader.deliver.service;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProviewTitleContainer implements Serializable {
    private static final long serialVersionUID = -1985883914988566602L;
    private static final String PROVIEW_STATUS_FINAL = "final";
    private List<ProviewTitleInfo> proviewTitleInfos = new ArrayList<>();

    public ProviewTitleInfo getLatestVersion() {
        BigInteger latestIntMajorPart = BigInteger.ZERO;
        BigInteger latestIntMinorPart = BigInteger.ZERO;
        ProviewTitleInfo latestProviewTitleInfo = null;

        for (final ProviewTitleInfo proviewTitleInfo : proviewTitleInfos) {
            final String currentVersion = new Version(proviewTitleInfo.getVersion())
                    .getFullVersion()
                    .substring(1);
            final String majorPart;
            final String minorPart;
            BigInteger intMajorPart;
            BigInteger intMinorPart = BigInteger.ZERO;

            if (currentVersion.contains(".")) {
                majorPart = currentVersion.substring(0, currentVersion.indexOf('.'));
                minorPart = currentVersion.substring(currentVersion.indexOf('.') + 1);

                intMajorPart = new BigInteger(majorPart);
                intMinorPart = new BigInteger(minorPart);
            } else {
                majorPart = currentVersion;
                intMajorPart = new BigInteger(majorPart);
            }

            final boolean nextMajorVersion = intMajorPart.compareTo(latestIntMajorPart) > 0;
            final boolean nextMinorVersion = intMajorPart.equals(latestIntMajorPart)
                    && latestIntMinorPart.compareTo(intMinorPart) < 0;
            if (nextMajorVersion || nextMinorVersion) {
                latestProviewTitleInfo = proviewTitleInfo;
                latestIntMajorPart = intMajorPart;
                latestIntMinorPart = intMinorPart;
            }
        }
        return latestProviewTitleInfo;
    }

    public List<ProviewTitleInfo> getAllMajorVersions() {
        final Map<BigInteger, ProviewTitleInfo> map = new HashMap<>();

        for (final ProviewTitleInfo proviewTitleInfo : proviewTitleInfos) {
            final BigInteger majorVersion = proviewTitleInfo.getMajorVersion();
            final BigInteger minorVersion = proviewTitleInfo.getMinorVersion();
            if (minorVersion != null) {
                // Using Proview new versioning system with major/minor
                if (!map.containsKey(majorVersion)) {
                    map.put(majorVersion, proviewTitleInfo);
                } else {
                    final ProviewTitleInfo currentInfo = map.get(majorVersion);
                    final BigInteger currentMinorVersion = currentInfo.getMinorVersion();
                    if (minorVersion.compareTo(currentMinorVersion) > 0) {
                        map.put(majorVersion, proviewTitleInfo);
                    }
                }
            } else {
                // Using Proview old versioning system.  Only has major version.
                final BigInteger key = BigInteger.ZERO;
                if (!map.containsKey(key)) {
                    map.put(key, proviewTitleInfo);
                } else {
                    final ProviewTitleInfo previousTitleInfo = map.get(key);
                    if (proviewTitleInfo.getMajorVersion().compareTo(previousTitleInfo.getMajorVersion()) > 0) {
                        map.put(key, proviewTitleInfo);
                    }
                }
            }
        }
        final List<ProviewTitleInfo> list = new ArrayList<>(map.values());
        Collections.sort(list);

        return list;
    }

    public List<BigInteger> getFinalMajorVersions() {
        return proviewTitleInfos.stream()
            .filter(titleInfo -> PROVIEW_STATUS_FINAL.equalsIgnoreCase(titleInfo.getStatus()))
            .map(ProviewTitleInfo::getMajorVersion)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * Determine if this title has been published to public
     *
     * @return boolean
     */
    public boolean hasBeenPublished() {
        boolean isPublished = false;

        for (final ProviewTitleInfo proviewTitleInfo : proviewTitleInfos) {
            if (proviewTitleInfo.getStatus().equalsIgnoreCase(PROVIEW_STATUS_FINAL)) {
                isPublished = true;
                break;
            }
        }
        return isPublished;
    }
}
