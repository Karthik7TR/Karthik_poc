package com.thomsonreuters.uscl.ereader.xpp.transformation.linking.map;

import static java.util.stream.Collectors.toList;

import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemImpl.SECTION_NUMBER_MAP_FILE;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.xpp.strategy.type.BundleFileType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.linking.recovery.SectionNumberService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.PartType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class SectionNumberMappingStep extends XppTransformationStep {
    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.ORIGINAL_DIR;
    private static final XppFormatFileSystemDir DESTINATION_DIR = XppFormatFileSystemDir.SECTION_NUMBERS_MAP_DIR;

    @Value("${xpp.section.number.map.xsl}")
    private File sectionNumberMapXsl;
    @Value("${xpp.merge.xsl}")
    private File mergeXsl;
    @Autowired
    private SectionNumberService sectionNumberService;

    @Override
    public void executeTransformation() throws Exception {
        final Transformer transformer = transformerBuilderFactory.create()
            .withXsl(sectionNumberMapXsl)
            .withParameter("pattern", sectionNumberService.getPattern())
            .build();
        final Transformer merger = transformerBuilderFactory.create()
            .withXsl(mergeXsl)
            .withParameter("tag-name", "map")
            .build();
        final List<File> mainFiles = fileSystem.getFiles(this, SOURCE_DIR)
            .entrySet()
            .stream()
            .flatMap(toSectionNumberMapFile(transformer))
            .collect(toList());
        transform(mainFiles, merger);
    }

    private Function<Map.Entry<String, Collection<File>>, Stream<File>> toSectionNumberMapFile(
        @NotNull final Transformer transformer) {
        return entry -> entry.getValue()
            .stream()
            .filter(file -> !file.isDirectory())
            .filter(file -> BundleFileType.getByFileName(file.getName())
                .equals(BundleFileType.MAIN_CONTENT))
            .filter(file -> !FilenameUtils.getExtension(file.getName())
                .equals(PartType.FOOTNOTE.getName()))
            .map(file -> transform(entry.getKey(), file, transformer));
    }

    private File transform(
        @NotNull final String materialNumber,
        @NotNull final File file,
        @NotNull final Transformer transformer) {
        final String fileName = file.getName() + "-" + SECTION_NUMBER_MAP_FILE;
        final File outputDir = fileSystem.getDirectory(this, DESTINATION_DIR, materialNumber);
        final TransformationCommand command =
            new TransformationCommandBuilder(transformer, new File(outputDir, fileName)).withInput(file)
                .build();
        transformationService.transform(command);
        return new File(outputDir, fileName);
    }

    private void transform(@NotNull final List<File> files, @NotNull final Transformer transformer) {
        final TransformationCommand command = new TransformationCommandBuilder(
            transformer,
            new File(fileSystem.getDirectory(this, DESTINATION_DIR), SECTION_NUMBER_MAP_FILE)).withInput(files)
                .build();
        transformationService.transform(command);
    }
}
