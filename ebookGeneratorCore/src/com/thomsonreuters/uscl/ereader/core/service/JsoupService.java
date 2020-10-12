package com.thomsonreuters.uscl.ereader.core.service;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class JsoupService {
    private static final String TEXT_NODE = "#text";

    public Document loadDocument(final File file) {
        try (InputStream input = new FileInputStream(file)) {
            final Document doc = Jsoup.parse(input, StandardCharsets.UTF_8.name(), StringUtils.EMPTY, Parser.xmlParser());
            applyXmlSettings(doc);
            return doc;
        } catch (final IOException e) {
            throw new EBookException(e);
        }
    }

    public Document parseXml(final String rootTag) {
        final Document doc = Jsoup.parse(String.format("<%s/>", rootTag), StringUtils.EMPTY, Parser.xmlParser());
        applyXmlSettings(doc);
        return doc;
    }

    private void applyXmlSettings(final Document doc) {
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        doc.outputSettings().escapeMode(EscapeMode.xhtml);
    }

    public void transformCharSequencesInElements(final Elements elements, final String target, final String replacement) {
        elements.stream()
                .map(Element::textNodes)
                .flatMap(Collection::stream)
                .forEach(textNode -> textNode.text(textNode.text()
                        .replaceAll(target, replacement)));
    }

    public void saveDocument(final File destDir, final String fileName, final Element doc) {
        saveDocument(new File(destDir, fileName), doc);
    }

    public void saveDocument(final File destFile, final Element doc) {
        try {
            FileUtils.write(destFile, doc.outerHtml(), StandardCharsets.UTF_8.name());
        } catch (final IOException e) {
            throw new EBookException(e);
        }
    }

    public Optional<Node> firstChild(final Element element) {
        return element.childNodes().stream().filter(node -> !TEXT_NODE.equals(node.nodeName())).findFirst();
    }

    public List<XmlDeclaration> selectXmlProcessingInstructions(final Element element, final String target) {
        final List<XmlDeclaration> foundNodes = new ArrayList<>();
        treeSearch(element, node -> node instanceof XmlDeclaration && target.equals(((XmlDeclaration) node).name()), foundNodes);
        return foundNodes;
    }

    private void treeSearch(final Node element, final Predicate<Node> searchCondition, final List<XmlDeclaration> output) {
        element.childNodes().forEach(child -> {
            if (searchCondition.test(child)) {
                output.add((XmlDeclaration) child);
            }
            treeSearch(child, searchCondition, output);
        });
    }

    public Document createDocument() {
        return parseXml(StringUtils.EMPTY);
    }
}
