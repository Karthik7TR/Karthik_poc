/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;

public class SplitBookTocFilter extends XMLFilterImpl {
		
	private List<String> splitTocGuidList;
	private static final Logger LOG = LogManager.getLogger(SplitBookTocFilter.class);
	
	private boolean bufferingTocGuid = false;
	private boolean leafNode = false;
	private boolean foundMatch = false;
	private boolean isEbook = false;
	
	private static final String URI = "";
	private static final String TITLE_BREAK = "titlebreak";
	private static final String TOC_GUID = "Guid";
	private static final String EBOOK = "EBook";
	private static final String EBOOK_TOC = "EBookToc";
	private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
	private static final String DOCUMENT_GUID = "DocumentGuid";
	private static final String MISSING_DOCUMENT = "MissingDocument";
	
	
	private int number = 1;
	private int total = 0;

	private String splitTilteId;
	private StringBuffer tmpValue = new StringBuffer();
	private Map<String,DocumentInfo> documentInfoMap = new HashMap<String,DocumentInfo>();
	private Map<String,String>  elementValueMap = new LinkedHashMap<String,String>();
	private List<String> wrongSplitTocNodes = new ArrayList<String>();
	private String splitNode = "";


	public List<String> getWrongSplitTocNode() {
		return wrongSplitTocNodes;
	}


	public void setWrongSplitTocNode(List<String> wrongSplitTocNode) {
		this.wrongSplitTocNodes = wrongSplitTocNode;
	}


	public String getSplitTilteId() {
		return splitTilteId;
	}


	public void setSplitTilteId(String splitTilteId) {
		this.splitTilteId = splitTilteId;
	}
	
	public Map<String, DocumentInfo> getDocumentInfoMap() {
		return documentInfoMap;
	}


	public void setDocumentInfoMap(Map<String, DocumentInfo> documentInfoMap) {
		this.documentInfoMap = documentInfoMap;
	}

	public int getNumber() {
		return number;
	}


	public void setNumber(int number) {
		this.number = number;
	}



	public List<String> getSplitTocGuidList() {
		return splitTocGuidList;
	}


	public void setSplitTocGuidList(List<String> splitDocumentList) {
		this.splitTocGuidList = splitDocumentList;
	}


	@Override
	public void startElement(String uri, String localName,
			String qName, Attributes atts) throws SAXException {
		 if (EBOOK.equals(qName)) {
			 isEbook = Boolean.TRUE;
		 }
		  if (EBOOK_TOC.equals(qName)) {
			 //This title break is to write at the top after the <EBook>
			 if(isEbook){
				 	super.startElement(URI, EBOOK, EBOOK,EMPTY_ATTRIBUTES);
				 	StringBuffer titleBreakBuffer = new StringBuffer();
				 	titleBreakBuffer.append("eBook 1 of ");
				 	titleBreakBuffer.append(splitTocGuidList.size()+1);
					String text = titleBreakBuffer.toString();
					super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);
					super.characters(text.toCharArray(), 0, text.length());
					super.endElement(URI, TITLE_BREAK, TITLE_BREAK);
					isEbook = false;
				}
			 	else{					 
					 if (elementValueMap.size()>0){						 
						 decideToWrite();
					 }
			 	}
		 }
		 else if (TOC_GUID.equals(qName)){
				bufferingTocGuid = Boolean.TRUE;
			}

	}
	
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		if(tmpValue.length() > 0) {
			for(int i=start; i<start+length; i++) {
				tmpValue.append(ch[i]);
			}
		}
		else{
			tmpValue.append(new String(ch, start, length));
		}
		
		if (bufferingTocGuid) {
			splitNode = StringUtils.substring(tmpValue.toString(), 0,33);
			if (splitTocGuidList.contains(splitNode)){
				foundMatch = true;					
			}
			bufferingTocGuid = Boolean.FALSE;
		}
		   
	}
	
		
	
	private void writeSplitToc(boolean isSplit) throws SAXException {	
		total++;
		if(isSplit){
			super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);

			LOG.debug("TitleBreak has been added at "+splitNode+ ", count "+total+" and title "+splitTilteId);
			total=0;
			number++;
			StringBuffer proviewDisplayName = new StringBuffer();
			proviewDisplayName.append("eBook ");
			proviewDisplayName.append(number);
			proviewDisplayName.append(" of "+(splitTocGuidList.size()+1));
			String text = proviewDisplayName.toString();
			super.characters(text.toCharArray(), 0, text.length());
			super.endElement(URI, TITLE_BREAK, TITLE_BREAK);
			if(!leafNode){
				LOG.error("Split at TOC node GUID "+ splitNode +" is at an incorrect level");
				wrongSplitTocNodes.add(splitNode);
			}
		}		
		
		 leafNode = Boolean.FALSE;
		
		super.startElement(URI, EBOOK_TOC, EBOOK_TOC, EMPTY_ATTRIBUTES);
		
		for (Map.Entry<String, String> entry : elementValueMap.entrySet()) {
			//Adding Document Info
			if(entry.getKey().equals(DOCUMENT_GUID) ){
					leafNode = Boolean.TRUE;
					DocumentInfo documentInfo = new DocumentInfo();
					if (number > 1){
						documentInfo.setSplitTitleId(splitTilteId+"_pt"+number);
					}
					else {
						documentInfo.setSplitTitleId(splitTilteId);
					}
					documentInfoMap.put(entry.getValue(),documentInfo);
			}
			else if (entry.getKey().equals(MISSING_DOCUMENT) ){
				leafNode = Boolean.TRUE;
			}
			super.startElement(URI, entry.getKey(), entry.getKey(), EMPTY_ATTRIBUTES);
			super.characters(entry.getValue().toCharArray(), 0, entry.getValue().length());
			super.endElement(URI, entry.getKey(), entry.getKey());
		}
		
		elementValueMap.clear();
		
	}
	
	private void decideToWrite() throws SAXException {
		
			if (!foundMatch) {
				writeSplitToc(false);
			} else {
				writeSplitToc(true);
				foundMatch = Boolean.FALSE;
			}
	}
	
	
    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException
    {      	
    	if(EBOOK_TOC.equals(qName)){
    		if (elementValueMap.size() > 0) {
    			decideToWrite();
    		}
    		super.endElement(uri, localName, qName);
    	}
    	else if (EBOOK.equals(qName)){
        		super.endElement(uri, localName, qName);        		
    	}
    	else{
    		elementValueMap.put(qName, tmpValue.toString());
    		tmpValue = new StringBuffer();
    	}
    	
    }    
   
}
