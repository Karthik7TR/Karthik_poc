/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;
import com.westgroup.novus.productapi.Document;
import com.westgroup.novus.productapi.Find;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

/**
 * Novus document and metadata fetch.
 */
public class DocServiceImpl implements DocService {
	
	//private static final Logger log = Logger.getLogger(DocServiceImpl.class);
	
	private NovusFactory novusFactory;

	@Override
	public void fetchDocuments(Collection<String> docGuids, String collectionName,
							   File contentDestinationDirectory,
							   File metadataDestinationDirectory) throws GatherException {
		Assert.isTrue(contentDestinationDirectory.exists(), "The content destination directory for the documents does not exist: " + contentDestinationDirectory);
		Assert.isTrue(contentDestinationDirectory.exists(), "The content destination directory for the document metadata does not exist: " + metadataDestinationDirectory);
		
		Novus novus = novusFactory.createNovus();
		boolean anyException = false;
		String docGuid = null;
		try {
			Find finder = novus.getFind();
			for (String guid : docGuids) {
				docGuid = guid;
				Document document = finder.getDocument(collectionName, null, guid);
				createContentFile(document, contentDestinationDirectory);
				createMetadataFile(document, metadataDestinationDirectory);
			}
		} catch (NovusException e) {
			anyException = true;
			GatherException ge = new GatherException("Novus error occurred fetching document " + docGuid, e);
			ge.setErrorCode(GatherResponse.CODE_NOVUS_ERROR);
			throw ge;
		} catch (IOException e) {
			anyException = true;
			GatherException ge = new GatherException("File I/O error writing document " + docGuid, e);
			ge.setErrorCode(GatherResponse.CODE_NOVUS_ERROR);
			throw ge;
		} finally {
			if (anyException) {
				// Remove all existing generated document files on any exception
				removeAllFilesInDirectory(contentDestinationDirectory);
				removeAllFilesInDirectory(metadataDestinationDirectory);
			}
			novus.shutdownMQ();
		}
	}
	
	private static final void createContentFile(Document document, File destinationDirectory)
									throws NovusException, IOException {
		String basename = document.getGuid() + EBConstants.XML_FILE_EXTENSION;
		File destinationFile = new File(destinationDirectory, basename);  
		createFile(document.getText(), destinationFile);
	}

	private static final void createMetadataFile(Document document, File destinationDirectory)
									throws NovusException, IOException {
		String basename = document.getGuid() + EBConstants.XML_FILE_EXTENSION;
		File destinationFile = new File(destinationDirectory, basename);  
		createFile(document.getMetaData(), destinationFile);
	}
	
	/**
	 * Create a file containing the specified content.
	 */
	private static final void createFile(String content, File destinationFile) throws IOException {
		FileOutputStream stream = new FileOutputStream(destinationFile);
		try {
			String charset = "UTF-8";	// explicitly set the character set
			Writer writer = new OutputStreamWriter(stream, charset);
			writer.write(content);
		} finally {
			stream.close();
		}
	}
	
	/**
	 * Delete all files in the specified directory.
	 * @param directory directory whose files will be removed
	 */
	public static void removeAllFilesInDirectory(File directory) {
		File[] files = directory.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	@Required
	public void setNovusFactory(NovusFactory factory) {
		this.novusFactory = factory;
	}
}
