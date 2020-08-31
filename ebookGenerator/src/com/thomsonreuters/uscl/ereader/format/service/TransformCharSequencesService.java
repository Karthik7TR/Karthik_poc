package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.nio.file.Files;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransformCharSequencesService {
    private static final String OCONNORS_TAG = "oconnor.annotations";
    private static final String ALL = "*";
    private static final String DOUBLE_HYPHEN = "--";
    private static final String EM_DASH = "\u2014";
    private static final String BULL_3 = "&bull3;";
    private static final String WHITE_BULLET = "○";

    @Autowired
    private JsoupService jsoup;

    @SneakyThrows
    public void transformCharSequences(final File srcDir, final File destDir) {
        Files.list(srcDir.toPath())
            .filter(Files::isRegularFile)
            .forEach(path -> transformCharSequencesInFile(path.toFile(), destDir));
    }

    private void transformCharSequencesInFile(final File file, final File destDir) {
        final Document document = jsoup.loadDocument(file);
        document.outputSettings().prettyPrint(false);
        transformDoubleHyphensIntoEmDashesInDocument(document);
        transformBulletsInDocument(document);
        jsoup.saveDocument(destDir, file.getName(), document);
    }

    private void transformDoubleHyphensIntoEmDashesInDocument(final Document document) {
        Elements oConnorsAnnotations = document.getElementsByTag(OCONNORS_TAG).select(ALL);
        jsoup.transformCharSequencesInElements(oConnorsAnnotations, DOUBLE_HYPHEN, EM_DASH);
    }

    private void transformBulletsInDocument(final Document document) {
        jsoup.transformCharSequencesInElements(document.getAllElements(), BULL_3, WHITE_BULLET);
    }
}
