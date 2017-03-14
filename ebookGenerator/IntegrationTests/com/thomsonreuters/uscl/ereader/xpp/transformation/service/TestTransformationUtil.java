package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;

/**
 * For usage in tests. Works as mock for TransformationUtil.
 */
public class TestTransformationUtil extends TransformationUtil
{
    @Override
    public boolean shouldSkip(final BookStep step)
    {
        return false;
    }
}
