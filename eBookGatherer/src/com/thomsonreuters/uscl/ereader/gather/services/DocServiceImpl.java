/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.util.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.util.NovusAPIHelper;
import com.westgroup.novus.productapi.Document;
import com.westgroup.novus.productapi.DocumentChunkRetrieval;
import com.westgroup.novus.productapi.Find;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

/**
 * This class corresponds with novus helper and gets TOC documents.
 * 
 * @author U0105927
 * 
 */
public class DocServiceImpl implements DocService {

	@Autowired
	public NovusAPIHelper novusAPIHelper;

	public String outputTocFileName = EBConstants.COLLECTION_SET_TYPE;

	List<EBookToc> eBookDocumentList = new ArrayList<EBookToc>();

	/**
	 * 
	 * users will alwyas provide us with collection name and not collectionSet
	 * names.
	 */
	public Document[] getDocFromNovus(List<EBookToc> docGuidList,
			String collectionName, String docFileLocationPath) 
	{
		Novus novusObject = null;
		Document[] documents = null;

		/*** for ebook builder we will always get Collection. ***/
		try {
			novusObject = novusAPIHelper.getNovusObject();
			Find find = novusObject.getFind();
			for (EBookToc eBookToc : docGuidList)
			{

				find.addDocumentInfo(collectionName, null,
						eBookToc.getDocGuid());
			}
			documents = find.getDocuments();
			// tocNodes = toc.getRootNodes(); //getAllDocuments(guid);
			// tocGuidList = extractTocListForEbook(tocNodes);

		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			novusAPIHelper.closeNovusConnection(novusObject);
		}

		for (Document document : documents) {
			processDocument(document,docFileLocationPath);
		}
		return documents;

	}

	private void processDocument(Document document,String docFileLocationPath) {

		try {
			System.out.println();
			System.out.println("DOCUMENT  METADATA");
			System.out.println(document.getMetaData());

			DocumentChunkRetrieval chunkRetriever = document
					.getDocumentChunkRetrieval();
			int chunkCount = chunkRetriever.getDocumentSizeInfo()
					.getTotalChunks();
			System.out.println("DOCUMENT  TEXT -  count = " + chunkCount);

			for (int i = 1; i <= chunkCount; i++) {
				String docChunk = chunkRetriever.getText(i);
				System.out.println("DOC " + i);
				System.out.println((docChunk)); // really large
			}
		} catch (NovusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	private void printDocumentFiles(String docFileLocationPath ,DocumentChunkRetrieval chunkRetriever )
	{


		try
		{
			//print
			OutputFormat format = new OutputFormat();
			
			format.setIndenting(true);
			format.setLineSeparator("\r\n");
			format.setEncoding("UTF-8");
			

			XMLSerializer serializer = new XMLSerializer(
			new FileOutputStream(new File(docFileLocationPath)), format);

			//serializer.serialize(chunkRetriever);

		} catch(IOException ie) {
		    ie.printStackTrace();
		    //throw new Exception("Failed while printing DOM to specified path ..."+ie);
		}
		
	}

	@Override
	public List<EBookToc> getDocFromNovus(String guid, String collectionName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void retrieveDocument(List<EBookToc> eBookTocDocumentList) {
		// TODO Auto-generated method stub

	}

}
