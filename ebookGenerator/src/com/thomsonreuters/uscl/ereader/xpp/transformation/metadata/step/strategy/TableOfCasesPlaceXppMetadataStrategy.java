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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TableOfCasesPlaceXppMetadataStrategy extends AbstractPlaceXppMetadataTransformStrategy
{
    private final File xslTransformationFile;
    private final XppFormatFileSystem xppFormatFileSystem;

    @Autowired
    public TableOfCasesPlaceXppMetadataStrategy(
        final XslTransformationService xslTransformationService,
        final TransformerBuilderFactory transformerBuilderFactory,
        @Value("${xpp.add.metadata.to.table.of.cases.xsl}") final File xslTransformationFile,
        final XppFormatFileSystem xppFormatFileSystem)
    {
        super(xslTransformationService, transformerBuilderFactory,
            new HashSet<>(Arrays.asList(BundleFileType.TABLE_OF_CASES, BundleFileType.TABLE_OF_ADDED_CASES)));
        this.xslTransformationFile = xslTransformationFile;
        this.xppFormatFileSystem = xppFormatFileSystem;
    }

    @Override
    protected List<TransformationCommand> getTransformationCommands(
        final File inputFile,
        final String materialNumber,
        final XppBookStep bookStep)
    {
        final Transformer transformer = transformerBuilderFactory.create().withXsl(xslTransformationFile).build();
        transformer.setParameter("TOCPartName", BundleFileType.getByFileName(inputFile.getName()));
        final File outputFile =
            xppFormatFileSystem.getStructureWithMetadataFile(bookStep, materialNumber, inputFile.getName());
        return asList(new TransformationCommandBuilder(transformer, outputFile).withInput(inputFile).build());
    }
}
