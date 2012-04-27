/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

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
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLIdFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLImageFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLInputFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLTableFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.InternalLinkResolverFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.ProcessingInstructionZapperFilter;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * Applies any post transformation on the HTML that needs to be done to cleanup or make
 * the HTML acceptable for ProView. 
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLCreateNamedAnchorsInternalLinksServiceImpl implements HTMLCreateNamedAnchorsInternalLinksService
{
	private static final Logger LOG = Logger.getLogger(HTMLCreateNamedAnchorsInternalLinksServiceImpl.class);
	
	private FileHandlingHelper fileHandlingHelper;
	private DocMetadataService docMetadataService;
		
	public void setfileHandlingHelper(FileHandlingHelper fileHandlingHelper)
	{
		this.fileHandlingHelper = fileHandlingHelper;
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
	 * @param title title of the book being published
	 * @param jobId the job identifier of the current transformation run
	 * @return the number of documents that had post transformations run on them
	 * 
	 * @throws if no source files are found or any parsing/transformation exception are encountered
	 */
	@Override
	public int transformHTML(final File srcDir, final File targetDir, 
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
			FileExtensionFilter fileExtFilter = new FileExtensionFilter();
			fileExtFilter.setAcceptedFileExtensions(new String[]{"posttransform"}); // lowercase compare
			fileHandlingHelper.setFilter(fileExtFilter);

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
		
		LOG.info("Fixing named anchors on post transformed files...");
		
		DocumentMetadataAuthority documentMetadataAuthority = docMetadataService.findAllDocMetadataForTitleByJobId(jobId);

		File anchorTargetListFile = new File(srcDir.getAbsolutePath(), "anchorTargetFile");
		
		HashMap<String, HashSet<String>>  targetAnchors = readTargetAnchorFile(anchorTargetListFile);
		int numDocs = 0;
		for(File htmlFile : htmlFiles)
		{
			transformHTMLFile(htmlFile, targetDir,  title, jobId, documentMetadataAuthority,targetAnchors);
			numDocs++;
		}
		
		File anchorTargetUnlinkFile = new File(targetDir.getAbsolutePath(), "anchorTargetUnlinkFile");
		createAnchorTargetList(anchorTargetUnlinkFile, targetAnchors);
		
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
	 * @param titleID title of the book being published
	 * @param jobIdentifier identifier of the job that will be used to retrieve the image metadata
	 * @param documentMetadataAuthority 
	 * 
	 * @throws if any parsing/transformation exception are encountered
	 */
	protected void transformHTMLFile(File sourceFile, File targetDir, 
			String titleID, Long jobIdentifier, final DocumentMetadataAuthority documentMetadataAuthority, HashMap<String, HashSet<String>> targetAnchors) throws EBookFormatException
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
					titleID, jobIdentifier, guid);
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser saxParser = factory.newSAXParser();
						
			HTMLIdFilter anchorIdFilter = new HTMLIdFilter();
			anchorIdFilter.setParent(saxParser.getXMLReader());
			if (docMetadata != null)
			{
				anchorIdFilter.setCurrentGuid(docMetadata.getProViewId());
			}
			else
			{
				anchorIdFilter.setCurrentGuid(guid);
			}
			anchorIdFilter.setTargetAnchors(targetAnchors);
						
			Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
			props.setProperty("omit-xml-declaration", "yes");
			
			Serializer serializer = SerializerFactory.getSerializer(props);
			outStream = new FileOutputStream(
					new File(targetDir, fileName.substring(0, fileName.indexOf(".")) + ".postAnchor"));
			serializer.setOutputStream(outStream);
			
			anchorIdFilter.setContentHandler(serializer.asContentHandler());
			
			inStream = new FileInputStream(sourceFile);

			anchorIdFilter.parse(new InputSource(inStream));
			
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
	protected HashMap<String, HashSet<String>> readTargetAnchorFile(File anchorTargetListFile) throws EBookFormatException
	{
		HashMap<String, HashSet<String>> anchors = new HashMap<String, HashSet<String>>();
		if (anchorTargetListFile.length() == 0)
		{
			return null;
		}
		else 
		{
			BufferedReader reader = null;
			try
			{
				LOG.info("Reading in anchor map file...");
				reader = new BufferedReader(new FileReader(anchorTargetListFile));
				String input = reader.readLine();
				while (input != null)
				{
					String[] line = input.split(",", -1);
					if (!line[1].equals(""))
					{
						HashSet<String> anchorSet = new HashSet<String>();
						String[] anchorList = line[1].split("\\|");
						for (String anchorVal : anchorList)
						{
							anchorSet.add(anchorVal);
						}
						anchors.put(line[0], anchorSet);
					}
					else
					{
						String message = "Please verify that each document GUID in the following file has " +
								"at least one anchor associated with it: " + 
								anchorTargetListFile.getAbsolutePath();
						LOG.error(message);
						throw new EBookFormatException(message);
					}
					input = reader.readLine();
				}
				LOG.info("Generated a map for " + anchors.size() + " guids that have anchors.");
			}
			catch(IOException e)
			{
				String message = "Could not read the DOC guid to anchors file: " + 
					anchorTargetListFile.getAbsolutePath();
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
					LOG.error("Unable to close file reader.", e);
				}
			}
		}
		return anchors;
		
	}
	/**
	 * Takes in a list of guids and a set of target anchors and writes them to the specified file.
	 * 
	 * @param anchorTargetListFile file to which the list will be written to
	 * @param targetAnchors guids and target anchors for that guid.
	 */
	protected void createAnchorTargetList(File anchorTargetListFile, HashMap<String, HashSet<String>> targetAnchors) 
			throws EBookFormatException
	{
		BufferedWriter writer = null;
		try
		{
			LOG.info("Writing anchor list to " + anchorTargetListFile.getAbsolutePath() + " file...");
			writer = new BufferedWriter(new FileWriter(anchorTargetListFile));
			
			for (Entry<String, HashSet<String>> guidAnchorEntry : targetAnchors.entrySet())
			{
				
				if (guidAnchorEntry.getValue().size() > 0)
				{
				writer.write(guidAnchorEntry.getKey());
				writer.write("|");
				for (String anchors : guidAnchorEntry.getValue())
				{
					writer.write(anchors);
					writer.write(",");
				}
				writer.newLine();
				}
			}
			LOG.info(targetAnchors.size() + " doc guid anchor references written successfuly to file.");
		}
		catch(IOException e)
		{
			String message = "Could not write to the static image list file: " + 
					anchorTargetListFile.getAbsolutePath();
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
				LOG.error("Unable to close anchor target list file.", e);
			}
			
			
			
		}
	}

}
