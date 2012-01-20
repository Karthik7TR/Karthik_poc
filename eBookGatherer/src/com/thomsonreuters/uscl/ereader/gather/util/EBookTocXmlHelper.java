/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;


/**
 * class creates EBook Toc xml.
 * @author U0105927
 *
 */
public class EBookTocXmlHelper 
{
	private EBookTocXmlHelper() {
		super();
	}

	public static void processTocListToCreateEBookTOC(List<EBookToc> eBookTocList, File tocFile) throws GatherException
	{
		//Get a DOM object
		Document dom =createDocument();
		createTocXMLStructure(dom,eBookTocList);
		printToFile(dom, tocFile);
	}


	/**
	 * Using JAXP in implementation independent manner create a document object
	 * using which we create a xml tree in memory
	 * @throws Exception 
	 */
	private static Document createDocument() throws GatherException {

		//get an instance of factory
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
				//get an instance of builder
				DocumentBuilder db = dbf.newDocumentBuilder();
		
				//create an instance of DOM
				dom = db.newDocument();

		}catch(ParserConfigurationException pce) {
			
			throw new GatherException("Failed to create DOM object ..."+pce);
			//System.exit(1);
		}
		return dom;

	}

	/**
	 * This method creates root element and hands over object to recursive function to 
	 * create detailed/nested toc structure. 
	 * TODO: need to create this function recursive. 
	 */
	private static void createTocXMLStructure(Document dom,List<EBookToc> eBookTocList){

		//create the root element <Books>
		Element rootElement = dom.createElement("EBook");
		dom.appendChild(rootElement);

		createRecusiveDomStructure(dom, eBookTocList, rootElement);

	}

	/**
	 * recursive function to process Toc tree 
	 * @param dom
	 * @param eBookTocList
	 * @param rootElement
	 */
	private static void createRecusiveDomStructure(Document dom, List<EBookToc> eBookTocList,
			Element rootElement){
		Iterator it  = eBookTocList.iterator();
		while(it.hasNext()) {
			EBookToc eBookToc = (EBookToc)it.next();
			//For each toc object  create <EBookToc> element and attach it to root
			Element bookEle = createEBookTocElements(dom,eBookToc);
			rootElement.appendChild(bookEle);
			if(eBookToc.getChildrenCount() > 0){
				createRecusiveDomStructure(dom,eBookToc.getChildren(),bookEle);
			}
		}
	}

	/**
	 * Helper method which creates a XML element 
	 * @param eBookToc The book for which we need to create an xml representation
	 * @return XML element snippet representing a book
	 */
	private static Element createEBookTocElements(Document dom,EBookToc eBookToc){

		Element bookEle = dom.createElement(EBConstants.TOC_ELEMENT);

		//create Name element and attach it to bookElement
		Element nameElement = dom.createElement(EBConstants.NAME_ELEMENT);
		Text nameText = dom.createTextNode(eBookToc.getName());
		nameElement.appendChild(nameText );
		bookEle.appendChild(nameElement);


		//create Guid element and attach it to bookElement
		Element guidElement = dom.createElement(EBConstants.GUID_ELEMENT);
		Text guidText = dom.createTextNode(eBookToc.getGuid());
		guidElement.appendChild(guidText);
		bookEle.appendChild(guidElement);
		
		//create parentGuid element and attach it to bookElement
		Element parentGuidElement = dom.createElement(EBConstants.PARENT_GUID_ELEMENT);
		Text parentGuidText = dom.createTextNode(eBookToc.getParentGuid());
		parentGuidElement.appendChild(parentGuidText);
		bookEle.appendChild(parentGuidElement);

		//create parentGuid element and attach it to bookElement
		Element documentGuidElement = dom.createElement(EBConstants.DOCUMENT_GUID_ELEMENT);
		Text documentGuidText = dom.createTextNode(eBookToc.getDocGuid());
		documentGuidElement.appendChild(documentGuidText);
		bookEle.appendChild(documentGuidElement);


		//create Metadata element and attach it to bookElement
		Element metadataElement = dom.createElement(EBConstants.METADATA_ELEMENT);
		Text metadataText = dom.createTextNode(eBookToc.getMetadata());
		metadataElement.appendChild(metadataText);
		bookEle.appendChild(metadataElement);

		return bookEle;

	}

	/**
	 * 
	 * prints the XML document to file.
	 * @param tocFile TODO
	 * @throws Exception 
     */
	private static void printToFile(Document dom, File tocFile) throws GatherException
	{

//		try
//		{
			//print
			OutputFormat format = new OutputFormat(dom);
			
			format.setIndenting(true);
			format.setLineSeparator("\r\n");
			format.setEncoding("UTF-8");
			

			XMLSerializer serializer = null;
			try {
				serializer = new XMLSerializer(
				new FileOutputStream(tocFile), format);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw new GatherException("Failed to find specified file path ..."+e);

			}

			try {
				serializer.serialize(dom);
			} catch (IOException e) {
				e.printStackTrace();
				throw new GatherException("Failed while printing DOM to specified path ..."+e);
			}

	}


}
