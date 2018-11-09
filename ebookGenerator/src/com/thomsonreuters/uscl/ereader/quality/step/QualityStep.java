package com.thomsonreuters.uscl.ereader.quality.step;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;

import static com.thomsonreuters.uscl.ereader.JobParameterKey.QUALITY_REPORTS;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.QualityFileSystem.COMPARE_UNIT_LIST_COLLECTOR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.QUALITY_DIR;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.transform.Transformer;

import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommand;
import com.thomsonreuters.uscl.ereader.common.xslt.TransformationCommandBuilder;
import com.thomsonreuters.uscl.ereader.quality.domain.request.CompareUnit;
import com.thomsonreuters.uscl.ereader.quality.domain.response.JsonResponse;
import com.thomsonreuters.uscl.ereader.quality.service.ComparisonService;
import com.thomsonreuters.uscl.ereader.quality.service.ReportService;
import com.thomsonreuters.uscl.ereader.quality.transformer.IdentityTransformer;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.QualityFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import lombok.SneakyThrows;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class QualityStep extends XppTransformationStep {
    public static final String DIVXML = "DIVXML";
    public static final String HTML = "HTML";
    static final String TRANSFORMED_EXTENSION = ".transformed";
    private static final String STREAM_TYPE = "streamType";

    @Autowired
    private ComparisonService comparisonService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private XppGatherFileSystem gatherFileSystem;
    @Autowired
    @Qualifier("xppQualityFileSystem")
    private QualityFileSystem qualityFileSystem;
    @Autowired
    private IdentityTransformer identityTransformer;

    @Value("${xpp.quality.divxml}")
    private File divXmlToTextXsl;
    @Value("${xpp.quality.html}")
    private File htmlToTextXsl;
    @Value("${xpp.entities.dtd}")
    private File entitiesDtdFile;

    @Override
    public void executeTransformation() {
        getJobExecutionContext().put(QUALITY_REPORTS, compareAndGetReports());
    }

    private Map<String, List<File>> compareAndGetReports() {
        final MultiKeyMap<String, Collection<File>> materialNameFilesMap = qualityFileSystem.getHtmlFileMap(this);
        final Map<String, List<CompareUnit>> compareUnitsMap = materialNameFilesMap.entrySet()
            .stream()
            .collect(groupingBy(entry -> entry.getKey()
                .getKey(0), mapping(entry -> {
                    final MultiKey<? extends String> materialNumberAndFileName = entry.getKey();
                    final Collection<File> htmlFiles = entry.getValue()
                            .stream()
                            .sorted(Comparator.comparing(File::getName))
                            .collect(Collectors.toList());
                    return getCompareUnit(materialNumberAndFileName, htmlFiles, this);
                }, COMPARE_UNIT_LIST_COLLECTOR)));

        return compareUnitsMap.entrySet()
            .stream()
            .collect(groupingBy(Map.Entry::getKey, mapping(entry -> {
                final List<CompareUnit> units = entry.getValue()
                    .stream()
                    .filter(unit -> !bothFilesEmpty(unit))
                    .collect(Collectors.toList());
                final JsonResponse jsonResponse = comparisonService.compare(units);
                final String materialNumber = entry.getKey();
                final String reportsDirPath =
                    new File(fileSystem.getDirectory(this, QUALITY_DIR, materialNumber), "reports").getAbsolutePath();
                return reportService.getReports(jsonResponse, reportsDirPath);
            }, reducing(new ArrayList<>(), (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            }))));
    }

    @SneakyThrows
    private boolean bothFilesEmpty(final CompareUnit unit) {
        return Files.size(Paths.get(unit.getSource())) == 0 && Files.size(Paths.get(unit.getTarget())) == 0;
    }

    /**
     * @param multiKey  A key to which these HTML files are mapped. Consists of material number and file name.
     * @param htmlFiles HTML pages that correspond to a single DIVXML file.
     * @param step      Reference to Quality Step to have ability to use to FileSystem interfaces.
     * @return Transformed Main and Footnote files. Left is Main, right is Footnote.
     */
    private Pair<CompareUnit, CompareUnit> getCompareUnit(
        final MultiKey<? extends String> multiKey,
        final Collection<File> htmlFiles,
        final BookStep step) {
        final Transformer divXmlTransformer = getTransformer(TransformerType.DIVXML);
        final Transformer htmlTransformer = getTransformer(TransformerType.HTML);
        final String materialNumber = multiKey.getKey(0);
        final String fileName = multiKey.getKey(1);

        final File original = gatherFileSystem.getXppSourceXmls(step)
            .get(materialNumber)
            .stream()
            .filter(file -> file.getName()
                .startsWith(fileName))
            .findFirst()
            .orElseThrow(RuntimeException::new);

        divXmlTransformer.setParameter(STREAM_TYPE, StreamType.DIVXML_MAIN.toString());
        final File textDivXmlMain = transform(singleton(original), divXmlTransformer, multiKey, TransformerType.DIVXML);
        divXmlTransformer.setParameter(STREAM_TYPE, StreamType.DIVXML_FOOTNOTE.toString());
        final File textDivXmlFootnote =
            transform(singleton(original), divXmlTransformer, multiKey, TransformerType.DIVXML);

        htmlTransformer.setParameter(STREAM_TYPE, StreamType.HTML_MAIN.toString());
        final File textHtmlMain = transform(htmlFiles, htmlTransformer, multiKey, TransformerType.HTML);
        htmlTransformer.setParameter(STREAM_TYPE, StreamType.HTML_FOOTNOTE.toString());
        final File textHtmlFootnote = transform(htmlFiles, htmlTransformer, multiKey, TransformerType.HTML);

        //Transforms entities to characters
        identityTransformer.transform(textDivXmlMain);
        identityTransformer.transform(textDivXmlFootnote);
        return Pair.of(
            new CompareUnit(textDivXmlMain.getAbsolutePath(), textHtmlMain.getAbsolutePath()),
            new CompareUnit(textDivXmlFootnote.getAbsolutePath(), textHtmlFootnote.getAbsolutePath()));
    }

    private Transformer getTransformer(final TransformerType type) {
        return transformerBuilderFactory.create()
            .withXsl(type == TransformerType.DIVXML ? divXmlToTextXsl : htmlToTextXsl)
            .withParameter("entitiesDocType", entitiesDtdFile.getAbsolutePath()
                .replace("\\", "/"))
            .build();
    }

    private TransformationCommand getCommand(
        final Transformer transformer,
        final File targetFile,
        final File... source) {
        if (source.length == 1) {
            return new TransformationCommandBuilder(transformer, targetFile).withInput(source[0])
                .withDtd(entitiesDtdFile)
                .build();
        } else {
            return new TransformationCommandBuilder(transformer, targetFile).withInput(Arrays.asList(source))
                .withDtd(entitiesDtdFile)
                .build();
        }
    }

    private File transform(
        final Collection<File> input,
        final Transformer transformer,
        final MultiKey<? extends String> multiKey,
        final TransformerType transformerType) {
        final String materialNumber = multiKey.getKey(0);
        final String fileName = multiKey.getKey(1);
        final File output = fileSystem.getFile(
            this,
            QUALITY_DIR,
            materialNumber,
            fileName
                + transformer.getParameter(STREAM_TYPE)
                + "."
                + transformerType
                + "."
                + materialNumber
                + TRANSFORMED_EXTENSION);
        final TransformationCommand command = getCommand(transformer, output, input.toArray(new File[0]));
        transformationService.transform(command);
        return output;
    }

    public enum StreamType {
        DIVXML_MAIN("main"),
        DIVXML_FOOTNOTE("footnote"),
        HTML_MAIN("main"),
        HTML_FOOTNOTE("footnote");

        StreamType(final String name) {
            this.name = name;
        }

        private String name;

        @Override
        public String toString() {
            return name;
        }
    }

    public enum TransformerType {
        DIVXML,
        HTML
    }
}
