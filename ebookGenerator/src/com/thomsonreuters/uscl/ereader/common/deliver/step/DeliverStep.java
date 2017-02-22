package com.thomsonreuters.uscl.ereader.common.deliver.step;

import java.util.List;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;

public interface DeliverStep extends BookStep
{
    /**
     * Returns list of split title IDs, which was successfully published before ProView failure
     */
    List<String> getPublishedSplitTitles();
}
