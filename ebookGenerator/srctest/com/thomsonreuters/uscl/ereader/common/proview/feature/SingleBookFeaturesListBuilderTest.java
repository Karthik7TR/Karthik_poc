package com.thomsonreuters.uscl.ereader.common.proview.feature;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.util.VersionUtilImpl;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class SingleBookFeaturesListBuilderTest extends FeatureListBuilderFixture {
    @Override
    protected FeaturesListBuilder createFeatureListBuilder(
        final ProviewTitleService proviewTitleService,
        final BookDefinition bookDefinition) {
        return new SingleBookFeaturesListBuilder(proviewTitleService, bookDefinition, new VersionUtilImpl());
    }

    @Before
    @Override
    public void onTestSetUp() {
        super.onTestSetUp();
        final Version version = new Version("v1.0");
        given(bookDefinition.isSplitBook()).willReturn(false);
        given(proviewTitleService.getLatestProviewTitleVersion(anyString())).willReturn(version);
        given(proviewTitleService.getPreviousTitles(version, "FullyQualifiedTitleId")).willReturn(
            Arrays
                .asList(new BookTitleId("FullyQualifiedTitleId", version), new BookTitleId("SplitBookTitle", version)));
    }

    @Test
    public void shouldReturnFeaturesWithNotesMigrationFeatureMinorUpdate() {
        //given
        //when
        final List<Feature> features = featuresListBuilder.withBookVersion(new Version("v1.1")).getFeatures();
        //then
        final List<Feature> expectedFeatures = getExpectedFeatures(bookDefinition);
        expectedFeatures.add(new Feature("AnnosSource", "FullyQualifiedTitleId/v1;SplitBookTitle/v1"));
        assertTrue(CollectionUtils.isEqualCollection(features, expectedFeatures));
    }

    @Test
    public void shouldReturnFeaturesWithoutSplitBookFeatures() {
        //given
        given(bookDefinition.isSplitBook()).willReturn(false);
        //when
        final List<Feature> features = featuresListBuilder.getFeatures();
        //then
        assertTrue(CollectionUtils.isEqualCollection(features, getExpectedFeatures(bookDefinition)));
    }

    @Test
    public void shouldReturnFeaturesWithNotesMigrationFeatureMajorUpdate() {
        //given
        //when
        final List<Feature> features = featuresListBuilder.withBookVersion(new Version("v2.0")).getFeatures();
        //then
        final List<Feature> expectedFeatures = getExpectedFeatures(bookDefinition);
        expectedFeatures.add(new Feature("AnnosSource", "FullyQualifiedTitleId/v1;SplitBookTitle/v1"));
        assertTrue(CollectionUtils.isEqualCollection(features, expectedFeatures));
    }
}
