package com.thomsonreuters.uscl.ereader.xpp.transformation.volumesmap.step;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformerBuilder;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.VolumeNumberAwareXppTransformationStep;
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class CreateVolumesMapStep extends VolumeNumberAwareXppTransformationStep {
    @Value("${xpp.volumes.map.xsl}")
    private File createVolumeMapByFileXsl;
    @Value("${xpp.volumes.map.merge.xsl}")
    private File mergeVolumesMapXsl;

    @Override
    public void executeTransformation() throws Exception {
        final List<File> volumesFiles = new ArrayList<>();
        final Map<String, Collection<File>> structureFiles = fileSystem.getStructureWithMetadataFiles(this);
        getXppBundles().forEach(bundle -> createBundleVolumesMap(structureFiles.get(bundle.getMaterialNumber()), bundle, volumesFiles));

        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(mergeVolumesMapXsl)
            .build();
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, fileSystem.getVolumesMapFile(this)).withInput(volumesFiles).build();
        transformationService.transform(command);
    }

    private void createBundleVolumesMap(final Collection<File> files, final XppBundle bundle, final List<File> volumesFiles) {
        files.stream()
            .filter(file -> file.getName().endsWith(".main"))
            .forEach(file -> createBundleFileVolumesMap(file, bundle, volumesFiles));
    }

    private void createBundleFileVolumesMap(final File file, final XppBundle bundle, final List<File> volumesFiles) {
        final String fileName = file.getName().replaceAll(".main", ".xml");
        final File outputFile = fileSystem.getFile(this, XppFormatFileSystemDir.VOLUMES_MAP_DIR, bundle.getMaterialNumber(), fileName);
        final TransformerBuilder transformerBuilder = transformerBuilderFactory.create()
            .withXsl(createVolumeMapByFileXsl)
            .withParameter("defaultVolumeNumber", getVolumeNumber(bundle));
        getSegOutlineFile()
            .ifPresent(segOutlineFile -> {
                transformerBuilder.withParameter("volNumber", getVolumeNumberByFileName(fileName))
                    .withParameter("segOutlineFilePath", segOutlineFile.getAbsolutePath().replace("\\", "/"));
            });
        final TransformationCommand command =
            new TransformationCommandBuilder(transformerBuilder.build(), outputFile).withInput(file).build();
        transformationService.transform(command);
        volumesFiles.add(outputFile);
    }
}
