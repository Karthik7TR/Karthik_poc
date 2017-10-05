package com.thomsonreuters.uscl.ereader.common.proview.feature;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.core.book.util.VersionUtilImpl;
import com.thomsonreuters.uscl.ereader.deliver.service.title.ProviewTitleService;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import com.thomsonreuters.uscl.ereader.proview.Feature;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class SplitBookFeatureListBuilderTest extends FeatureListBuilderFixture {
    @Override
    protected FeaturesListBuilder createFeatureListBuilder(
        final ProviewTitleService proviewTitleService,
        final BookDefinition bookDefinition) {
        return new SplitBookFeatureListBuilder(proviewTitleService, bookDefinition, new VersionUtilImpl());
    }

    @Before
    @Override
    public void onTestSetUp() {
        super.onTestSetUp();
        given(proviewTitleService.getProviewTitleDocs(any(BookTitleId.class)))
            .willReturn(Collections.singletonList(new Doc("doc1", "doc1.html", 0, null)))
            .willReturn(Collections.singletonList(new Doc("doc2", "doc2.html", 0, null)));

        final Version version = new Version("v1.0");
        given(proviewTitleService.getLatestProviewTitleVersion(anyString())).willReturn(version);
        given(proviewTitleService.getPreviousTitles(version, "FullyQualifiedTitleId"))
            .willReturn(Collections.singletonList(new BookTitleId("FullyQualifiedTitleId", version)));
    }

    @Test
    public void shouldReturnFeaturesWithNotesMigrationFeature() {
        //given
        final Map<BookTitleId, List<Doc>> titleDocs = new HashMap<>();
        titleDocs.put(
            new BookTitleId("FullyQualifiedTitleId", new Version("v1.0")),
            Collections.singletonList(new Doc("doc1", "doc1.html", 0, null)));
        //when
        final List<Feature> features = featuresListBuilder.withBookVersion(new Version("v1.1"))
            .withTitleDocs(titleDocs)
            .forTitleId(new BookTitleId("FullyQualifiedTitleId", new Version("v1.0")))
            .getFeatures();
        //then
        final List<Feature> expectedFeatures = getExpectedFeatures(bookDefinition);
        expectedFeatures.add(new Feature("AnnosSource", "FullyQualifiedTitleId/v1"));
        assertTrue(CollectionUtils.isEqualCollection(features, expectedFeatures));
    }
}
