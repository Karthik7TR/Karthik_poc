package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.VolumeNumberAwareXppTransformationStep;
import org.springframework.beans.factory.annotation.Value;

/**
 * Extract TOC to intermediate format which is ready to use in title.xml
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class ExtractTocStep extends VolumeNumberAwareXppTransformationStep {
    @Value("${xpp.extract.toc.xsl}")
    private File extractTocXsl;
    @Value("${xpp.merge.volume.tocs.xsl}")
    private File mergeVolumeTocsXsl;
    @Value("${xpp.unite.tocs.xsl}")
    private File uniteTocsXsl;
    @Value("${xpp.tox.depth.threshold}")
    private int depthThreshold;

    @Override
    public void executeTransformation() throws Exception {
        final List<File> tocFiles = getXppBundles().stream()
            .map(this::toBundleToc)
            .collect(Collectors.toList());
        uniteTocs(tocFiles);
    }

    private File toBundleToc(final XppBundle bundle) {
        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(extractTocXsl)
            .withParameter("isPocketPart", bundle.isPocketPartPublication())
            .build();
        final List<File> transformedFiles = bundle.getOrderedFileList()
            .stream()
            .map(fileName -> generatePartToc(bundle, transformer, fileName))
            .collect(Collectors.toList());
        return mergeVolumeToc(transformedFiles, bundle);
    }

    private File generatePartToc(final XppBundle bundle, final Transformer transformer, final String fileName) {
        final String materialNumber = bundle.getMaterialNumber();
        final File sourceFile =
            fileSystem.getSectionbreaksFile(this, materialNumber, fileName.replaceAll(".xml", ".main"));
        final File outputFile = fileSystem.getBundlePartTocFile(fileName, materialNumber, this);
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, outputFile).withInput(sourceFile)
                .build();
        transformationService.transform(command);
        return outputFile;
    }

    private File mergeVolumeToc(final List<File> transformedFiles, final XppBundle bundle) {
        final File mergedVolumeTOC = fileSystem.getMergedBundleTocFile(bundle.getMaterialNumber(), this);
        final Transformer merger = transformerBuilderFactory.create()
            .withXsl(mergeVolumeTocsXsl)
            .withParameter("volumeNum", getVolumeNumber(bundle))
            .withParameter("isPocketPart", bundle.isPocketPartPublication())
            .build();
        final TransformationCommand command =
            new TransformationCommandBuilder(merger, mergedVolumeTOC).withInput(transformedFiles)
                .build();
        transformationService.transform(command);
        return mergedVolumeTOC;
    }

    private void uniteTocs(final List<File> tocFiles) {
        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(uniteTocsXsl)
            .withParameter("depthThreshold", depthThreshold)
            .build();
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, fileSystem.getTocFile(this)).withInput(tocFiles)
                .build();
        transformationService.transform(command);
    }
}
