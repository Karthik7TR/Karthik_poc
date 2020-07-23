package com.thomsonreuters.uscl.ereader.frontmatter.service;

import com.thomsonreuters.uscl.ereader.FrontMatterFileName;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
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
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Service that generates HTML for all the Front Matter pages.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
@Slf4j
@Setter
@Getter
@Service
public class CreateFrontMatterServiceImpl implements CreateFrontMatterService {
    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_PLACEHOLDER = "er:#ebook_generator";
    private static final String WLN_LOGO_PLACEHOLDER = "er:#WestlawLogo";

    @Value("#{${frontMatter.logoPlaceHolder}}")
    private Map<String, String> frontMatterLogoPlaceHolder;

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

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#generateAllFrontMatterPages(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
     */
    @Override
    public void generateAllFrontMatterPages(final File outputDir, final BookDefinition bookDefinition, final boolean withPageNumbers, final Map<String, List<String>> frontMatterPdfImageNames)
        throws EBookFrontMatterGenerationException {
        final File titlePage = new File(outputDir, FrontMatterFileName.FRONT_MATTER_TITLE + HTML_EXTENSION);
        writeHTMLFile(titlePage, generateTitlePage(bookDefinition, withPageNumbers));

        log.debug("Front Matter Title HTML page generated.");

        final File copyrightPage = new File(outputDir, FrontMatterFileName.COPYRIGHT + HTML_EXTENSION);
        writeHTMLFile(copyrightPage, generateCopyrightPage(bookDefinition, withPageNumbers));

        log.debug("Front Matter Copyright HTML page generated.");

        for (final FrontMatterPage page : bookDefinition.getFrontMatterPages()) {
            final File additionalPage =
                new File(outputDir, FrontMatterFileName.ADDITIONAL_FRONT_MATTER + page.getId() + HTML_EXTENSION);
            writeHTMLFile(additionalPage, generateAdditionalFrontMatterPage(bookDefinition, page.getId(), frontMatterPdfImageNames));

            log.debug("Front Matter Additional HTML page " + page.getId() + " generated.");
        }

        if (!bookDefinition.isCwBook()) {
            final File researchAssistancePage =
                    new File(outputDir, FrontMatterFileName.RESEARCH_ASSISTANCE + HTML_EXTENSION);
            writeHTMLFile(researchAssistancePage, generateResearchAssistancePage(bookDefinition, withPageNumbers));
            log.debug("Front Matter Research Assistance HTML page generated.");

            final File westlawNextPage = new File(outputDir, FrontMatterFileName.WESTLAW + HTML_EXTENSION);
            writeHTMLFile(westlawNextPage, generateWestlawNextPage(withPageNumbers));
            log.debug("Front Matter WestlawNext HTML page generated.");
        }
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getTitlePage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
     */
    @Override
    public String getTitlePage(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException {
        String output = generateTitlePage(bookDefinition, false)
            .replace(CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css");
        output = replaceImages(output);
        return output;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getCopyrightPage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
     */
    @Override
    public String getCopyrightPage(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException {
        String output = generateCopyrightPage(bookDefinition, false)
            .replace(CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css");
        output = replaceImages(output);
        return output;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getAdditionalFrontPage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition, java.lang.Long)
     */
    @Override
    public String getAdditionalFrontPage(final BookDefinition bookDefinition, final Long front_matter_page_id)
        throws EBookFrontMatterGenerationException {
        final String output = generateAdditionalFrontMatterPage(bookDefinition, front_matter_page_id, Collections.emptyMap())
            .replace(CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css");
        return output;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getResearchAssistancePage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
     */
    @Override
    public String getResearchAssistancePage(final BookDefinition bookDefinition)
        throws EBookFrontMatterGenerationException {
        final String output = generateResearchAssistancePage(bookDefinition, false)
            .replace(CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css");
        return output;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getWestlawNextPage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
     */
    @Override
    public String getWestlawNextPage(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException {
        final String output =
            generateWestlawNextPage(false).replace(CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css")
                .replace(WLN_LOGO_PLACEHOLDER, "frontMatterImage.mvc?imageName=EBook_Generator_WestlawNextLogo.png");
        return output;
    }

    /**
     * Writes the passed in text to the specified file on the system.
     *
     * @param aFile target file
     * @param text HTML text to be writen to the file
     * @throws EBookFrontMatterGenerationException encountered issues while attempting to write
     * to the specified file
     */
    protected void writeHTMLFile(final File aFile, final String text) throws EBookFrontMatterGenerationException {
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aFile), "UTF8"))) {
            out.write(text);
            out.close();
        } catch (final IOException e) {
            final String errMessage = "Failed to write the following file to NAS: " + aFile.getAbsolutePath();
            log.error(errMessage);
            throw new EBookFrontMatterGenerationException(errMessage, e);
        }
    }

    private String replaceImages(String output) {
        for (final Map.Entry<String, String> entry : frontMatterLogoPlaceHolder.entrySet()) {
            output = output.replace(entry.getKey(), "frontMatterImage.mvc?imageName=" + entry.getValue());
        }
        return output;
    }

    /**
     * Transforms the template using text from BookDefinition to generate HTML for the Title Page.
     *
     * @param bookDefinition defines the book for which front matter is being generated
     * @return HTML that represents the Title page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    private String generateTitlePage(final BookDefinition bookDefinition, final boolean withPageNumbers) throws EBookFrontMatterGenerationException {
        return transformTemplate(
            new FrontMatterTitlePageFilter(bookDefinition, withPageNumbers),
            getFrontMatterTitlePageTemplate());
    }

    /**
     * Transforms the template using text from BookDefinition to generate HTML for the Copyright Page.
     *
     * @param bookDefinition defines the book for which front matter is being generated
     * @return HTML that represents the Title page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    private String generateCopyrightPage(final BookDefinition bookDefinition, final boolean withPageNumbers)
        throws EBookFrontMatterGenerationException {
        Resource resource = bookDefinition.isCwBook()
                ? getFrontMatterCanadianCopyrightPageTemplate()
                : getFrontMatterCopyrightPageTemplate();
        return transformTemplate(
                new FrontMatterCopyrightPageFilter(bookDefinition, withPageNumbers),
                resource);

    }

    /**
     * Transforms the template using text from BookDefinition to generate HTML for the Copyright Page.
     *
     * @param bookDefinition defines the book for which front matter is being generated
     * @param pageId additional front matter page identifier
     * @return HTML that represents the Title page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    private String generateAdditionalFrontMatterPage(final BookDefinition bookDefinition, final Long pageId, final Map<String, List<String>> frontMatterPdfImageNames)
        throws EBookFrontMatterGenerationException {
        return transformTemplate(
            new FrontMatterAdditionalFrontMatterPageFilter(bookDefinition, pageId, frontMatterPdfImageNames),
            getFrontMatterAdditionalPagesTemplate());
    }

    /**
     * Transforms the template using text from BookDefinition to generate HTML for the
     * Research Assistance Page.
     *
     * @param bookDefinition defines the book for which front matter is being generated
     * @return HTML that represents the Title page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    private String generateResearchAssistancePage(final BookDefinition bookDefinition, final boolean withPageNumbers)
        throws EBookFrontMatterGenerationException {
        return transformTemplate(
            new FrontMatterResearchAssistancePageFilter(bookDefinition, withPageNumbers),
            getFrontMatterResearchAssistancePageTemplate());
    }

    /**
     * Transforms the template to generate HTML for the WestlawNext Page.
     *
     * @return HTML that represents the Title page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    private String generateWestlawNextPage(final boolean withPageNumbers) throws EBookFrontMatterGenerationException {
        return transformTemplate(new FrontMatterWestlawNextPageFilter(withPageNumbers), getFrontMatterWestlawNextPageTemplate());
    }

    /**
     * Helper method that applies the passed in filter on the template to generate the HTML
     *
     * @param filter XML filter to be applied on the template
     * @param template template file associated with the filter
     * @return HTML representing the rendered page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    private String transformTemplate(final XMLFilterImpl filter, final Resource template)
        throws EBookFrontMatterGenerationException {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream(2048);

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

        String output = null;
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
