package com.thomsonreuters.uscl.ereader.xpp.transformation.sectionbreaks.step;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles.BaseFilesByBaseNameIndex;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles.BaseFilesByTypeIndex;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Add sectionbreak tags to original XML
 * TODO: unite this step with PlaceXppMetadataStep
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class AddSectionbreaksStep extends XppTransformationStep {
    private static final String MAIN_DOCUMENT_WITH_SECTIONBREAKS_PARAM = "mainFile";
    private static final String FOOTNOTES_DOCUMENT_ORIGINAL_PARAM = "footnotesFile";

    @Value("${xpp.add.sectionbreaks.original.xsl}")
    private File addSectionbreaksToOriginalXsl;

    @Value("${xpp.add.links.from.main.to.footnotes.xsl}")
    private File addLinksFromMainToFootnotes;

    @Value("${xpp.add.sectionbreaks.original.footnotes.xsl}")
    private File addSectionbreaksToOriginalFootnotesXsl;

    private Transformer transformerMainType;
    private Transformer transformerOtherTypes;
    private Transformer transformerFootnotes;

    @Override
    public void executeTransformation() throws IOException {
        transformerMainType = transformerBuilderFactory.create().withXsl(addSectionbreaksToOriginalXsl).build();
        transformerOtherTypes = transformerBuilderFactory.create().withXsl(addLinksFromMainToFootnotes).build();
        transformerFootnotes =
            transformerBuilderFactory.create().withXsl(addSectionbreaksToOriginalFootnotesXsl).build();

        for (final Map.Entry<String, BaseFilesByBaseNameIndex> filesByMaterialNumber : fileSystem
            .getStructureWithMetadataFilesIndex(this).getFilesByMaterialNumber()) {
            final String materialNumber = filesByMaterialNumber.getKey();
            FileUtils.forceMkdir(fileSystem.getSectionbreaksDirectory(this, materialNumber));
            for (final Map.Entry<String, BaseFilesByTypeIndex> filesByBaseName : filesByMaterialNumber.getValue()
                .filesByBaseName()) {
                transformSingleFile(materialNumber, filesByBaseName);
            }
        }
    }

    private void transformSingleFile(
        final String materialNumber,
        final Map.Entry<String, BaseFilesByTypeIndex> filesByBaseName) {
        final File mainFile = filesByBaseName.getValue().get(PartType.MAIN);
        final File footnotesFile = filesByBaseName.getValue().get(PartType.FOOTNOTE);

        final File mainFileWithSectionbreaks =
            fileSystem.getSectionbreaksFile(this, materialNumber, mainFile.getName());
        final File footnotesFileWithSectionbreaks =
            fileSystem.getSectionbreaksFile(this, materialNumber, footnotesFile.getName());

        final BundleFileType bundleFileType = BundleFileType.getByFileName(mainFile.getName());
        if (bundleFileType == BundleFileType.MAIN_CONTENT) {
            transformMainFile(transformerMainType, mainFile, footnotesFile, mainFileWithSectionbreaks);
        } else {
            transformMainFile(transformerOtherTypes, mainFile, footnotesFile, mainFileWithSectionbreaks);
        }

        transformFootnoteFile(footnotesFile, mainFileWithSectionbreaks, footnotesFileWithSectionbreaks);
    }

    private void transformMainFile(
        final Transformer transformer,
        final File inputMainFile,
        final File parameterFootnoteFile,
        final File outputMainFile) {
        transformFile(
            transformer,
            inputMainFile,
            FOOTNOTES_DOCUMENT_ORIGINAL_PARAM,
            parameterFootnoteFile,
            outputMainFile);
    }

    private void transformFootnoteFile(
        final File inputFootnoteFile,
        final File parameterMainFile,
        final File outputFootnotesFile) {
        transformFile(
            transformerFootnotes,
            inputFootnoteFile,
            MAIN_DOCUMENT_WITH_SECTIONBREAKS_PARAM,
            parameterMainFile,
            outputFootnotesFile);
    }

    private void transformFile(
        final Transformer transformer,
        final File inputFile,
        final String paramName,
        final File parameterFile,
        final File outputFile) {
        transformer.setParameter(paramName, parameterFile.getAbsolutePath().replace("\\", "/"));

        transformationService
            .transform(new TransformationCommandBuilder(transformer, outputFile).withInput(inputFile).build());
    }
}
