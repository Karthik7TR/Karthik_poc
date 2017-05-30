package com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy;

import java.util.Set;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.type.BundleFileType;
import org.jetbrains.annotations.NotNull;

/**
 *  TOC Generator strategy. Describe behavior of classes, responsible for generation of TOC for different types of DIVXML components
 */
public interface TocGenerationStrategy
{
    /**
     * @return - DIVXML component type, instance responsible for generation of TOC from DIVXML file of this type.
     */
    @NotNull
    Set<BundleFileType> getBundleFileTypes();

    /**
     * Performing generation of TOC
     */
    void performTocGeneration(@NotNull BookStep bookStep);
}
