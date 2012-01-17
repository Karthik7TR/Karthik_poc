/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.parsinghandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;

/**
 * This class defines the handler that parses the Toc xml to generate a list of GUIDs.
 *
 * @author <a href="mailto:Nirupam.Chatterjee@thomsonreuters.com">Nirupam Chatterjee</a> u0072938
 */
public class TOCXmlHandler extends DefaultHandler
{
	private List<String> guidList = new ArrayList<String>();
	private String tempVal;
	

	private static final String DOCUMENT_GUID_ELEMENT ="DocumentGuid";
	
	public void startElement(String uri, String localName, String qName, Attributes atts)
			throws SAXParseException
	{
		if (qName.equalsIgnoreCase(DOCUMENT_GUID_ELEMENT))
		{
			tempVal = "";
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase(DOCUMENT_GUID_ELEMENT)) {
			//add normalized first line cite
			guidList.add(tempVal);
		}
	}	
	
	public void setGuidList(List<String> aList)
	{
		guidList = aList;
	}
	
	public List<String> getGuidList()
	{
		return guidList;
	}
	
	public List<String> parseDocument(File metadataFile) {
			
			//get a factory
			SAXParserFactory spf = SAXParserFactory.newInstance();
			try {
			
				//get a new instance of parser
				SAXParser sp = spf.newSAXParser();
				
//				sp.parse(metadataFile, this);
				
				//parse the file and also register this class for call backs
				sp.parse("C:\\Users\\U0072938\\Documents\\EBookToc.xml", this);
				
				printData();
				
			}catch(SAXException se) {
				se.printStackTrace();
			}catch(ParserConfigurationException pce) {
				pce.printStackTrace();
			}catch (IOException ie) {
				ie.printStackTrace();
			}
			return this.guidList;
		}

	/**
	 * Go through the doc metadata and print
	 * the contents
	 */
	private void printData(){
		
		System.out.println("Values of guids published are being printed out ");
		
		for (int i=0; i<guidList.size(); i++) {
			System.out.println(guidList.get(i));
		}

		
	}

	public void runExample() {
			parseDocument(new File("C:\\Users\\U0072938\\Documents\\DocMetaDataSample.xml"));
	//		printData();
		}

	public static void main(String[] args){
		TOCXmlHandler spe = new TOCXmlHandler();
		spe.runExample();
}	
}
