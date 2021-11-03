package com.thomsonreuters.uscl.ereader.core.service;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface JsoupService {
    Document loadDocument(final File file);

    Document parseXml(final String rootTag);

    Document parseHtml(final String htmlText);

    void transformCharSequencesInElements(final Elements elements, final String target, final String replacement);

    void saveDocument(final File destDir, final String fileName, final Element doc);

    void saveDocument(final File destFile, final Element doc);

    Optional<Node> firstChild(final Element element);

    List<XmlDeclaration> selectXmlProcessingInstructions(final Element element, final String target);

    List<Node> selectNodes(final Element element, final Predicate<Node> searchCondition);

    Document createDocument();
}
