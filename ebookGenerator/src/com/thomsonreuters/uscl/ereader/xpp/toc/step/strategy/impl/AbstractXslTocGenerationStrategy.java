package com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.impl;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.TocGenerationStrategy;
import com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.type.BundleFileType;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract class, contains common logic to generate TOC via xslt.
 */
public abstract class AbstractXslTocGenerationStrategy implements TocGenerationStrategy
{
    private final Set<BundleFileType> bundleFileTypes;
    private final XslTransformationService xslTransformationService;
    private final TransformerBuilderFactory transformerBuilderFactory;

    /**
     * @param bundleFileTypes - DIVXML component types, instance responsible for generation of TOC from DIVXML file of this types
     * @param xslTransformationService
     * @param transformerBuilderFactory
     */
    protected AbstractXslTocGenerationStrategy(final XslTransformationService xslTransformationService,
                                               final TransformerBuilderFactory transformerBuilderFactory,
                                               final BundleFileType ... bundleFileTypes)
    {
        this.xslTransformationService = xslTransformationService;
        this.transformerBuilderFactory = transformerBuilderFactory;
        this.bundleFileTypes = new HashSet<>();
        fillBundleFileTypes(bundleFileTypes);
    }

    private void fillBundleFileTypes(final BundleFileType ... bundleFileTypes)
    {
        if (bundleFileTypes.length == 0)
        {
            throw new IllegalArgumentException("At least one bundle file type must be provided, but found null");
        }

        for (final BundleFileType type : bundleFileTypes)
        {
            this.bundleFileTypes.add(requireNonNull(type, "Provided bundle file type must not be null"));
        }
    }

    @Override
    public void performTocGeneration(@NotNull final String bundleFileName, @NotNull final XppBundle bundle, @NotNull final BookStep bookStep)
    {
        final TransformationUnit[] transformationUnits = getTransformationUnits();
        for (final TransformationUnit unit : transformationUnits)
        {
            xslTransformationService.transform(unit.createTransformer(bundle, transformerBuilder(), bookStep),
                                               unit.getInputFiles(bundleFileName, bundle, bookStep),
                                               unit.getOutputFile(bundleFileName, bundle, bookStep));
        }
    }

    /**
     * Get transformation units, number of units equal number of transformations
     * Transformation units have to be ordered
     * @return
     */
    protected abstract TransformationUnit[] getTransformationUnits();

    /**
     * Creates transformBuilder, to build new transformer
     */
    private TransformerBuilder transformerBuilder()
    {
        return transformerBuilderFactory.create();
    }

    @NotNull
    @Override
    public Set<BundleFileType> getBundleFileTypes()
    {
        return bundleFileTypes;
    }

    /**
     * Unit of transformation, provide needed transformation data, Like I/O files, transformer params etc.
     */
    protected interface TransformationUnit
    {
        /**
         * Create xsl transformer, set xsl file, params etc.
         */
        @NotNull
        Transformer createTransformer(@NotNull XppBundle bundle, @NotNull TransformerBuilder builder, @NotNull BookStep step);

        /**
         * Get File representation of input DIVXML file(s)
         */
        @NotNull
        Collection<File> getInputFiles(@NotNull String bundleFileName, @NotNull XppBundle bundle, @NotNull BookStep step);

        /**
         * Get File representation of output file
         */
        @NotNull
        File getOutputFile(@NotNull String fileName, @NotNull XppBundle bundle, @NotNull BookStep step);
    }
}
