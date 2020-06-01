package com.thomsonreuters.uscl.ereader.common.proview.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.util.VersionUtil;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract class contains common builder methods e.g. creating of default features list.
 */
@RequiredArgsConstructor
public abstract class AbstractFeaturesListBuilder implements FeaturesListBuilder {
    private final ProviewTitleService proviewTitleService;
    private final BookDefinition bookDefinition;
    private final VersionUtil versionUtil;
    private boolean withPageNumbers;
    private boolean withThesaurus;

    private Version newBookVersion;

    @NotNull
    @Override
    public FeaturesListBuilder withBookVersion(final Version version) {
        newBookVersion = version;
        return this;
    }

    @NotNull
    @Override
    public List<Feature> getFeatures() {
        final List<Feature> features = createDefaultFeaturesList();

        final Version previousVersion =
            proviewTitleService.getLatestProviewTitleVersion(bookDefinition.getFullyQualifiedTitleId());
        if (shouldCreateFeature(previousVersion, newBookVersion)) {
            final List<BookTitleId> previousBookTitles =
                proviewTitleService.getPreviousTitles(previousVersion, bookDefinition.getFullyQualifiedTitleId());
            addNotesMigrationFeature(features, previousBookTitles);
        }

        return features;
    }

     @Override
     public FeaturesListBuilder withPageNumbers(final boolean withPageNumbers) {
         this.withPageNumbers = withPageNumbers;
         return this;
     }

     @Override
     public FeaturesListBuilder withThesaurus(final boolean withThesaurus) {
         this.withThesaurus = withThesaurus;
         return this;
     }

    private List<Feature> createDefaultFeaturesList() {
        final List<Feature> features = new ArrayList<>();
        features.add(DefaultProviewFeatures.PRINT.feature);

        if (bookDefinition.getAutoUpdateSupportFlag()) {
            features.add(DefaultProviewFeatures.AUTO_UPDATE.feature);
        }

        if (bookDefinition.getSearchIndexFlag()) {
            features.add(DefaultProviewFeatures.SEARCH_INDEX.feature);
        }

        if (bookDefinition.getEnableCopyFeatureFlag()) {
            features.add(DefaultProviewFeatures.COPY.feature);
        }

        if (bookDefinition.getOnePassSsoLinkFlag()) {
            Collections.addAll(features,
                DefaultProviewFeatures.ONE_PASS_SSO_WWW_WESTLAW.feature,
                DefaultProviewFeatures.ONE_PASS_SSO_NEXT_WESTLAW.feature
            );
        }

        if (bookDefinition.isELooseleafsEnabled()) {
            features.add(DefaultProviewFeatures.ELOOSELEAFS_BUCKET.feature);
        }

        if (withPageNumbers) {
            Collections.addAll(features,
                DefaultProviewFeatures.PAGE_NUMBERS.feature,
                DefaultProviewFeatures.SPAN_PAGES.feature
            );
        }

        if (withThesaurus) {
            Collections.addAll(features,
                DefaultProviewFeatures.SEARCH_FIELDS.feature,
                DefaultProviewFeatures.SEARCH_TEMPLATE.feature,
                DefaultProviewFeatures.THESAURUS_TERMS.feature
            );
        }

        return features;
    }

    protected abstract void addNotesMigrationFeature(List<Feature> features, List<BookTitleId> titleIds);

    @Nullable
    protected Feature createNotesMigrationFeature(@NotNull final Collection<BookTitleId> titleIds) {
        Feature feature = null;
        if (!titleIds.isEmpty() && isTitlesPromotedToFinal(titleIds)) {
            final String featureValue = titleIds.stream()
                .map(BookTitleId::getTitleIdWithMajorVersion)
                .collect(Collectors.joining(";"));
            feature = new Feature("AnnosSource", featureValue);
        }
        return feature;
    }

    private boolean isTitlesPromotedToFinal(final Collection<BookTitleId> titleIds) {
        return titleIds.stream()
            .map(BookTitleId::getTitleId)
            .map(titleId -> {
                final Version previousVersion = proviewTitleService.getLatestProviewTitleVersion(bookDefinition.getFullyQualifiedTitleId());
                final Version versionToCheck = versionUtil.isMajorUpdate(previousVersion, newBookVersion) ? previousVersion : newBookVersion;
                return proviewTitleService.isMajorVersionPromotedToFinal(titleId, versionToCheck);
            })
            .allMatch(Boolean.TRUE::equals);
    }

    private boolean shouldCreateFeature(final Version previousVersion, final Version newVersion) {
        return previousVersion != null
            && newVersion != null
            && !previousVersion.equals(newVersion);
    }

    private enum DefaultProviewFeatures {
        PRINT(new Feature("Print")),
        AUTO_UPDATE(new Feature("AutoUpdate")),
        SEARCH_INDEX(new Feature("SearchIndex")),
        COPY(new Feature("Copy")),
        ONE_PASS_SSO_WWW_WESTLAW(new Feature("OnePassSSO", "www.westlaw.com")),
        ONE_PASS_SSO_NEXT_WESTLAW(new Feature("OnePassSSO", "next.westlaw.com")),
        ELOOSELEAFS_BUCKET(new Feature("tr_opt_TitleType", "eReference")),
        PAGE_NUMBERS(new Feature("PageNos")),
        SPAN_PAGES(new Feature("SpanPages")),
        SEARCH_FIELDS(new Feature("SearchFields", "fields.xml")),
        SEARCH_TEMPLATE(new Feature("SearchTemplate", "template.xml")),
        THESAURUS_TERMS(new Feature("ThesaurusTerms", "thesaurus.xml"));

        private final Feature feature;

        DefaultProviewFeatures(final Feature feature) {
            this.feature = feature;
        }
    }
}
