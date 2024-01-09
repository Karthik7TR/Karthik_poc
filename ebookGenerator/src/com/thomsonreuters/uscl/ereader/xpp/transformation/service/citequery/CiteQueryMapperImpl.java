package com.thomsonreuters.uscl.ereader.xpp.transformation.service.citequery;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.step.XppTransformationStep;
import com.thomsonreuters.uscl.ereader.xpp.utils.links.CiteQueryProcessor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@Service
public class CiteQueryMapperImpl implements CiteQueryMapper {
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Autowired
    private CiteQueryProcessor citeQueryProcessor;

    @Override
    public @NotNull CiteQueryMapperResponse createMappingFile(
        @NotNull final File htmlFile,
        @NotNull final String materialNumber,
        @NotNull final XppTransformationStep step) {
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();
            final Handler handler = new Handler(materialNumber, htmlFile, step);
            saxParser.parse(htmlFile, handler);
            return handler.getResponse();
        } catch (final Exception e) {
            throw new RuntimeException("Cannot parse file", e);
        }
    }

    private final class Handler extends DefaultHandler {
        private Map<String, String> idToHrefMap;
        private StringBuilder tagBuilder;
        private boolean isInsideCiteQuery;
        private String materialNumber;
        private String currentId;
        private File inputFile;
        private XppTransformationStep step;
        private final CiteQueryMapperResponse response;

        private Handler(final String materialNumber, final File inputFile, final XppTransformationStep step) {
            idToHrefMap = new HashMap<>();
            tagBuilder = new StringBuilder();
            this.materialNumber = materialNumber;
            this.inputFile = inputFile;
            this.step = step;
            response = new CiteQueryMapperResponse(
                "file:///" + getOutputFile().getAbsolutePath().replace("\\", "/").replace("//", "/"));
        }

        private File getOutputFile() {
            return fileSystem.getExternalLinksMappingFile(step, materialNumber, inputFile.getName());
        }

        public CiteQueryMapperResponse getResponse() {
            return response;
        }

        @Override
        public void startElement(
            final String uri,
            final String localName,
            final String qName,
            final Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase("cite.query")) {
                isInsideCiteQuery = true;
                tagBuilder.append("<cite.query ");
                for (int i = 0; i < attributes.getLength(); i++) {
                    tagBuilder.append(attributes.getQName(i) + "=\"");
                    tagBuilder.append(attributes.getValue(i) + "\"");
                    tagBuilder.append(i == attributes.getLength() - 1 ? ">" : " ");
                }
                currentId = attributes.getValue("ID");
            }
        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            if (isInsideCiteQuery) {
                isInsideCiteQuery = false;
                tagBuilder.append("</cite.query>");
                try {
                    final String citeQueryTag = tagBuilder.toString();
                    final String reference = citeQueryProcessor.getLink(citeQueryTag);
                    if (StringUtils.isNotBlank(reference)) {
                        idToHrefMap.put(currentId, reference);
                    } else {
                        response.addFailedTag(citeQueryTag);
                    }
                } catch (final Exception e) {
                    throw new SAXException(e);
                }
                tagBuilder = new StringBuilder();
            }
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            if (isInsideCiteQuery) {
                tagBuilder.append(new String(ch, start, length));
            }
        }

        @Override
        public void endDocument() throws SAXException {
            final File output = getOutputFile();
            output.getParentFile().mkdirs();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(output))) {
                writer.write("<mapping>");
                for (final Map.Entry<String, String> entry : idToHrefMap.entrySet()) {
                    final StringBuilder sb = new StringBuilder();
                    sb.append("<entry id=\"");
                    sb.append(entry.getKey());
                    sb.append("\" href=\"");
                    sb.append(entry.getValue().replace("&", "&amp;"));
                    sb.append("\" />");
                    writer.write(sb.toString());
                }
                writer.write("</mapping>");
            } catch (final IOException e) {
                throw new SAXException(e);
            }
        }
    }
}
