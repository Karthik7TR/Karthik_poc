package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;

public class SplitBookTocFilter extends XMLFilterImpl {
		
	private List<String> splitTocGuidList;
	private static final Logger LOG = Logger.getLogger(SplitBookTocFilter.class);
	
	private boolean bufferingTocGuid = false;
	private boolean foundMatch = false;
	private boolean isEbook = false;
	
	private static final String URI = "";
	private static final String TITLE_BREAK = "titlebreak";
	private static final String TOC_GUID = "Guid";
	private static final String EBOOK = "EBook";
	private static final String EBOOK_TOC = "EBookToc";
	private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
	private static final String DOCUMENT_GUID = "DocumentGuid";
	
	
	private int number = 1;

	private String splitTilteId;
	private String titleBreakText;
	private StringBuffer tmpValue = new StringBuffer();
	private Map<String,DocumentInfo> documentInfoMap = new HashMap<String,DocumentInfo>();
	private Map<String,String>  elementValueMap = new LinkedHashMap<String,String>();


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


	public String getTitleBreakText() {
		return titleBreakText;
	}


	public void setTitleBreakText(String titleBreakText) {
		this.titleBreakText = titleBreakText;
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
					String text = titleBreakText+1;
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
			if (splitTocGuidList.contains(StringUtils.substring(tmpValue.toString(), 0,33))){
				foundMatch = true;					
			}
			bufferingTocGuid = Boolean.FALSE;
		}
		   
	}
	
		
	
	private void writeSplitToc(boolean isSplit) throws SAXException {
		if(isSplit){
			super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);
			number++;
			String text = titleBreakText+number;
			super.characters(text.toCharArray(), 0, text.length());
			super.endElement(URI, TITLE_BREAK, TITLE_BREAK);
		}
		
		super.startElement(URI, EBOOK_TOC, EBOOK_TOC, EMPTY_ATTRIBUTES);
		
		for (Map.Entry<String, String> entry : elementValueMap.entrySet()) {
			//Adding Document Info
			if(entry.getKey().equals(DOCUMENT_GUID) ){
					DocumentInfo documentInfo = new DocumentInfo();
					if (number > 1){
						documentInfo.setSplitTitleId(splitTilteId+"_pt"+number);
					}
					else {
						documentInfo.setSplitTitleId(splitTilteId);
					}
					documentInfoMap.put(entry.getValue(),documentInfo);
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