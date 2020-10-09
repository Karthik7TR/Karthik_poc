package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Component
public class MinorVersionMappingFileSaver {
    private static final String MAPPING = "mapping";
    private static final String DOC = "doc";
    private static final String OLDID = "oldid";
    private static final String NEWIDS = "newids";
    private static final String DELIMITER = ";";

    @Autowired
    private JsoupService jsoup;

    public void saveMapToFile(final Map<String, Set<String>> oldIdToNewIds, final File destDir, final String fileName) {
        Document document = jsoup.createDocument();

        Element mapping = document.appendElement(MAPPING);
        new TreeMap<>(oldIdToNewIds).forEach((oldId, newIds) -> {
            mapping.appendChild(docElement(oldId, String.join(DELIMITER, newIds)));
        });

        jsoup.saveDocument(destDir, fileName, document);
    }

    private Node docElement(final String oldId, final String newIds) {
        Element doc = new Element(DOC);
        doc.attr(OLDID, oldId);
        doc.attr(NEWIDS, newIds);
        return doc;
    }
}
