/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
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
 * Novus document related services.
 */
public class DocServiceImpl implements DocService {
	
	private static final Logger log = Logger.getLogger(DocServiceImpl.class);
	
	private NovusFactory novusFactory;

	@Override
	public void fetchDocuments(Collection<String> docGuids, String collectionName,
							   File contentDestinationDirectory,
							   File metadataDestinationDirectory) throws GatherException {
		log.debug("docGuids: " + docGuids);
		log.debug("collectionName=" + collectionName);
		log.debug("contentDestinationDirectory=" + contentDestinationDirectory);
		log.debug("metadataDestinationDirectory=" + metadataDestinationDirectory);
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
		FileWriter writer = new FileWriter(destinationFile);
		try {
			writer.write(content);
		} finally {
			writer.close();
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

//	/**
//	 * 
//	 * users will alwyas provide us with collection name and not collectionSet
//	 * names.
//	 */
//	public Document[] getDocFromNovus(List<EBookToc> docGuidList,
//			String collectionName, String docFileLocationPath) 
//	{
//		Novus novusObject = null;
//		Document[] documents = null;
//
//		/*** for ebook builder we will always get Collection. ***/
//		try {
//			novusObject = novusAPIHelper.getNovusObject();
//			Find find = novusObject.getFind();
//			for (EBookToc eBookToc : docGuidList)
//			{
//
//				find.addDocumentInfo(collectionName, null,
//						eBookToc.getDocGuid());
//			}
//			documents = find.getDocuments();
//			// tocNodes = toc.getRootNodes(); //getAllDocuments(guid);
//			// tocGuidList = extractTocListForEbook(tocNodes);
//
//		}catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			novusAPIHelper.closeNovusConnection(novusObject);
//		}
//
//		for (Document document : documents) {
//			processDocument(document,docFileLocationPath);
//		}
//		return documents;
//
//	}

//	private void processDocument(Document document,String docFileLocationPath) {
//
//		try {
//			System.out.println();
//			System.out.println("DOCUMENT  METADATA");
//			System.out.println(document.getMetaData());
//
//			DocumentChunkRetrieval chunkRetriever = document
//					.getDocumentChunkRetrieval();
//			int chunkCount = chunkRetriever.getDocumentSizeInfo()
//					.getTotalChunks();
//			System.out.println("DOCUMENT  TEXT -  count = " + chunkCount);
//
//			for (int i = 1; i <= chunkCount; i++) {
//				String docChunk = chunkRetriever.getText(i);
//				System.out.println("DOC " + i);
//				System.out.println((docChunk)); // really large
//			}
//		} catch (NovusException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//
//	}
//	private void printDocumentFiles(String docFileLocationPath ,DocumentChunkRetrieval chunkRetriever )
//	{
//
//
//		try
//		{
//			//print
//			OutputFormat format = new OutputFormat();
//			
//			format.setIndenting(true);
//			format.setLineSeparator("\r\n");
//			format.setEncoding("UTF-8");
//			
//
//			XMLSerializer serializer = new XMLSerializer(
//			new FileOutputStream(new File(docFileLocationPath)), format);
//
//			//serializer.serialize(chunkRetriever);
//
//		} catch(IOException ie) {
//		    ie.printStackTrace();
//		    //throw new Exception("Failed while printing DOM to specified path ..."+ie);
//		}
//		
//	}

	
	@Required
	public void setNovusFactory(NovusFactory factory) {
		this.novusFactory = factory;
	}

}
