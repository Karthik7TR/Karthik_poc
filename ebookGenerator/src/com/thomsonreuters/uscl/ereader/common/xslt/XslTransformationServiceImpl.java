package com.thomsonreuters.uscl.ereader.common.xslt;

import static org.apache.commons.io.FileUtils.forceDelete;
import static org.apache.commons.io.FileUtils.openInputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service("xslTransformationService")
public class XslTransformationServiceImpl implements XslTransformationService
{
    private static final Logger LOG = LogManager.getLogger(XslTransformationServiceImpl.class);
    private static final String START_WRAPPER_TAG = "<Document>";
    private static final String END_WRAPPER_TAG = "</Document>";

    @Override
    public void transform(@NotNull final TransformationCommand command)
    {
        final long start = System.currentTimeMillis();
        final File output = command.getOutputFile();
        try (final InputStream inputStream = getInputStream(command))
        {
            final File tempOutputFile = new File(output, "temp");
            final File out = output.isDirectory() ? tempOutputFile : output;
            command.getTransformer().transform(new StreamSource(inputStream), new StreamResult(out));

            if (tempOutputFile.exists())
            {
                forceDelete(tempOutputFile);
            }
            LOG.debug(
                String.format(
                    "Transformed to %s. Total time: %dms",
                    output.getAbsolutePath(),
                    System.currentTimeMillis() - start));
        }
        catch (final IOException | TransformerException e)
        {
            final String message = String.format(
                "Transformation error. Cannot transform %s to %s",
                command.getInputPath(),
                output.getAbsolutePath());
            LOG.error(message, e);
            throw new XslTransformationException(message, e);
        }
    }

    private InputStream getInputStream(final TransformationCommand command) throws IOException
    {
        if (command.isMultiInput())
        {
            final List<InputStream> inputStreams = new ArrayList<>();
            inputStreams.add(new ByteArrayInputStream(START_WRAPPER_TAG.getBytes()));
            for (final File inputFile : command.getInputFiles())
            {
                inputStreams.add(new FileInputStream(inputFile));
            }
            inputStreams.add(new ByteArrayInputStream(END_WRAPPER_TAG.getBytes()));
            return new SequenceInputStream(Collections.enumeration(inputStreams));
        }
        else
        {
            return openInputStream(command.getInputFile());
        }
    }
}
