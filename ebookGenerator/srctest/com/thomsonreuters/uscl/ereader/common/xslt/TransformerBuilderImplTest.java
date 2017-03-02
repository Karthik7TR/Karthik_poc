package com.thomsonreuters.uscl.ereader.common.xslt;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class TransformerBuilderImplTest
{
    private static final String XSL = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + " <xsl:stylesheet version=\"2.0\""
        + " xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns=\"http://www.w3.org/1999/xhtml\""
        + " xmlns:x=\"http://www.sdl.com/xpp\" exclude-result-prefixes=\"x\"></xsl:stylesheet>";

    @InjectMocks
    private TransformerBuilderImpl builder;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File xsl;
    private File incorrectXsl;

    @Before
    public void setUp() throws IOException
    {
        incorrectXsl = new File(temporaryFolder.getRoot(), "incorrect.xsl");
        xsl = new File(temporaryFolder.getRoot(), "correct.xsl");
        FileUtils.writeStringToFile(xsl, XSL);
    }

    @Test
    public void shouldCreateTransformer()
    {
        //given
        builder.create(xsl);
        //when
        final Transformer transformer = builder.build();
        //then
        assertThat(transformer, notNullValue());
    }

    @Test
    public void shouldThrowExceptionIfCannotCreate()
    {
        //given
        thrown.expect(XslTransformationException.class);
        //when
        builder.create(incorrectXsl);
        //then
    }
}
