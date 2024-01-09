package com.thomsonreuters.uscl.ereader.xpp.transformation.footnotes.step;

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
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class CrossPageLinkedFootnotesStep extends XppTransformationStep {
    private static final String MAIN_DOCUMENT_PARAM = "mainFile";

    private static final XppFormatFileSystemDir INPUT_DIR = XppFormatFileSystemDir.SECTIONBREAKS_DIR;
    private static final XppFormatFileSystemDir OUTPUT_DIR = XppFormatFileSystemDir.CROSS_PAGE_FOOTNOTES;

    @Value("${xpp.crosspage.footnotes.xsl}")
    private File crosspageFootnotes;

    private Transformer footnotesFileTransformer;

    @Override
    public void executeTransformation() throws Exception {
        footnotesFileTransformer = transformerBuilderFactory.create().withXsl(crosspageFootnotes).build();
        for (final Map.Entry<String, BaseFilesByBaseNameIndex> filesByMaterialNumber : fileSystem
            .getBaseFilesIndex(this, INPUT_DIR).getFilesByMaterialNumber()) {
            final String materialNumber = filesByMaterialNumber.getKey();
            FileUtils.forceMkdir(fileSystem.getDirectory(this, OUTPUT_DIR, materialNumber));
            for (final Map.Entry<String, BaseFilesByTypeIndex> filesByBaseName : filesByMaterialNumber.getValue()
                .filesByBaseName()) {
                transformSingleFile(materialNumber, filesByBaseName);
            }
        }
    }

    private void transformSingleFile(
        final String materialNumber,
        final Map.Entry<String, BaseFilesByTypeIndex> filesByBaseName) throws IOException {
        final File mainFile = filesByBaseName.getValue().get(PartType.MAIN);
        final File footnotesFile = filesByBaseName.getValue().get(PartType.FOOTNOTE);

        final File processedMainFile = fileSystem.getFile(this, OUTPUT_DIR, materialNumber, mainFile.getName());
        FileUtils.copyFile(mainFile, processedMainFile);

        final File processedFootnotesFile = fileSystem.getFile(this, OUTPUT_DIR, materialNumber, footnotesFile.getName());

        footnotesFileTransformer.setParameter(
            MAIN_DOCUMENT_PARAM,
            mainFile.getAbsolutePath().replace("\\", "/"));

        transformationService.transform(
            new TransformationCommandBuilder(footnotesFileTransformer, processedFootnotesFile).withInput(footnotesFile)
            .build());
    }

}
