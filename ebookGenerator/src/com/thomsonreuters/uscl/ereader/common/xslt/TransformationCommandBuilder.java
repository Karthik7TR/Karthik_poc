package com.thomsonreuters.uscl.ereader.common.xslt;

import java.io.File;
import java.util.Collection;

import javax.xml.transform.Transformer;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public final class TransformationCommandBuilder
{
    @NotNull
    private TransformationCommand command;

    public TransformationCommandBuilder(@NotNull final Transformer transformer, final File outputFile)
    {
        command = new TransformationCommand(transformer, outputFile);
    }

    public TransformationCommandBuilder withInput(@NotNull final File inputFile)
    {
        command.setInputFile(inputFile);
        command.setInputFiles(null);
        return this;
    }

    public TransformationCommandBuilder withInput(@NotNull final Collection<File> inputFiles)
    {
        Assert.isTrue(!inputFiles.isEmpty(), "List of input files should not be empty");
        command.setInputFile(null);
        command.setInputFiles(inputFiles);
        return this;
    }

    public TransformationCommandBuilder withDtd(@NotNull final File dtdFile)
    {
        command.setDtdFile(dtdFile);
        return this;
    }

    @NotNull
    public TransformationCommand build()
    {
        Assert.notNull(command.getOutputFile());
        if (command.getInputFile() == null && command.getInputFiles() == null)
        {
            throw new XslTransformationException("Set either input file or input files for TransformationCommand");
        }
        return command;
    }
}
