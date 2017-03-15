package com.thomsonreuters.uscl.ereader.common.xslt;

import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class XslTransformationServiceImplTest
{
    @InjectMocks
    private XslTransformationServiceImpl service;
    @Mock
    private Transformer transformer;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File input;
    private File input2;
    private File output;

    @Before
    public void setUp()
    {
        input = new File(temporaryFolder.getRoot(), "input.xsl");
        input2 = new File(temporaryFolder.getRoot(), "input2.xsl");
        output = new File(temporaryFolder.getRoot(), "output.xsl");
    }

    @Test
    public void shouldThrowExceptionIfCannotOpenFile()
    {
        //given
        thrown.expect(XslTransformationException.class);
        //when
        service.transform(transformer, input, output);
        //then
    }

    @Test
    public void shouldTransform() throws TransformerException, IOException
    {
        //given
        input.createNewFile();
        output.createNewFile();
        //when
        service.transform(transformer, input, output);
        //then
        then(transformer).should().transform(any(Source.class), any(Result.class));
    }

    @Test
    public void shouldTransformIfOutputIsDirectory() throws TransformerException, IOException
    {
        //given
        input.createNewFile();
        output = temporaryFolder.getRoot();
        //when
        service.transform(transformer, input, output);
        //then
        then(transformer).should().transform(any(Source.class), any(Result.class));
    }

    @Test
    public void shouldTransformIfMultipleFiles() throws TransformerException, IOException
    {
        //given
        input.createNewFile();
        input2.createNewFile();
        output = temporaryFolder.getRoot();
        //when
        service.transform(transformer, Arrays.asList(input, input2), output);
        //then
        then(transformer).should().transform(any(Source.class), any(Result.class));
    }

    @Test
    public void shouldThrowExceptionIfCannotOpenOneOfFiles()
    {
        //given
        thrown.expect(XslTransformationException.class);
        //when
        service.transform(transformer, Arrays.asList(input, input2), output);
        //then
    }

    @Test
    public void shouldThrowExceptionIfCannotTransform() throws TransformerException, IOException
    {
        //given
        input.createNewFile();
        input2.createNewFile();
        thrown.expect(XslTransformationException.class);
        doThrow(new TransformerException("")).when(transformer).transform(any(Source.class), any(Result.class));
        //when
        service.transform(transformer, Arrays.asList(input, input2), output);
        //then
    }

    @Test
    public void shouldThrowExceptionIfNoInputFiles()
    {
        //given
        thrown.expect(IllegalArgumentException.class);
        //when
        service.transform(transformer, Collections.<File>emptyList(), output);
        //then
    }
}
