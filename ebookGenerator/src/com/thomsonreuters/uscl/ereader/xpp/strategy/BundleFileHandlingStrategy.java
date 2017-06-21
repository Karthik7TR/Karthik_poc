package com.thomsonreuters.uscl.ereader.xpp.strategy;

import java.io.File;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import org.jetbrains.annotations.NotNull;

/**
 *  Describe behavior of classes, responsible for handling of different types of DIVXML components
 */
public interface BundleFileHandlingStrategy
{
    /**
     * @return - DIVXML component type, instance responsible for DIVXML file of this type.
     */
    @NotNull
    Set<BundleFileType> getBundleFileTypes();

    /**
     * Performing handling
     */
    void performHandling(@NotNull File inputFile, @NotNull String materialNumber, @NotNull BookStep bookStep);
}
