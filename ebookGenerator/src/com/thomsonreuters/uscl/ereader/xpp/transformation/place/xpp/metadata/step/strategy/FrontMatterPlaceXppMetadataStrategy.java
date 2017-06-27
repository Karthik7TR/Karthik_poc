package com.thomsonreuters.uscl.ereader.xpp.transformation.place.xpp.metadata.step.strategy;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilderFactory;
import com.thomsonreuters.uscl.ereader.common.xslt.XslTransformationService;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppBookStep;
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
        @Value("${xpp.add.metadata.to.frontmatter.xsl}") final File xslTransformationFile)
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
    public void performHandling(final File inputFile, final String materialNumber, final XppBookStep bookStep)
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(xslTransformationFile).build();
        final int volumeIndex = getBundleIndexByMaterialNumber(bookStep.getXppBundles(), materialNumber);
        transformer.setParameter("volumeName", String.format("vol%s", volumeIndex + 1));
        xslTransformationService.transform(
            transformer,
            inputFile,
            xppFormatFileSystem.getStructureWithMetadataFile(bookStep, materialNumber, inputFile.getName()));
    }

    private int getBundleIndexByMaterialNumber(final List<XppBundle> xppBundles, final String materialNumber)
    {
        for (int i = 0; i < xppBundles.size(); i++)
        {
            if (materialNumber.equals(xppBundles.get(i).getMaterialNumber()))
                return i;
        }
        throw new IllegalArgumentException(
            String.format("Book does not contain bundle with material number %s", materialNumber));
    }
}
