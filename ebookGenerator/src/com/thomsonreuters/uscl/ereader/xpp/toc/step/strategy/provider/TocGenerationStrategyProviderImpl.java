package com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.provider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.TocGenerationStrategy;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.type.BundleFileType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Hold all available TocGenerationStrategy instances.
 * To define what instance should be returned, compare BundleFileType from instance with provided BundleFileType.
 * If there is no instance for provided BundleFileType, UnsupportedOperationException will be thrown.
 */
@Component
public class TocGenerationStrategyProviderImpl implements TocGenerationStrategyProvider
{
    private final Map<BundleFileType, TocGenerationStrategy> availableStrategies;

    public TocGenerationStrategyProviderImpl()
    {
        this(Collections.<TocGenerationStrategy>emptyList());
    }

    @Autowired(required = false)
    public TocGenerationStrategyProviderImpl(final List<TocGenerationStrategy> strategies)
    {
        availableStrategies = new HashMap<>();
        for (final TocGenerationStrategy strategy : strategies)
        {
            assignStrategyToTypes(strategy);
        }
    }

    private void assignStrategyToTypes(final TocGenerationStrategy strategy)
    {
        for (final BundleFileType type : strategy.getBundleFileTypes())
        {
            if (availableStrategies.put(type, strategy) != null)
            {
                throw new UnsupportedOperationException("Several strategies with the same bundle types found ()");
            }
        }
    }

    @NotNull
    @Override
    public TocGenerationStrategy getTocGenerationStrategy(@NotNull final BundleFileType bundleFileType)
    {
        final TocGenerationStrategy resultStrategy = availableStrategies.get(bundleFileType);
        if (resultStrategy == null)
        {
            throw new UnsupportedOperationException("Generation strategy for provided file type, does not exist");
        }
        return resultStrategy;
    }
}
