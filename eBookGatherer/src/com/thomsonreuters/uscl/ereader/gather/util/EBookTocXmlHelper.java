/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

	public static void processTocListToCreateEBookTOC(List<EBookToc> eBookTocList, File tocFile) throws GatherException {
		try {
			Document dom = createDocument();
			createTocXMLStructure(dom,eBookTocList);
			writeDocumentToFile(dom, tocFile);
		} catch (Exception e) {
			throw new GatherException("Error creating Table of Contents (TOC) file: " + tocFile, e);
		}
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
		for (EBookToc eBookToc : eBookTocList) {
			//For each toc object  create <EBookToc> element and attach it to root
			Element bookElement = createEBookTocElements(dom,eBookToc);
			rootElement.appendChild(bookElement);
			if(eBookToc.getChildrenCount() > 0){
				createRecusiveDomStructure(dom,eBookToc.getChildren(),bookElement);
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

		if (nameText.getTextContent().indexOf("<") > -1) {
			    String newNameTextString = nameText.getTextContent().replaceAll("\\<.*?>","");
				nameText.setTextContent(newNameTextString);
			}
		
		nameElement.appendChild(nameText);
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
		if (metadataText.getTextContent().indexOf("<") > -1) {
		    String newMetadataString = metadataText.getTextContent().replaceAll("\\<.*?>","");
		    metadataText.setTextContent(newMetadataString);
		}
		
		metadataElement.appendChild(metadataText);
		bookEle.appendChild(metadataElement);

		return bookEle;

	}

	/**
	 * prints the XML document to file.
	 * @param file the TOC file to be created
	 * @throws Exception on file creation error
     */
	private static void writeDocumentToFile(Document dom, File file) throws Exception {
		
		OutputFormat format = new OutputFormat(dom);
		format.setIndenting(true);
		format.setLineSeparator("\r\n");
		String charEncoding = "UTF-8";
		format.setEncoding(charEncoding);
		// nonescaped means for example leave &apos; alone for Name and Metadata otherwise it converts to &amp;apos;
		String[] nonEscaped = {"Name","Metadata"};
		format.setNonEscapingElements(nonEscaped); 
		Writer out = new OutputStreamWriter(new FileOutputStream(file), charEncoding);
		try {
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(dom);
		} finally {
			out.close();
		}
	}
}
