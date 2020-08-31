package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collection;

@Service
public class TransformTocServiceImpl implements TransformTocService {
    private static final String NAME_TAG = "Name";
    private static final String ALL = "*";
    private static final String DOUBLE_HYPHEN = "--";
    private static final String EM_DASH = "\u2014";

    @Autowired
    private JsoupService jsoup;

    @Override
    public void transformToc(final File toc, final File destDir) {
        final Document tocDocument = jsoup.loadDocument(toc);
        tocDocument.outputSettings().prettyPrint(false);
        transformDoubleHyphensIntoLongDashes(tocDocument);
        jsoup.saveDocument(destDir, toc.getName(), tocDocument);
    }

    private void transformDoubleHyphensIntoLongDashes(final Document document) {
        Elements elements = document.getElementsByTag(NAME_TAG).select(ALL);
        jsoup.transformCharSequencesInElements(elements, DOUBLE_HYPHEN, EM_DASH);
    }
}
