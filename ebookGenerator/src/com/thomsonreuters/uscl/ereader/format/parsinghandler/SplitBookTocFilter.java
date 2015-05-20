package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.proview.TocNode;

public class SplitBookTocFilter extends XMLFilterImpl {
		
	private List<String> splitTocGuidList;
	private TocNode currentNode  = new TocEntry(1);
	private static final Logger LOG = Logger.getLogger(SplitBookTocFilter.class);
	
	private boolean bufferingTocGuid = false;
	private boolean bufferingText = false;
	private boolean isDocGuid = false;
	private boolean foundMatch = false;
	private boolean isEbook = false;
	
	private static final String URI = "";
	private static final String TITLE_BREAK = "titlebreak";
	private static final String NAME = "Name";
	private static final String TOC_GUID = "Guid";
	private static final String EBOOK = "EBook";
	private static final String EBOOK_TOC = "EBookToc";
	private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
	private static final String DOCUMENT_GUID = "DocumentGuid";
	
	
	private StringBuilder tocGuid = new StringBuilder();
	private StringBuilder textBuffer = new StringBuilder();
	private static int number = 0;

	private String titleBreakText;
	Map<String,DocumentInfo> documentInfoMap = new HashMap<String,DocumentInfo>();
	
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
	

	public static int getNumber() {
		return number;
	}


	public static void setNumber(int number) {
		SplitBookTocFilter.number = number;
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
			 if(isEbook){
					String text = titleBreakText+0;
					super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);
					super.characters(text.toCharArray(), 0, text.length());
					super.endElement(URI, TITLE_BREAK, TITLE_BREAK);
					isEbook=false;
				}
		 }
		 else if (TOC_GUID.equals(qName)){
				bufferingTocGuid = Boolean.TRUE;
			}
		 else if (NAME.equals(qName)) {
				bufferingText = Boolean.TRUE;
			}
		 else {
			 	if (DOCUMENT_GUID.equals(qName)){
			 		isDocGuid = Boolean.TRUE;
			 	}
			 	super.startElement(uri, localName, qName, atts);
		 }

	}
	
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
				 
		if (bufferingTocGuid) {
			String tocGuidText = new String(ch, start, length);
			tocGuid.append(ch, start, length);
			if (splitTocGuidList.contains(tocGuidText.substring(0, 33))){
				foundMatch = true;					
			}
		}
		else if (bufferingText) {
			textBuffer.append(ch, start, length);
		}
		else {
			
			 if (isDocGuid){
				String documentGuidText = new String(ch, start, length);
				if (!documentInfoMap.containsKey(documentGuidText)){
					DocumentInfo documentInfo = new DocumentInfo();
					documentInfo.setSplitTitleId(titleBreakText+number);
					documentInfoMap.put(documentGuidText,documentInfo);
				}
				isDocGuid = Boolean.FALSE;
			}
			super.characters(ch, start, length);
		}
		   
	}
	
		
	
	private void writeSplitToc(TocNode node,boolean isSplit) throws SAXException {
		if(isSplit){
			super.startElement(URI, TITLE_BREAK, TITLE_BREAK, EMPTY_ATTRIBUTES);
			number++;
			String text = titleBreakText+number;
			super.characters(text.toCharArray(), 0, text.length());
			super.endElement(URI, TITLE_BREAK, TITLE_BREAK);
		}
		
		super.startElement(URI, EBOOK_TOC, EBOOK_TOC, EMPTY_ATTRIBUTES);
		super.startElement(URI, NAME, NAME, EMPTY_ATTRIBUTES);
		String nameText = currentNode.getText();
		super.characters(nameText.toCharArray(), 0, nameText.length());
		super.endElement(URI, NAME, NAME);
		
		super.startElement(URI, TOC_GUID, TOC_GUID, EMPTY_ATTRIBUTES);
		String tocDocGuidText = currentNode.getTocGuid();
		super.characters(tocDocGuidText.toCharArray(), 0, tocDocGuidText.length());
		
	}
	
	
	

    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException
    {
    	if (TOC_GUID.equals(qName)) {
			bufferingTocGuid = Boolean.FALSE;
			currentNode.setTocNodeUuid(tocGuid.toString());
			tocGuid = new StringBuilder();
			if (!foundMatch) {
				writeSplitToc(currentNode,false);
			}
			else{
				writeSplitToc(currentNode,true);
				foundMatch = Boolean.FALSE;
			}
		}
    	if (NAME.equals(qName)) {
			bufferingText = Boolean.FALSE;
			currentNode.setText(textBuffer.toString());
			textBuffer = new StringBuilder();
		}
    	else{

    		super.endElement(uri, localName, qName);
    	}
    	
    }  

    
   
}
