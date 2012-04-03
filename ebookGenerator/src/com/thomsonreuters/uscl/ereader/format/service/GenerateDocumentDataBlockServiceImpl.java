/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.InputSource;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetaDataGuidParserService;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.gather.step.GatherDocAndMetadataTask;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;

/**
 * This class builds Document data block for document file. 
 *
 * @author <a href="mailto:Mahendra.Survase@thomsonreuters.com">Mahendra Survase</a> u0105927
 */
public class GenerateDocumentDataBlockServiceImpl implements
		GenerateDocumentDataBlockService {
	
	private static final Logger LOG = Logger.getLogger(GenerateDocumentDataBlockServiceImpl.class);
	private DocMetadataService docMetadataService;


	@Required
	public void setDocMetadataService(DocMetadataService docMetadataService) {
		this.docMetadataService = docMetadataService;
	}
	

	/**
	 * Builds document data block using passed in collection,version information.
	 * @param version
	 * @param guidCollection
	 * @param docGuid
	 * @return
	 * 
	 */
	private InputStream buildDocumentDataBlock(boolean version,
			String collectionName, String cite) {
			String currentDate = dateString();
			StringBuffer documentDataBlocks = new StringBuffer();
			documentDataBlocks.append("<document-data>");
			documentDataBlocks.append("<collection>");
			documentDataBlocks.append(collectionName);
			documentDataBlocks.append("</collection>");
			documentDataBlocks.append("<datetime>"+currentDate+"</datetime>");  
			documentDataBlocks.append("<versioned>");
			documentDataBlocks.append(version);
			documentDataBlocks.append("</versioned>");
			documentDataBlocks.append("<doc-type></doc-type>");
			documentDataBlocks.append("<cite></cite>"); 
			documentDataBlocks.append("</document-data>");
			
			LOG.debug("document dataBlock :"+documentDataBlocks);
			return new ByteArrayInputStream(documentDataBlocks.toString().getBytes());
		
	}
	

	/**
	 * Based on passed in document guid this method retrieve corresponding collectionName using metadata service and builds documentData block. 
	 * returns as InputStream. 
	 * @param titleId
	 * @param jobInstanceId
	 * @param docGuid
	 * @return
	 * @throws EBookFormatException
	 */
	@Override
	public InputStream getDocumentDataBlockAsStream(String titleId,int jobInstanceId ,String docGuid) throws EBookFormatException{
				
			 DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docGuid);
			 if(docMetadata == null){
					
				 String message = "Document metadata could not be found by passed in guid =" +docGuid +" and title Id ="+titleId
							+" and jobInstanceId ="+jobInstanceId;
					LOG.error(message);
					
					throw new EBookFormatException(message);

			 }
			 
			 String collectionName = docMetadata.getCollectionName();
			 boolean version = false;
			 String cite = null;
			 InputStream stringBuffer = buildDocumentDataBlock(version, collectionName, cite);
			 
		return stringBuffer;
	}
	
	/**
	 * Returns data in yyyyddMMhhmmss format. 
	 * @return
	 */
	private String dateString(){
		
		Date date = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyddMMhhmmss");
		String formattedDate = sdf.format(date); 
		System.out.println(formattedDate);
		
		return formattedDate;
	}

