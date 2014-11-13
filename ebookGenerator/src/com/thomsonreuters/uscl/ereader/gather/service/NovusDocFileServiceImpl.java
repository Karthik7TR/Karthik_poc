/*
 * Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.jms.IllegalStateException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.core.book.domain.NortFileLocation;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.filter.DocFilenameFilter;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler.NovusDocFileParser;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

/**
 * Codes Workbench Novus document and metadata split.
 * @author Dong Kim
 */
public class NovusDocFileServiceImpl implements NovusDocFileService {
	private static String CHARSET = "UTF-8";	// explicitly set the character set

	private static final Logger Log = Logger.getLogger(NovusDocFileServiceImpl.class);

	public GatherResponse  fetchDocuments(HashMap<String, Integer> docGuidsMap, File rootCodesWorkbenchLandingStrip, 
			String cwbBookName,  List<NortFileLocation> fileLocations, File contentDestinationDirectory, 
			File metadataDestinationDirectory) 
					throws GatherException {
		Assert.isTrue(contentDestinationDirectory.exists(),
				"The content destination directory for the documents does not exist: "
						+ contentDestinationDirectory);
		Assert.isTrue(contentDestinationDirectory.exists(),
				"The content destination directory for the document metadata does not exist: "
						+ metadataDestinationDirectory);

		GatherResponse gatherResponse = new GatherResponse();
		gatherResponse.setNodeCount(docGuidsMap.size());
		
		String publishStatus = "DOC Generate Step Completed";
		int missingGuidsCount = 0;
		try(FileOutputStream stream = new FileOutputStream(StringUtils.substringBeforeLast(contentDestinationDirectory.getAbsolutePath(), System.getProperty("file.separator")) + "_doc_missing_guids.txt");
				Writer writer = new OutputStreamWriter(stream, CHARSET)) {
			
			Integer tocSequence = 0;
			for (NortFileLocation fileLocation : fileLocations) {
				String contentPath = String.format("%s/%s", cwbBookName, fileLocation.getLocationName());
        		File contentDirectory = new File(rootCodesWorkbenchLandingStrip, contentPath);
        		if (!contentDirectory.exists()) 
        		{
					throw new IllegalStateException("Expected Codes Workbench content directory does not exist: " + 
							contentDirectory.getAbsolutePath());
				}

        		File[] docFiles = contentDirectory.listFiles(new DocFilenameFilter());
        		if(docFiles.length == 0) 
        		{
        			throw new IllegalStateException("Expected Codes Workbench doc file but none exists: " + 
							contentDirectory.getAbsolutePath());
        		} 
        		else if(docFiles.length > 1) 
        		{
        			throw new IllegalStateException("Too many Codes Workbench doc files exists: " + 
							contentDirectory.getAbsolutePath());
        		}
        		String docCollectionName = null;
        		for (File docFile: docFiles)
        		{
        			String fileName = docFile.getName();
					if (fileName.lastIndexOf("-") > -1)
					{
						docCollectionName = fileName.substring(0, fileName.lastIndexOf("-")); 
					}
					
					NovusDocFileParser parser = new NovusDocFileParser(docCollectionName, docGuidsMap, 
							contentDestinationDirectory, metadataDestinationDirectory, gatherResponse, tocSequence);
					parser.parseXML(docFile);
					tocSequence = parser.getTocSequence();
        		}
			}
			
			// Process missing documents
			for(Entry<String, Integer> entry : docGuidsMap.entrySet()) {
				String docGuid = entry.getKey();
				int count = entry.getValue();
				if(count > 0) {
					Log.debug("Document is not found for the guid " + docGuid);
					writer.write(docGuid);
					writer.write("\n");
					missingGuidsCount++;
				}
			}
		} catch (IOException e) {
			GatherException ge = new GatherException(
					"File I/O error writing missing guid document ", e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus =  "DOC Step Failed File I/O error writing document";

			throw ge;
		} catch (IllegalStateException e) { 
			GatherException ge = new GatherException(e.getMessage(), e,
					GatherResponse.CODE_FILE_ERROR);
			publishStatus =  "DOC Step Failed Codes Workbench doc file";

			throw ge;
		} catch (GatherException e) {
			publishStatus = "DOC Step Failed";
			throw e;
		} finally {
			gatherResponse.setPublishStatus(publishStatus);
			
			if (missingGuidsCount > 0) {
				GatherException ge = new GatherException(
						"Null documents are found for the current ebook ",
						GatherResponse.CODE_FILE_ERROR);
				missingGuidsCount = 0; //reset to 0
				throw ge;
			}
		}
		return(gatherResponse);
	}
	
}
