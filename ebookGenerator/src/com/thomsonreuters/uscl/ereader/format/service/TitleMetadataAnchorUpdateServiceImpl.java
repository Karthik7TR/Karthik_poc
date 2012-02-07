/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.TitleXMLTOCFilter;

/**
 * Updates all the anchor references to include proper document prefixes.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class TitleMetadataAnchorUpdateServiceImpl implements TitleMetadataAnchorUpdateService {

	private static final Logger LOG = Logger.getLogger(TitleMetadataAnchorUpdateServiceImpl.class);

	/**
	 * Update all the anchor references to match the the format docFamGuid/anchorName.
	 * 
	 * @param srcTitleXML the source title.xml file to be updated.
	 * @param trgTitleXML location where the updated file should be generated to.
	 * @param docToToc the file that contains mappings of DOC to TOC Guids
	 */
	public void updateAnchors(File srcTitleXML, File trgTitleXML, File docToToc)
		throws EBookFormatException
	{
        if (srcTitleXML == null || !srcTitleXML.exists())
        {
        	throw new IllegalArgumentException("srcTitleXML must be a valid file.");
        }
        
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
        
        try
		{
			LOG.debug("Transforming anchor references from Title.xml file: " + srcTitleXML.getAbsolutePath());
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();
			
			TitleXMLTOCFilter tocFilter = new TitleXMLTOCFilter(docToToc);
			tocFilter.setParent(saxParser.getXMLReader());
									
			Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
			props.setProperty("omit-xml-declaration", "yes");
			
			Serializer serializer = SerializerFactory.getSerializer(props);
			outStream = new FileOutputStream(trgTitleXML);
			serializer.setOutputStream(outStream);
			
			tocFilter.setContentHandler(serializer.asContentHandler());
			
			inStream = new FileInputStream(srcTitleXML);

			tocFilter.parse(new InputSource(inStream));
		}
		catch(IOException e)
		{
			String errMessage = "Unable to perform IO operations on Title.xml file: " + 
					srcTitleXML.getAbsolutePath() + " or " + trgTitleXML.getAbsolutePath();
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
		catch(SAXException e)
		{
			String errMessage = "Encountered a SAX Exception while processing: " + 
					srcTitleXML.getAbsolutePath();
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
		catch(ParserConfigurationException e)
		{
			String errMessage = "Encountered a SAX Parser Configuration Exception while processing: " + 
					srcTitleXML.getAbsolutePath();
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
				LOG.error("Unable to close files related to the " + srcTitleXML.getAbsolutePath()
						+ " file transformation.", e);
			}
		}

		LOG.debug("Anchors in " + trgTitleXML.getAbsolutePath() + " were successfully transformed.");
	}
}
