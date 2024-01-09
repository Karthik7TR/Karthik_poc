package com.thomsonreuters.uscl.ereader.common.step;

import java.util.Collection;
import java.util.function.Supplier;

public interface ServiceSplitBookTitlesAwareStep extends SplitBookTitlesAwareStep {
    @Override
    default <T extends Collection<String>> T getSplitTitles(final Supplier<T> collectionSupplier) {
        final T splitTitlesIds = collectionSupplier.get();
        splitTitlesIds.addAll(getTitlesFromService());
        return splitTitlesIds;
    }

    Collection<String> getTitlesFromService();
}
