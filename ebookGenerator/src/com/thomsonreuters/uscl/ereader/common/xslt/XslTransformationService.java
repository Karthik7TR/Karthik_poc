package com.thomsonreuters.uscl.ereader.common.xslt;

import java.io.File;
import java.util.List;

import javax.xml.transform.Transformer;

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
    void transform(Transformer transformer, File input, File output);

    /**
     * Transform list of source input files to output file using transformer
     * @param transformer XSLT transformer
     * @param input input file
     * @param output output file
     */
    void transform(Transformer transformer, List<File> input, File output);
}
