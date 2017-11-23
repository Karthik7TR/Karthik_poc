package com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.generate.title.metadata.step.DocumentName;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

/**
 * Creates HTML pages using pages in original XML markup
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class TransformationToHtmlStep extends XppTransformationStep {
    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.POCKET_PART_LINKS_DIR;
    private static final XppFormatFileSystemDir DESTINATION_DIR = XppFormatFileSystemDir.HTML_PAGES_DIR;

    @Value("${xpp.transform.to.html.xsl}")
    private File transformToHtmlXsl;
    @Value("${xpp.entities.dtd}")
    private File entitiesDtdFile;

    @Override
    public void executeTransformation() throws Exception {
        final Collection<XppBundle> bundles = getXppBundles();
        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(transformToHtmlXsl)
            .withParameter("entitiesDocType", entitiesDtdFile.getAbsolutePath().replace("\\", "/"))
            .build();
        final PagePrefix pagePrefix = new PagePrefix(getXppBundles());
        for (final Entry<String, Collection<File>> dir : fileSystem.getFiles(this, SOURCE_DIR).entrySet()) {
            final String materialNumber = dir.getKey();
            final boolean isPocketPart = bundles.stream()
                .filter(bundle -> bundle.getMaterialNumber().equals(materialNumber))
                .findFirst()
                .map(XppBundle::isPocketPartPublication)
                .orElse(false);
            pagePrefix.switchVolume(materialNumber);
            FileUtils.forceMkdir(fileSystem.getDirectory(this, DESTINATION_DIR, materialNumber));
            for (final File part : dir.getValue()) {
                final TransformationCommand command = createCommand(transformer, materialNumber, part, pagePrefix, isPocketPart);
                transformationService.transform(command);
            }
        }
    }

    private TransformationCommand createCommand(
        final Transformer transformer,
        final String materialNumber,
        final File part,
        final PagePrefix pagePrefix,
        final boolean isPocketPart) {
        final String partName = part.getName();
        transformer.setParameter("fileBaseName", FilenameUtils.removeExtension(partName));
        transformer.setParameter("pagePrefix", pagePrefix.getPagePrefix());
        transformer.setParameter("divXmlName", new DocumentName(partName).getBaseName());
        transformer.setParameter("documentUidMapDoc", getPath(fileSystem.getAnchorToDocumentIdMapFile(this)));
        transformer.setParameter(
            "summaryTocDocumentUidMapDoc",
            getPath(fileSystem.getAnchorToDocumentIdMapFile(this, materialNumber)));
        transformer.setParameter("isPocketPart", isPocketPart);

        final File htmlPageFile = fileSystem.getHtmlPageFile(this, pagePrefix.getMaterialNumber(), partName);
        return new TransformationCommandBuilder(transformer, htmlPageFile).withInput(part).build();
    }

    private String getPath(final File anchorMap) {
        return anchorMap.getAbsolutePath().replace("\\", "/");
    }

    private static final class PagePrefix {
        private final Map<String, Integer> volumesNumberMap = new HashMap<>();
        private Integer currentVolume;
        private String materialNumber;

        private PagePrefix(@NotNull final List<XppBundle> bundles) {
            Integer volume = 1;
            for (final XppBundle bundle : bundles) {
                volumesNumberMap.put(bundle.getMaterialNumber(), volume++);
            }
        }

        private void switchVolume(@NotNull final String materialNumber) {
            this.materialNumber = materialNumber;
            currentVolume = volumesNumberMap.get(materialNumber);
        }

        private String getPagePrefix() {
            final StringBuilder builder = new StringBuilder();
            if (volumesNumberMap.size() > 1) {
                builder.append("Vol").append(currentVolume).append("-");
            }
            return builder.toString();
        }

        private String getMaterialNumber() {
            return materialNumber;
        }
    }
}
