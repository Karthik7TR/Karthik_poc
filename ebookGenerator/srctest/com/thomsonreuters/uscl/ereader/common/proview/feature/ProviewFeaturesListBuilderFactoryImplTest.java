package com.thomsonreuters.uscl.ereader.common.proview.feature;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class ProviewFeaturesListBuilderFactoryImplTest {
    private ProviewFeaturesListBuilderFactory proviewFeaturesListBuilderFactory;
    @Mock
    private BookDefinition bookDefinition;

    @Before
    public void onTestSetUp() {
        proviewFeaturesListBuilderFactory = new ProviewFeaturesListBuilderFactoryImpl(null, null);
    }

    @Test
    public void shouldReturnFeaturesListBuilderForSplitBook() {
        //given
        given(bookDefinition.isSplitBook()).willReturn(true);
        //when
        final FeaturesListBuilder actualFeaturesListBuilder = proviewFeaturesListBuilderFactory.create(bookDefinition);
        //then
        assertThat(actualFeaturesListBuilder, instanceOf(SplitBookFeatureListBuilder.class));
    }

    @Test
    public void shouldReturnFeaturesListBuilderForSingleBook() {
        //given
        given(bookDefinition.isSplitBook()).willReturn(false);
        //when
        final FeaturesListBuilder actualFeaturesListBuilder = proviewFeaturesListBuilderFactory.create(bookDefinition);
        //then
        assertThat(actualFeaturesListBuilder, instanceOf(SingleBookFeaturesListBuilder.class));
    }
}
