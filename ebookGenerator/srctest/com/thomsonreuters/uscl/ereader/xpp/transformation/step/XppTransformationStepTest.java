package com.thomsonreuters.uscl.ereader.xpp.transformation.step;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.StepTestUtil;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class XppTransformationStepTest {
    private static final String FIRST_MATERIAL = "11111";
    private static final String SECOND_MATERIAL = "22222";

    @InjectMocks
    private XppTransformationStep xppStep = new XppTransformationStep() {
        @Override
        public void executeTransformation() throws Exception {
            //stub
        }
    };
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private BookDefinition bookDefinition;
    @Mock
    private PrintComponent splitterPrintComponent;
    @Mock
    private PrintComponent firstPrintComponent;
    @Mock
    private PrintComponent secondPrintComponent;
    @Mock
    private XppBundle firstXppBundle;
    @Mock
    private XppBundle secondXppBundle;

    @Before
    public void onTestSetUp() {
        StepTestUtil.givenBook(chunkContext, bookDefinition);
        given(splitterPrintComponent.getSplitter()).willReturn(true);
        given(splitterPrintComponent.getComponentOrder()).willReturn(2);
        given(firstPrintComponent.getSplitter()).willReturn(false);
        given(firstPrintComponent.getComponentOrder()).willReturn(1);
        given(firstPrintComponent.getMaterialNumber()).willReturn(FIRST_MATERIAL);
        given(secondPrintComponent.getSplitter()).willReturn(false);
        given(secondPrintComponent.getComponentOrder()).willReturn(3);
        given(secondPrintComponent.getMaterialNumber()).willReturn(SECOND_MATERIAL);
        given(firstXppBundle.getMaterialNumber()).willReturn(FIRST_MATERIAL);
        given(secondXppBundle.getMaterialNumber()).willReturn(SECOND_MATERIAL);
    }

    @Test
    public void shouldReturnEmptyBundlesList() {
        //given
        StepTestUtil.givenBookBundles(chunkContext, null);
        //when
        final List<XppBundle> bundles = xppStep.getXppBundles();
        //then
        assertThat(bundles, empty());
    }

    @Test
    public void shouldReturnBundlesList() {
        //given
        StepTestUtil.givenBookBundles(chunkContext, Arrays.asList(new XppBundle()));
        //when
        final List<XppBundle> bundles = xppStep.getXppBundles();
        //then
        assertThat(bundles, hasSize(1));
    }

    @Test
    public void shouldReturnMapWithOneEntryIfThereAreNoSplitters() {
        //given
        given(bookDefinition.getPrintComponents()).willReturn(collect(HashSet::new, firstPrintComponent, secondPrintComponent));
        StepTestUtil.givenBookBundles(chunkContext, collect(ArrayList::new, firstXppBundle, secondXppBundle));
        //when
        final Map<Integer, List<XppBundle>> splitParts = xppStep.getSplitPartsBundlesMap();
        //then
        assertThat(splitParts.entrySet(), hasSize(1));
        assertThat(splitParts.get(1), hasSize(2));
        assertThat(splitParts.get(1), contains(firstXppBundle, secondXppBundle));
    }

    @Test
    public void shouldReturnMapWithSeveralEntriesIfThereAreAnySplitters() {
        //given
        given(bookDefinition.getPrintComponents()).willReturn(collect(HashSet::new, firstPrintComponent, splitterPrintComponent, secondPrintComponent));
        StepTestUtil.givenBookBundles(chunkContext, collect(ArrayList::new, firstXppBundle, secondXppBundle));
        //when
        final Map<Integer, List<XppBundle>> splitParts = xppStep.getSplitPartsBundlesMap();
        //then
        assertThat(splitParts.entrySet(), hasSize(2));
        assertThat(splitParts.get(1), hasSize(1));
        assertThat(splitParts.get(1), contains(firstXppBundle));
        assertThat(splitParts.get(2), hasSize(1));
        assertThat(splitParts.get(2), contains(secondXppBundle));
    }

    @Test
    public void shouldReturnXppBundleWithProvidedMaterial() {
        //given
        StepTestUtil.givenBookBundles(chunkContext, collect(ArrayList::new, firstXppBundle, secondXppBundle));
        //when
        final XppBundle firstResultBundle = xppStep.getBundleByMaterial(FIRST_MATERIAL);
        final XppBundle secondResultBundle = xppStep.getBundleByMaterial(SECOND_MATERIAL);
        //then
        assertThat(firstResultBundle, equalTo(firstXppBundle));
        assertThat(secondResultBundle, equalTo(secondXppBundle));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfBundleWithProvidedMaterialNotExists() {
        //given
        StepTestUtil.givenBookBundles(chunkContext, collect(ArrayList::new, firstXppBundle, secondXppBundle));
        //when
        xppStep.getBundleByMaterial("777");
    }

    private <E, C extends Collection<E>> C collect(final Supplier<C> collectionSupplier, final E ... elements) {
        final C collection = collectionSupplier.get();
        Stream.of(elements).forEach(collection::add);
        return collection;
    }
}
