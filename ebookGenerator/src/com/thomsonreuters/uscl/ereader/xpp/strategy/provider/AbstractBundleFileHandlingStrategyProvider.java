package com.thomsonreuters.uscl.ereader.xpp.strategy.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.xpp.strategy.BundleFileHandlingStrategy;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract provider contains common logic.
 */
public abstract class AbstractBundleFileHandlingStrategyProvider<S extends BundleFileHandlingStrategy>
    implements BundleFileHandlingStrategyProvider<S> {
    private final Map<BundleFileType, S> availableStrategies;

    protected AbstractBundleFileHandlingStrategyProvider(final List<S> strategies) {
        availableStrategies = new HashMap<>();
        for (final S strategy : strategies) {
            assignStrategyToTypes(strategy);
        }
    }

    private void assignStrategyToTypes(final S strategy) {
        for (final BundleFileType type : strategy.getBundleFileTypes()) {
            if (availableStrategies.put(type, strategy) != null) {
                throw new UnsupportedOperationException("Several strategies with the same bundle types found ()");
            }
        }
    }

    @NotNull
    @Override
    public S getStrategy(@NotNull final BundleFileType bundleFileType) {
        final S resultStrategy = availableStrategies.get(bundleFileType);
        if (resultStrategy == null) {
            throw new UnsupportedOperationException("Generation strategy for provided file type, does not exist");
        }
        return resultStrategy;
    }
}
