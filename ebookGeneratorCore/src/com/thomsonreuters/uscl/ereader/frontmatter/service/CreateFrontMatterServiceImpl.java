package com.thomsonreuters.uscl.ereader.frontmatter.service;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Service that generates HTML for all the Front Matter pages.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class CreateFrontMatterServiceImpl implements CreateFrontMatterService, ResourceLoaderAware
{
    private static final Logger LOG = LogManager.getLogger(CreateFrontMatterServiceImpl.class);
    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_PLACEHOLDER = "er:#ebook_generator";
    private static final String WLN_LOGO_PLACEHOLDER = "er:#EBook_Generator_WestlawNextLogo";

    private Map<String, String> frontMatterLogoPlaceHolder = new HashMap<>();

    private ResourceLoader resourceLoader;
    private String frontMatterTitlePageTemplateLocation;
    private String frontMatterCopyrightPageTemplateLocation;
    private String frontMatterAdditionalPagesTemplateLocation;
    private String frontMatterResearchAssistancePageTemplateLocation;
    private String frontMatterWestlawNextPageTemplateLocation;

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#generateAllFrontMatterPages(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
     */
    @Override
    public void generateAllFrontMatterPages(final File outputDir, final BookDefinition bookDefinition)
        throws EBookFrontMatterGenerationException
    {
        final File titlePage = new File(outputDir, FrontMatterFileName.FRONT_MATTER_TITLE + HTML_EXTENSION);
        writeHTMLFile(titlePage, generateTitlePage(bookDefinition));

        LOG.debug("Front Matter Title HTML page generated.");

        final File copyrightPage = new File(outputDir, FrontMatterFileName.COPYRIGHT + HTML_EXTENSION);
        writeHTMLFile(copyrightPage, generateCopyrightPage(bookDefinition));

        LOG.debug("Front Matter Copyright HTML page generated.");

        for (final FrontMatterPage page : bookDefinition.getFrontMatterPages())
        {
            final File additionalPage =
                new File(outputDir, FrontMatterFileName.ADDITIONAL_FRONT_MATTER + page.getId() + HTML_EXTENSION);
            writeHTMLFile(additionalPage, generateAdditionalFrontMatterPage(bookDefinition, page.getId()));

            LOG.debug("Front Matter Additional HTML page " + page.getId() + " generated.");
        }

        final File researchAssistancePage = new File(outputDir, FrontMatterFileName.RESEARCH_ASSISTANCE + HTML_EXTENSION);
        writeHTMLFile(researchAssistancePage, generateResearchAssistancePage(bookDefinition));

        LOG.debug("Front Matter Research Assistance HTML page generated.");

        final File westlawNextPage = new File(outputDir, FrontMatterFileName.WESTLAW + HTML_EXTENSION);
        writeHTMLFile(westlawNextPage, generateWestlawNextPage());

        LOG.debug("Front Matter WestlawNext HTML page generated.");
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getTitlePage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
     */
    @Override
    public String getTitlePage(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException
    {
        String output = generateTitlePage(bookDefinition)
            .replace(CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css");
        for (final Map.Entry<String, String> entry : frontMatterLogoPlaceHolder.entrySet())
        {
            output = output.replace(entry.getKey(), "frontMatterImage.mvc?imageName=" + entry.getValue());
        }
        return output;
    }

    public Map<String, String> getFrontMatterLogoPlaceHolder()
    {
        return frontMatterLogoPlaceHolder;
    }

    public void setFrontMatterLogoPlaceHolder(final Map<String, String> frontMatterLogoPlaceHolder)
    {
        this.frontMatterLogoPlaceHolder = frontMatterLogoPlaceHolder;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getCopyrightPage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
     */
    @Override
    public String getCopyrightPage(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException
    {
        final String output = generateCopyrightPage(bookDefinition)
            .replace(CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css");
        return output;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getAdditionalFrontPage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition, java.lang.Long)
     */
    @Override
    public String getAdditionalFrontPage(final BookDefinition bookDefinition, final Long front_matter_page_id)
        throws EBookFrontMatterGenerationException
    {
        final String output = generateAdditionalFrontMatterPage(bookDefinition, front_matter_page_id)
            .replace(CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css");
        return output;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getResearchAssistancePage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
     */
    @Override
    public String getResearchAssistancePage(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException
    {
        final String output = generateResearchAssistancePage(bookDefinition)
            .replace(CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css");
        return output;
    }

    /* (non-Javadoc)
     * @see com.thomsonreuters.uscl.ereader.format.service.CreateFrontMatterService#getWestlawNextPage(com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition)
     */
    @Override
    public String getWestlawNextPage(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException
    {
        final String output =
            generateWestlawNextPage().replace(CSS_PLACEHOLDER, "frontMatterCss.mvc?cssName=ebook_generator.css")
                .replace(WLN_LOGO_PLACEHOLDER, "frontMatterImage.mvc?imageName=EBook_Generator_WestlawNextLogo.png");
        return output;
    }

    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public void setFrontMatterTitlePageTemplateLocation(final String frontMatterTitlePageTemplateLocation)
    {
        this.frontMatterTitlePageTemplateLocation = frontMatterTitlePageTemplateLocation;
    }

    public void setFrontMatterCopyrightPageTemplateLocation(final String frontMatterCopyrightPageTemplateLocation)
    {
        this.frontMatterCopyrightPageTemplateLocation = frontMatterCopyrightPageTemplateLocation;
    }

    public void setFrontMatterAdditionalPagesTemplateLocation(final String frontMatterAdditionalPagesTemplateLocation)
    {
        this.frontMatterAdditionalPagesTemplateLocation = frontMatterAdditionalPagesTemplateLocation;
    }

    public void setFrontMatterResearchAssistancePageTemplateLocation(
        final String frontMatterResearchAssistancePageTemplateLocation)
    {
        this.frontMatterResearchAssistancePageTemplateLocation = frontMatterResearchAssistancePageTemplateLocation;
    }

    public void setFrontMatterWestlawNextPageTemplateLocation(final String frontMatterWestlawNextPageTemplateLocation)
    {
        this.frontMatterWestlawNextPageTemplateLocation = frontMatterWestlawNextPageTemplateLocation;
    }

    /**
     * Writes the passed in text to the specified file on the system.
     *
     * @param aFile target file
     * @param text HTML text to be writen to the file
     * @throws EBookFrontMatterGenerationException encountered issues while attempting to write
     * to the specified file
     */
    protected void writeHTMLFile(final File aFile, final String text) throws EBookFrontMatterGenerationException
    {
        Writer out = null;

        try
        {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(aFile), "UTF8"));
            out.write(text);
            out.close();
        }
        catch (final IOException e)
        {
            final String errMessage = "Failed to write the following file to NAS: " + aFile.getAbsolutePath();
            LOG.error(errMessage);
            throw new EBookFrontMatterGenerationException(errMessage, e);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (final IOException e)
            {
                LOG.error("Unable to close I/O streams.", e);
            }
        }
    }

    /**
     * Transforms the template using text from BookDefinition to generate HTML for the Title Page.
     *
     * @param bookDefinition defines the book for which front matter is being generated
     * @return HTML that represents the Title page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    protected String generateTitlePage(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException
    {
        return transformTemplate(
            new FrontMatterTitlePageFilter(bookDefinition),
            getFrontMatterTitlePageTemplateLocation());
    }

    /**
     * Transforms the template using text from BookDefinition to generate HTML for the Copyright Page.
     *
     * @param bookDefinition defines the book for which front matter is being generated
     * @return HTML that represents the Title page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    protected String generateCopyrightPage(final BookDefinition bookDefinition) throws EBookFrontMatterGenerationException
    {
        return transformTemplate(
            new FrontMatterCopyrightPageFilter(bookDefinition),
            getFrontMatterCopyrightTemplateLocation());
    }

    /**
     * Transforms the template using text from BookDefinition to generate HTML for the Copyright Page.
     *
     * @param bookDefinition defines the book for which front matter is being generated
     * @param pageId additional front matter page identifier
     * @return HTML that represents the Title page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    protected String generateAdditionalFrontMatterPage(final BookDefinition bookDefinition, final Long pageId)
        throws EBookFrontMatterGenerationException
    {
        return transformTemplate(
            new FrontMatterAdditionalFrontMatterPageFilter(bookDefinition, pageId),
            getFrontMatterAdditionalPagesTemplateLocation());
    }

    /**
     * Transforms the template using text from BookDefinition to generate HTML for the
     * Research Assistance Page.
     *
     * @param bookDefinition defines the book for which front matter is being generated
     * @return HTML that represents the Title page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    protected String generateResearchAssistancePage(final BookDefinition bookDefinition)
        throws EBookFrontMatterGenerationException
    {
        return transformTemplate(
            new FrontMatterResearchAssistancePageFilter(bookDefinition),
            getFrontMatterResearchAssistanceTemplateLocation());
    }

    /**
     * Transforms the template to generate HTML for the WestlawNext Page.
     *
     * @return HTML that represents the Title page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    protected String generateWestlawNextPage() throws EBookFrontMatterGenerationException
    {
        return transformTemplate(new FrontMatterWestlawNextPageFilter(), getFrontMatterWestlawNextTemplateLocation());
    }

    /**
     * Helper method that applies the passed in filter on the template to generate the HTML
     *
     * @param filter XML filter to be applied on the template
     * @param template template file associated with the filter
     * @return HTML representing the rendered page
     * @throws EBookFrontMatterGenerationException encountered a failure while transforming the template
     */
    private String transformTemplate(final XMLFilterImpl filter, final Resource template) throws EBookFrontMatterGenerationException
    {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream(2048);

        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");

        final Serializer serializer = SerializerFactory.getSerializer(props);
        serializer.setOutputStream(new EntityDecodedOutputStream(outStream, true));
        final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();

        try
        {
            filter.setParent(saxParserFactory.newSAXParser().getXMLReader());
            filter.setContentHandler(serializer.asContentHandler());
            filter.parse(new InputSource(new EntityEncodedInputStream(template.getInputStream())));
        }
        catch (final IOException e)
        {
            final String message = "An IOException occurred while generating the Front Matter Title Page.";
            LOG.error(message);
            throw new EBookFrontMatterGenerationException(message, e);
        }
        catch (final SAXException e)
        {
            final String message = "Could not generate Front Matter Title Page.";
            LOG.error(message);
            throw new EBookFrontMatterGenerationException(message, e);
        }
        catch (final ParserConfigurationException e)
        {
            final String message =
                "An exception occurred when configuring " + "the parser to generate the Front Matter Title Page.";
            LOG.error(message);
            throw new EBookFrontMatterGenerationException(message, e);
        }

        String output = null;
        try
        {
            output = outStream.toString("UTF-8");
        }
        catch (final UnsupportedEncodingException e)
        {
            final String message = "Could not encode front matter HTML into UTF-8.";
            LOG.error(message);
            throw new EBookFrontMatterGenerationException(message, e);
        }

        return output;
    }

    private Resource getFrontMatterTitlePageTemplateLocation()
    {
        return resourceLoader.getResource(frontMatterTitlePageTemplateLocation);
    }

    private Resource getFrontMatterCopyrightTemplateLocation()
    {
        return resourceLoader.getResource(frontMatterCopyrightPageTemplateLocation);
    }

    private Resource getFrontMatterAdditionalPagesTemplateLocation()
    {
        return resourceLoader.getResource(frontMatterAdditionalPagesTemplateLocation);
    }

    private Resource getFrontMatterResearchAssistanceTemplateLocation()
    {
        return resourceLoader.getResource(frontMatterResearchAssistancePageTemplateLocation);
    }

    private Resource getFrontMatterWestlawNextTemplateLocation()
    {
        return resourceLoader.getResource(frontMatterWestlawNextPageTemplateLocation);
    }
}
