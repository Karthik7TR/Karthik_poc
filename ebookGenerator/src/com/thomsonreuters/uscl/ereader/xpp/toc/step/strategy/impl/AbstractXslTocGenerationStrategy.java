package com.thomsonreuters.uscl.ereader.xpp.toc.step.strategy.impl;

import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
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
    public void performTocGeneration(@NotNull final BookStep bookStep)
    {
        xslTransformationService.transform(createTransformer(bookStep), getInputFile(bookStep), getOutputFile(bookStep));
    }

    /**
     * Create xsl transformer, set xsl file, params etc.
     */
    @NotNull
    protected abstract Transformer createTransformer(@NotNull BookStep bookStep);

    /**
     * Get File representation of input DIVXML file
     */
    @NotNull
    protected abstract File getInputFile(@NotNull BookStep bookStep);

    /**
     * Get File representatiob of output file
     */
    @NotNull
    protected abstract File getOutputFile(@NotNull BookStep bookStep);

    /**
     * Creates transformBuilder, to build new transformer
     */
    @NotNull
    protected TransformerBuilder transformerBuilder()
    {
        return transformerBuilderFactory.create();
    }

    @NotNull
    @Override
    public Set<BundleFileType> getBundleFileTypes()
    {
        return bundleFileTypes;
    }
}
