/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * Resolves XSL Include conflicts by including an empty XSL for any XSL that have
 * already been 
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XSLIncludeResolver implements URIResolver 
{
	private static final Logger LOG = Logger.getLogger(XSLIncludeResolver.class);
	private static final String CONTEXT_AND_ANALYSIS = "ContextAndAnalysis.xsl";
	private List<String> includedXSLTs = new ArrayList<String>();
	private File emptyXSL = new File("/apps/eBookBuilder/staticContent/Platform/Universal/_Empty.xsl");
	private File platformDir = new File("/apps/eBookBuilder/staticContent/Platform");
	private File westlawNextDir = new File("/apps/eBookBuilder/staticContent/WestlawNext/DefaultProductView");
	
	public File getPlatformDir() {
		return platformDir;
	}

	public void setPlatformDir(File platformDir) {
		this.platformDir = platformDir;
	}

	public File getWestlawNextDir() {
		return westlawNextDir;
	}

	public void setWestlawNextDir(File westlawNextDir) {
		this.westlawNextDir = westlawNextDir;
	}

	private boolean includeAnnotations = false;
	
	public boolean getIncludeAnnotations() 
	{
		return includeAnnotations;
	}

	public void setIncludeAnnotations(boolean includeAnnotations) 
	{
		this.includeAnnotations = includeAnnotations;
	}

	public File getEmptyXSL() 
	{
		return emptyXSL;
	}

	public void setEmptyXSL(File emptyXSL) 
	{
		this.emptyXSL = emptyXSL;
	}

	public Source resolve(String href, String base) throws TransformerException
	{
		StreamSource source = null;
		try
		{
			if (includeAnnotations && href.equalsIgnoreCase(CONTEXT_AND_ANALYSIS))
			{
				// Use a different XSL style sheet if annotations is enabled for Context and Analysis
				href = "eBook" + CONTEXT_AND_ANALYSIS;
			}
			
			boolean forcePlatform = findForcePlatformAttribute(href, base);
			File includeXSLT = findXslFile(href, forcePlatform);

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
		catch (IOException e)
		{
			throw new TransformerException("Could not get canonical path for '" + 
					href + "' href and '" + base + "' base.");
		}
		catch (Exception e)
		{
			throw new TransformerException(e);
		}
		
		return source;
	}
	
	private File findXslFile(String filename, boolean forcePlatform) throws IOException
	{
		File xsl = null;
		if(!forcePlatform)
		{
			xsl = recursivelySearchXslInDirectory(filename, westlawNextDir); 
		}
		
		if(xsl != null)
		{
			return xsl;
		} 
		else
		{
			return recursivelySearchXslInDirectory(filename, platformDir); 
		}
	}
	
	private File recursivelySearchXslInDirectory(String filename, File directory) throws IOException
	{
		Collection<File> xslFiles = FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		
		for(File xsl : xslFiles) 
		{
			if(!StringUtils.containsIgnoreCase(xsl.getCanonicalPath(), "CobaltMobile") && 
					!StringUtils.containsIgnoreCase(xsl.getCanonicalPath(), "web2") &&
					!StringUtils.containsIgnoreCase(xsl.getCanonicalPath(), "Weblinks") &&
					xsl.getName().equals(filename)) 
			{
				return xsl;
			}
		}
		
		return null;
	}
	
	private boolean findForcePlatformAttribute(String href, String base) throws Exception
	{
		try 
		{
			File xsltBase = new File(new URI(base));
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			XSLForcePlatformAttributeFilter forcePlatformFilter = new XSLForcePlatformAttributeFilter(href);
			saxParser.parse(xsltBase, forcePlatformFilter);

			return forcePlatformFilter.isForcePlatform();
		}
		catch(IOException e)
		{
			String errMessage = "Unable to perform IO operations related to following source file: " + base;
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
		catch(SAXException e)
		{
			String errMessage = "Encountered a SAX Exception while processing: " + base;
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
		catch(ParserConfigurationException e)
		{
			String errMessage = "Encountered a SAX Parser Configuration Exception while processing: " + base;
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
	}

	protected List<String> getIncludedXSLTs() 
	{
		return includedXSLTs;
	}

	protected void setIncludedXSLTs(List<String> includedXSLTs) 
	{
		this.includedXSLTs = includedXSLTs;
	}
}
