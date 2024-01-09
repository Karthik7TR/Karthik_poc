package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.frontmatter.exception.EBookFrontMatterGenerationException;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterAdditionalFrontMatterPageFilter;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterCopyrightPageFilter;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterResearchAssistancePageFilter;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterTitlePageFilter;
import com.thomsonreuters.uscl.ereader.frontmatter.parsinghandler.FrontMatterWestlawNextPageFilter;
import com.thomsonreuters.uscl.ereader.ioutil.EntityDecodedOutputStream;
import com.thomsonreuters.uscl.ereader.ioutil.EntityEncodedInputStream;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Setter
@Getter
@Service
public class BaseFrontMatterServiceImpl implements BaseFrontMatterService {
    private static final int STREAM_SIZE = 2048;
    @Value("classpath:templates/frontMatterTitleTemplate.xml")
    private Resource frontMatterTitlePageTemplate;
    @Value("classpath:templates/frontMatterCopyrightTemplate.xml")
    private Resource frontMatterCopyrightPageTemplate;
    @Value("classpath:templates/frontMatterCanadianCopyrightTemplate.xml")
    private Resource frontMatterCanadianCopyrightPageTemplate;
    @Value("classpath:templates/frontMatterAdditionalPagesTemplate.xml")
    private Resource frontMatterAdditionalPagesTemplate;
    @Value("classpath:templates/frontMatterResearchAssistanceTemplate.xml")
    private Resource frontMatterResearchAssistancePageTemplate;
    @Value("classpath:templates/frontMatterWestlawNextTemplate.xml")
    private Resource frontMatterWestlawNextPageTemplate;

    public String generateTitlePage(final BookDefinition bookDefinition, final boolean withPageNumbers) throws EBookFrontMatterGenerationException {
        return transformTemplate(
                new FrontMatterTitlePageFilter(bookDefinition, withPageNumbers),
                getFrontMatterTitlePageTemplate());
    }

    public String generateCopyrightPage(final BookDefinition bookDefinition, final boolean withPageNumbers)
            throws EBookFrontMatterGenerationException {
        Resource resource = bookDefinition.isCwBook()
                ? getFrontMatterCanadianCopyrightPageTemplate()
                : getFrontMatterCopyrightPageTemplate();
        return transformTemplate(
                new FrontMatterCopyrightPageFilter(bookDefinition, withPageNumbers),
                resource);

    }

    public String generateAdditionalFrontMatterPage(final BookDefinition bookDefinition, final Long pageId, final Map<String, List<String>> frontMatterPdfImageNames)
            throws EBookFrontMatterGenerationException {
        return transformTemplate(
                new FrontMatterAdditionalFrontMatterPageFilter(bookDefinition, pageId, frontMatterPdfImageNames),
                getFrontMatterAdditionalPagesTemplate());
    }

    public String generateResearchAssistancePage(final BookDefinition bookDefinition, final boolean withPageNumbers)
            throws EBookFrontMatterGenerationException {
        return transformTemplate(
                new FrontMatterResearchAssistancePageFilter(bookDefinition, withPageNumbers),
                getFrontMatterResearchAssistancePageTemplate());
    }

    public String generateWestlawNextPage(final boolean withPageNumbers) throws EBookFrontMatterGenerationException {
        return transformTemplate(new FrontMatterWestlawNextPageFilter(withPageNumbers), getFrontMatterWestlawNextPageTemplate());
    }

    private String transformTemplate(final XMLFilterImpl filter, final Resource template)
            throws EBookFrontMatterGenerationException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream(STREAM_SIZE);

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");

        final Serializer serializer = SerializerFactory.getSerializer(props);
        serializer.setOutputStream(new EntityDecodedOutputStream(outStream, true));
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        try {
            filter.setParent(saxParserFactory.newSAXParser().getXMLReader());
            filter.setContentHandler(serializer.asContentHandler());
            filter.parse(new InputSource(new EntityEncodedInputStream(template.getInputStream())));
        } catch (final IOException e) {
            final String message = "An IOException occurred while generating the Front Matter Title Page.";
            log.error(message);
            throw new EBookFrontMatterGenerationException(message, e);
        } catch (final SAXException e) {
            final String message = "Could not generate Front Matter Title Page.";
            log.error(message);
            throw new EBookFrontMatterGenerationException(message, e);
        } catch (final ParserConfigurationException e) {
            final String message =
                    "An exception occurred when configuring " + "the parser to generate the Front Matter Title Page.";
            log.error(message);
            throw new EBookFrontMatterGenerationException(message, e);
        }

        String output;
        try {
            output = outStream.toString("UTF-8");
        } catch (final UnsupportedEncodingException e) {
            final String message = "Could not encode front matter HTML into UTF-8.";
            log.error(message);
            throw new EBookFrontMatterGenerationException(message, e);
        }

        return output;
    }
}
