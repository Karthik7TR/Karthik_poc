package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup.GroupDetails;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroupContainer;
import org.apache.commons.lang3.tuple.Pair;

public interface ProviewGroupListService {

    ProviewGroupsContainer getProviewGroups(final ProviewGroupForm form,
            final List<ProviewGroup> allLatestProviewGroups) throws ProviewException;

    Pair<List<String>, List<GroupDetails>> getGroupDetailsList(final ProviewGroupListFilterForm form,
            final Map<String, ProviewGroupContainer> allProviewGroups);
}
