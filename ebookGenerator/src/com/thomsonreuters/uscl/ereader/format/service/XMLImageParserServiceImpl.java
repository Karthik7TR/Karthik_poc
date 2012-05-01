/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.XMLImageTagHandler;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * Parses XML files and identifies all image references and prints them to a file.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class XMLImageParserServiceImpl implements XMLImageParserService
{
	private static final Logger LOG = Logger.getLogger(XMLImageParserServiceImpl.class);

	private FileHandlingHelper fileHandlingHelper;

	public void setfileHandlingHelper(FileHandlingHelper fileHandlingHelper)
	{
		this.fileHandlingHelper = fileHandlingHelper;
	}

	/**
	 * Reads through all the XML files found in the provided directory and parses out all
	 * image references and generates the specified file that contains a list of GUIDs for 
	 * the referenced images.
	 * 
	 * @param xmlDir directory that contains the XML files to be parsed
	 * @param imgRef file which will be written that contains a list of all the referenced image GUIDs
	 * @param docImageMap file which will be written that contains a list of images embedded in each document
	 * 
	 * @return number of documents processed to generate lists
	 * @throws EBookFormatException if any fatal errors are encountered
	 */
	@Override
	public int generateImageList(File xmlDir, File imgRef, File docImageMap) throws EBookFormatException 
	{
        if (xmlDir == null || !xmlDir.isDirectory())
        {
        	throw new IllegalArgumentException("xmlDir must be a directory, not null or a regular file.");
        }
        
        LOG.info("Parsing out image references from XML files out of the following XML directory: " + 
        		xmlDir.getAbsolutePath());
        
        ArrayList<File> fileList = new ArrayList<File>();
        
        try
        {
        	fileHandlingHelper.getFileList(xmlDir, fileList);
        }
        catch(FileNotFoundException e)
        {
        	String errMessage = "No XML files were found in specified directory. " +
					"Please verify that the correct XML path was specified.";
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
        
        Set<String> guids = new HashSet<String>();
        Map<String, Set<String>> docImgMap = new HashMap<String, Set<String>>();
        int numDocsParsed = 0;
        for (File file : fileList)
        {
        	parseXMLFile(file, guids, docImgMap);
        	numDocsParsed++;
        }
        
        createImageList(imgRef, guids);
        createDocToImgMap(docImageMap, docImgMap);
        
        LOG.info("Parsed out " + guids.size() + " image references from the XML files in the provided directory.");
		
		return numDocsParsed;
	}
	
	/**
	 * Parses the provided XML file, extracting any image references and adding them to the 
	 * supplied GUID list.
	 * 
	 * @param xmlFile XML file to be parsed
	 * @param guidList Set of GUIDs that new Image GUIDs should be appended to
	 * @param docImgMap Map of image GUIDs associated to each document
	 * 
	 * @throws EBookFormatException if any parsing issues have been encountered
	 */
	protected void parseXMLFile(File xmlFile, Set<String> guidList, Map<String, Set<String>> docImgMap) 
			throws EBookFormatException
	{		
		FileInputStream xmlStream = null;
		String docGuid = xmlFile.getName().substring(0, xmlFile.getName().indexOf(".")); 
		try
		{
			//LOG.debug("Parsing following doc for image references: " + xmlFile);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			XMLImageTagHandler handler = new XMLImageTagHandler();
			Set<String> imgGuids = new HashSet<String>();
			handler.setGuidList(imgGuids);
			
			xmlStream = new FileInputStream(xmlFile);
			
			saxParser.parse(xmlStream, handler);
			
			guidList.addAll(imgGuids);
			docImgMap.put(docGuid, imgGuids);

			LOG.debug("Parsed out " + guidList.size() + " image guids from " + xmlFile + "." );
		}
		catch(IOException e)
		{
			String message = "Parser throw an exception while parsing the following file: " + 
					xmlFile.getAbsolutePath();
			LOG.error(message);
			throw new EBookFormatException(message, e);
		}
		catch(SAXException e)
		{
			String message = "Parser throw an exception while parsing the following file: " + 
					xmlFile.getAbsolutePath();
			LOG.error(message);
			throw new EBookFormatException(message, e);
		}
		catch(ParserConfigurationException e)
		{
			String message = "ParserConfigurationException thrown while parsing the following file: " + 
					xmlFile.getAbsolutePath();
			LOG.error(message);
			throw new EBookFormatException(message, e);
		}
		finally
		{
			try
			{
				if (xmlStream != null)
				{
					xmlStream.close();
				}				
			}
			catch(IOException e)
			{
				String message = "Unable to close the XML file: " + 
						xmlFile.getAbsolutePath();
				LOG.error(message);
				throw new EBookFormatException(message, e);
			}
		}
	}
	
	/**
	 * Takes in a list of images and writes them to the specified file.
	 * 
	 * @param imgListFile file to which the list will be written to
	 * @param imgList set of image guids to be written
	 */
	protected void createImageList(File imgListFile, Set<String> imgList) throws EBookFormatException
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(imgListFile));
			for (String guid : imgList)
			{
				if (guid == null || guid.length() < 30 || guid.length() > 36)
				{
					String message = "Invalid GUID encountered in the Image GUID list: " + guid;
					LOG.error(message);
					throw new EBookFormatException(message);
				}
				
				writer.write(guid);
				writer.newLine();
			}
		}
		catch(IOException e)
		{
			String message = "Could not write to the Image list file: " + 
					imgListFile.getAbsolutePath();
			LOG.error(message);
			throw new EBookFormatException(message, e);
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
				LOG.error("Unable to close Image GUID list file.", e);
			}
		}
	}
	
	/**
	 * Takes in a map of doc to image associations and writes them to the specified file.
	 * 
	 * @param docToImgMapFile file to which the map will be persisted
	 * @param docToImgMap the map that contains all the document to image associations
	 */
	protected void createDocToImgMap(File docToImgMapFile, Map<String, Set<String>> docToImgMap) 
			throws EBookFormatException
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(docToImgMapFile));
			for (String doc : docToImgMap.keySet())
			{
				if (doc == null || doc.length() < 32 || doc.length() >= 34)
				{
					String message = "Invalid document GUID encountered in the Document to Image GUID map: " + doc;
					LOG.error(message);
					throw new EBookFormatException(message);
				}
				
				writer.write(doc);
				writer.write("|");
				
				for (String imgGuid : docToImgMap.get(doc))
				{
					if (imgGuid == null || imgGuid.length() < 30 || imgGuid.length() > 36)
					{
						String message = "Invalid image GUID encountered in the Document to Image GUID map: " 
								+ imgGuid;
						LOG.error(message);
						throw new EBookFormatException(message);
					}
					
					writer.write(imgGuid + ",");
				}
				writer.newLine();
			}
		}
		catch(IOException e)
		{
			String message = "Could not write to the Document to Image GUID map file: " + 
					docToImgMapFile.getAbsolutePath();
			LOG.error(message);
			throw new EBookFormatException(message, e);
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
				LOG.error("Unable to close Document to Image GUID map file.", e);
			}
		}
	}
}
