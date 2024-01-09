package com.thomsonreuters.uscl.ereader.common.xslt;

import org.jetbrains.annotations.NotNull;

/**
 * Performs XSLT transformation
 */
public interface XslTransformationService {
    /**
     * Run XSLT transformation
     * @param command transformation command
     */
    void transform(@NotNull TransformationCommand command);
}
