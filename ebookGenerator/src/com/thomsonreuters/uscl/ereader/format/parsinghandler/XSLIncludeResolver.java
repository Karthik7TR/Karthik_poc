package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Resolves XSL Include conflicts by including an empty XSL for any XSL that have
 * already been
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XSLIncludeResolver implements URIResolver
{
    private static final Logger LOG = LogManager.getLogger(XSLIncludeResolver.class);
    private static final String CONTEXT_AND_ANALYSIS = "ContextAndAnalysis.xsl";
    private static final String NOTES_OF_DECISIONS = "NotesOfDecisions.xsl";
    private List<String> includedXSLTs = new ArrayList<>();
    private File emptyXSL;
    private File platformDir;
    private File westlawNextDir;

    private boolean includeAnnotations;
    private boolean includeNotesOfDecisions;

    public File getPlatformDir()
    {
        return platformDir;
    }

    public void setPlatformDir(final File platformDir)
    {
        this.platformDir = platformDir;
    }

    public File getWestlawNextDir()
    {
        return westlawNextDir;
    }

    public void setWestlawNextDir(final File westlawNextDir)
    {
        this.westlawNextDir = westlawNextDir;
    }

    public boolean getIncludeAnnotations()
    {
        return includeAnnotations;
    }

    public void setIncludeAnnotations(final boolean includeAnnotations)
    {
        this.includeAnnotations = includeAnnotations;
    }

    public boolean isIncludeNotesOfDecisions()
    {
        return includeNotesOfDecisions;
    }

    public void setIncludeNotesOfDecisions(final boolean includeNotesOfDecisions)
    {
        this.includeNotesOfDecisions = includeNotesOfDecisions;
    }

    public File getEmptyXSL()
    {
        return emptyXSL;
    }

    public void setEmptyXSL(final File emptyXSL)
    {
        this.emptyXSL = emptyXSL;
    }

    @Override
    public Source resolve(String href, final String base) throws TransformerException
    {
        StreamSource source = null;
        try
        {
            if (includeAnnotations && href.equalsIgnoreCase(CONTEXT_AND_ANALYSIS))
            {
                // Use a different XSL style sheet if annotations is enabled for Context and Analysis
                href = "eBook" + CONTEXT_AND_ANALYSIS;
            }
            else if (includeNotesOfDecisions && href.equalsIgnoreCase(NOTES_OF_DECISIONS))
            {
                // Use a different XSL style sheet for Notes of Decision in case they are enabled
                href = "eBook" + NOTES_OF_DECISIONS;
            }

            final boolean forcePlatform = findForcePlatformAttribute(href, base);
            final File includeXSLT = findXslFile(href, forcePlatform);

            if (includeXSLT != null)
            {
                if (includedXSLTs.contains(includeXSLT.getCanonicalPath()))
                {
                    source = new StreamSource(emptyXSL);
                }
                else
                {
                    LOG.debug("includedXSLT: " + includeXSLT.getCanonicalPath());
                    includedXSLTs.add(includeXSLT.getCanonicalPath());
                    source = new StreamSource(includeXSLT);
                }
            }
            else
            {
                throw new TransformerException("Could not locate referenced '" + href + "' XSLT.");
            }
        }
        catch (final IOException e)
        {
            throw new TransformerException(
                "Could not get canonical path for '" + href + "' href and '" + base + "' base.", e);
        }
        catch (final Exception e)
        {
            throw new TransformerException(e);
        }

        return source;
    }

    private File findXslFile(final String filename, final boolean forcePlatform) throws IOException
    {
        File xsl = null;
        if (!forcePlatform)
        {
            xsl = recursivelySearchXslInDirectory(filename, westlawNextDir);
        }

        if (xsl != null)
        {
            return xsl;
        }
        else
        {
            return recursivelySearchXslInDirectory(filename, platformDir);
        }
    }

    private File recursivelySearchXslInDirectory(final String filename, final File directory) throws IOException
    {
        final Collection<File> xslFiles =
            FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
        for (final File xsl : xslFiles)
        {
            if (!StringUtils.containsIgnoreCase(xsl.getCanonicalPath(), "CobaltMobile")
                && !StringUtils.containsIgnoreCase(xsl.getCanonicalPath(), "web2")
                && !StringUtils.containsIgnoreCase(xsl.getCanonicalPath(), "Weblinks")
                && xsl.getName().equals(filename))
            {
                return xsl;
            }
        }

        return null;
    }

    private boolean findForcePlatformAttribute(final String href, final String base) throws Exception
    {
        try
        {
            final File xsltBase = new File(new URI(base));

            final SAXParserFactory factory = SAXParserFactory.newInstance();
            final SAXParser saxParser = factory.newSAXParser();

            final XSLForcePlatformAttributeFilter forcePlatformFilter = new XSLForcePlatformAttributeFilter(href);
            saxParser.parse(xsltBase, forcePlatformFilter);

            return forcePlatformFilter.isForcePlatform();
        }
        catch (final IOException e)
        {
            final String errMessage = "Unable to perform IO operations related to following source file: " + base;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
        catch (final SAXException e)
        {
            final String errMessage = "Encountered a SAX Exception while processing: " + base;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
        catch (final ParserConfigurationException e)
        {
            final String errMessage = "Encountered a SAX Parser Configuration Exception while processing: " + base;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
    }

    protected List<String> getIncludedXSLTs()
    {
        return includedXSLTs;
    }

    protected void setIncludedXSLTs(final List<String> includedXSLTs)
    {
        this.includedXSLTs = includedXSLTs;
    }
}
