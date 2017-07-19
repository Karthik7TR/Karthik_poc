package com.thomsonreuters.uscl.ereader.xpp.transformation.place.xpp.metadata.step.strategy;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppBookStep;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPlaceXppMetadataSimpleTransformationStrategy implements PlaceXppMetadataStrategy
{
    private final XslTransformationService xslTransformationService;
    private final TransformerBuilderFactory transformerBuilderFactory;
    private final Set<BundleFileType> bundleFileTypes;

    protected AbstractPlaceXppMetadataSimpleTransformationStrategy(
        @NotNull final XslTransformationService xslTransformationService,
        @NotNull final TransformerBuilderFactory transformerBuilderFactory,
        @NotNull final Set<BundleFileType> bundleFileTypes)
    {
        this.xslTransformationService = xslTransformationService;
        this.transformerBuilderFactory = transformerBuilderFactory;
        this.bundleFileTypes = bundleFileTypes;
    }

    @NotNull
    @Override
    public Set<BundleFileType> getBundleFileTypes()
    {
        return bundleFileTypes;
    }

    @Override
    public void performHandling(@NotNull final File inputFile, @NotNull final String materialNumber, @NotNull final XppBookStep bookStep)
    {
        for (final TransformationUnit unit : getTransformationUnits(transformerBuilderFactory, inputFile, materialNumber, bookStep))
        {
            final Transformer transformer = unit.getTransformer();
            final int volumeIndex = getBundleIndexByMaterialNumber(bookStep.getXppBundles(), materialNumber);
            transformer.setParameter("volumeName", String.format("vol%s", volumeIndex + 1));
            xslTransformationService.transform(transformer, unit.getInputFile(), unit.getOutputFile());
        }
    }

    @NotNull
    protected abstract TransformationUnit[] getTransformationUnits(@NotNull TransformerBuilderFactory transformerBuilderFactory,
                                                                   @NotNull File inputFile,
                                                                   @NotNull String materialNumber,
                                                                   @NotNull XppBookStep bookStep);

    private int getBundleIndexByMaterialNumber(@NotNull final List<XppBundle> xppBundles, @NotNull final String materialNumber)
    {
        for (int i = 0; i < xppBundles.size(); i++)
        {
            if (materialNumber.equals(xppBundles.get(i).getMaterialNumber()))
            {
                return i;
            }
        }
        throw new IllegalArgumentException(
            String.format("Book does not contain bundle with material number %s", materialNumber));
    }
}
