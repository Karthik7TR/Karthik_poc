package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.thomsonreuters.uscl.ereader.StepTestUtil;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.toc.group.FileGroupHelper;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.batch.core.scope.context.ChunkContext;

@RunWith(MockitoJUnitRunner.class)
public final class ExtractTocStepTest {
    private static final String FIRST_BUNDLE_FILE_NAME = "1-one_1.DIVXML.xml";
    private static final String FIRST_CHILD_CHAPTER_FILE_NAME = "2-one_1_1A.DIVXML.xml";
    private static final String SECOND_BUNDLE_FILE_NAME = "2-two_2.DIVXML.xml";
    private static final String MATERIAL_NUMBER = "123456";
    private static final String SECOND_MATERIAL_NUMBER = "1234567";

    @InjectMocks
    private ExtractTocStep step;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private XppFormatFileSystem fileSystem;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;
    @Mock
    private XslTransformationService transformationService;
    @Mock
    private XppBundle bundle;
    @Mock
    private BookDefinition bookDefinition;
    @Mock
    private FileGroupHelper fileGroupHelper;
    @Mock
    private File firstTocBundleFile;
    @Mock
    private File secondTocBundleFile;
    @Mock
    private File tocFile;
    @Mock
    private File tocFirstPartFile;
    @Mock
    private File tocSecondPartFile;
    @Mock
    private File sourceFirstBundleFile;
    @Mock
    private File sourceFirstBundleChildChapterFile;
    @Mock
    private File sourceSecondBundleFile;
    @Mock
    private File secondBundleFirstSourceFile;
    @Mock
    private File secondBundleSecondSourceFile;
    @Mock
    private File mergedTocFile;
    @Mock
    private File secondMergedTocFile;
    @Mock
    private File volumesMapFile;
    @Captor
    private ArgumentCaptor<TransformationCommand> commandCaptor;

    @Before
    public void init() throws IOException {
        given(bundle.getOrderedFileList()).willReturn(Arrays.asList(FIRST_BUNDLE_FILE_NAME, SECOND_BUNDLE_FILE_NAME));
        given(bundle.getMaterialNumber()).willReturn(MATERIAL_NUMBER);

        given(bookDefinition.getPrintComponents()).willReturn(Collections.emptySet());
        StepTestUtil.givenBook(chunkContext, bookDefinition);
        StepTestUtil.givenBookBundles(chunkContext, Arrays.asList(bundle));

        given(volumesMapFile.getAbsolutePath()).willReturn(StringUtils.EMPTY);

        given(
            fileSystem.getSectionbreaksFile(step, MATERIAL_NUMBER, FIRST_BUNDLE_FILE_NAME.replaceAll(".xml", ".main")))
                .willReturn(sourceFirstBundleFile);
        given(
            fileSystem.getSectionbreaksFile(step, MATERIAL_NUMBER, SECOND_BUNDLE_FILE_NAME.replaceAll(".xml", ".main")))
                .willReturn(sourceSecondBundleFile);
        given(
                fileSystem.getSectionbreaksFile(step, MATERIAL_NUMBER, FIRST_CHILD_CHAPTER_FILE_NAME.replaceAll(".xml", ".main")))
                .willReturn(sourceFirstBundleChildChapterFile);
        given(
            fileSystem.getSectionbreaksFile(step, SECOND_MATERIAL_NUMBER, FIRST_BUNDLE_FILE_NAME.replaceAll(".xml", ".main")))
                .willReturn(secondBundleFirstSourceFile);
        given(
            fileSystem.getSectionbreaksFile(step, SECOND_MATERIAL_NUMBER, SECOND_BUNDLE_FILE_NAME.replaceAll(".xml", ".main")))
                .willReturn(secondBundleSecondSourceFile);
        given(fileSystem.getBundlePartTocFile(eq(FIRST_BUNDLE_FILE_NAME), anyString(), eq(step)))
            .willReturn(firstTocBundleFile);
        given(fileSystem.getBundlePartTocFile(eq(SECOND_BUNDLE_FILE_NAME), anyString(), eq(step)))
            .willReturn(secondTocBundleFile);
        given(fileSystem.getTocFile(step)).willReturn(tocFile);
        given(fileSystem.getTocPartFile(step, 1)).willReturn(tocFirstPartFile);
        given(fileSystem.getTocPartFile(step, 2)).willReturn(tocSecondPartFile);
        given(fileSystem.getMergedBundleTocFile(MATERIAL_NUMBER, step)).willReturn(mergedTocFile);
        given(fileSystem.getMergedBundleTocFile(SECOND_MATERIAL_NUMBER, step)).willReturn(secondMergedTocFile);
        given(fileSystem.getVolumesMapFile(step)).willReturn(volumesMapFile);

        given(mergedTocFile.createNewFile()).willReturn(true);

        given(fileGroupHelper.isGroupRoot(FIRST_BUNDLE_FILE_NAME, bundle)).willReturn(false);
        given(fileGroupHelper.isGroupPart(FIRST_BUNDLE_FILE_NAME, bundle)).willReturn(false);
        given(fileGroupHelper.isGroupRoot(SECOND_BUNDLE_FILE_NAME, bundle)).willReturn(false);
        given(fileGroupHelper.isGroupPart(SECOND_BUNDLE_FILE_NAME, bundle)).willReturn(false);
    }

