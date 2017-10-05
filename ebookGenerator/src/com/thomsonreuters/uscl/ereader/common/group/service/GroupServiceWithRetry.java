package com.thomsonreuters.uscl.ereader.common.group.service;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.GroupDefinition;

public interface GroupServiceWithRetry {
    void createGroup(GroupDefinition groupDefinition) throws ProviewException;
}
