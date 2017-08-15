package com.thomsonreuters.uscl.ereader.common.xslt;

import static org.apache.commons.io.FileUtils.forceDelete;
import static org.apache.commons.io.FileUtils.openInputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    private static final String DOCTYPE = "<!DOCTYPE root SYSTEM \"%s\">%n";

    @Override
    public void transform(@NotNull final TransformationCommand command)
    {
        try
        {
            transformInner(command);
        }
        catch (final IOException | TransformerException e)
        {
            final String message = String.format(
                "Transformation error. Cannot transform %s to %s",
                command.getInputPath(),
                command.getOutputFile().getAbsolutePath());
            LOG.error(message, e);
            throw new XslTransformationException(message, e);
        }
    }

    private void transformInner(@NotNull final TransformationCommand command) throws IOException, TransformerException
    {
        final long start = System.currentTimeMillis();
        final File output = command.getOutputFile();
        final File tempInputFile = Files.createTempFile(command.getOutputFile().getName(), "input").toFile();
        final File tempOutputFile = new File(output, "temp");

        try (final InputStream inputStream = getInputStream(command, tempInputFile))
        {
            final File out = output.isDirectory() ? tempOutputFile : output;
            command.getTransformer().transform(new StreamSource(inputStream), new StreamResult(out));
            final long duration = System.currentTimeMillis() - start;
            LOG.debug(String.format("Transformed to %s. Total time: %dms", output.getAbsolutePath(), duration));
        }
        finally
        {
            if (tempInputFile.exists())
                forceDelete(tempInputFile);
            if (tempOutputFile.exists())
                forceDelete(tempOutputFile);
        }
    }

    private InputStream getInputStream(final TransformationCommand command, final File tempInputFile) throws IOException
    {
        if (command.getDtdFile() != null)
        {
            final List<InputStream> inputStreams = new ArrayList<>();
            final String dtdDefinition = String.format(DOCTYPE, command.getDtdFile().getAbsolutePath());
            inputStreams.add(new ByteArrayInputStream(dtdDefinition.getBytes()));
            inputStreams.add(getInputWithoutXmlDeclaration(command, tempInputFile));
            return new SequenceInputStream(Collections.enumeration(inputStreams));
        }
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

    private InputStream getInputWithoutXmlDeclaration(final TransformationCommand command, final File tempInputFile)
        throws IOException
    {
        final FileInputStream openInputStream = openInputStream(command.getInputFile());
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(openInputStream, "UTF-8"));
            final BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempInputFile), StandardCharsets.UTF_8)))
        {
            final String firstLine = reader.readLine();
            if (firstLine != null && !firstLine.contains("<?xml "))
            {
                writer.write(firstLine + System.getProperty("line.separator"));
            }
            String currentLine;
            while ((currentLine = reader.readLine()) != null)
            {
                writer.write(currentLine + System.getProperty("line.separator"));
            }
        }
        return openInputStream(tempInputFile);
    }
}
