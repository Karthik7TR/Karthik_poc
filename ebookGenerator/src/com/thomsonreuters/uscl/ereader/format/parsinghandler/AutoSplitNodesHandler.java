package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import com.thomsonreuters.uscl.ereader.proview.TableOfContents;
import com.thomsonreuters.uscl.ereader.proview.TocEntry;
import com.thomsonreuters.uscl.ereader.proview.TocNode;

public class AutoSplitNodesHandler extends XMLFilterImpl {

	private int percentage;
	private int level = 0;
	private Map<String,String> splitTocTextMap = new HashMap<String,String>();
	int determinedPartSize;
	int margin;
	
	
	Integer splitMargin = 0;
	String splitGuid = null;
	int splitDepth = 0;
	private TocNode currentNode;
	private TocNode previousNode;
	private int currentDepth = 0;
	private int previousDepth = 0;
	private StringBuilder tocGuid = new StringBuilder();
	private StringBuilder docGuid = new StringBuilder();
	private boolean bufferingTocGuid = false;
	private boolean bufferingDocGuid = false;
	private static final String EBOOK = "EBook";
	private static final String EBOOK_TOC = "EBookToc";
	private static final String TOC_GUID = "Guid";
	private static final String DOCUMENT_GUID = "DocumentGuid";
	private static final String NAME = "Name";
	private boolean bufferingText = false;
	private StringBuilder textBuffer = new StringBuilder();
	
	String splitText = null;
	String forwardText = null;

	private int splitSize;
	private List<String> splitTocGuidList = new ArrayList<String>();
	
	public List<String> getSplitTocGuidList() {
		return splitTocGuidList;
	}

	public void setSplitTocGuidList(List<String> splitTocGuidList) {
		this.splitTocGuidList = splitTocGuidList;
	}

	public AutoSplitNodesHandler(){
		
	}
	
	public AutoSplitNodesHandler(Integer partSize, Integer thresholdPercent){
		this.determinedPartSize = partSize;
		this.percentage = thresholdPercent;
		this.previousNode = tableOfContents;
	}
	
	public int getDeterminedPartSize() {
		return determinedPartSize;
	}

	public void setDeterminedPartSize(int determinedPartSize) {
		this.determinedPartSize = determinedPartSize;
	}

	private static final Logger LOG = Logger.getLogger(AutoSplitNodesHandler.class);

	private TableOfContents tableOfContents = new TableOfContents();

	public TableOfContents getTableOfContents() {
		return tableOfContents;
	}

	public void setTableOfContents(TableOfContents tableOfContents) {
		this.tableOfContents = tableOfContents;
	}


