package com.thomsonreuters.uscl.ereader.common.step;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface SplitBookTitlesAwareStep extends BookStep {
    <T extends Collection<String>> T getSplitTitles(Supplier<T> collectionSupplier);

    default List<String> getSplitTitles() {
        return getSplitTitles(ArrayList::new);
    }
}
