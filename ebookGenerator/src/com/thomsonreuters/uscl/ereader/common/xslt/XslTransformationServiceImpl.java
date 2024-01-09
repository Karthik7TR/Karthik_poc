package com.thomsonreuters.uscl.ereader.common.xslt;

import static org.apache.commons.io.FileUtils.forceDelete;
import static org.apache.commons.io.FileUtils.openInputStream;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service("xslTransformationService")
@Slf4j
public class XslTransformationServiceImpl implements XslTransformationService {
    private static final String START_WRAPPER_TAG = "<Document>";
    private static final String END_WRAPPER_TAG = "</Document>";
    private static final String DOCTYPE = "<!DOCTYPE root SYSTEM \"%s\">%n";
    private static final int BUFFER_SIZE = 1024;

    @Override
    public void transform(@NotNull final TransformationCommand command) {
        try {
            transformInner(command);
        } catch (final IOException | TransformerException e) {
            final String message = String.format(
                "Transformation error. Cannot transform %s to %s",
                command.getInputPath(),
                command.getOutputFile().getAbsolutePath());
            log.error(message, e);
            throw new XslTransformationException(message, e);
        }
    }

    private void transformInner(@NotNull final TransformationCommand command) throws IOException, TransformerException {
        final long start = System.currentTimeMillis();
        final File output = command.getOutputFile();
        final File tempInputFile = Files.createTempFile(command.getOutputFile().getName(), "input").toFile();
        final File tempOutputFile = new File(output, "temp");

        try (final InputStream inputStream = getInputStream(command, tempInputFile)) {
            final File out = output.isDirectory() ? tempOutputFile : output;
            command.getTransformer().transform(new StreamSource(inputStream), new StreamResult(out));
            final long duration = System.currentTimeMillis() - start;
            log.debug(String.format("Transformed to %s. Total time: %dms", output.getAbsolutePath(), duration));
        } finally {
            if (tempInputFile.exists())
                forceDelete(tempInputFile);
            if (tempOutputFile.exists())
                forceDelete(tempOutputFile);
        }
    }

    private InputStream getInputStream(final TransformationCommand command, final File tempInputFile)
        throws IOException {
        final String dtdDefinition =
            command.getDtdFile() != null ? String.format(DOCTYPE, command.getDtdFile().getAbsolutePath()) : null;
        if (command.isMultiInput()) {
            return getMultiFilesStream(command, dtdDefinition);
        } else {
            return getSingleFileInputStream(command, tempInputFile, dtdDefinition);
        }
    }

    private InputStream getMultiFilesStream(final TransformationCommand command, final String dtdDefinition)
        throws FileNotFoundException {
        final List<InputStream> inputStreams = new ArrayList<>();
        if (dtdDefinition != null)
            inputStreams.add(new ByteArrayInputStream(dtdDefinition.getBytes()));
        inputStreams.add(new ByteArrayInputStream(START_WRAPPER_TAG.getBytes()));
        for (final File inputFile : command.getInputFiles()) {
            inputStreams.add(new FileInputStream(inputFile));
        }
        inputStreams.add(new ByteArrayInputStream(END_WRAPPER_TAG.getBytes()));
        return new SequenceInputStream(Collections.enumeration(inputStreams));
    }

    private InputStream getSingleFileInputStream(
        final TransformationCommand command,
        final File tempInputFile,
        final String dtdDefinition) throws IOException {
        if (dtdDefinition == null) {
            return openInputStream(command.getInputFile());
        } else {
            final List<InputStream> inputStreams = new ArrayList<>();
            inputStreams.add(new ByteArrayInputStream(dtdDefinition.getBytes()));
            inputStreams.add(getInputWithoutXmlDeclaration(command, tempInputFile));
            return new SequenceInputStream(Collections.enumeration(inputStreams));
        }
    }

    private InputStream getInputWithoutXmlDeclaration(final TransformationCommand command, final File tempInputFile)
        throws IOException {
        final FileInputStream openInputStream = openInputStream(command.getInputFile());
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(openInputStream, "UTF-8"));
            final BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(tempInputFile), StandardCharsets.UTF_8))) {
            char[] buffer = new char[BUFFER_SIZE];

            reader.read(buffer);
            final String first = new String(buffer);
            final String xmlRemoved = first.replace("<?xml version='1.0' encoding='UTF-8'?>", "")
                .replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
            writer.write(xmlRemoved);
            buffer = new char[BUFFER_SIZE];

            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                writer.write(trimBuffer(buffer, charsRead));
                buffer = new char[BUFFER_SIZE];
            }
        }
        return openInputStream(tempInputFile);
    }

    private char[] trimBuffer(final char[] buffer, final int size) {
        if (size < BUFFER_SIZE && size != 0) {
            final char[] result = new char[size];
            System.arraycopy(buffer, 0, result, 0, size);
            return result;
        }
        return buffer;
    }
}
