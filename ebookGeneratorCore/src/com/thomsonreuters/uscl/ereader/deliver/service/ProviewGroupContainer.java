package com.thomsonreuters.uscl.ereader.deliver.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * handle a list of ProviewGroups
 *
 * @author uc209819
 *
 */
public class ProviewGroupContainer implements Serializable {
    private static final long serialVersionUID = -1985883914988566602L;
    private static final String PROVIEW_STATUS_FINAL = "final";

    private List<ProviewGroup> proviewGroups = new CopyOnWriteArrayList<>();

    public List<ProviewGroup> getProviewGroups() {
        return proviewGroups;
    }

    public void setProviewGroups(final List<ProviewGroup> proviewGroups) {
        this.proviewGroups = new CopyOnWriteArrayList<>(proviewGroups);
    }

    public String getGroupId() {
        return proviewGroups.get(0).getGroupId();
    }

    /**
     *
     * @return the latest version of the group
     */
    public ProviewGroup getLatestVersion() {
        Integer latestVersion = 0;
        ProviewGroup latestProviewGroup = null;

        for (final ProviewGroup proviewGroup : proviewGroups) {
            final String currentVersion = proviewGroup.getGroupVersion().substring(1);
            Integer intVersion = 0;

            intVersion = Integer.parseInt(currentVersion);

            if (intVersion > latestVersion) {
                latestProviewGroup = proviewGroup;
                latestVersion = intVersion;
            }
        }
        return latestProviewGroup;
    }

    public List<ProviewGroup> getAllMajorVersions() {
        final Map<Integer, ProviewGroup> map = new HashMap<>();

        for (final ProviewGroup proviewGroup : proviewGroups) {
            final Integer key = 0;
            if (!map.containsKey(key)) {
                map.put(key, proviewGroup);
            } else {
                final ProviewGroup previousTitleInfo = map.get(key);
                if (proviewGroup.getVersion() > previousTitleInfo.getVersion()) {
                    map.put(key, proviewGroup);
                }
            }
        }

        final List<ProviewGroup> list = new ArrayList<>(map.values());
        Collections.sort(list);

        return list;
    }

    /**
     * Determine if this group has been published to public
     *
     * @return boolean
     */
    public boolean hasBeenPublished() {
        boolean isPublished = false;

        for (final ProviewGroup proviewGroup : proviewGroups) {
            if (proviewGroup.getGroupStatus().equalsIgnoreCase(PROVIEW_STATUS_FINAL)) {
                isPublished = true;
                break;
            }
        }
        return isPublished;
    }

    public ProviewGroup getGroupByVersion(final String version) {
        for (final ProviewGroup group : proviewGroups) {
            if (version.equals(group.getVersion().toString())) {
                return group;
            }
        }
        return null;
    }
}
