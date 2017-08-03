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
public class IndexPlaceXppMetadataStrategy extends AbstractPlaceXppMetadataTransformStrategy
{
    private final File xslTransformationFile;
    private final File xslIndexBreakTransformationFile;
    private final XppFormatFileSystem xppFormatFileSystem;

    @Autowired
    public IndexPlaceXppMetadataStrategy(
        final XslTransformationService xslTransformationService,
        final TransformerBuilderFactory transformerBuilderFactory,
        final XppFormatFileSystem xppFormatFileSystem,
        @Value("${xpp.add.metadata.to.index.xsl}") final File xslTransformationFile,
        @Value("${xpp.index.toc.breaks.xsl}") final File xslIndexBreakTransformationFile)
    {
        super(xslTransformationService, transformerBuilderFactory, new HashSet<>(Arrays.asList(BundleFileType.INDEX)));
        this.xslTransformationFile = xslTransformationFile;
        this.xslIndexBreakTransformationFile = xslIndexBreakTransformationFile;
        this.xppFormatFileSystem = xppFormatFileSystem;
    }

    @NotNull
    @Override
    protected List<TransformationCommand> getTransformationCommands(
        @NotNull final File inputFile,
        @NotNull final String materialNumber,
        @NotNull final XppBookStep step)
    {
        final Transformer indexBreakTransformer =
            transformerBuilderFactory.create().withXsl(xslIndexBreakTransformationFile).build();
        final File indexBreakFile =
            xppFormatFileSystem.getIndexBreaksFile(step, materialNumber, "indexBreaks-" + inputFile.getName());

        final Transformer transformer = transformerBuilderFactory.create().withXsl(xslTransformationFile).build();
        transformer.setParameter("indexBreaksDoc", indexBreakFile.getAbsolutePath().replace("\\", "/"));
        final File outputFile =
            xppFormatFileSystem.getStructureWithMetadataFile(step, materialNumber, inputFile.getName());

        final TransformationCommand indexBreakCommand =
            new TransformationCommandBuilder(indexBreakTransformer, indexBreakFile).withInput(inputFile).build();
        final TransformationCommand addMetadataToIndexCommand =
            new TransformationCommandBuilder(transformer, outputFile).withInput(inputFile).build();
        return asList(indexBreakCommand, addMetadataToIndexCommand);
    }
}
