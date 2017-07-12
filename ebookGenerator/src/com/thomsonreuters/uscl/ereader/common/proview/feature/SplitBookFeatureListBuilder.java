package com.thomsonreuters.uscl.ereader.common.proview.feature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

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
public class SplitBookFeatureListBuilder extends AbstractFeaturesListBuilder
{
    private final ProviewTitleService proviewTitleService;
    private final Map<BookTitleId, Feature> splitBookVolumesFeatures = new HashMap<>();

    private Map<BookTitleId, List<Doc>> titleDocs;
    private BookTitleId titleId;

    SplitBookFeatureListBuilder(
        @NotNull final ProviewTitleService proviewTitleService,
        @NotNull final BookDefinition bookDefinition,
        @NotNull final VersionUtil versionUtil)
    {
        super(proviewTitleService, bookDefinition, versionUtil);
        this.proviewTitleService = proviewTitleService;
    }

    @NotNull
    @Override
    public FeaturesListBuilder withTitleDocs(@NotNull final Map<BookTitleId, List<Doc>> titleDocs)
    {
        this.titleDocs = titleDocs;
        return this;
    }

    @NotNull
    @Override
    public FeaturesListBuilder forTitleId(@NotNull final BookTitleId titleId)
    {
        this.titleId = titleId;
        return this;
    }

    @Override
    protected void addNotesMigrationFeature(@NotNull final List<Feature> features, @NotNull final List<BookTitleId> titleIds)
    {
        if (splitBookVolumesFeatures.isEmpty())
        {
            fillVolumesFeatures(titleIds);
        }
        final Feature notesMigrationFeature = splitBookVolumesFeatures.get(titleId);
        if (notesMigrationFeature != null)
        {
            features.add(notesMigrationFeature);
        }
    }

    private void fillVolumesFeatures(@NotNull final List<BookTitleId> previousBookTitles)
    {
        final Map<BookTitleId, List<Doc>> docsByTitlesPrev = getDocsByVolumes(previousBookTitles);
        for (final Entry<BookTitleId, List<Doc>> e : titleDocs.entrySet())
        {
            final Feature notesMigrationFeature = getTitleNotesMigrationFeature(e.getValue(), docsByTitlesPrev);
            if (notesMigrationFeature != null)
            {
                splitBookVolumesFeatures.put(e.getKey(), notesMigrationFeature);
            }
        }
    }

    @Nullable
    private Feature getTitleNotesMigrationFeature(@NotNull final List<Doc> currentTitleDocs,
                                                  @NotNull final Map<BookTitleId, List<Doc>> docsInPreviousVersion)
    {
        final Set<BookTitleId> sourceTitles = new TreeSet<>();
        for (final Doc doc : currentTitleDocs)
        {
            for (final Entry<BookTitleId, List<Doc>> e : docsInPreviousVersion.entrySet())
            {
                if (e.getValue().contains(doc))
                {
                    sourceTitles.add(e.getKey());
                }
            }
        }
        return createNotesMigrationFeature(sourceTitles);
    }

    @NotNull
    private Map<BookTitleId, List<Doc>> getDocsByVolumes(@NotNull final List<BookTitleId> titleIds)
    {
        final Map<BookTitleId, List<Doc>> volumesDocs = new HashMap<>();
        for (final BookTitleId bookTitleId : titleIds)
        {
            volumesDocs.put(bookTitleId, proviewTitleService.getProviewTitleDocs(bookTitleId));
        }
        return volumesDocs;
    }
}
