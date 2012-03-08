/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLAnchorFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLEmptyHeading2Filter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLImageFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLInputFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLTableFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.ProcessingInstructionZapperFilter;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * Applies any post transformation on the HTML that needs to be done to cleanup or make
 * the HTML acceptable for ProView. 
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLTransformerServiceImpl implements HTMLTransformerService
{
	private static final Logger LOG = Logger.getLogger(HTMLTransformerServiceImpl.class);
	
	private FileHandlingHelper fileHandlingHelper;
	private ImageService imgService;
	private DocMetadataService docMetadataService;
	
	private static final String START_WRAPPER_TAG = "<div id=\"coid_website_documentWidgetDiv\">";
	private static final String END_WRAPPER_TAG = "</div>";
	
	public void setfileHandlingHelper(FileHandlingHelper fileHandlingHelper)
	{
		this.fileHandlingHelper = fileHandlingHelper;
	}
	
	public void setimgService(ImageService imgService)
	{
		this.imgService = imgService;
	}
	
	public void setdocMetadataService(DocMetadataService docMetadataService)
	{
		this.docMetadataService = docMetadataService;
	}
	
	/**
	 * This method applies multiple XMLFilters to the source HTML to apply various
	 * post transformation rules to the HTML.
	 * 
	 * @param srcDir source directory that contains the html files
	 * @param targetDir target directory where the resulting post transformation files are written to
	 * @param staticImgList target file to which a list of referenced static files will be written out to
	 * @param title title of the book being published
	 * @param jobId the job identifier of the current transformation run
	 * @return the number of documents that had post transformations run on them
	 * 
	 * @throws if no source files are found or any parsing/transformation exception are encountered
	 */
	@Override
	public int transformHTML(final File srcDir, final File targetDir, final File staticImgList,final boolean isTableViewRequired, 
			final String title, final Long jobId) throws EBookFormatException
	{
        if (srcDir == null || !srcDir.isDirectory())
        {
        	throw new IllegalArgumentException("srcDir must be a directory, not null or a regular file.");
        }
		
        //retrieve list of all transformed files that need HTML wrappers
		List<File> htmlFiles = new ArrayList<File>();
		
		try
		{
			fileHandlingHelper.getFileList(srcDir, htmlFiles);
		}
        catch(FileNotFoundException e)
        {
        	String errMessage = "No html files were found in specified directory. " +
					"Please verify that the correct path was specified.";
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}

		if(!targetDir.exists())
		{
			targetDir.mkdirs();
		}
		
		LOG.info("Applying post transformations on transformed files...");
		
		Set<String> staticImages = new HashSet<String>();
		int numDocs = 0;
		for(File htmlFile : htmlFiles)
		{
			transformHTMLFile(htmlFile, targetDir, staticImages,isTableViewRequired, title, jobId);
			numDocs++;
		}
		
		createStaticImageList(staticImgList, staticImages);

		LOG.info("Post transformations successfully applied to " + numDocs + " files.");
		return numDocs;
	}
	
	/**
	 * This method applies the various XMLFilter(s) to the passed in source file and generates
	 * a new file in the target directory. It also parses out all the static image references
	 * and saves them off in a set to be serialized later.
	 * 
	 * @param sourceFile source file to be transformed
	 * @param targetDir target directory where the resulting post transformation file is to be written
	 * @param staticImgRef set to which a list of referenced static files will be added to
	 * @param titleID title of the book being published
	 * @param jobIdentifier identifier of the job that will be used to retrieve the image metadata
	 * 
	 * @throws if any parsing/transformation exception are encountered
	 */
	protected void transformHTMLFile(File sourceFile, File targetDir, Set<String> staticImgRef, final boolean isTableViewRequired,
			String titleID, Long jobIdentifier) throws EBookFormatException
	{

		String fileName = sourceFile.getName();
		String guid = fileName.substring(0, fileName.indexOf("."));
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		SequenceInputStream intermediateStream = null;
		SequenceInputStream wrappedStream = null;
		try
		{
			LOG.debug("Transforming following html file: " + sourceFile.getAbsolutePath());
			
			DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(
					titleID, Integer.parseInt(jobIdentifier.toString()), guid);
			
			String firstlineCite = "";
			if (docMetadata != null)
			{
				firstlineCite = docMetadata.getNormalizedFirstlineCite();
			}
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();
			
			HTMLEmptyHeading2Filter emptyH2Filter = new HTMLEmptyHeading2Filter();
			emptyH2Filter.setParent(saxParser.getXMLReader());
			HTMLTableFilter tableFilter =null;
			
			if (isTableViewRequired)
			{
				tableFilter = new HTMLTableFilter();
			    tableFilter.setParent(emptyH2Filter);			
			}
			
			HTMLImageFilter imageFilter = new HTMLImageFilter();
			imageFilter.setStaticImageRefs(staticImgRef);
			
			if (isTableViewRequired && tableFilter != null)
			{
			    imageFilter.setParent(tableFilter);
			}
			else 
			{
				imageFilter.setParent(emptyH2Filter);
			}

			ProcessingInstructionZapperFilter piZapperFilter = new ProcessingInstructionZapperFilter();
			piZapperFilter.setParent(imageFilter);
			
//			HTMLClassAttributeFilter classAttFilter = new HTMLClassAttributeFilter();
//			classAttFilter.setParent(piZapperFilter);
			
			HTMLInputFilter inputFilter = new HTMLInputFilter();
			inputFilter.setParent(piZapperFilter);
			
			HTMLAnchorFilter anchorFilter = new HTMLAnchorFilter();
			anchorFilter.setimgService(imgService);
			anchorFilter.setjobInstanceId(jobIdentifier);
			anchorFilter.setFirstlineCite(firstlineCite);
			anchorFilter.setParent(inputFilter);
						
			Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
			props.setProperty("omit-xml-declaration", "yes");
			
			Serializer serializer = SerializerFactory.getSerializer(props);
			outStream = new FileOutputStream(
					new File(targetDir, fileName.substring(0, fileName.indexOf(".")) + ".postTransform"));
			serializer.setOutputStream(outStream);
			
			anchorFilter.setContentHandler(serializer.asContentHandler());
			
			inStream = new FileInputStream(sourceFile);
			intermediateStream = new SequenceInputStream(
					new ByteArrayInputStream(START_WRAPPER_TAG.getBytes()), inStream);
			wrappedStream = new SequenceInputStream(intermediateStream, 
					new ByteArrayInputStream(END_WRAPPER_TAG.getBytes()));

			anchorFilter.parse(new InputSource(wrappedStream));
			
			LOG.debug(sourceFile.getAbsolutePath() + " successfully transformed.");
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
				if (wrappedStream != null)
				{
					wrappedStream.close();
				}
				if (intermediateStream != null)
				{
					intermediateStream.close();
				}
				if (outStream != null)
				{
					outStream.close();
				}
			}
			catch(IOException e)
			{
				LOG.error("Unable to close files related to the " + fileName + " file post transformation.", e);
			}
		}
	}
	
	/**
	 * Takes in a list of static images and writes them to the specified file.
	 * 
	 * @param imgListFile file to which the list will be written to
	 * @param imgFileNames a set of static image file names to be written
	 */
	protected void createStaticImageList(File imgListFile, Set<String> imgFileNames) 
			throws EBookFormatException
	{
		BufferedWriter writer = null;
		try
		{
			LOG.info("Writing static images to " + imgListFile.getAbsolutePath() + " file...");
			writer = new BufferedWriter(new FileWriter(imgListFile));
			for (String fileName : imgFileNames)
			{
				if (StringUtils.isEmpty(fileName))
				{
					String message = "Invalid image file name encountered: " + fileName;
					LOG.error(message);
					throw new EBookFormatException(message);
				}
				
				writer.write(fileName);
				writer.newLine();
			}
			LOG.info(imgFileNames.size() + " image references written successfuly to file.");
		}
		catch(IOException e)
		{
			String message = "Could not write to the static image list file: " + 
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
				LOG.error("Unable to close static image list file.", e);
			}
		}
	}
}
