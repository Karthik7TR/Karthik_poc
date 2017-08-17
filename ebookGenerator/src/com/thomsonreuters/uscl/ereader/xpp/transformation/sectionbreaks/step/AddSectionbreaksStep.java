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
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
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
public class AddSectionbreaksStep extends XppTransformationStep
{
    private static final String MAIN_DOCUMENT_WITH_SECTIONBREAKS_PARAM = "mainDocumentWithSectionbreaks";
    private static final String FOOTNOTES_DOCUMENT_ORIGINAL_PARAM = "footnotesOriginal";

    @Value("${xpp.add.sectionbreaks.original.xsl}")
    private File addSectionbreaksToOriginalXsl;

    @Value("${xpp.add.sectionbreaks.original.footnotes.xsl}")
    private File addSectionbreaksToOriginalFootnotesXsl;

    @Override
    public void executeTransformation() throws IOException
    {
        final Transformer transformerMain =
            transformerBuilderFactory.create().withXsl(addSectionbreaksToOriginalXsl).build();
        final Transformer transformerFootnotes =
            transformerBuilderFactory.create().withXsl(addSectionbreaksToOriginalFootnotesXsl).build();

        for (final Map.Entry<String, BaseFilesByBaseNameIndex> filesByMaterialNumber : fileSystem
            .getStructureWithMetadataFilesIndex(this).getFilesByMaterialNumber())
        {
            final String materialNumber = filesByMaterialNumber.getKey();
            FileUtils.forceMkdir(fileSystem.getSectionbreaksDirectory(this, materialNumber));
            for (final Map.Entry<String, BaseFilesByTypeIndex> filesByBaseName : filesByMaterialNumber.getValue()
                .filesByBaseName())
            {
                transformSingleFile(transformerMain, transformerFootnotes, materialNumber, filesByBaseName);
            }
        }
    }

    private void transformSingleFile(
        final Transformer transformerMain,
        final Transformer transformerFootnotes,
        final String materialNumber,
        final Map.Entry<String, BaseFilesByTypeIndex> filesByBaseName) throws IOException
    {
        final File mainFile = filesByBaseName.getValue().get(PartType.MAIN);
        final File footnotesFile = filesByBaseName.getValue().get(PartType.FOOTNOTE);

        final File mainFileWithSectionbreaks =
            fileSystem.getSectionbreaksFile(this, materialNumber, mainFile.getName());
        final File footnotesFileWithSectionbreaks =
            fileSystem.getSectionbreaksFile(this, materialNumber, footnotesFile.getName());

        final BundleFileType bundleFileType = BundleFileType.getByFileName(mainFile.getName());
        if (bundleFileType == BundleFileType.MAIN_CONTENT)
        {
            transformerMain.setParameter(
                FOOTNOTES_DOCUMENT_ORIGINAL_PARAM,
                footnotesFile.getAbsolutePath().replace("\\", "/"));

            final TransformationCommand command =
                new TransformationCommandBuilder(transformerMain, mainFileWithSectionbreaks).withInput(mainFile)
                    .build();
            transformationService.transform(command);
        }
        else
        {
            FileUtils.copyFile(mainFile, mainFileWithSectionbreaks);
        }

        transformerFootnotes.setParameter(
            MAIN_DOCUMENT_WITH_SECTIONBREAKS_PARAM,
            mainFileWithSectionbreaks.getAbsolutePath().replace("\\", "/"));
        final TransformationCommand command =
            new TransformationCommandBuilder(transformerFootnotes, footnotesFileWithSectionbreaks)
                .withInput(footnotesFile).build();
        transformationService.transform(command);
    }
}
