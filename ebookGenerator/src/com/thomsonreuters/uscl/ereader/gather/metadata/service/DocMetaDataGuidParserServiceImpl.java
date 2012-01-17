/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.metadata.service;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


import com.thomsonreuters.uscl.ereader.gather.exception.EBookGatherException;
import com.thomsonreuters.uscl.ereader.gather.parsinghandler.TOCXmlHandler;

/**
 * Parses TOC XML files and creates a doc guid list.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public class DocMetaDataGuidParserServiceImpl implements DocMetaDataGuidParserService
{
	private static final Logger LOG = Logger.getLogger(DocMetaDataGuidParserServiceImpl.class);


	/**
	 * Reads through all the XML files found in the provided directory and parses out
	 * the TOC.xml to create a list of  document GUIDs.
	 * 
	 * @param xmlDir directory that contains the TOC XML file to be parsed
	 * 
	 * @return List of document guids
	 * @throws EBookGatherException if any fatal errors are encountered
	 */
	@Override
	public List<String> generateDocGuidList(File xmlDir) throws EBookGatherException 
	{
		
        List<String> guids = new ArrayList<String>();
        if (xmlDir == null || !xmlDir.isDirectory())
        {
        	throw new IllegalArgumentException("xmlDir must be a directory, not null or a regular file.");
        }
        
        LOG.info("Parsing out the document guids from TOC xml file of the following XML directory: " + 
        		xmlDir.getAbsolutePath());
        
        try
        {
        	guids = parseXMLFile(xmlDir);
        }
        catch(Exception e)
        {
        	String errMessage = "No XML files were found in specified directory. " +
					"Please verify that the correct XML path was specified.";
			LOG.error(errMessage);
			throw new EBookGatherException(errMessage, e);
		}
        
        
        LOG.info("Parsed out " + guids.size() + " doc guids from the XML files in the provided directory.");
		
		return guids;
	}
	
	/**
	 * Parses the provided XML file, extracting the doc guid list and adding them to the 
	 * supplied GUID list.
	 * 
	 * @param xmlFile XML file to be parsed
	 * @param guidList List of document GUIDs 
	 * 
	 * @throws EBookGatherException if any parsing issues have been encountered
	 */
	protected List<String> parseXMLFile(File xmlFile) throws EBookGatherException
	{		
		
		TOCXmlHandler handler = new TOCXmlHandler();		
		try
		{
			LOG.debug("Parsing following toc for doc guids: " + xmlFile);
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(xmlFile, handler);
		}
		catch(IOException e)
		{
			String message = "Parser throw an exception while parsing the following file: " + 
					xmlFile.getAbsolutePath();
			LOG.error(message);
			throw new EBookGatherException(message, e);
		}
		catch(SAXException e)
		{
			String message = "Parser throw an exception while parsing the following file: " + 
					xmlFile.getAbsolutePath();
			LOG.error(message);
			throw new EBookGatherException(message, e);
		}
		catch(ParserConfigurationException e)
		{
			String message = "ParserConfigurationException thrown while parsing the following file: " + 
					xmlFile.getAbsolutePath();
			LOG.error(message);
			throw new EBookGatherException(message, e);
		}
		
		return handler.getGuidList();
	}
}
