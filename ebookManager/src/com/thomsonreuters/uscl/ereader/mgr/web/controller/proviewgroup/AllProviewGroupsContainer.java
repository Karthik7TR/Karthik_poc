package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AllProviewGroupsContainer {
    private Map<String, ProviewGroupContainer> allProviewGroups;
    private List<ProviewGroup> allLatestProviewGroups;
    private List<ProviewGroup> selectedProviewGroups;

    public static AllProviewGroupsContainer initEmpty() {
        return AllProviewGroupsContainer.builder()
            .allProviewGroups(Collections.emptyMap())
            .allLatestProviewGroups(Collections.emptyList())
            .selectedProviewGroups(Collections.emptyList())
            .build();
    }
}
