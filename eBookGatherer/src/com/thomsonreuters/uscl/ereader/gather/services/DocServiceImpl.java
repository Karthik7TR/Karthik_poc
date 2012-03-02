/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
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
 * Novus document and metadata fetch.
 */
public class DocServiceImpl implements DocService {

	private static final Logger Log = Logger.getLogger(DocServiceImpl.class);

	private NovusFactory novusFactory;
	
	private NovusUtility novusUtility;	


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thomsonreuters.uscl.ereader.gather.services.DocService#fetchDocuments
	 * (java.util.Collection, java.lang.String, java.io.File, java.io.File)
	 */
	@Override
	public void fetchDocuments(Collection<String> docGuids,
			String collectionName, File contentDestinationDirectory,
			File metadataDestinationDirectory) throws GatherException {
		Assert.isTrue(contentDestinationDirectory.exists(),
				"The content destination directory for the documents does not exist: "
						+ contentDestinationDirectory);
		Assert.isTrue(contentDestinationDirectory.exists(),
				"The content destination directory for the document metadata does not exist: "
						+ metadataDestinationDirectory);

		Novus novus = novusFactory.createNovus();
		final Integer docRetryCount = new Integer(novusUtility.getDocRetryCount());		
		boolean anyException = false;
		String docGuid = null;
		try {
			Find finder = novus.getFind();
			finder.setResolveIncludes(true);

			int tocSequence = 0;
			for (String guid : docGuids) {
				tocSequence++;
				docGuid = guid;
				Document document = null;

				// This is the counter for checking how many Novus retries we
				// are making
				Integer novusRetryCounter = 0;
				while (novusRetryCounter <= docRetryCount) {
					try {
						if (collectionName == null) {
							document = finder.getDocument(null, guid);
						} else {
							document = finder.getDocument(collectionName, null,
									guid);
						}
						break;
					} catch (final Exception exception) {
						try {
							novusRetryCounter = novusUtility.handleException(exception,
									novusRetryCounter, docRetryCount);
						} catch (NovusException e) {
							anyException = true;
							GatherException ge = new GatherException(
									"Novus error occurred fetching document " + docGuid, e,
									GatherResponse.CODE_NOVUS_ERROR);
							throw ge;
						} catch (IOException e) {
							anyException = true;
							GatherException ge = new GatherException(
									"File I/O error writing document " + docGuid, e,
									GatherResponse.CODE_FILE_ERROR);
							throw ge;
						} catch (NullPointerException e) {
							anyException = true;
							GatherException ge = new GatherException(
									"Null document fetching guid " + docGuid, e,
									GatherResponse.CODE_FILE_ERROR);
							throw ge;
						} catch (Exception e) {
							anyException = true;
							GatherException ge = new GatherException(
									"Exception happened in fetching doc for " + docGuid, e,
									GatherResponse.CODE_FILE_ERROR);
							throw ge;
						} 
					}
				}
				createContentFile(document, contentDestinationDirectory, docRetryCount);
				createMetadataFile(document, metadataDestinationDirectory,
						tocSequence, docRetryCount);
			}
		} catch (NovusException e) {
			anyException = true;
			GatherException ge = new GatherException(
					"Novus error occurred fetching document " + docGuid, e,
					GatherResponse.CODE_NOVUS_ERROR);
			throw ge;
		} catch (IOException e) {
			anyException = true;
			GatherException ge = new GatherException(
					"File I/O error writing document " + docGuid, e,
					GatherResponse.CODE_FILE_ERROR);
			throw ge;
		} catch (NullPointerException e) {
			anyException = true;
			GatherException ge = new GatherException(
					"Null document fetching guid " + docGuid, e,
					GatherResponse.CODE_FILE_ERROR);
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

	/**
	 * @param document
	 * @param destinationDirectory
	 * @throws NovusException
	 * @throws IOException
	 */
	private final void createContentFile(Document document,
			File destinationDirectory, int retryCount) throws NovusException, IOException {
		String basename = document.getGuid() + EBConstants.XML_FILE_EXTENSION;
		File destinationFile = new File(destinationDirectory, basename);
		String docText = null;

		// This is the counter for checking how many Novus retries we are making
		// for doc retrieval
		Integer novusDocRetryCounter = 0;	
		while (novusDocRetryCounter <= retryCount) {
			try {
				docText = document.getText();
				if((document.getErrorCode() == null) || (document.getErrorCode().endsWith("00"))) {
					break;
				} else {
					Log.error("Exception happened while retreving text for the guid " + document.getGuid() + " with an error code " + document.getErrorCode() + "Retry count is "
							+ novusDocRetryCounter);
					novusDocRetryCounter++;					
				}
			} catch (final Exception exception) {
				try {
					novusDocRetryCounter = novusUtility.handleException(exception,
							novusDocRetryCounter, retryCount);
				} catch (Exception e) {
					Log.error("Exception in handleException " + e.getMessage());
				}
			}
		}
		createFile(docText.toString(), destinationFile);
	}

	/**
	 * @param document
	 * @param destinationDirectory
	 * @param tocSeqNum
	 * @throws NovusException
	 * @throws IOException
	 */
	private final void createMetadataFile(Document document,
			File destinationDirectory, int tocSeqNum, int retryCount) throws NovusException,
			IOException {
		String basename = tocSeqNum + "-" + document.getCollection() + "-"
				+ document.getGuid() + EBConstants.XML_FILE_EXTENSION;
		File destinationFile = new File(destinationDirectory, basename);

		String docMetaData = null;
		// This is the counter for checking how many Novus retries we are making
		// for meta data retrieval
		Integer novusMetaRetryCounter = 0;		
		while (novusMetaRetryCounter < retryCount) {
			try {
				docMetaData = document.getMetaData();
				if ((document.getErrorCode() == null) || (document.getErrorCode().endsWith("00"))) { 
					break;
				} else {
					Log.error("Exception happened while retreving metadata for the guid " + document.getGuid() + " with an error code " + document.getErrorCode() + "Retry count is "
							+ novusMetaRetryCounter);
					novusMetaRetryCounter++;
				}
			} catch (final Exception exception) {
				try {
					novusMetaRetryCounter = novusUtility.handleException(exception,
							novusMetaRetryCounter, retryCount);
				} catch (Exception e) {
					Log.error("Exception in handleException " + e.getMessage());
				}
			}
		}
		createFile(docMetaData.toString(), destinationFile);
	}

	/**
	 * Create a file containing the specified content.
	 */
	private static final void createFile(String content, File destinationFile) throws IOException {
		FileOutputStream stream = new FileOutputStream(destinationFile);
		Writer writer = null;
		try {
			String charset = "UTF-8";	// explicitly set the character set
			writer = new OutputStreamWriter(stream, charset);
			writer.write(content);
			writer.flush();
		} finally {
			writer.close();
			stream.close();
		}
	}

	/**
	 * Delete all files in the specified directory.
	 * @param directory directory whose files will be removed
	 */
	public static void removeAllFilesInDirectory(File directory) {
		FileUtils.deleteQuietly(directory);
	}

	@Required
	public void setNovusFactory(NovusFactory factory) {
		this.novusFactory = factory;
	}

	@Required
	public void setNovusUtility(NovusUtility novusUtil) {
		this.novusUtility = novusUtil;
	}
}
