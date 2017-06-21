package com.thomsonreuters.uscl.ereader.xpp.strategy.provider;

import com.thomsonreuters.uscl.ereader.xpp.strategy.BundleFileHandlingStrategy;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Strategy provider, provide instance of strategy defined by DIVXML component type
 */
public interface BundleFileHandlingStrategyProvider<S extends BundleFileHandlingStrategy>
{
    /**
     * @param bundleFileType - DIVXML component type
     */
    @NotNull
    S getStrategy(@NotNull BundleFileType bundleFileType);
}
