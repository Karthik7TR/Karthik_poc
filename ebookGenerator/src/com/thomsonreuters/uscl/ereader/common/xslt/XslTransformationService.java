package com.thomsonreuters.uscl.ereader.common.xslt;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.transform.Transformer;

import org.jetbrains.annotations.NotNull;

/**
 * Performs XSLT transformation
 */
public interface XslTransformationService
{
    /**
     * Transform source file to output file using transformer
     * @param transformer XSLT transformer
     * @param input input file
     * @param output output file
     */
    void transform(@NotNull Transformer transformer, @NotNull File input, @NotNull File output);

    /**
     * Transform list of source input files to output file using transformer
     * @param transformer XSLT transformer
     * @param input input file
     * @param output output file
     */
    void transform(@NotNull Transformer transformer, @NotNull Collection<File> input, @NotNull File output);

    /**
     * Transform list of source input streams to output file using transformer
     * @param transformer XSLT transformer
     * @param inputStreams input streams
     * @param inputPath for logging purposes
     * @param output output file
     */
    void transform(@NotNull Transformer transformer, @NotNull Collection<InputStream> inputStreams, @NotNull String inputPath, @NotNull File output);

}
