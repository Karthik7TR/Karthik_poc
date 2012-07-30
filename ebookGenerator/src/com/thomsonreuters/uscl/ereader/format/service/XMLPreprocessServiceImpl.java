/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.XMLContentChangerFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * Applies any preprocess transformation on the XML that need to be done to add mark ups
 * or content to the documents
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XMLPreprocessServiceImpl implements XMLPreprocessService
{
	private static final Logger LOG = Logger.getLogger(XMLPreprocessServiceImpl.class);
	
	private FileHandlingHelper fileHandlingHelper;
	
	public void setfileHandlingHelper(FileHandlingHelper fileHandlingHelper)
	{
		this.fileHandlingHelper = fileHandlingHelper;
	}
	
	/**
	 * This method applies XMLFilters to the source XML to apply various
	 * preprocess rules to the XML.
	 * 
	 * @param srcDir source directory that contains the html files
	 * @param targetDir target directory where the resulting post transformation files are written to
	 * @param isFinalStage detemines if content is from Final or Review stage
	 * @param copyright list of DocumentCopyright used in filter to change copyright message
	 * @param currencies list of DocumentCurrency used in filter to change currency message
	 * 
	 * @return the number of documents that were preprocessed
	 * 
	 * @throws if no source files are found or any parsing/transformation exception are encountered
	 */
	@Override
	public int transformXML(final File srcDir, final File targetDir, final boolean isFinalStage, final List<DocumentCopyright> copyrights,
			final List<DocumentCurrency> currencies) throws EBookFormatException
	{
        if (srcDir == null || !srcDir.isDirectory())
        {
        	throw new IllegalArgumentException("srcDir must be a directory, not null or a regular file.");
        }
        
        HashMap<String, String> copyrightMap = new HashMap<String, String>();
        for(DocumentCopyright copyright : copyrights) {
        	copyrightMap.put(copyright.getCopyrightGuid(), copyright.getNewText());
        }
        HashMap<String, String> currencyMap = new HashMap<String, String>();
        for(DocumentCurrency currency : currencies) {
        	currencyMap.put(currency.getCurrencyGuid(), currency.getNewText());
        }

        //retrieve list of all xml files that need preprocessing
		List<File> xmlFiles = new ArrayList<File>();
		
		try
		{
			fileHandlingHelper.getFileList(srcDir, xmlFiles);
		}
        catch(FileNotFoundException e)
        {
        	String errMessage = "No xml files were found in specified directory. " +
					"Please verify that the correct path was specified.";
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}

		if(!targetDir.exists())
		{
			targetDir.mkdirs();
		}
		
		LOG.info("Applying preprocess on xml files...");
		
		int numDocs = 0;
		for(File xmlFile : xmlFiles)
		{
			transformXMLFile(xmlFile, targetDir, isFinalStage, copyrightMap, currencyMap);
			numDocs++;
		}

		LOG.info("Preprocess successfully applied to " + numDocs + " files.");
		return numDocs;
	}
	
	/**
	 * This method applies the various XMLFilter(s) to the passed in source file and generates
	 * a new file in the target directory.
	 * 
	 * @param sourceFile source file to be transformed
	 * @param targetDir target directory where the resulting post transformation file is to be written
	 * @param isFinalStage detemines if content is from Final or Review stage
	 * @param copyrightMap map of currency guids and message
	 * @param currencyMap map of copyright guids and message
	 * 
	 * @throws if any parsing/transformation exception are encountered
	 */
	protected void transformXMLFile(File sourceFile, File targetDir, boolean isFinalStage, HashMap<String, String> copyrightMap,
			HashMap<String, String> currencyMap) throws EBookFormatException
	{

		String fileName = sourceFile.getName();
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		try
		{
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();

			XMLContentChangerFilter contentChangerFilter = new XMLContentChangerFilter();
			contentChangerFilter.setFinalStage(isFinalStage);
			contentChangerFilter.setCopyrightMap(copyrightMap);
			contentChangerFilter.setCurrencyMap(currencyMap);
			contentChangerFilter.setParent(saxParser.getXMLReader());
			
			Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
			props.setProperty("omit-xml-declaration", "yes");
			
			Serializer serializer = SerializerFactory.getSerializer(props);
			outStream = new FileOutputStream(
					new File(targetDir, fileName.substring(0, fileName.indexOf(".")) + ".preprocess"));
			serializer.setOutputStream(outStream);
			
			contentChangerFilter.setContentHandler(serializer.asContentHandler());
			
			inStream = new FileInputStream(sourceFile);

			contentChangerFilter.parse(new InputSource(inStream));

			LOG.debug("Successfully preprocessed:" + sourceFile.getAbsolutePath());
		}
		catch(IOException e)
		{
			String errMessage = "Unable to perform IO operations related to following source file: " + fileName;
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
		catch(SAXException e)
		{
			String errMessage = "Encountered a SAX Exception while processing: " + fileName;
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
		catch(ParserConfigurationException e)
		{
			String errMessage = "Encountered a SAX Parser Configuration Exception while processing: " + fileName;
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
		finally
		{
			try
			{
				if (inStream != null)
				{
					inStream.close();
				}
				if (outStream != null)
				{
					outStream.close();
				}
			}
			catch(IOException e)
			{
				LOG.error("Unable to close files related to the " + fileName + " file after preprocess.", e);
			}
		}
	}
	
}
