package com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.common.StreamType;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.springframework.beans.factory.annotation.Value;

/**
 * Creates original XMLs from XPP XMLs
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class OriginalStructureTransformationStep extends XppTransformationStep {
    @Value("${xpp.entities.dtd}")
    private File entitiesDtdFile;
    @Value("${xpp.transform.to.phoenix.xsl}")
    private File transformToPhoenixXsl;
    @Value("${xpp.cite.query.processor.xsl}")
    private File citeQueryProcessorXsl;
    @Resource(name = "xppGatherFileSystem")
    private XppGatherFileSystem xppGatherFileSystem;

    @Override
    public void executeTransformation() throws Exception {
        Map<String, Collection<File>> xppXmls = xppGatherFileSystem.getXppSourceXmls(this);
        for (final Map.Entry<String, Collection<File>> xppDir : xppXmls.entrySet()) {
            final File bundleOriginalDir = fileSystem.getOriginalDirectory(this, xppDir.getKey());
            bundleOriginalDir.mkdirs();
        }
        xppXmls = processCiteQueries(xppXmls);
        generateXmls(xppXmls, StreamType.MAIN);
        generateXmls(xppXmls, StreamType.FOOTNOTES);
    }

    private void generateXmls(final Map<String, Collection<File>> xppXmls, final StreamType type) {
        final Transformer transformerToOriginal = transformerBuilderFactory.create()
            .withXsl(transformToPhoenixXsl)
            .withParameter("entitiesDocType", entitiesDtdFile.getAbsolutePath().replace("\\", "/"))
            .withParameter("type", type.getName())
            .build();

        for (final Map.Entry<String, Collection<File>> xppDir : xppXmls.entrySet()) {
            for (final File xppFile : xppDir.getValue()) {
                transformerToOriginal
                    .setParameter("bundlePartType", BundleFileType.getByFileName(xppFile.getName()).name());
                final File originalFile = getOutputFile(xppDir.getKey(), xppFile.getName(), type);

                final TransformationCommand command =
                    new TransformationCommandBuilder(transformerToOriginal, originalFile).withInput(xppFile).build();
                transformationService.transform(command);
            }
        }
    }

    private File getOutputFile(final String materialNumber, final String fileName, final StreamType type) {
        if (type == StreamType.MAIN) {
            return fileSystem.getOriginalFile(this, materialNumber, fileName);
        } else if (type == StreamType.FOOTNOTES) {
            return fileSystem.getFootnotesFile(this, materialNumber, fileName);
        }
        throw new IllegalArgumentException("Unsupported stream type: " + type.getName());
    }

    private Map<String, Collection<File>> processCiteQueries(final Map<String, Collection<File>> originalBundles) {
        final Transformer citeQueryProcessor =
            transformerBuilderFactory.create().withXsl(citeQueryProcessorXsl).build();

        final Map<String, Collection<File>> processedMap = new HashMap<>();

        for (final Map.Entry<String, Collection<File>> xppDir : originalBundles.entrySet()) {
            final Collection<File> files = new HashSet<>();
            for (final File xppFile : xppDir.getValue()) {
                final File citeQueryProcessedFile =
                    fileSystem.getCiteQueryProcessedFile(this, xppDir.getKey(), xppFile.getName());
                final TransformationCommand command =
                    new TransformationCommandBuilder(citeQueryProcessor, citeQueryProcessedFile).withInput(xppFile)
                        .withDtd(entitiesDtdFile)
                        .build();
                transformationService.transform(command);
                files.add(citeQueryProcessedFile);
            }
            processedMap.put(xppDir.getKey(), files);
        }
        return processedMap;
    }
}
