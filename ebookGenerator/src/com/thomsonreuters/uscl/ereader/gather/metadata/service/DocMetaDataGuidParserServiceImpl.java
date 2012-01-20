/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.metadata.service;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	public void generateDocGuidList(File xmlDir, File docGuidListDir) throws EBookGatherException 
	{
		
        List<String> docGuidList = new ArrayList<String>();
        if (xmlDir == null || !xmlDir.isDirectory())
        {
        	throw new IllegalArgumentException("xmlDir must be a directory, not null or a regular file.");
        }
        
        LOG.info("Parsing out the document guids from TOC xml file of the following XML directory: " + 
        		xmlDir.getAbsolutePath());
        
        try
        {
        	docGuidList = parseXMLFile(xmlDir);
        	createDocGuidListFile(docGuidListDir, docGuidList);
        }
        catch(Exception e)
        {
        	String errMessage = "No XML files were found in specified directory. " +
					"Please verify that the correct XML path was specified.";
			LOG.error(errMessage);
			throw new EBookGatherException(errMessage, e);
		}
        
        
        LOG.info("Parsed out " + docGuidList.size() + " doc guids from the XML files in the provided directory.");
		
		return;
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

	/**
	 * Takes in a list of document guids and writes them to the specified file.
	 * 
	 * @param docGuidFile file to which the list will be written to
	 * @param docGuidList list of image guids to be written
	 */
	protected void createDocGuidListFile(File docGuidFile, List<String> docGuidList) throws EBookGatherException
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(docGuidFile));
			for (String guid : docGuidList)
			{
				if (guid == null || guid.length() < 32 || guid.length() >= 34)
				{
					String message = "Invalid GUID encountered in the Doc GUID list: " + guid;
					LOG.error(message);
					throw new EBookGatherException(message);
				}
				
				writer.write(guid);
				writer.newLine();
			}
		}
		catch(IOException e)
		{
			String message = "Could not write to the docGuid list file: " + 
			docGuidFile.getAbsolutePath();
			LOG.error(message);
			throw new EBookGatherException(message, e);
		}		
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch (IOException e)
			{
				LOG.error("Unable to close doc GUID list file.", e);
			}
		}
	}
}