    @Test
    public void shouldTransformOneFile() throws Exception {
        //given
        //when
        step.executeTransformation();
        //then
        then(transformationService).should(times(4)).transform(commandCaptor.capture());
        final Iterator<TransformationCommand> iterator = commandCaptor.getAllValues().iterator();

        TransformationCommand command = iterator.next();
        assertThat(command.getInputFile(), is(sourceFirstBundleFile));
        assertThat(command.getOutputFile(), is(firstTocBundleFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), not(nullValue()));

        command = iterator.next();
        assertThat(command.getInputFile(), is(sourceSecondBundleFile));
        assertThat(command.getOutputFile(), is(secondTocBundleFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), not(nullValue()));

        command = iterator.next();
        assertThat(command.getInputFiles(), contains(firstTocBundleFile, secondTocBundleFile));
        assertThat(command.getOutputFile(), is(mergedTocFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), is(IsNull.notNullValue()));

        command = iterator.next();
        assertThat(command.getInputFiles(), contains(mergedTocFile));
        assertThat(command.getOutputFile(), is(tocFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), is(IsNull.notNullValue()));
    }

    @Test
    public void shouldTransformSplitBook() throws Exception {
        //given
        initSplitBookMocks();
        //when
        step.executeTransformation();
        //then
        then(transformationService).should(times(8)).transform(commandCaptor.capture());
        final Iterator<TransformationCommand> iterator = commandCaptor.getAllValues().iterator();

        TransformationCommand command = iterator.next();
        assertThat(command.getInputFile(), is(sourceFirstBundleFile));
        assertThat(command.getOutputFile(), is(firstTocBundleFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), not(nullValue()));

        command = iterator.next();
        assertThat(command.getInputFile(), is(sourceSecondBundleFile));
        assertThat(command.getOutputFile(), is(secondTocBundleFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), not(nullValue()));

        command = iterator.next();
        assertThat(command.getInputFiles(), contains(firstTocBundleFile, secondTocBundleFile));
        assertThat(command.getOutputFile(), is(mergedTocFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), is(IsNull.notNullValue()));

        command = iterator.next();
        assertThat(command.getInputFiles(), contains(mergedTocFile));
        assertThat(command.getOutputFile(), is(tocFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), is(IsNull.notNullValue()));

        command = iterator.next();
        assertThat(command.getInputFile(), is(secondBundleFirstSourceFile));
        assertThat(command.getOutputFile(), is(firstTocBundleFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), not(nullValue()));

        command = iterator.next();
        assertThat(command.getInputFile(), is(secondBundleSecondSourceFile));
        assertThat(command.getOutputFile(), is(secondTocBundleFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), not(nullValue()));

        command = iterator.next();
        assertThat(command.getInputFiles(), contains(firstTocBundleFile, secondTocBundleFile));
        assertThat(command.getOutputFile(), is(secondMergedTocFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), is(IsNull.notNullValue()));

        command = iterator.next();
        assertThat(command.getInputFiles(), contains(secondMergedTocFile));
        assertThat(command.getOutputFile(), is(tocSecondPartFile));
        assertThat(command.getTransformer().getParameter("isPocketPart"), is(IsNull.notNullValue()));
    }

