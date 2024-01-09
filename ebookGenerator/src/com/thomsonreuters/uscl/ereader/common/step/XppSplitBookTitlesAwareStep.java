package com.thomsonreuters.uscl.ereader.common.step;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.LongStream;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;

public interface XppSplitBookTitlesAwareStep extends SplitBookTitlesAwareStep {
    @Override
    default <T extends Collection<String>> T getSplitTitles(final Supplier<T> collectionSupplier) {
        final BookDefinition bookDefinition = getBookDefinition();
        final String titleId = bookDefinition.getFullyQualifiedTitleId();

        final T titleIds = collectionSupplier.get();
        titleIds.add(titleId);

        final Long partsCount = bookDefinition.getPrintComponents().stream()
            .filter(PrintComponent::getSplitter)
            .count() + 1;
        LongStream.range(1, partsCount)
            .map(part -> ++part)
            .mapToObj(partNumber -> String.format("%s_pt%s", titleId, partNumber))
            .forEach(titleIds::add);

        return titleIds;
    }
}
