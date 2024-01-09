package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.trgr.cobalt.util.urlbuilder.Container;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;

import static com.thomsonreuters.uscl.ereader.core.book.util.PageNumberUtil.addVolumeToPageNumber;
import static com.thomsonreuters.uscl.ereader.format.links.UrlBuilderConstants.CARSWELL_WESTLAW_CONTAINER;
import static com.thomsonreuters.uscl.ereader.format.links.UrlBuilderConstants.URL_BUILDER_CONTAINER_ATTRIBUTE;

@Service
public class TransformCharSequencesService {
    private static final String OCONNORS_TAG = "oconnor.annotations";
    private static final String ALL = "*";
    private static final String DOUBLE_HYPHEN = "--";
    private static final String EM_DASH = "\u2014";
    private static final String BULL_3 = "&bull3;";
    private static final String WHITE_BULLET = "○";
    private static final String CITE_QUERY = "cite.query";

    @Autowired
    private JsoupService jsoup;

    @Autowired
    private DuplicatedPagebreaksResolver duplicatedPagebreaksResolver;

    @Autowired
    private InnerDocumentAnchorsMarker innerDocumentAnchorsMarker;

    @SneakyThrows
    public void transformCharSequences(final File srcDir, final File destDir, final boolean isCwBook, final boolean isPrintPageNumbers, final boolean pageVolumesSet) {
        Files.list(srcDir.toPath())
            .filter(Files::isRegularFile)
            .forEach(path -> transformCharSequencesInFile(path.toFile(), destDir, isCwBook, isPrintPageNumbers, pageVolumesSet));
    }

    private void transformCharSequencesInFile(final File file, final File destDir, final boolean isCwBook, final boolean shouldProtectPagebreaks, final boolean pageVolumesSet) {
        final Document document = jsoup.loadDocument(file);
        document.outputSettings().prettyPrint(false);
        transformDoubleHyphensIntoEmDashesInDocument(document);
        transformBulletsInDocument(document);
        addContainerAttributeToCiteQuery(document, isCwBook);
        duplicatedPagebreaksResolver.fixDuplicatedPagebreaks(document);
        protectPagebreaks(document, shouldProtectPagebreaks, pageVolumesSet);
        innerDocumentAnchorsMarker.markInnerDocumentAnchors(document);

        jsoup.saveDocument(destDir, file.getName(), document);
    }

    private void transformDoubleHyphensIntoEmDashesInDocument(final Document document) {
        Elements oConnorsAnnotations = document.getElementsByTag(OCONNORS_TAG).select(ALL);
        jsoup.transformCharSequencesInElements(oConnorsAnnotations, DOUBLE_HYPHEN, EM_DASH);
    }

    private void transformBulletsInDocument(final Document document) {
        jsoup.transformCharSequencesInElements(document.getAllElements(), BULL_3, WHITE_BULLET);
    }

    private void addContainerAttributeToCiteQuery(final Document document, final boolean isCwBook) {
        final String container = isCwBook ? CARSWELL_WESTLAW_CONTAINER : Container.COBALT.name();
        document.getElementsByTag(CITE_QUERY).forEach(e -> e.attr(URL_BUILDER_CONTAINER_ATTRIBUTE, container));
    }

    private void protectPagebreaks(final Document document, final boolean shouldProtectPagebreaks, final boolean pageVolumesSet) {
        document.getElementsByTag(PageNumberUtil.PAGEBREAK).forEach(pagebreak -> {
            if (shouldProtectPagebreaks) {
                if (pageVolumesSet) {
                    addVolumeToPageNumber(pagebreak);
                }
                pagebreak.after(new TextNode(PageNumberUtil.protectPagebreak(pagebreak)));
            }
            pagebreak.remove();
        });
    }
}
