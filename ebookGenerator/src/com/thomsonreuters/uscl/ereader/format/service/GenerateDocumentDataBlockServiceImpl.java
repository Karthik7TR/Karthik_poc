/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;

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
	 * @param versioned
	 * @param guidCollection
	 * @param docGuid
	 * @return
	 * 
	 */
	private InputStream buildDocumentDataBlock(String versioned,
			String collectionName, String cite) {
			String currentDate = dateString();
			StringBuffer documentDataBlocks = new StringBuffer();
			documentDataBlocks.append("<document-data>");
			documentDataBlocks.append("<collection>");
			documentDataBlocks.append(collectionName);
			documentDataBlocks.append("</collection>");
			documentDataBlocks.append("<datetime>"+currentDate+"</datetime>");  
			documentDataBlocks.append("<versioned>");
			documentDataBlocks.append(versioned);
			documentDataBlocks.append("</versioned>");
			documentDataBlocks.append("<doc-type></doc-type>");
			documentDataBlocks.append("<cite></cite>"); 
			documentDataBlocks.append("</document-data>");
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
	public InputStream getDocumentDataBlockAsStream(String titleId, Long jobInstanceId ,String docGuid) throws EBookFormatException{
				
			 DocMetadata docMetadata = docMetadataService.findDocMetadataByPrimaryKey(titleId, jobInstanceId, docGuid);
			 if(docMetadata == null){
					
				 String message = "Document metadata could not be found for given guid =" +docGuid +" and title Id ="+titleId
							+" and jobInstanceId ="+jobInstanceId;
					LOG.error(message);
					
					throw new EBookFormatException(message);

			 }
			 
			 String collectionName = docMetadata.getCollectionName();
			 String versioned = "False";
			 String cite = null;
			 InputStream stringBuffer = buildDocumentDataBlock(versioned, collectionName, cite);
			 
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
		
		return formattedDate;
	}

		
}
