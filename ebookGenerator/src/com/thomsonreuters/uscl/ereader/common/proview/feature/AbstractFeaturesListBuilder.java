package com.thomsonreuters.uscl.ereader.common.proview.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.util.VersionUtil;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract class contains common builder methods e.g. creating of default features list.
 */
public abstract class AbstractFeaturesListBuilder implements FeaturesListBuilder
{
    private final ProviewTitleService proviewTitleService;
    private final BookDefinition bookDefinition;
    private final VersionUtil versionUtil;

    private Version newBookVersion;

    protected AbstractFeaturesListBuilder(@NotNull final ProviewTitleService proviewTitleService,
                                          @NotNull final BookDefinition bookDefinition,
                                          @NotNull final VersionUtil versionUtil)
    {
        this.proviewTitleService = proviewTitleService;
        this.bookDefinition = bookDefinition;
        this.versionUtil = versionUtil;
    }

    @NotNull
    @Override
    public FeaturesListBuilder withBookVersion(final Version version)
    {
        newBookVersion = version;
        return this;
    }

    @NotNull
    @Override
    public List<Feature> getFeatures()
    {
        final List<Feature> features = createDefaultFeaturesList();

        final Version previousVersion = proviewTitleService
            .getLatestProviewTitleVersion(bookDefinition.getFullyQualifiedTitleId());
        if (shouldCreateFeature(previousVersion, newBookVersion))
        {
            final List<BookTitleId> previousBookTitles = proviewTitleService.getPreviousTitles(previousVersion, bookDefinition.getFullyQualifiedTitleId());
            addNotesMigrationFeature(features, previousBookTitles);
        }

        return features;
    }

    private List<Feature> createDefaultFeaturesList()
    {
        final List<Feature> features = new ArrayList<>();
        features.add(DefaultProviewFeatures.PRINT.feature);

        if (bookDefinition.getAutoUpdateSupportFlag())
        {
            features.add(DefaultProviewFeatures.AUTO_UPDATE.feature);
        }

        if (bookDefinition.getSearchIndexFlag())
        {
            features.add(DefaultProviewFeatures.SEARCH_INDEX.feature);
        }

        if (bookDefinition.getEnableCopyFeatureFlag())
        {
            features.add(DefaultProviewFeatures.COPY.feature);
        }

        if (bookDefinition.getOnePassSsoLinkFlag())
        {
            features.add(DefaultProviewFeatures.ONE_PASS_SSO_WWW_WESTLAW.feature);
            features.add(DefaultProviewFeatures.ONE_PASS_SSO_NEXT_WESTLAW.feature);
        }

        if (bookDefinition.isSplitBook())
        {
            features.add(DefaultProviewFeatures.FULL_ANCHOR_MAP.feature);
            features.add(DefaultProviewFeatures.COMBINED_TOC.feature);
        }

        if (SourceType.XPP == bookDefinition.getSourceType())
        {
            features.add(DefaultProviewFeatures.PAGE_NUMBERS.feature);
            features.add(DefaultProviewFeatures.SPAN_PAGES.feature);
        }

        return features;
    }

    protected abstract void addNotesMigrationFeature(List<Feature> features, List<BookTitleId> titleIds);

    @Nullable
    protected Feature createNotesMigrationFeature(@NotNull final Collection<BookTitleId> titleIds)
    {
        Feature feature = null;
        if (!titleIds.isEmpty() && isTitlesPromotedToFinal(titleIds))
        {
            final List<String> titlesWithMajorVersion = new ArrayList<>();
            for (final BookTitleId bookTitle : titleIds)
            {
                titlesWithMajorVersion.add(bookTitle.getTitleIdWithMajorVersion());
            }
            final String featureValue = StringUtils.join(titlesWithMajorVersion, ";");
            feature = new Feature("AnnosSource", featureValue);
        }
        return feature;
    }

    private boolean isTitlesPromotedToFinal(final Collection<BookTitleId> titleIds)
    {
        boolean isTitlesFinal = true;
        for (final BookTitleId bookTitle : titleIds)
        {
            if (!proviewTitleService.isMajorVersionPromotedToFinal(bookTitle.getTitleId(), newBookVersion))
            {
                isTitlesFinal = false;
                break;
            }
        }
        return isTitlesFinal;
    }

    private boolean shouldCreateFeature(final Version previousVersion, final Version newVersion)
    {
        return previousVersion != null
            && newVersion != null
            && !previousVersion.equals(newVersion)
            && !versionUtil.isMajorUpdate(previousVersion, newVersion);
    }

    private enum DefaultProviewFeatures
    {
        PRINT(new Feature("Print")),
        AUTO_UPDATE(new Feature("AutoUpdate")),
        SEARCH_INDEX(new Feature("SearchIndex")),
        COPY(new Feature("Copy")),
        ONE_PASS_SSO_WWW_WESTLAW(new Feature("OnePassSSO", "www.westlaw.com")),
        ONE_PASS_SSO_NEXT_WESTLAW(new Feature("OnePassSSO", "next.westlaw.com")),
        FULL_ANCHOR_MAP(new Feature("FullAnchorMap")),
        COMBINED_TOC(new Feature("CombinedTOC")),
        PAGE_NUMBERS(new Feature("PageNos")),
        SPAN_PAGES(new Feature("SpanPages"));

        private final Feature feature;

        DefaultProviewFeatures(final Feature feature)
        {
            this.feature = feature;
        }
    }
}
