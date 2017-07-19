package com.thomsonreuters.uscl.ereader.xpp.transformation.place.xpp.metadata.step.strategy;

import java.io.File;

import javax.xml.transform.Transformer;

import org.jetbrains.annotations.NotNull;

public class TransformationUnit
{
    private final File inputFile;
    private final File outputFile;
    private final Transformer transformer;

    TransformationUnit(@NotNull final File inputFile, @NotNull final File outputFile, @NotNull final Transformer transformer)
    {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.transformer = transformer;
    }

    @NotNull
    public File getInputFile()
    {
        return inputFile;
    }

    @NotNull
    public File getOutputFile()
    {
        return outputFile;
    }

    @NotNull
    public Transformer getTransformer()
    {
        return transformer;
    }
}
