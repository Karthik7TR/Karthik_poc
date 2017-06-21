package com.thomsonreuters.uscl.ereader.xpp.transformation.place.xpp.metadata.step.strategy;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Place Xpp metadata tags to FrontMatter document of bundle main content files
 */
@Component
public class FrontMatterPlaceXppMetadataStrategy implements PlaceXppMetadataStrategy
{
    private static final Set<BundleFileType> TYPES = new HashSet<>(Arrays.asList(BundleFileType.FRONT));

    private final XslTransformationService xslTransformationService;
    private final TransformerBuilderFactory transformerBuilderFactory;
    private final XppFormatFileSystem xppFormatFileSystem;
    private final File xslTransformationFile;

    @Autowired
    public FrontMatterPlaceXppMetadataStrategy(
        final XslTransformationService xslTransformationService,
        final TransformerBuilderFactory transformerBuilderFactory,
        final XppFormatFileSystem xppFormatFileSystem,
        @Value("${xpp.extract.toc.xsl}"/*TODO: <-- put xslt file key here*/) final File xslTransformationFile)
    {
        this.xslTransformationService = xslTransformationService;
        this.transformerBuilderFactory = transformerBuilderFactory;
        this.xppFormatFileSystem = xppFormatFileSystem;
        this.xslTransformationFile = xslTransformationFile;
    }

    @Override
    public Set<BundleFileType> getBundleFileTypes()
    {
        return TYPES;
    }

    @Override
    public void performHandling(final File inputFile, final String materialNumber, final BookStep bookStep)
    {
        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(xslTransformationFile)
            .build();
        xslTransformationService.transform(
            transformer, inputFile, xppFormatFileSystem.getStructureWithMetadataFile(bookStep, materialNumber, inputFile.getName()));
    }
}
