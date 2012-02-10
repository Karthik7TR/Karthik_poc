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

/**
 * Resolves XSL Include conflicts by including an empty XSL for any XSL that have
 * already been 
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class XSLIncludeResolver implements URIResolver {
	private List<String> includedXSLTs = new ArrayList<String>();
	private File emptyXSL = new File("/nas/Xslt/Universal/_Empty.xsl");
	
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
				else
				{
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
}
