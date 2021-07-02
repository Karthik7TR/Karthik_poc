package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import org.apache.commons.lang3.tuple.Pair;

public interface ProviewGroupListService {

    AllProviewGroupsContainer getProviewGroups(final ProviewGroupForm form,
                                               final Map<String, ProviewGroupContainer> allProviewGroups,
                                               final List<ProviewGroup> allLatestProviewGroups) throws ProviewException;

    Map<String, ProviewGroupContainer> getAllProviewGroups() throws ProviewException;

    Pair<List<String>, List<GroupDetails>> getGroupDetailsList(final ProviewGroupListFilterForm form,
            final Map<String, ProviewGroupContainer> allProviewGroups);
}
