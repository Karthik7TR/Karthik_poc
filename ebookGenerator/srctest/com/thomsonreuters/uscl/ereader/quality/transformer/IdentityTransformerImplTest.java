package com.thomsonreuters.uscl.ereader.quality.transformer;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import java.io.File;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class IdentityTransformerImplTest {
    private IdentityTransformer sut;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private XslTransformationService transformationService;
    @Mock
    private TransformerBuilderFactory transformerBuilderFactory;
    @Mock
    private TransformerBuilder mockBuilder;
    @Mock
    private Transformer mockTransformer;
    @Mock
    private File mockXsl;

    private File input;

    @Before
    @SneakyThrows
    public void setup() {
        input = temporaryFolder.newFile();
        given(transformerBuilderFactory.create()).willReturn(mockBuilder);
        given(mockBuilder.withXsl(eq(mockXsl))).willReturn(mockBuilder);
        doReturn(mockTransformer).when(mockBuilder)
            .build();
        doAnswer(invocationOnMock -> new File(input.getAbsolutePath() + "_temp").createNewFile())
            .when(transformationService)
            .transform(any());
        sut = new IdentityTransformerImpl(mockXsl, transformationService, transformerBuilderFactory);
    }

    @After
    public void cleanup() {
        temporaryFolder.delete();
    }

    @Test
    @SneakyThrows
    public void shouldTransform() {
        final long timeCreated = input.lastModified();
        //Sleep for a bit to make sure lastModified() returns different result
        //if the file is indeed modified
        Thread.sleep(2);
        sut.transform(input);
        final long timeModified = input.lastModified();
        assertThat(timeModified, greaterThan(timeCreated));
    }
}