    @Test
    public void shouldExtractTocForRutterChapterBlock() throws Exception {
        //given
        final List<File> inputFiles = Arrays.asList(sourceFirstBundleFile, sourceFirstBundleChildChapterFile);
        final List<String> groupFileNames = Arrays.asList(FIRST_BUNDLE_FILE_NAME, FIRST_CHILD_CHAPTER_FILE_NAME);
        when(fileGroupHelper.isGroupRoot(eq(FIRST_BUNDLE_FILE_NAME), eq(bundle))).thenReturn(true);
        when(fileGroupHelper.isGroupRoot(eq(FIRST_CHILD_CHAPTER_FILE_NAME), eq(bundle))).thenReturn(false);
        when(fileGroupHelper.isGroupPart(eq(FIRST_BUNDLE_FILE_NAME), eq(bundle))).thenReturn(true);
        when(fileGroupHelper.isGroupPart(eq(FIRST_CHILD_CHAPTER_FILE_NAME), eq(bundle))).thenReturn(true);
        when(fileGroupHelper.getGroupFileNames(eq(FIRST_BUNDLE_FILE_NAME), eq(bundle))).thenReturn(groupFileNames);
        when(fileGroupHelper.getGroupFileNames(eq(FIRST_CHILD_CHAPTER_FILE_NAME), eq(bundle))).thenReturn(groupFileNames);
        given(bundle.getOrderedFileList()).willReturn(Arrays.asList(FIRST_BUNDLE_FILE_NAME, FIRST_CHILD_CHAPTER_FILE_NAME));
        //when
        step.executeTransformation();
        //then
        then(transformationService).should(times(3)).transform(commandCaptor.capture());
        final Iterator<TransformationCommand> iterator = commandCaptor.getAllValues().iterator();

        TransformationCommand command = iterator.next();
        assertEquals(inputFiles, command.getInputFiles());
        assertEquals(firstTocBundleFile, command.getOutputFile());

        command = iterator.next();
        assertEquals(Collections.singletonList(firstTocBundleFile), command.getInputFiles());
        assertEquals(mergedTocFile, command.getOutputFile());

        command = iterator.next();
        assertThat(command.getInputFiles(), contains(mergedTocFile));
        assertEquals(tocFile, command.getOutputFile());
    }

    private void initSplitBookMocks() {
        final Set<PrintComponent> printComponents = getMockedCollection(PrintComponent.class, HashSet::new,
            printComponent -> {
                given(printComponent.getSplitter()).willReturn(false);
                given(printComponent.getComponentOrder()).willReturn(1);
                given(printComponent.getMaterialNumber()).willReturn(MATERIAL_NUMBER);
            },
            printComponent -> {
                given(printComponent.getSplitter()).willReturn(true);
                given(printComponent.getComponentOrder()).willReturn(2);
            },
            printComponent -> {
                given(printComponent.getSplitter()).willReturn(false);
                given(printComponent.getComponentOrder()).willReturn(3);
                given(printComponent.getMaterialNumber()).willReturn(SECOND_MATERIAL_NUMBER);
            });
        given(bookDefinition.getPrintComponents()).willReturn(printComponents);
        given(bookDefinition.isSplitBook()).willReturn(true);

        final List<XppBundle> xppBundles = getMockedCollection(XppBundle.class, ArrayList::new,
            xppBundle -> {
                given(xppBundle.getMaterialNumber()).willReturn(MATERIAL_NUMBER);
                given(xppBundle.getOrderedFileList()).willReturn(Arrays.asList(FIRST_BUNDLE_FILE_NAME, SECOND_BUNDLE_FILE_NAME));
            },
            xppBundle -> {
                given(xppBundle.getMaterialNumber()).willReturn(SECOND_MATERIAL_NUMBER);
                given(xppBundle.getOrderedFileList()).willReturn(Arrays.asList(FIRST_BUNDLE_FILE_NAME, SECOND_BUNDLE_FILE_NAME));
            });
        StepTestUtil.givenBookBundles(chunkContext, xppBundles);
    }

    private <M, C extends Collection<M>> C getMockedCollection(final Class<M> elementType,
                                                               final Supplier<C> collectionSupplier,
                                                               final Consumer<M> ... mockBehaviourConsumers) {
        final C collection = collectionSupplier.get();
        M elementMock;

        for (final Consumer<M> mockBehaviourConsumer : mockBehaviourConsumers) {
            elementMock = mock(elementType);
            mockBehaviourConsumer.accept(elementMock);
            collection.add(elementMock);
        }

        return collection;
    }
}
