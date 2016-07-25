/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;

/**
 * Parses through the Title.xml file and modifies the "s" attribute of each toc entry
 * to the correct format.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TitleXMLTOCFilter extends XMLFilterImpl{
	
	private static final Logger LOG = LogManager.getLogger(TitleXMLTOCFilter.class);
	private Map<String, String> anchors;
	
	public TitleXMLTOCFilter(File anchorMap) throws EBookFormatException
	{
        if (anchorMap == null || !anchorMap.exists())
        {
        	throw new IllegalArgumentException(
        			"File passed into TitleXMLTOCFilter constructor must be a valid file.");
        }
		
		anchors = new HashMap<String, String>();
		
		BufferedReader reader = null;
		try
		{
			LOG.info("Reading in TOC anchor map file...");
			int numTocs = 0;
			reader = new BufferedReader(new FileReader(anchorMap));
			String input = reader.readLine();
			while (input != null)
			{
				String[] line = input.split(",", -1);
				if (!line[1].equals(""))
				{
					String[] tocGuids = line[1].split("\\|");
					for (String toc : tocGuids)
					{
						numTocs++;
						anchors.put(toc, line[0]);
					}
				}
				else
				{
					String message = "Please verify that each document GUID in the following file has " +
							"at least one TOC guid associated with it: " + 
							anchorMap.getAbsolutePath();
					LOG.error(message);
					throw new EBookFormatException(message);
				}
				input = reader.readLine();
			}
			LOG.info("Generated a map for " + numTocs + " TOCs.");
		}
		catch(IOException e)
		{
			String message = "Could not read the DOC guid to TOC guid map file: " + 
					anchorMap.getAbsolutePath();
			LOG.error(message);
			throw new EBookFormatException(message, e);
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException e)
			{
				LOG.error("Unable to close DOC guid to TOC guid file reader.", e);
			}
		}
		
		if (anchors.size()==0)
		{
			String message = "No TOC to DOC mapping were loaded, please double check that the following" +
					" file is not empty: " + 
					anchorMap.getAbsolutePath();
			LOG.error(message);
			throw new EBookFormatException(message);
		}
	}
	
	/**
	 * Returns the build up anchor map that was created from parsing the
	 * passed in anchor map file.
	 * 
	 * @return map of TOC Guids to Doc Guids
	 */
	public Map<String, String> getTocToDocMapping()
	{
		return anchors;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) 
			throws SAXException
	{
		if (qName.equalsIgnoreCase("entry"))
		{
			if (atts != null)
			{
				String tocGuid = atts.getValue("s");
				if (tocGuid != null)
				{
					if (tocGuid.length() > 0)
					{
						AttributesImpl newAtts = new AttributesImpl(atts);
						
						newAtts.removeAttribute(newAtts.getIndex("s"));
						String docGuid = anchors.get(tocGuid);
						if (docGuid == null || docGuid.length() < 1)
						{
							String message = "Could not find DOC Guid in mapping file for TOC: " + tocGuid;
							LOG.error(message);
							throw new SAXException(message);
						}
						String newAnchorRef = docGuid + "/" + tocGuid;
						newAtts.addAttribute("", "", "s", "CDATA", newAnchorRef);
						
						super.startElement(uri, localName, qName, newAtts);
					}
					else
					{
						String message = "Encountered TOC Guid that was empty please take a look at Title.xml file.";
						LOG.error(message);
						throw new SAXException(message);
					}
				}
				else
				{
					super.startElement(uri, localName, qName, atts);
				}
			}
		}
		else
		{
			super.startElement(uri, localName, qName, atts);
		}
	}
}
