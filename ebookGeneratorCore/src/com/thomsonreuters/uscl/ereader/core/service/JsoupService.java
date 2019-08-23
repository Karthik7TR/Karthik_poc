package com.thomsonreuters.uscl.ereader.core.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.parser.Parser;
import org.springframework.stereotype.Component;

@Component
public class JsoupService {
    public Document loadDocument(final File file) {
        try (InputStream input = new FileInputStream(file)) {
            return Jsoup.parse(input, StandardCharsets.UTF_8.name(), "", Parser.xmlParser());
        } catch (final IOException e) {
            throw new EBookException(e);
        }
    }

    public void saveDocument(final File destDir, final File file, final Document doc) {
        try {
            FileUtils.write(new File(destDir, file.getName()), doc.outerHtml(), StandardCharsets.UTF_8.name());
        } catch (final IOException e) {
            throw new EBookException(e);
        }
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
}
