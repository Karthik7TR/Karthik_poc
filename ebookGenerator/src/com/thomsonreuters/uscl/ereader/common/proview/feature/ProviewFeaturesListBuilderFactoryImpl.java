package com.thomsonreuters.uscl.ereader.common.proview.feature;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.util.VersionUtil;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Creates proview features list builder according to split flag
 */
@Component("proviewFeaturesListBuilderFactory")
public class ProviewFeaturesListBuilderFactoryImpl implements ProviewFeaturesListBuilderFactory {
    private final VersionUtil versionUtil;
    private final ProviewTitleService proviewTitleService;

    @Autowired
    public ProviewFeaturesListBuilderFactoryImpl(
        final VersionUtil versionUtil,
        final ProviewTitleService proviewTitleService) {
        this.versionUtil = versionUtil;
        this.proviewTitleService = proviewTitleService;
    }

    /**
     * Create proview features list builder according to split flag
     */
    @NotNull
    @Override
    public FeaturesListBuilder create(@NotNull final BookDefinition bookDefinition) {
        final FeaturesListBuilder featuresListBuilder;
        if (bookDefinition.isSplitBook()) {
            featuresListBuilder = new SplitBookFeatureListBuilder(proviewTitleService, bookDefinition, versionUtil);
        } else {
            featuresListBuilder = new SingleBookFeaturesListBuilder(proviewTitleService, bookDefinition, versionUtil);
        }
        return featuresListBuilder;
    }
}
