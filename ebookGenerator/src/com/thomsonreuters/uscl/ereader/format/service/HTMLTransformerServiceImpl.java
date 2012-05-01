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
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
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
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLTagIdDedupingFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.InternalLinkResolverFilter;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.ProcessingInstructionZapperFilter;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
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
	 * @param docsGuidFile contains the list of doc GUID's that represent the physical docs.
	 * @param deDuppingFile target file where dedupping anchors are updated.
	 * @return the number of documents that had post transformations run on them
	 * 
	 * @throws if no source files are found or any parsing/transformation exception are encountered
	 */
	@Override
	public int transformHTML(final File srcDir, final File targetDir, final File staticImgList, final boolean isTableViewRequired, 
			final String title, final Long jobId,HashMap<String, HashSet<String>> targetAnchors, final File docsGuidFile, final File deDuppingFile) throws EBookFormatException
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
		
		targetAnchors = new HashMap<String, HashSet<String>>();
		
		DocumentMetadataAuthority documentMetadataAuthority = docMetadataService.findAllDocMetadataForTitleByJobId(jobId);
		//TODO: for each record in the Document Metadata Authority, update it to replace section symbols with lowercase s.
		//There may be other characters that we need to take into account.  There is a XSLT template in SpecialCharacters.xsl.
		int numDocs = 0;
		for(File htmlFile : htmlFiles)
		{
			transformHTMLFile(htmlFile, targetDir, staticImages,isTableViewRequired, title, jobId, documentMetadataAuthority, targetAnchors, docsGuidFile, deDuppingFile);
			numDocs++;
		}
		
		createStaticImageList(staticImgList, staticImages);
		File anchorTargetFile = new File(targetDir.getAbsolutePath(), "anchorTargetFile");
		createAnchorTargetList(anchorTargetFile, targetAnchors);

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
	 * @param documentMetadataAuthority
	 * @param deDuppingFile target file where dedupping anchors are updated. 
	 * 
	 * @throws if any parsing/transformation exception are encountered
	 */
	protected void transformHTMLFile(File sourceFile, File targetDir, Set<String> staticImgRef, final boolean isTableViewRequired,
			String titleID, Long jobIdentifier, final DocumentMetadataAuthority documentMetadataAuthority, HashMap<String, HashSet<String>> targetAnchors, final File docsGuidFile, final File deDuppingFile) throws EBookFormatException
	{

		String fileName = sourceFile.getName();
		String guid = fileName.substring(0, fileName.indexOf("."));
		FileInputStream inStream = null;
		FileOutputStream outStream = null;
		SequenceInputStream intermediateStream = null;
		SequenceInputStream wrappedStream = null;
		try
		{
			//LOG.debug("Transforming following html file: " + sourceFile.getAbsolutePath());
			DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(
					titleID, jobIdentifier, guid);
			
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
			
			HTMLTagIdDedupingFilter tagIdDedupingFilter = new HTMLTagIdDedupingFilter(guid);
			
			tagIdDedupingFilter.setParent(emptyH2Filter);
			
			HTMLTableFilter tableFilter = new HTMLTableFilter(isTableViewRequired);
			tableFilter.setParent(tagIdDedupingFilter);
			
			HTMLImageFilter imageFilter = new HTMLImageFilter();
			imageFilter.setStaticImageRefs(staticImgRef);
			imageFilter.setParent(tableFilter);

			ProcessingInstructionZapperFilter piZapperFilter = new ProcessingInstructionZapperFilter();
			piZapperFilter.setParent(imageFilter);
			
			InternalLinkResolverFilter internalLinkResolverFilter = new InternalLinkResolverFilter(documentMetadataAuthority, docsGuidFile);
			internalLinkResolverFilter.setParent(piZapperFilter);
			
			HTMLInputFilter inputFilter = new HTMLInputFilter();
			inputFilter.setParent(internalLinkResolverFilter);
			
			HTMLAnchorFilter anchorFilter = new HTMLAnchorFilter();
			anchorFilter.setimgService(imgService);
			anchorFilter.setjobInstanceId(jobIdentifier);
			anchorFilter.setFirstlineCite(firstlineCite);
			anchorFilter.setParent(inputFilter);
			anchorFilter.setTargetAnchors(targetAnchors);
			if (docMetadata != null)
			{
				anchorFilter.setCurrentGuid(docMetadata.getProViewId());
			}
			else
			{
				anchorFilter.setCurrentGuid(guid);
			}
	
			
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
			
			targetAnchors.putAll(anchorFilter.getTargetAnchors());
			
			List<String> deDuppingList = tagIdDedupingFilter.getDuplicateIdList();
			
			if (deDuppingList.size() > 0)
			{
			    insertDeduppingAnchorRecords(deDuppingList,deDuppingFile);
			}

			LOG.debug("Successfully transformed:" + sourceFile.getAbsolutePath());
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
				
				writer.write(guidAnchorEntry.getKey());
				writer.write(",");
				for (String anchors : guidAnchorEntry.getValue())
				{
					writer.write(anchors);
					writer.write("|");
				}
				writer.newLine();
			}
			LOG.info(targetAnchors.size() + " anchor references written successfuly to file.");
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
	
	/**
	 * Takes in a list of de-dupping anchors and writes them to the specified file.
	 * @param deduppingIds list of de-dupping anchor ids.
	 * @param deDuppingFile file to which the list will be written to
	 * @throws EBookFormatException
	 */
	private void insertDeduppingAnchorRecords(List<String> deduppingIds, File deDuppingFile) 
			throws EBookFormatException
	{
		Writer writer = null;
		try 
		{
		LOG.info("Writing de-dupping anchors info  to " + deDuppingFile.getAbsolutePath() + " file...");
		FileOutputStream outStream = new FileOutputStream(deDuppingFile,true);
		String charset = "UTF-8";
		writer = new OutputStreamWriter(outStream, charset);
		for (String id : deduppingIds)
		{
			writer.write(id);
			writer.write("\n");
		}
		}
		catch(Exception e)
		{
			String message = "Could not write to the de-dupping anchors list file: " + 
					deDuppingFile.getAbsolutePath();
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
				LOG.error("Unable to close de-dupping anchors list file.", e);
			}
		}
	}
}
