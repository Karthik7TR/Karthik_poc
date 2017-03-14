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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service("xslTransformationService")
public class XslTransformationServiceImpl implements XslTransformationService
{
    private static final Logger LOG = LogManager.getLogger(XslTransformationServiceImpl.class);
    private static final String START_WRAPPER_TAG = "<Document>";
    private static final String END_WRAPPER_TAG = "</Document>";

    @Override
    public void transform(final Transformer transformer, final File input, final File output)
    {
        try (final InputStream inputStream = FileUtils.openInputStream(input))
        {
            LOG.debug(String.format("Transforming %s.", input.getAbsolutePath()));
            transform(transformer, inputStream, output);
        }
        catch (final IOException | TransformerException e)
        {
            final String message = String.format(
                "Transformation error. Cannot transform %s to %s",
                input.getAbsolutePath(),
                output.getAbsolutePath());
            LOG.error(message, e);
            throw new XslTransformationException(message, e);
        }
    }

    @Override
    public void transform(final Transformer transformer, final List<File> input, final File output)
    {
        if (!CollectionUtils.isEmpty(input))
        {
            LOG.debug(String.format("Transforming %s.", input.get(0).getParentFile().getAbsolutePath()));
            try
            {
                final List<InputStream> inputStreams = combineToWrappedListOfStreams(input);

                try (final SequenceInputStream sequenceInputStream = new SequenceInputStream(Collections.enumeration(inputStreams)))
                {
                    transform(transformer, sequenceInputStream, output);
                }
            }
            catch (final IOException | TransformerException e)
            {
                final String message = String.format(
                    "Transformation error. Cannot transform %s to %s",
                    input.get(0).getParentFile().getAbsolutePath(),
                    output.getAbsolutePath());
                LOG.error(message, e);
                throw new XslTransformationException(message, e);
            }
        }
    }

    private void transform(final Transformer transformer, final InputStream inputStream, final File output) throws TransformerException, IOException
    {
        final long start = System.currentTimeMillis();

        final Source source = new StreamSource(inputStream);
        final File out = output.isDirectory() ? getTempOutputFile(output) : output;
        final Result result = new StreamResult(out);
        transformer.transform(source, result);

        if (getTempOutputFile(output).exists())
        {
            FileUtils.forceDelete(getTempOutputFile(output));
        }

        final long end = System.currentTimeMillis();
        final long totalTime = end - start;
        LOG.debug(
            String.format(
                "Transformed to %s. Total time: %dms",
                output.getAbsolutePath(),
                totalTime));
    }

    private List<InputStream> combineToWrappedListOfStreams(final List<File> input) throws FileNotFoundException
    {
        final List<InputStream> inputStreams = new ArrayList<>();
        inputStreams.add(new ByteArrayInputStream(START_WRAPPER_TAG.getBytes()));
        for (final File html : input)
        {
            inputStreams.add(new FileInputStream(html));
        }
        inputStreams.add(new ByteArrayInputStream(END_WRAPPER_TAG.getBytes()));
        return inputStreams;
    }

    private File getTempOutputFile(final File output)
    {
        return new File(output, "temp");
    }
}
