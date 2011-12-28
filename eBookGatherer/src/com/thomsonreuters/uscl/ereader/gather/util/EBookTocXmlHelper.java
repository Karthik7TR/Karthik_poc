/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/

package com.thomsonreuters.uscl.ereader.gather.util;


import java.io.File;
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

/**
 * class creates EBook Toc xml.
 * @author U0105927
 *
 */
public class EBookTocXmlHelper 
{	

	public void processTocListToCreateEBookTOC(List<EBookToc> eBookTocList){
		System.out.println("Started .. ");
		//Get a DOM object
		Document dom =createDocument();
		createRootElement(dom,eBookTocList);
		printToFile(dom);
		System.out.println("Generated file successfully.");
	}


	/**
	 * Using JAXP in implementation independent manner create a document object
	 * using which we create a xml tree in memory
	 */
	private Document createDocument() {

		//get an instance of factory
		Document dom = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
		//get an instance of builder
		DocumentBuilder db = dbf.newDocumentBuilder();

		//create an instance of DOM
		dom = db.newDocument();

		}catch(ParserConfigurationException pce) {
			//dump it
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
			System.exit(1);
		}
		return dom;

	}

	/**
	 * This method creates root element and hands over object to recursive function to 
	 * create detailed/nested toc structure. 
	 * TODO: need to create this function recursive. 
	 */
	private void createRootElement(Document dom,List<EBookToc> eBookTocList){

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
	private void createRecusiveDomStructure(Document dom, List<EBookToc> eBookTocList,
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
	private Element createEBookTocElements(Document dom,EBookToc eBookToc){

		Element bookEle = dom.createElement(EBConstants.TOC_ROOT_ELEMENT);

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
		guidElement.appendChild(parentGuidText);
		bookEle.appendChild(parentGuidElement);


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
     */
	private void printToFile(Document dom){

		try
		{
			//print
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);

			XMLSerializer serializer = new XMLSerializer(
			new FileOutputStream(new File("C:/temp/ebook.xml")), format);

			serializer.serialize(dom);

		} catch(IOException ie) {
		    ie.printStackTrace();
		}
	}


}
