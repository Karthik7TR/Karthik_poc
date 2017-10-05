package com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step.strategy;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppBookStep;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPlaceXppMetadataTransformStrategy implements PlaceXppMetadataStrategy {
    private final XslTransformationService xslTransformationService;
    protected final TransformerBuilderFactory transformerBuilderFactory;
    private final Set<BundleFileType> bundleFileTypes;

    protected AbstractPlaceXppMetadataTransformStrategy(
        @NotNull final XslTransformationService xslTransformationService,
        @NotNull final TransformerBuilderFactory transformerBuilderFactory,
        @NotNull final Set<BundleFileType> bundleFileTypes) {
        this.xslTransformationService = xslTransformationService;
        this.transformerBuilderFactory = transformerBuilderFactory;
        this.bundleFileTypes = bundleFileTypes;
    }

    @NotNull
    @Override
    public Set<BundleFileType> getBundleFileTypes() {
        return bundleFileTypes;
    }

    @Override
    public void performHandling(
        @NotNull final File inputFile,
        @NotNull final String materialNumber,
        @NotNull final XppBookStep step) {
        for (final TransformationCommand command : getTransformationCommands(inputFile, materialNumber, step)) {
            final Transformer transformer = command.getTransformer();
            transformer.setParameter("volumeName", materialNumber);
            xslTransformationService.transform(command);
        }
    }

    @NotNull
    protected abstract List<TransformationCommand> getTransformationCommands(
        @NotNull File inputFile,
        @NotNull String materialNumber,
        @NotNull XppBookStep bookStep);
}
