/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	public void setfileHandler(FileHandlingHelper fileHandlingHelper)
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
	 * 
	 * @return number of documents processed to generate lists
	 * @throws EBookFormatException if any fatal errors are encountered
	 */
	@Override
	public int generateImageList(File xmlDir, File imgRef) throws EBookFormatException 
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
        
        List<String> guids = new ArrayList<String>();
        int numDocsParsed = 0;
        for (File file : fileList)
        {
        	parseXMLFile(file, guids);
        	numDocsParsed++;
        }
        
        createImageList(imgRef, guids);
        
        LOG.info("Parsed out " + guids.size() + " image references from the XML files in the provided directory.");
		
		return numDocsParsed;
	}
	
	/**
	 * Parses the provided XML file, extracting any image references and adding them to the 
	 * supplied GUID list.
	 * 
	 * @param xmlFile XML file to be parsed
	 * @param guidList List of GUIDs that new Image GUIDs should be appended to
	 * 
	 * @throws EBookFormatException if any parsing issues have been encountered
	 */
	protected void parseXMLFile(File xmlFile, List<String> guidList) throws EBookFormatException
	{		
		try
		{
			LOG.debug("Parsing following doc for image references: " + xmlFile);
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			
			XMLImageTagHandler handler = new XMLImageTagHandler();
			handler.setGuidList(guidList);
			
			saxParser.parse(xmlFile, handler);
			LOG.debug("Finished parsing " + xmlFile + " list contains " + guidList.size() + " image guids.");
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
	}
	
	/**
	 * Takes in a list of images and writes them to the specified file.
	 * 
	 * @param imgListFile file to which the list will be written to
	 * @param imgList list of image guids to be written
	 */
	protected void createImageList(File imgListFile, List<String> imgList) throws EBookFormatException
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(imgListFile));
			for (String guid : imgList)
			{
				if (guid == null || guid.length() < 32 || guid.length() >= 34)
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
}