	public void setSplitSize(int splitSize) {
		this.splitSize = splitSize;
	}



	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
			if (EBOOK.equals(qName)) {
				currentDepth = 0;
				previousDepth = 0;
			} else if (EBOOK_TOC.equals(qName)) { 
				currentDepth++;
				currentNode = new TocEntry(currentDepth);
				TocNode parentNode = determineParent();
				currentNode.setParent(parentNode);
				parentNode.addChild(currentNode);
				previousDepth = currentDepth;
				previousNode = currentNode;
			} else if (TOC_GUID.equals(qName)) {
				bufferingTocGuid = Boolean.TRUE;
			} else if (DOCUMENT_GUID.equals(qName)) {
				bufferingDocGuid = Boolean.TRUE;
			}
			else if (NAME.equals(qName)) {
				bufferingText = Boolean.TRUE;
			}
	}

	/**
	 * Buffers toc and doc guids and the corresponding text for each node
	 * encountered during the parse.
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		
			if (bufferingTocGuid) {
				tocGuid.append(ch, start, length);
			} else if (bufferingDocGuid) {
				docGuid.append(ch, start, length);
			}
			else if (bufferingText) {
				textBuffer.append(ch, start, length);
			}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		
			if (TOC_GUID.equals(qName)) {
				bufferingTocGuid = Boolean.FALSE;
				currentNode.setTocNodeUuid(tocGuid.toString());
				tocGuid = new StringBuilder();
			} else if (DOCUMENT_GUID.equals(qName)) {
				bufferingDocGuid = Boolean.FALSE;
				currentNode.setDocumentUuid(docGuid.toString());
				docGuid = new StringBuilder(); 
			} 
			else if (NAME.equals(qName)) {
				bufferingText = Boolean.FALSE;
				currentNode.setText(textBuffer.toString());
				textBuffer = new StringBuilder();
			}
			else if (EBOOK_TOC.equals(qName)) {
				currentDepth--;
			}
	}

	@Override
	public void endDocument() throws SAXException {
		getTableOfContentsforSplits();
	}

	protected void getTableOfContentsforSplits() throws SAXException {
		
			splitSize = determinedPartSize;
			margin = getMargin(splitSize);
			if (tableOfContents.getChildren().size() > 0) {
				for (TocNode child : tableOfContents.getChildren()) {
					getSplitTocNode(child);
				}
			}
	}
	
	
	
	private void getSplitTocNode(TocNode node) throws SAXException {

		//Intialize values
		if (level == 0) {			
			splitMargin = 0;
			splitGuid = null;
			splitDepth = 0;
		}
		level = level + 1;
		
		// First Child of EBOOK_TOC should not be part of the split nodes
		int firstChild = 0;
		for (TocNode child : node.getChildren()) {
			firstChild++;
			
			//Traveling from backward margin to forward margin
			if (level >= splitSize - margin && level <= splitSize +margin) {
				if ((splitDepth == 0 || splitDepth >= child.getDepth()) && firstChild != 1) {
					splitDepth = child.getDepth();
					splitGuid = child.getTocGuid();
					splitText = child.getText();
					splitMargin = level;
				}

			}

			//Decide to go backward or forward based on the depth of the node.
			if (level == splitSize + margin) {	
				//Based on the depth get the split TocGuid
				if (splitDepth != 0 ) {						
					splitGuid = StringUtils.substring(splitGuid.toString(), 0,33);
							
					splitTocTextMap.put(splitGuid,splitText);
					splitTocGuidList.add(splitGuid);					
					margin = getMargin(determinedPartSize-(splitMargin - determinedPartSize));						
					splitSize = determinedPartSize - (level - splitMargin);		
					
				} 
				level = 0;
			}

			getSplitTocNode(child);
		}
	}


	public Integer getMargin(int size) {
		Integer marginBasedOnSize = 0;
		try {
			marginBasedOnSize = (int) (size * percentage / 100);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		LOG.debug("Split size "+size+" and margin "+marginBasedOnSize);
		return marginBasedOnSize;
	}

	protected TocNode determineParent() throws SAXException {
		if (currentDepth > previousDepth) {
		// LOG.debug("Determined parent of " + currentNode + " to be " +
		// previousNode);
			return previousNode;
		} else if (currentDepth == previousDepth) {
			// LOG.debug("Determined parent of " + currentNode + " to be " +
			// previousNode.getParent());
			return previousNode.getParent();
		} else if (currentDepth < previousDepth) {
			return searchForParentInAncestryOfNode(previousNode, currentDepth - 1);
		} else {
			String message = "Could not determine parent when adding node: " + currentNode;
			LOG.error(message);
			throw new SAXException(message);
		}
	}

	/**
	 * Travels up a node's ancestry until reaching the desired depth.
	 * 
	 * @param node
	 *            the node whose ancestry to interrogate.
	 * @param desiredDepth
	 *            the depth to terminate the search. This could eventually be
	 *            changed, if needed, to be a node ID if all nodes know who
	 *            their immediate parent is (by identifier).
	 */
	protected TocNode searchForParentInAncestryOfNode(TocNode node, int desiredDepth) throws SAXException {
		if (node.getDepth() == desiredDepth) {
			// LOG.debug("Found parent in the ancestry: " + node);
			return node;
		} else if (node.getDepth() < desiredDepth) {
			String message = "Failed to identify a parent for node: "
					+ node
					+ " because of possibly bad depth assignment.  This is very likely a programming error in the TitleManifestFilter.";
			LOG.error(message);
			throw new SAXException(message); 
		} else {
			return searchForParentInAncestryOfNode(node.getParent(), desiredDepth);
		}
	}

	public Map<String,String> getSplitTocTextMap() {
		return splitTocTextMap;
	}


}
