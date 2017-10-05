package com.thomsonreuters.uscl.ereader.common.group.service;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.proview.ProviewRetry;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;
import com.thomsonreuters.uscl.ereader.group.service.GroupService;

public class GroupServiceWithRetryImpl implements GroupServiceWithRetry {
    @Resource(name = "groupService")
    private GroupService groupService;

    @ProviewRetry
    @Override
    public void createGroup(final GroupDefinition groupDefinition) throws ProviewException {
        groupService.createGroup(groupDefinition);
    }
}
