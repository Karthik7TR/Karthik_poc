package com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.provider;

import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.TocGenerationStrategy;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.type.BundleFileType;
import org.jetbrains.annotations.NotNull;

/**
 * TOC Generation Strategy provider, provide instance of TocGenerationStrategy defined by DIVXML component type
 */
public interface TocGenerationStrategyProvider
{
    /**
     * @param bundleFileType - DIVXML component type
     */
    @NotNull
    TocGenerationStrategy getTocGenerationStrategy(@NotNull BundleFileType bundleFileType);
}
