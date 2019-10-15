package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.nio.file.Files;
import java.util.Collection;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import lombok.SneakyThrows;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransformDoubleHyphensIntoEmDashesService {

    private static final String OCONNORS_TAG = "oconnor.annotations";
    private static final String DOUBLE_HYPHEN = "--";
    private static final String EM_DASH = "\u2014";

    @Autowired
    private JsoupService jsoup;

    @SneakyThrows
    public void transformDoubleHyphensIntoEmDashes(final File srcDir, final File destDir) {
        Files.list(srcDir.toPath())
            .filter(Files::isRegularFile)
            .forEach(path -> transformDoubleHyphensIntoEmDashesInFile(path.toFile(), destDir));
    }

    private void transformDoubleHyphensIntoEmDashesInFile(final File file, final File destDir) {
        final Document document = jsoup.loadDocument(file);
        document.outputSettings().prettyPrint(false);
        transformDoubleHyphensIntoEmDashesInDocument(document);
        jsoup.saveDocument(destDir, file.getName(), document);
    }

    private void transformDoubleHyphensIntoEmDashesInDocument(final Document document) {
        document.getElementsByTag(OCONNORS_TAG).select("*").stream()
            .map(Element::textNodes)
            .flatMap(Collection::stream)
            .forEach(textNode ->
                textNode.text(textNode.text().replaceAll(DOUBLE_HYPHEN, EM_DASH)));
    }
}
