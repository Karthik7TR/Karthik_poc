package com.thomsonreuters.uscl.ereader.common.xslt;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("xslTransformationService")
public class XslTransformationServiceImpl implements XslTransformationService
{
    private static final Logger LOG = LogManager.getLogger(XslTransformationServiceImpl.class);
    private static final String START_WRAPPER_TAG = "<Document>";
    private static final String END_WRAPPER_TAG = "</Document>";

    @Override
    public void transform(@NotNull final Transformer transformer, @NotNull final File input, @NotNull final File output)
    {
        try
        {
            final InputStream inputStream = FileUtils.openInputStream(input);
            innerTransform(transformer, inputStream, output, input.getAbsolutePath());
        }
        catch (final IOException e)
        {
            final String message = String.format("Transformation error. Cannot open file %s", input.getAbsolutePath());
            LOG.error(message, e);
            throw new XslTransformationException(message, e);
        }
    }

    @Override
    public void transform(
        @NotNull final Transformer transformer,
        @NotNull final List<File> input,
        @NotNull final File output)
    {
        try
        {
            Assert.isTrue(!input.isEmpty(), "List of input files should not be empty");

            final String inputPath = input.get(0).getParentFile().getAbsolutePath();
            final List<InputStream> inputStreams = combineToWrappedListOfStreams(input);
            final SequenceInputStream sequenceInputStream =
                new SequenceInputStream(Collections.enumeration(inputStreams));
            innerTransform(transformer, sequenceInputStream, output, inputPath);
        }
        catch (final IOException e)
        {
            final String message = "Transformation error. Cannot open file";
            LOG.error(message, e);
            throw new XslTransformationException(message, e);
        }
    }

    private void innerTransform(
        final Transformer transformer,
        final InputStream inputStream,
        final File output,
        final String inputPath)
    {
        try (final InputStream input = inputStream)
        {
            final long start = System.currentTimeMillis();

            final Source source = new StreamSource(input);
            final File out = output.isDirectory() ? getTempOutputFile(output) : output;
            final Result result = new StreamResult(out);
            transformer.transform(source, result);

            if (getTempOutputFile(output).exists())
            {
                FileUtils.forceDelete(getTempOutputFile(output));
            }

            final long end = System.currentTimeMillis();
            final long totalTime = end - start;
            LOG.debug(String.format("Transformed to %s. Total time: %dms", output.getAbsolutePath(), totalTime));
        }
        catch (final IOException | TransformerException e)
        {
            final String message =
                String.format("Transformation error. Cannot transform %s to %s", inputPath, output.getAbsolutePath());
            LOG.error(message, e);
            throw new XslTransformationException(message, e);
        }
    }

    private List<InputStream> combineToWrappedListOfStreams(final List<File> input) throws FileNotFoundException
    {
        final List<InputStream> inputStreams = new ArrayList<>();
        inputStreams.add(new ByteArrayInputStream(START_WRAPPER_TAG.getBytes()));
        for (final File inputFile : input)
        {
            inputStreams.add(new FileInputStream(inputFile));
        }
        inputStreams.add(new ByteArrayInputStream(END_WRAPPER_TAG.getBytes()));
        return inputStreams;
    }

    private File getTempOutputFile(final File output)
    {
        return new File(output, "temp");
    }
}
