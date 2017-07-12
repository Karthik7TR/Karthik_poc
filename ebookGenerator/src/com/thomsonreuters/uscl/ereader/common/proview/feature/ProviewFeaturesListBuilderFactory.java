package com.thomsonreuters.uscl.ereader.common.proview.feature;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.jetbrains.annotations.NotNull;

/**
 * Creates builder of proview features list, according to bookDefinition
 */
public interface ProviewFeaturesListBuilderFactory
{
    /**
     * Create builder of proview features list, according to bookDefinition
     * @return
     */
    @NotNull
    FeaturesListBuilder create(@NotNull BookDefinition bookDefinition);
}
