package com.thomsonreuters.uscl.ereader.xpp.transformation.linking.recovery;

import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemImpl.SECTION_NUMBER_MAP_FILE;

import java.io.File;
import java.util.function.Consumer;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Inserts section guids where they are missing in Main content and Index for section numbers
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class InternalLinksRecoveryStep extends XppTransformationStep {
    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.ORIGINAL_PAGES_DIR;
    private static final XppFormatFileSystemDir DESTINATION_DIR = XppFormatFileSystemDir.INTERNAL_LINKS_RECOVERY_DIR;

    @Value("${xpp.internal.links.recovery.xsl}")
    private File internalLinksRecoveryXsl;
    @Autowired
    private SectionNumberService sectionNumberService;

    @Override
    public void executeTransformation() throws Exception {
        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(internalLinksRecoveryXsl)
            .withParameter("pattern", sectionNumberService.getPattern())
            .withParameter("primarySourcePrefixPattern", sectionNumberService.getPrefixPattern())
            .build();
        fileSystem.getFiles(this, SOURCE_DIR)
            .forEach((materialNumber, files) -> files.forEach(recoverInternalLinks(transformer, materialNumber)));
    }

    private Consumer<File> recoverInternalLinks(final Transformer transformer, final String materialNumber) {
        return file -> {
            if (BundleFileType.getByFileName(file.getName())
                .equals(BundleFileType.INDEX)) {
                transformer.setParameter(
                    "sectionNumberMapFile",
                    new File(
                        fileSystem.getDirectory(this, XppFormatFileSystemDir.SECTION_NUMBERS_MAP_DIR),
                        SECTION_NUMBER_MAP_FILE).getAbsolutePath()
                            .replace("\\", "/"));
                transform(materialNumber, file, transformer);
            } else {
                copyFile(file, materialNumber);
            }
        };
    }

    @SneakyThrows
    private void copyFile(final File src, final String materialNumber) {
        final File destDir = fileSystem.getDirectory(this, DESTINATION_DIR, materialNumber);
        FileUtils.copyFileToDirectory(src, destDir);
    }

    private void transform(
        @NotNull final String materialNumber,
        @NotNull final File file,
        @NotNull final Transformer transformer) {
        final TransformationCommand command = new TransformationCommandBuilder(
            transformer,
            fileSystem.getFile(this, DESTINATION_DIR, materialNumber, file.getName())).withInput(file)
                .build();
        transformationService.transform(command);
    }
}
