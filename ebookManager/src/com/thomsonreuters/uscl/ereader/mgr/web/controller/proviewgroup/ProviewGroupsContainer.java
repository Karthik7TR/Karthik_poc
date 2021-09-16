package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.Collections;
import java.util.List;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProviewGroupsContainer {
    private List<ProviewGroup> allLatestProviewGroups;
    private List<ProviewGroup> selectedProviewGroups;

    public static ProviewGroupsContainer initEmpty() {
        return ProviewGroupsContainer.builder()
            .allLatestProviewGroups(Collections.emptyList())
            .selectedProviewGroups(Collections.emptyList())
            .build();
    }
}
