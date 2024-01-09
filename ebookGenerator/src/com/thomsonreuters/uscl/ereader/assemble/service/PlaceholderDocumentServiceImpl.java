package com.thomsonreuters.uscl.ereader.assemble.service;

import com.thomsonreuters.uscl.ereader.assemble.exception.PlaceholderDocumentServiceException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.PlaceholderDocumentFilter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * PlaceholderDocumentServiceImpl utilizes a SAX Parser and XML document template to generate placeholder documents.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@Setter
@Getter
@Service
public class PlaceholderDocumentServiceImpl implements PlaceholderDocumentService {
    @Value("classpath:templates/placeholderDocumentTemplate.xml")
    private Resource placeholderDocumentTemplate;

    @Override
    public void generatePlaceholderDocument(
        final OutputStream documentStream,
        final String displayText,
        final String tocGuid,
        final List<String> anchors) throws PlaceholderDocumentServiceException {
        if (StringUtils.isBlank(displayText)) {
            throw new IllegalArgumentException("displayText must not be null or empty. Was: [" + displayText + "]");
        }
        if (StringUtils.isBlank(tocGuid)) {
            throw new IllegalArgumentException("tocGuid must not be null or empty. Was: [" + tocGuid + "]");
        }
        if (documentStream == null) {
            throw new IllegalArgumentException(
                "The OutputStream to write the placeholder document to must not be null. Confirm that the caller of this method has supplied a valid OutputStream.");
        }
        if (Objects.isNull(placeholderDocumentTemplate) || !placeholderDocumentTemplate.exists()) {
            throw new IllegalArgumentException(
                "The placeholderDocumentTemplateLocation was not configured properly (missing or null). This is likely a Spring configuration error that needs to be resolved by a developer.");
        }
        final PlaceholderDocumentFilter placeholderDocumentFilter =
            new PlaceholderDocumentFilter(displayText, tocGuid, anchors);

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");

        final Serializer serializer = SerializerFactory.getSerializer(props);
        serializer.setOutputStream(documentStream);
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        try {
            placeholderDocumentFilter.setParent(saxParserFactory.newSAXParser().getXMLReader());
            placeholderDocumentFilter.setContentHandler(serializer.asContentHandler());
            placeholderDocumentFilter.parse(new InputSource(getPlaceholderDocumentTemplate().getInputStream()));
        } catch (final IOException e) {
            throw new PlaceholderDocumentServiceException(
                "An IOException occurred while generating the placeholder document.",
                e);
        } catch (final SAXException e) {
            throw new PlaceholderDocumentServiceException("Could not generate placeholder document", e);
        } catch (final ParserConfigurationException e) {
            throw new PlaceholderDocumentServiceException(
                "An exception occurred when configuring the parser to generate the placeholder document.",
                e);
        }
    }
}
