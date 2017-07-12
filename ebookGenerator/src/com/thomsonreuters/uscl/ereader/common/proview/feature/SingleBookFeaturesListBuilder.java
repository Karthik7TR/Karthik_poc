package com.thomsonreuters.uscl.ereader.common.proview.feature;

import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.util.VersionUtil;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import org.jetbrains.annotations.NotNull;

/**
 * Features list builder for single book, titleDocs and TitleId params are ignored
 */
public class SingleBookFeaturesListBuilder extends AbstractFeaturesListBuilder
{
    SingleBookFeaturesListBuilder(
        @NotNull final ProviewTitleService proviewTitleService,
        @NotNull final BookDefinition bookDefinition,
        @NotNull final VersionUtil versionUtil)
    {
        super(proviewTitleService, bookDefinition, versionUtil);
    }

    @NotNull
    @Override
    public FeaturesListBuilder withTitleDocs(@NotNull final Map<BookTitleId, List<Doc>> titleDocs)
    {
        return this;
    }

    @NotNull
    @Override
    public FeaturesListBuilder forTitleId(@NotNull final BookTitleId titleId)
    {
        return this;
    }

    @Override
    protected void addNotesMigrationFeature(@NotNull final List<Feature> features, @NotNull final List<BookTitleId> titleIds)
    {
        final Feature notesMigrationFeature = createNotesMigrationFeature(titleIds);
        if (notesMigrationFeature != null)
        {
            features.add(notesMigrationFeature);
        }
    }
}
