package com.thomsonreuters.uscl.ereader.common.proview.feature;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.util.VersionUtil;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Features list builder for split book.
 */
public class SplitBookFeatureListBuilder extends AbstractFeaturesListBuilder {
    private static final Feature FULL_ANCHOR_FEATURE = new Feature("FullAnchorMap");
    private static final Feature COMBINED_TOC_FEATURE = new Feature("CombinedTOC");

    private final ProviewTitleService proviewTitleService;
    private final Map<BookTitleId, Feature> splitBookVolumesFeatures = new HashMap<>();

    private Map<BookTitleId, List<Doc>> titleDocs;
    private BookTitleId titleId;

    SplitBookFeatureListBuilder(
        @NotNull final ProviewTitleService proviewTitleService,
        @NotNull final BookDefinition bookDefinition,
        @NotNull final VersionUtil versionUtil) {
        super(proviewTitleService, bookDefinition, versionUtil);
        this.proviewTitleService = proviewTitleService;
    }

    @NotNull
    @Override
    public FeaturesListBuilder withTitleDocs(@NotNull final Map<BookTitleId, List<Doc>> titleDocs) {
        this.titleDocs = titleDocs;
        return this;
    }

    @NotNull
    @Override
    public FeaturesListBuilder forTitleId(@NotNull final BookTitleId titleId) {
        this.titleId = titleId;
        return this;
    }

    @NotNull
    @Override
    public List<Feature> getFeatures() {
        final List<Feature> features = super.getFeatures();
        Collections.addAll(features, FULL_ANCHOR_FEATURE, COMBINED_TOC_FEATURE);
        return features;
    }

    @Override
    protected void addNotesMigrationFeature(
        @NotNull final List<Feature> features,
        @NotNull final List<BookTitleId> titleIds) {
        if (splitBookVolumesFeatures.isEmpty()) {
            fillVolumesFeatures(titleIds);
        }
        Optional.ofNullable(splitBookVolumesFeatures.get(titleId))
            .ifPresent(features::add);
    }

    private void fillVolumesFeatures(@NotNull final List<BookTitleId> previousBookTitles) {
        final Map<BookTitleId, List<Doc>> docsByTitlesPrev = getDocsByVolumes(previousBookTitles);
        titleDocs.forEach((titleId, documents) -> Optional.ofNullable(getTitleNotesMigrationFeature(documents, docsByTitlesPrev))
            .ifPresent(feature -> splitBookVolumesFeatures.put(titleId, feature)));
    }

    @Nullable
    private Feature getTitleNotesMigrationFeature(
        @NotNull final List<Doc> currentTitleDocs,
        @NotNull final Map<BookTitleId, List<Doc>> docsInPreviousVersion) {
        final Set<BookTitleId> sourceTitles = docsInPreviousVersion.entrySet().stream()
            .filter(entry -> !Collections.disjoint(entry.getValue(), currentTitleDocs))
            .map(Entry::getKey)
            .collect(Collectors.toCollection(TreeSet::new));
        return createNotesMigrationFeature(sourceTitles);
    }

    @NotNull
    private Map<BookTitleId, List<Doc>> getDocsByVolumes(@NotNull final List<BookTitleId> titleIds) {
        return titleIds.stream().collect(
            Collectors.toMap(Function.identity(), proviewTitleService::getProviewTitleDocs, (oldVal, newVal) -> newVal, HashMap::new));
    }
}
