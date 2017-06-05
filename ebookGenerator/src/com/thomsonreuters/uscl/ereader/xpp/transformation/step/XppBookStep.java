package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import java.util.List;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Description of XPP pathway step behavior
 */
public interface XppBookStep extends BookStep
{
    /**
     * Get ordered list of publication's bundles
     */
    @NotNull
    List<XppBundle> getXppBundles();
}
