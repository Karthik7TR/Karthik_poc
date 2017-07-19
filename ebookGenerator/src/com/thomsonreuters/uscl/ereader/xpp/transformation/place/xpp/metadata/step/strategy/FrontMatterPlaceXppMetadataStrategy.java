package com.thomsonreuters.uscl.ereader.xpp.transformation.place.xpp.metadata.step.strategy;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppBookStep;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Place Xpp metadata tags to FrontMatter document of bundle main content files
 */
@Component
public class FrontMatterPlaceXppMetadataStrategy extends AbstractPlaceXppMetadataTransformStrategy
{
    private final File xslTransformationFile;
    private final XppFormatFileSystem xppFormatFileSystem;

    @Autowired
    public FrontMatterPlaceXppMetadataStrategy(
        final XslTransformationService xslTransformationService,
        final TransformerBuilderFactory transformerBuilderFactory,
        final XppFormatFileSystem xppFormatFileSystem,
        @Value("${xpp.add.metadata.to.frontmatter.xsl}") final File xslTransformationFile)
    {
        super(xslTransformationService, transformerBuilderFactory, new HashSet<>(Arrays.asList(BundleFileType.FRONT)));
        this.xslTransformationFile = xslTransformationFile;
        this.xppFormatFileSystem = xppFormatFileSystem;
    }

    @NotNull
    @Override
    protected TransformationUnit[] getTransformationUnits(@NotNull final TransformerBuilderFactory transformerBuilderFactory,
                                                          @NotNull final File inputFile,
                                                          @NotNull final String materialNumber,
                                                          @NotNull final XppBookStep bookStep)
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(xslTransformationFile).build();
        final File outputFile = xppFormatFileSystem.getStructureWithMetadataFile(bookStep, materialNumber, inputFile.getName());
        return new TransformationUnit[]{new TransformationUnit(inputFile, outputFile, transformer)};
    }
}
