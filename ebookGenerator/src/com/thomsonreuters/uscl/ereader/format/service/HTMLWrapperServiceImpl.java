/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;

/**
 * The HTMLWrapperService iterates through a directory of transformed raw HTML files and 
 * wraps the raw files with proper HTML header and container tags.
 * 
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class HTMLWrapperServiceImpl implements HTMLWrapperService
{
	private static final Logger LOG = Logger.getLogger(TransformerServiceImpl.class);
	
	private FileHandlingHelper fileHandlingHelper;
	private KeyCiteBlockGenerationServiceImpl keyCiteBlockGenerationService;
	
	public void setfileHandlingHelper(FileHandlingHelper fileHandlingHelper)
	{
		this.fileHandlingHelper = fileHandlingHelper;
	}
	
	public void setKeyCiteBlockGenerationService(KeyCiteBlockGenerationServiceImpl keyCiteBlockGenerationService)
	{
		this.keyCiteBlockGenerationService = keyCiteBlockGenerationService;
	}

	/**
     * Wraps all transformed files found in the passed in transformation directory and writes the
     * properly marked up HTML files to the specified target directory. If the directory does not exist
     * the service creates it.
     *
     * @param transDir the directory that contains all the intermediate generated HTML files generated
     * by the Transformer Service for this eBook.
     * @param htmlDir the target directory to which all the properly marked up HTML files will be written out to.
     * @param docToTocMapping location of the file that contains the document to TOC mappings that
     * will be used to generate anchors for the TOC references
     * 
     * @param titleId
     * 
     * @param jobId
     * 
     * @param docGuid
     * 
     * @return The number of documents that had wrappers added
     * 
     * @throws EBookFormatException if an error occurs during the process.
	 */
	@Override
	public int addHTMLWrappers(File transDir, File htmlDir, File docToTocMap, String titleId, long jobId, boolean keyciteToplineFlag) throws EBookFormatException 
	{
        if (transDir == null || !transDir.isDirectory())
        {
        	throw new IllegalArgumentException("transDir must be a directory, not null or a regular file.");
        }
        
        if (docToTocMap == null || !docToTocMap.exists())
        {
        	throw new IllegalArgumentException("docToTocMap must be an existing file on the system.");
        }
        
        //retrieve list of all transformed files that need HTML wrappers
		ArrayList<File> transformedFiles = new ArrayList<File>();
		
		try
		{
//			FileExtensionFilter fileExtFilter = new FileExtensionFilter();
//			fileExtFilter.setAcceptedFileExtensions(new String[]{"postunlink"}); // lowercase compare
//			fileHandlingHelper.setFilter(fileExtFilter);

			fileHandlingHelper.getFileList(transDir, transformedFiles);
		}
        catch(FileNotFoundException e)
        {
        	String errMessage = "No transformed files were found in specified directory. " +
					"Please verify that the correct transformed path was specified.";
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, e);
		}
        
		if(!htmlDir.exists())
		{
			htmlDir.mkdirs();
		}
		
		Map<String, String[]> anchorMap = new HashMap<String, String[]>();
		readTOCGuidList(docToTocMap, anchorMap);
		
		LOG.info("Adding HTML containers around transformed files...");
				
		int numDocs = 0;
		for(File transFile : transformedFiles)
		{
			addHTMLWrapperToFile(transFile, htmlDir, anchorMap, titleId, jobId, keyciteToplineFlag);
			numDocs++;
		}

		LOG.info("HTML containers successfully added to transformed files");
		return numDocs;
	}
	
	/**
	 * Takes in a .tranformed file and wraps it with the appropriate header and footers, the resulting file is
	 * written to the specified HTML directory.
	 * 
	 * @param transformedFile source file that will be wrapped with appropriate header and footer
	 * @param htmlDir target directory the newly created file with the wrappers will be written to
	 * @param anchorMap cached authority map of anchors that need to be inserted into each document
	 * @param titleId
	 * @param jobId
	 * @param keyciteToplineFlag
	 * 
	 * @throws EBookFormatException thrown if any IO exceptions are encountered
	 */
	final void addHTMLWrapperToFile(File transformedFile, File htmlDir, Map<String, String[]> anchorMap, String titleId, Long jobId, boolean keyciteToplineFlag) 
			throws EBookFormatException
	{
		String fileName = transformedFile.getName();
		
		LOG.debug("Adding wrapper around: " + fileName);
		String guid = fileName.substring(0, fileName.indexOf("."));
		
		StringBuffer anchors = new StringBuffer();
		
		generateAnchorStream(anchorMap, guid, anchors);
		ByteArrayInputStream anchorStream = new ByteArrayInputStream(anchors.toString().getBytes());
		
		File output = new File(htmlDir, guid + ".html");
		
		if (output.exists())
		{
			//delete file if it exists since the output buffer will just append on to it
			//otherwise restarting the step would double up the file
			output.delete();
		}
				
		FileOutputStream outputStream = null;
		InputStream headerStream = null;
		InputStream transFileStream = null;
		InputStream footerStream = null;
		InputStream keyciteStream = null;
		try
		{
			outputStream = new FileOutputStream(output, true);
			
			headerStream = getClass().getResourceAsStream("/StaticFiles/HTMLHeader.txt");
			IOUtils.copy(headerStream, outputStream);
			
			IOUtils.copy(anchorStream, outputStream);
			
			if (keyciteToplineFlag)
			{
				keyciteStream = keyCiteBlockGenerationService.getKeyCiteInfo(titleId, jobId, guid);
				IOUtils.copy(keyciteStream, outputStream);
			}			
			
			transFileStream = new FileInputStream(transformedFile);
			IOUtils.copy(transFileStream, outputStream);
			
			footerStream = getClass().getResourceAsStream("/StaticFiles/HTMLFooter.txt");
			IOUtils.copy(footerStream, outputStream);
		}
		catch(IOException ioe)
		{
			String errMessage = "Failed to add HTML contrainers around the following transformed file: " + fileName;
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage, ioe);
		}
		finally
		{
			try
			{
				if (outputStream != null)
				{
					outputStream.close();
				}
				if (headerStream != null)
				{
					headerStream.close();
				}
				if (transFileStream != null)
				{
					transFileStream.close();
				}
				if (footerStream != null)
				{
					footerStream.close();
				}
				if (keyciteStream != null)
				{
					keyciteStream.close();
				}
			}
			catch (IOException e)
			{
				LOG.error("Unable to close I/O streams.", e);
			}
		}
	}
	
	/**
	 * Reads in a list of TOC Guids that are associated to each Doc Guid to later be used
	 * for anchor insertion and generates a map.
	 * 
	 * @param docGuidsFile file containing the DOC to TOC guid relationships
	 * @param docToTocGuidMap in memory map generated based on values found in the provided file
	 */
	protected void readTOCGuidList(File docGuidsFile, Map<String, String[]> docToTocGuidMap)
		throws EBookFormatException
	{
		BufferedReader reader = null;
		try
		{
			LOG.info("Reading in TOC anchor map file...");
			int numDocs = 0;
			int numTocs = 0;
			reader = new BufferedReader(new FileReader(docGuidsFile));
			String input = reader.readLine();
			while (input != null)
			{
				numDocs++;
				String[] line = input.split(",", -1);
				if (!line[1].equals(""))
				{
					String[] tocGuids = line[1].split("\\|");
					numTocs = numTocs + tocGuids.length;
					docToTocGuidMap.put(line[0], tocGuids);
				}
				else
				{
					String message = "No TOC guid was found for the " + numDocs + " document. " +
							"Please verify that each document GUID in the following file has " +
							"at least one TOC guid associated with it: " + 
							docGuidsFile.getAbsolutePath();
					LOG.error(message);
					throw new EBookFormatException(message);
				}
				input = reader.readLine();
			}
			LOG.info("Generated a map for " + numDocs + " DOCs with " + numTocs + " TOC references.");
		}
		catch(IOException e)
		{
			String message = "Could not read the DOC guid to TOC guid map file: " + 
					docGuidsFile.getAbsolutePath();
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
	}
	
	/**
	 * Generates the anchor string that will be appended to the beginning of the document.
	 * 
	 * @param anchorMap map containing references to the TOC nodes above the currently processing document
	 * @param guid key by which the referenced TOCs will be looked up
	 * @param anchorStr buffered string that will contain all the needed anchors concatenated in correct format
	 * @throws EBookFormatException if one of the documents TOC references are not found.
	 */
	public void generateAnchorStream(Map<String, String[]> anchorMap, String guid, StringBuffer anchorStr)
		throws EBookFormatException
	{
		String[] anchors = anchorMap.get(guid);
		if (anchors == null || anchors.length < 1)
		{
			String errMessage = "No TOC anchor references were found for the following GUID: " + guid;
			LOG.error(errMessage);
			throw new EBookFormatException(errMessage);
		}
		
		for (String anchor : anchors)
		{
			anchorStr.append("<a name=\"");
			anchorStr.append(anchor);
			anchorStr.append("\"></a>");
		}
	}
}
