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
public class IndexXppMetadataStrategy extends AbstractPlaceXppMetadataTransformStrategy {
    private static final String FORM_INDEX = "form_Index";
    private static final String INDEX = "Index";

    static final String MATERIAL_NUMBER = "materialNumber";
    static final String INDEX_ID = "indexId";
    static final String INDEX_NAME = "indexName";
    static final String INDEX_ID_FORM_INDEX = "formIndex";
    static final String INDEX_NAME_FORM_INDEX = "Index to Forms";

    private final File xslTransformationFile;
    private final XppFormatFileSystem xppFormatFileSystem;

    @Autowired
    public IndexXppMetadataStrategy(
        final XslTransformationService xslTransformationService,
        final TransformerBuilderFactory transformerBuilderFactory,
        final XppFormatFileSystem xppFormatFileSystem,
        @Value("${xpp.add.metadata.to.index.xsl}") final File xslTransformationFile) {
        super(xslTransformationService, transformerBuilderFactory, new HashSet<>(Arrays.asList(BundleFileType.INDEX)));
        this.xslTransformationFile = xslTransformationFile;
        this.xppFormatFileSystem = xppFormatFileSystem;
    }

    @NotNull
    @Override
    protected List<TransformationCommand> getTransformationCommands(
        @NotNull final File inputFile,
        @NotNull final String materialNumber,
        @NotNull final XppBookStep step) {
        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(xslTransformationFile)
            .build();
        final File outputFile =
            xppFormatFileSystem.getStructureWithMetadataFile(step, materialNumber, inputFile.getName());

        final TransformationCommand addMetadataToIndexCommand =
            new TransformationCommandBuilder(transformer, outputFile).withInput(inputFile)
                .build();
        return asList(addMetadataToIndexCommand);
    }

    @Override
    public void performHandling(
        @NotNull final File inputFile,
        @NotNull final String materialNumber,
        @NotNull final XppBookStep step) {
        for (final TransformationCommand command : getTransformationCommands(inputFile, materialNumber, step)) {
            final Transformer transformer = command.getTransformer();
            transformer.setParameter(MATERIAL_NUMBER, materialNumber);
            transformer.setParameter(INDEX_ID, getIndexId(inputFile));
            transformer.setParameter(INDEX_NAME, getIndexName(inputFile));
            xslTransformationService.transform(command);
        }
    }

    private String getIndexId(final File inputFile) {
        return inputFile.getName().contains(FORM_INDEX) ? INDEX_ID_FORM_INDEX : INDEX;
    }

    private String getIndexName(final File inputFile) {
        return inputFile.getName().contains(FORM_INDEX) ? INDEX_NAME_FORM_INDEX : INDEX;
    }
}
