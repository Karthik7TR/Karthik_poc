package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllProviewGroupsProvider {
    private volatile Map<String, ProviewGroupContainer> allProviewGroups;
    private final ProviewHandler proviewHandler;

    @Autowired
    private AllProviewGroupsProvider(final ProviewHandler proviewHandler) {
        this.proviewHandler = proviewHandler;
    }

    public Map<String, ProviewGroupContainer> getAllProviewGroups(final boolean isRefresh) throws ProviewException {
        if (allProviewGroups == null || isRefresh) {
            synchronized (AllProviewGroupsProvider.class) {
                if (allProviewGroups == null || isRefresh) {
                    allProviewGroups = new ConcurrentHashMap<>(proviewHandler.getAllProviewGroupInfo());
                }
            }
        }
        return allProviewGroups;
    }

    public void updateGroupStatus(final String groupId, final String groupVersion, final String newStatus)
            throws ProviewException {
        final Map<String, ProviewGroupContainer> proviewGroups = getAllProviewGroups(false);
        proviewGroups.computeIfPresent(groupId, (key, value) -> {
            value.getProviewGroups().stream()
                    .filter(item -> item.getVersion().toString().equals(groupVersion))
                    .forEach(item -> item.setGroupStatus(newStatus));
            return value;
        });
    }
}
