package com.thomsonreuters.uscl.ereader.common.xslt;

import java.io.File;
import java.util.Collection;

import javax.xml.transform.Transformer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TransformationCommand {
    @NotNull
    private final Transformer transformer;
    @Nullable
    private File inputFile;
    @Nullable
    private Collection<File> inputFiles;
    @NotNull
    private File outputFile;
    @Nullable
    private File dtdFile;

    TransformationCommand(@NotNull final Transformer transformer, final File outputFile) {
        this.transformer = transformer;
        this.outputFile = outputFile;
    }

    @NotNull
    public File getInputFile() {
        return inputFile;
    }

    void setInputFile(@Nullable final File inputFile) {
        this.inputFile = inputFile;
    }

    @NotNull
    public Collection<File> getInputFiles() {
        return inputFiles;
    }

    void setInputFiles(@Nullable final Collection<File> inputFiles) {
        this.inputFiles = inputFiles;
    }

    @NotNull
    public File getOutputFile() {
        return outputFile;
    }

    @NotNull
    public Transformer getTransformer() {
        return transformer;
    }

    void setDtdFile(final File dtdFile) {
        this.dtdFile = dtdFile;
    }

    public File getDtdFile() {
        return dtdFile;
    }

    public boolean isMultiInput() {
        return inputFile == null;
    }

    @NotNull
    public String getInputPath() {
        if (isMultiInput()) {
            return inputFiles.iterator().next().getParentFile().getAbsolutePath();
        } else {
            return inputFile.getAbsolutePath();
        }
    }
}
