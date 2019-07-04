package com.thomsonreuters.uscl.ereader.common.proview.feature;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;

import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
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
    private static final String SPLIT_BOOK_TITLE = "SplitBookTitle";
    private static final String FULLY_QUALIFIED_TITLE_ID = "FullyQualifiedTitleId";
    private static final String ANNOS_SOURCE_VALUE = "FullyQualifiedTitleId/v1;SplitBookTitle/v1";
    private static final String ANNOS_SOURCE = "AnnosSource";
    private static final String PAGE_NOS = "PageNos";
    private static final String SPAN_PAGES = "SpanPages";

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
        given(proviewTitleService.getPreviousTitles(version, FULLY_QUALIFIED_TITLE_ID)).willReturn(
            Arrays
                .asList(new BookTitleId(FULLY_QUALIFIED_TITLE_ID, version), new BookTitleId(SPLIT_BOOK_TITLE, version)));
    }

    @Test
    public void shouldReturnFeaturesWithNotesMigrationFeatureMinorUpdate() {
        //given
        //when
        final List<Feature> features = featuresListBuilder.withBookVersion(new Version("v1.1")).getFeatures();
        //then
        final List<Feature> expectedFeatures = getExpectedFeatures(bookDefinition);
        expectedFeatures.add(new Feature(ANNOS_SOURCE, ANNOS_SOURCE_VALUE));
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
        expectedFeatures.add(new Feature(ANNOS_SOURCE, ANNOS_SOURCE_VALUE));
        assertTrue(CollectionUtils.isEqualCollection(features, expectedFeatures));
    }

    @Test
    public void checkPageNosFeatureExistence() {
        //given
        given(bookDefinition.isPrintPageNumbers()).willReturn(true);
        given(bookDefinition.getSourceType()).willReturn(SourceType.TOC);
        //when
        final List<Feature> features = featuresListBuilder.getFeatures();
        //then
        assertTrue(features.stream().anyMatch(feature -> PAGE_NOS.equals(feature.getName())));
        assertTrue(features.stream().anyMatch(feature -> SPAN_PAGES.equals(feature.getName())));
    }

    @Test
    public void checkPageNosFeatureAbsence() {
        //given
        given(bookDefinition.isPrintPageNumbers()).willReturn(false);
        given(bookDefinition.getSourceType()).willReturn(SourceType.TOC);
        //when
        final List<Feature> features = featuresListBuilder.getFeatures();
        //then
        assertTrue(features.stream().noneMatch(feature -> PAGE_NOS.equals(feature.getName())));
        assertTrue(features.stream().noneMatch(feature -> SPAN_PAGES.equals(feature.getName())));
    }
}
