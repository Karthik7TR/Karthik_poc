package com.thomsonreuters.uscl.ereader.xpp.transformation.metadata.step.strategy;

import static java.util.Arrays.asList;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppBookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LrreMetadataStrategy extends AbstractPlaceXppMetadataTransformStrategy
{
    private final File xslTransformationFile;
    private final XppFormatFileSystem xppFormatFileSystem;

    @Autowired
    public LrreMetadataStrategy(
        final XslTransformationService xslTransformationService,
        final TransformerBuilderFactory transformerBuilderFactory,
        final XppFormatFileSystem xppFormatFileSystem,
        @Value("${xpp.add.metadata.to.lrre.xsl}") final File xslTransformationFile)
    {
        super(
            xslTransformationService,
            transformerBuilderFactory,
            new HashSet<>(Arrays.asList(BundleFileType.TABLE_OF_LRRE, BundleFileType.TABLE_OF_ADDED_LRRE)));
        this.xslTransformationFile = xslTransformationFile;
        this.xppFormatFileSystem = xppFormatFileSystem;
    }

    @NotNull
    @Override
    protected List<TransformationCommand> getTransformationCommands(
        @NotNull final File inputFile,
        @NotNull final String materialNumber,
        @NotNull final XppBookStep step)
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(xslTransformationFile).build();
        final File outputFile =
            xppFormatFileSystem.getStructureWithMetadataFile(step, materialNumber, inputFile.getName());

        final TransformationCommand addMetadataToLrreCommand =
            new TransformationCommandBuilder(transformer, outputFile).withInput(inputFile).build();
        return asList(addMetadataToLrreCommand);
    }
}
