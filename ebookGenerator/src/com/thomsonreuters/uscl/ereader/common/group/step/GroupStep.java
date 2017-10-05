package com.thomsonreuters.uscl.ereader.common.group.step;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;

public interface GroupStep extends BookStep {
    /**
     * Returns group version of new created group, or {@code null} if no group was created
     */
    Long getGroupVersion();
}