//
//
//	@Override
//	public int generateDocumentDataBlock(final File docDataFile, 
//			 File docsGuidsTextFile,String titleId,final Long jobInstanceId ) 
//		throws EBookFormatException 
//	{
//		int numDocsGenerated= 0;
//		String version="False";
//		LOG.info("Generating DocumentDataBlock block files for each document...");
//		
//		/******* parsing guid names and retrieving collection from metadata table *******************/
//		/**
//		 * 1) read one cpGuid at a time and 
//		 * 2) read corresponding xml file from shared location 
//		 * 3) get corresponding collection name and 
//		 * 4) build document datablock.
//		 */
//		List<String> docGuids = null;
//		HashMap<String,String> guidCollection = new HashMap();
//		
//		List<File> fileList = new ArrayList<File>();
//		FileExtensionFilter filterExtention = new FileExtensionFilter();
//		String [] stringArray = {".xml"};
//		filterExtention.setAcceptedFileExtensions(stringArray);
//		File[] docfiles = docDataFile.listFiles(filterExtention);
//		fileList.addAll(Arrays.asList(docfiles));	
//		
//		guidCollection = generateGuidCollectionMap(docsGuidsTextFile, titleId, jobInstanceId,docGuids );
//		
//		Set set=guidCollection.keySet();   
//		Iterator iterator=set.iterator();
//		
//		
//		
//		while(iterator.hasNext()){
//			String cpGuid =(String) iterator.next();
//			String collecitonName = guidCollection.get(cpGuid);
//			getDocumentDataBlockAsStream(titleId,jobInstanceId.intValue(),cpGuid);
//			
//			
//		}
//   
//		
//		for (File file : docfiles) {
//			numDocsGenerated ++;
//			//File docfile = new File(file.getAbsoluteFile().toString());
//			Reader reader;
//			try {
//				LOG.debug(file);
//				InputStream inputStream= new FileInputStream(file);
//				reader = new InputStreamReader(inputStream,"UTF-8");
//				//reader.
//				
//			} catch (FileNotFoundException e) {
//				String message = "File not found " +
//						"while reading document data from shared location for titleId " +titleId +" jobId "+jobInstanceId ;
//				LOG.error(message);
//				e.printStackTrace();
//				throw new EBookFormatException(message);
//				
//			} catch (UnsupportedEncodingException e) {
//				String message = "Unsupported encoding exception " +
//						"while reading document data from shared location for titleId " +titleId +" jobId "+jobInstanceId ;
//				LOG.error(message);
//				e.printStackTrace();
//				throw new EBookFormatException(message);
//				
//			}
//			 
//			InputSource is = new InputSource(reader);
//			is.setEncoding("UTF-8");
//			LOG.debug("is.getByteStream() >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+is.getByteStream()); 
//			 
//		//	saxParser.parse(is, handler)
//			
//		}
//		
//    	
//		/**********************************************************************************************************/
//
//
//		LOG.info("Generated DocumentDataBlock block files for " + numDocsGenerated + " documents.");
//		
//		return numDocsGenerated;
//	}
//	/**
//	 * Builds document data block using passed in collection,version information.
//	 * @param version
//	 * @param guidCollection
//	 * @param docGuid
//	 * @return
//	 * 
//	 * 
//
//	 */
//	private StringBuffer buildDocumentDataBlock_temp(String version,
//			HashMap<String, String> guidCollection, String docGuid) {
//			
//			String currentDate = dateString();
//			StringBuffer documentDataBlocks = new StringBuffer();
//			documentDataBlocks.append("<document-data>");
//			documentDataBlocks.append("<collection>");
//			documentDataBlocks.append(guidCollection.get(docGuid));
//			documentDataBlocks.append("</collection>");
//			documentDataBlocks.append("<datetime>"+currentDate+"</datetime>");  
//			documentDataBlocks.append("<versioned>");
//			documentDataBlocks.append(version);
//			documentDataBlocks.append("</versioned>");
//			documentDataBlocks.append("<doc-type></doc-type>");
//			documentDataBlocks.append("<cite></cite>"); 
//			documentDataBlocks.append("</document-data>");
//			
//			
//		return documentDataBlocks;
//		
//	}
//	
//
//	
//	/**
//	 * parse docGuidText files and generated map with guid and corresponding collection name. 
//	 * 
//	 * @param docsGuidsFile
//	 * @param titleId
//	 * @param jobInstanceId
//	 * @param docGuids
//	 * @return
//	 */
//	private HashMap<String,String>  generateGuidCollectionMap(File docsGuidsFile, String titleId,
//			final Long jobInstanceId, List<String> docGuids) 
//	{
//		HashMap<String, String> guidCollection = new HashMap();
//		try {
//			docGuids = GatherDocAndMetadataTask.readDocGuidsFromTextFile(docsGuidsFile);
//		} catch (IOException e) {
//			new EBookFormatException("failed to read Guild List from text file ");
//			e.printStackTrace();
//		}
//    	
//    	
//    	for (String docGuidId : docGuids) {
//			
//    		DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId.intValue(), docGuidId);
//    		guidCollection.put(docGuidId, docMetadata.getCollectionName());
//    		
//    		System.out.println("Collection name retirived from metadata. ="+docMetadata.getCollectionName());
//    		
//		}
//    	return  guidCollection;
//	}
//	
//	
//	
//		
		
}
