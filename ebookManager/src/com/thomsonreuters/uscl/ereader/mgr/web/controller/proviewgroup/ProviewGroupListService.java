package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;

public interface ProviewGroupListService {

    AllProviewGroupsContainer getProviewGroups(final ProviewGroupForm form,
                                               final Map<String, ProviewGroupContainer> allProviewGroups,
                                               final List<ProviewGroup> allLatestProviewGroups) throws ProviewException;
}
