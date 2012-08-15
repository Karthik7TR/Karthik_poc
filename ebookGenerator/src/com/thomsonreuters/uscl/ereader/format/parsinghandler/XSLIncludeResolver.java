/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

/**
 * Resolves XSL Include conflicts by including an empty XSL for any XSL that have
 * already been 
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class XSLIncludeResolver implements URIResolver 
{
	private static final Logger LOG = Logger.getLogger(XSLIncludeResolver.class);
	private static final String CONTEXT_AND_ANALYSIS = "ContextAndAnalysis.xsl";
	private List<String> includedXSLTs = new ArrayList<String>();
	private File emptyXSL = new File("/nas/Xslt/Universal/_Empty.xsl");
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
			File xsltBase = new File(new URI(base));
			File includeXSLT = new File(xsltBase.getParentFile(), href);
			
			if (includeXSLT.exists())
			{
				if (includedXSLTs.contains(includeXSLT.getCanonicalPath()))
				{
					source = new StreamSource(emptyXSL);
				}
				else if (includeAnnotations && includeXSLT.getName().equals(CONTEXT_AND_ANALYSIS))
				{
					includedXSLTs.add(includeXSLT.getCanonicalPath());
					
					// Use a different XSL style sheet if annotations is enabled for Context and Analysis content
					href = href.replaceFirst(CONTEXT_AND_ANALYSIS, "eBook" + CONTEXT_AND_ANALYSIS);
					includeXSLT = new File(xsltBase.getParentFile(), href);
					LOG.debug("includedXSLT: " + includeXSLT.getCanonicalPath());
					source = new StreamSource(includeXSLT);
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
				throw new TransformerException("Could not locat referenced '" + 
						includeXSLT.getAbsolutePath() + "' XSLT.");
			}
		}
		catch (URISyntaxException e)
		{
			throw new TransformerException("Could not resolve the URI in the XSLIncludeResolver for '" + 
					href + "' href and '" + base + "' base.");
		}
		catch (IOException e)
		{
			throw new TransformerException("Could not get canonical path for '" + 
					href + "' href and '" + base + "' base.");
		}
		
		return source;
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
