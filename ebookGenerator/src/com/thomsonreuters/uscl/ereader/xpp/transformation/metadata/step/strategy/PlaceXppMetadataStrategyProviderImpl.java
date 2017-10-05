package com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step.strategy;

import java.util.Collections;
import java.util.List;

import com.thomsonreuters.uscl.ereader.xpp.strategy.provider.AbstractBundleFileHandlingStrategyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Hold all available PlaceXppMetadataStrategy instances.
 * To define what instance should be returned, compare BundleFileType from instance with provided BundleFileType.
 * If there is no instance for provided BundleFileType, UnsupportedOperationException will be thrown.
 */
@Component("placeXppMetadataStrategyProvider")
public class PlaceXppMetadataStrategyProviderImpl
    extends AbstractBundleFileHandlingStrategyProvider<PlaceXppMetadataStrategy> {
    public PlaceXppMetadataStrategyProviderImpl() {
        this(Collections.<PlaceXppMetadataStrategy>emptyList());
    }

    @Autowired(required = false)
    public PlaceXppMetadataStrategyProviderImpl(final List<PlaceXppMetadataStrategy> strategies) {
        super(strategies);
    }
}
