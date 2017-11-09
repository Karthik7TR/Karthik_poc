package com.thomsonreuters.uscl.ereader.xpp.transformation.toc.step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import com.thomsonreuters.uscl.ereader.xpp.utils.bundle.BundleUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * Extract TOC to intermediate format which is ready to use in title.xml
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class ExtractTocStep extends XppTransformationStep {
    @Value("${xpp.unite.tocs.xsl}")
    private File uniteTocsXsl;
    @Value("${xpp.extract.toc.xsl}")
    private File extractTocXsl;
    @Value("${xpp.merge.volume.tocs.xsl}")
    private File mergeVolumeTocsXsl;
    @Value("${xpp.tox.depth.threshold}")
    private int depthThreshold;

    @Override
    public void executeTransformation() throws Exception {
        final List<File> tocFiles = new ArrayList<>();
        for (final XppBundle bundle : getXppBundles()) {
            generateBundleTocs(bundle, tocFiles);
        }
        uniteTocs(tocFiles);
    }

    private void generateBundleTocs(final XppBundle bundle, final List<File> tocFiles) {
        final boolean isPocketPart = BundleUtils.isPocketPart(bundle);
        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(extractTocXsl)
            .withParameter("isPocketPart", isPocketPart)
            .build();
        final List<File> transformedFiles = new ArrayList<>();
        for (final String fileName : bundle.getOrderedFileList()) {
            transformedFiles.add(generatePartToc(bundle, transformer, fileName));
        }
        tocFiles.add(mergeVolumeToc(transformedFiles, bundle));
    }

    private File generatePartToc(final XppBundle bundle, final Transformer transformer, final String fileName) {
        final File sourceFile =
            fileSystem.getSectionbreaksFile(this, bundle.getMaterialNumber(), fileName.replaceAll(".xml", ".main"));
        final File outputFile = fileSystem.getBundlePartTocFile(fileName, bundle.getMaterialNumber(), this);
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, outputFile).withInput(sourceFile).build();
        transformationService.transform(command);
        return outputFile;
    }

    private File mergeVolumeToc(final List<File> transformedFiles, final XppBundle bundle) {
        final File mergedVolumeTOC = fileSystem.getMergedBundleTocFile(bundle.getMaterialNumber(), this);
        final Transformer merger = transformerBuilderFactory.create().withXsl(mergeVolumeTocsXsl).build();
        final TransformationCommand command =
            new TransformationCommandBuilder(merger, mergedVolumeTOC).withInput(transformedFiles).build();
        transformationService.transform(command);
        return mergedVolumeTOC;
    }

    private void uniteTocs(final List<File> tocFiles) {
        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(uniteTocsXsl)
            .withParameter("depthThreshold", depthThreshold)
            .build();
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, fileSystem.getTocFile(this)).withInput(tocFiles).build();
        transformationService.transform(command);
    }
}
