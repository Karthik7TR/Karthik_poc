package com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step.strategy;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;

import java.io.File;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppBookStep;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class IndexXppMetadataStrategyTest {
    @InjectMocks
    private IndexXppMetadataStrategy sut;
    @Mock
    private XslTransformationService xslTransformationService;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private File xslTransformationFile;
    @Mock
    private XppFormatFileSystem xppFormatFileSystem;
    @Mock
    private Transformer transformer;

    private File inputFile;
    private File outputFile;
    private String materialNumber = "88005553535";
    @Mock
    private XppBookStep step;

    @Before
    public void setup() {
        inputFile = new File("1002-ETCD_Volume_1_form_Index.DIVXML.main");
        outputFile = new File("output");
    }

    @Test
    public void shouldPerformHandling() {
        //given
        given(transformerBuilderFactory.create()
            .withXsl(xslTransformationFile)
            .build()).willReturn(transformer);
        given(xppFormatFileSystem.getStructureWithMetadataFile(step, materialNumber, inputFile.getName()))
            .willReturn(outputFile);
        //when
        sut.performHandling(inputFile, materialNumber, step);
        //then
        then(transformer).should()
            .setParameter(Matchers.eq(IndexXppMetadataStrategy.MATERIAL_NUMBER), Matchers.eq(materialNumber));
        then(transformer).should()
            .setParameter(
                Matchers.eq(IndexXppMetadataStrategy.INDEX_ID),
                Matchers.eq(IndexXppMetadataStrategy.INDEX_ID_FORM_INDEX));
        then(transformer).should()
            .setParameter(
                Matchers.eq(IndexXppMetadataStrategy.INDEX_NAME),
                Matchers.eq(IndexXppMetadataStrategy.INDEX_NAME_FORM_INDEX));
        then(xslTransformationService).should()
            .transform(any());
    }
}
