package com.thomsonreuters.uscl.ereader.common.proview.feature;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

public abstract class FeatureListBuilderFixture {
    protected FeaturesListBuilder featuresListBuilder;
    @Mock
    protected BookDefinition bookDefinition;
    @Mock
    protected SplitNodeInfo splitNodeInfo;
    @Mock
    protected ProviewTitleService proviewTitleService;

    @Before
    public void onTestSetUp() {
        featuresListBuilder = createFeatureListBuilder(proviewTitleService, bookDefinition);

        final Version version = new Version("v1.0");
        given(proviewTitleService.getLatestProviewTitleVersion(anyString())).willReturn(version);
        given(proviewTitleService.getPreviousTitles(version, "FullyQualifiedTitleId"))
            .willReturn(Collections.singletonList(new BookTitleId("FullyQualifiedTitleId", version)));
        given(proviewTitleService.isMajorVersionPromotedToFinal(anyString(), eq(new Version("v1.1")))).willReturn(true);
        given(proviewTitleService.isMajorVersionPromotedToFinal(anyString(), eq(new Version("v1.0")))).willReturn(true);

        given(splitNodeInfo.getBookVersionSubmitted()).willReturn("1.0");
        given(splitNodeInfo.getSplitBookTitle()).willReturn("SplitBookTitle");

        given(bookDefinition.getAutoUpdateSupportFlag()).willReturn(true);
        given(bookDefinition.getSearchIndexFlag()).willReturn(true);
        given(bookDefinition.getEnableCopyFeatureFlag()).willReturn(true);
        given(bookDefinition.getOnePassSsoLinkFlag()).willReturn(true);
        given(bookDefinition.isSplitBook()).willReturn(true);
        given(bookDefinition.getSourceType()).willReturn(SourceType.XPP);
        given(bookDefinition.getFullyQualifiedTitleId()).willReturn("FullyQualifiedTitleId");
        given(bookDefinition.getSplitNodes()).willReturn(Collections.singleton(splitNodeInfo));
    }

    protected abstract FeaturesListBuilder createFeatureListBuilder(
        ProviewTitleService proviewTitleService,
        BookDefinition bookDefinition);

    @Test
    public void shouldReturnFeaturesWithoutNoteMigration() {
        //given
        //when
        final List<Feature> features = featuresListBuilder.getFeatures();
        //then
        assertTrue(CollectionUtils.isEqualCollection(features, getExpectedFeatures(bookDefinition)));
    }

    @Test
    public void shouldReturnFeaturesWithoutXppPagesFeatures() {
        //given
        given(bookDefinition.getSourceType()).willReturn(SourceType.FILE);
        //when
        final List<Feature> features = featuresListBuilder.getFeatures();
        //then
        assertTrue(CollectionUtils.isEqualCollection(features, getExpectedFeatures(bookDefinition)));
    }

    @Test
    public void shouldReturnFeaturesWithoutOnePassFeatures() {
        //given
        given(bookDefinition.getOnePassSsoLinkFlag()).willReturn(false);
        //when
        final List<Feature> features = featuresListBuilder.getFeatures();
        //then
        assertTrue(CollectionUtils.isEqualCollection(features, getExpectedFeatures(bookDefinition)));
    }

    @Test
    public void shouldReturnFeaturesWithoutCopyFeature() {
        //given
        given(bookDefinition.getEnableCopyFeatureFlag()).willReturn(false);
        //when
        final List<Feature> features = featuresListBuilder.getFeatures();
        //then
        assertTrue(CollectionUtils.isEqualCollection(features, getExpectedFeatures(bookDefinition)));
    }

    @Test
    public void shouldReturnFeaturesWithoutSearchIndexFeature() {
        //given
        given(bookDefinition.getSearchIndexFlag()).willReturn(false);
        //when
        final List<Feature> features = featuresListBuilder.getFeatures();
        //then
        assertTrue(CollectionUtils.isEqualCollection(features, getExpectedFeatures(bookDefinition)));
    }

    @Test
    public void shouldReturnFeaturesWithoutAutoUpdateFeature() {
        //given
        given(bookDefinition.getAutoUpdateSupportFlag()).willReturn(false);
        //when
        final List<Feature> features = featuresListBuilder.getFeatures();
        //then
        assertTrue(CollectionUtils.isEqualCollection(features, getExpectedFeatures(bookDefinition)));
    }

    @Test
    public void shouldReturnFeaturesWithoutNotesMigrationFeatureWithSameVersion() {
        //given
        //when
        final List<Feature> features = featuresListBuilder.withBookVersion(new Version("v1.0")).getFeatures();
        //then
        assertTrue(CollectionUtils.isEqualCollection(features, getExpectedFeatures(bookDefinition)));
    }

    protected List<Feature> getExpectedFeatures(final BookDefinition bookDefinition) {
        return getExpectedFeatures(bookDefinition, false);
    }

    protected List<Feature> getExpectedFeatures(final BookDefinition bookDefinition, final boolean withPageNumbers) {
        final List<Feature> features = new ArrayList<>();
        features.add(new Feature("Print"));

        if (bookDefinition.getAutoUpdateSupportFlag()) {
            features.add(new Feature("AutoUpdate"));
        }

        if (bookDefinition.getSearchIndexFlag()) {
            features.add(new Feature("SearchIndex"));
        }

        if (bookDefinition.getEnableCopyFeatureFlag()) {
            features.add(new Feature("Copy"));
        }

        if (bookDefinition.getOnePassSsoLinkFlag()) {
            features.add(new Feature("OnePassSSO", "www.westlaw.com"));
            features.add(new Feature("OnePassSSO", "next.westlaw.com"));
        }

        if (bookDefinition.isSplitBook()) {
            features.add(new Feature("FullAnchorMap"));
            features.add(new Feature("CombinedTOC"));
        }

        if (withPageNumbers) {
            features.add(new Feature("PageNos"));
            features.add(new Feature("SpanPages"));
        }

        return features;
    }
}
