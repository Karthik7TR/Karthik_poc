/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.XpathStack;

/**
 * Extract NORT nodes from NORT xml file generated from Codes Workbench
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class NovusNortFileParser extends DefaultHandler {
	private static final Logger LOG = Logger.getLogger(NovusNortFileParser.class);

	private static final String N_LOAD = "/n-load";
	private static final String RELATIONSHIP = N_LOAD + "/n-relationship"; 
	private static final String RELBASE = RELATIONSHIP + "/n-relbase"; 
	private static final String RELTARGET = RELATIONSHIP + "/n-reltarget"; 
	private static final String NORT_PAYLOAD = RELATIONSHIP + "/n-relpayload/n-nortpayload";
	
	private static final String START_DATE = NORT_PAYLOAD + "/n-start-date"; 
	private static final String END_DATE = NORT_PAYLOAD + "/n-end-date"; 
	private static final String PUB_TAGGED_HEADING = NORT_PAYLOAD + "/pub-tagged-heading"; 
	private static final String DOC_GUID = NORT_PAYLOAD + "/n-doc-guid"; 
	private static final String RANK = NORT_PAYLOAD + "/n-rank"; 
	private static final String LABEL = NORT_PAYLOAD + "/n-label/heading";
	private static final String NODE_TYPE = NORT_PAYLOAD + "/node-type"; 
	private static final String GRAFT_POINT_FLAG = NORT_PAYLOAD + "/graft-point-flag";
	private static final String N_VIEW = NORT_PAYLOAD + "/n-view";
	private Date cutoffDate;
	private XpathStack xpathStack = null;
	private final int nortFileLevel;
	
	private HashMap<String, RelationshipNode> nortNodeMap = new HashMap<>();
	private HashMap<String, HashMap<Integer, String>> documentLevelMap = null;
	
	private List<RelationshipNode> roots = new ArrayList<RelationshipNode>();
	private List<RelationshipNode> duplicateNodes = new ArrayList<RelationshipNode>();
	private RelationshipNode currentNode;
	private StringBuffer tempVal = null;
	
	public NovusNortFileParser(Date cutoffDate, int nortFileLevel, HashMap<String, HashMap<Integer, String>> documentMap) {
		super();
		this.cutoffDate = cutoffDate;
		this.nortFileLevel = nortFileLevel;
		this.documentLevelMap = documentMap;
		xpathStack = new XpathStack();
	}

	public List<RelationshipNode> parseDocument(File nortFile) throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		
		try (InputStream inputStream = new FileInputStream(nortFile);
				Reader reader = new InputStreamReader(inputStream, "UTF-8")){
			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			sp.parse(is, this);
		}
		
		// Throw error if duplicate nodes which were not marked as deleted are found.
		if(duplicateNodes.size() > 0) {
			StringBuffer buffer = new StringBuffer();
			for(RelationshipNode duplicateNode: duplicateNodes) {
				buffer.append(duplicateNode.getNortGuid());
				buffer.append(", ");
			}
			LOG.error("Duplicate NORT node(s) found: " + buffer.toString() + " in file " + nortFile.getAbsolutePath());
			throw new SAXException("Duplicate NORT node(s) found: " + buffer.toString() + " in file " + nortFile.getAbsolutePath());
		}
		
		if(roots.size() > 0) {
			createParentChildRelationships();
		} else {
			LOG.error("No root node(s) found in file " + nortFile.getAbsolutePath());
			throw new SAXException("No root node(s) found in file " + nortFile.getAbsolutePath());
		}
		
		// Sort root nodes based on node rank
		List<RelationshipNode> rootList = new ArrayList<RelationshipNode>(roots);
		Collections.sort(rootList);
		return rootList;
	}
	
	private void createParentChildRelationships() {
		for(RelationshipNode node : nortNodeMap.values()) {
			RelationshipNode parentNode = nortNodeMap.get(node.getParentNortGuid());
			if(parentNode != null) {
				parentNode.getChildNodes().add(node);
				node.setParentNode(parentNode);
			}
		}
	}
	

	@Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
		xpathStack.push(qName);
		String currentXpath = xpathStack.toXPathString();
        if (currentXpath.equalsIgnoreCase(RELATIONSHIP)) {
        	currentNode = new RelationshipNode();
        } 
        if(inExtractXpath(currentXpath)) {
            tempVal = new StringBuffer();
        }
    }
	
	@Override
	public void characters(char buf[], int offset, int len) throws SAXException
	{
		if(tempVal != null) {
			for(int i=offset; i<offset+len; i++) {
				tempVal.append(buf[i]);
			}
		}
	}
	
	private boolean inExtractXpath(String xpath) {
		boolean extractPath = false;
		switch(xpath) {
			case RELBASE:
			case RELTARGET:
			case START_DATE:
			case END_DATE:
			case DOC_GUID:
			case RANK:
			case LABEL:
			case NODE_TYPE:
			case GRAFT_POINT_FLAG:
			case N_VIEW:
			case PUB_TAGGED_HEADING:
				extractPath = true;
				break;
			default:
				extractPath = false;
		}
		return extractPath;
	}

    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException {
    	String currentXpath = xpathStack.toXPathString();
    	if (currentXpath.equalsIgnoreCase(RELATIONSHIP)) {
    		addCurrentNodeToMap();
        } 
    	
    	if(inExtractXpath(currentXpath)) {
    		String value = tempVal.toString();
	    	if (currentXpath.equalsIgnoreCase(RELBASE)) {
	    		LOG.debug("Parsing Novus NORT GUID " + value);
	        	currentNode.setNortGuid(value);
	        } else if (currentXpath.equalsIgnoreCase(RELTARGET)) {
	        	currentNode.setParentNortGuid(value);
	        } else if (currentXpath.equalsIgnoreCase(START_DATE)) {
		        currentNode.setStartDateStr(value);
	        } else if (currentXpath.equalsIgnoreCase(END_DATE)) {
		        currentNode.setEndDateStr(value);
	        } else if (currentXpath.equalsIgnoreCase(PUB_TAGGED_HEADING)) {
	        	if(StringUtils.isNotBlank(value)) {
	        		boolean pubTaggedHeadingExists = (value.equalsIgnoreCase("Y") ? true : false);
		        	currentNode.setPubTaggedHeadingExists(pubTaggedHeadingExists);
	        	}
	        } else if (currentXpath.equalsIgnoreCase(DOC_GUID)) {
	        	String docGuid = value;
	        	if(documentLevelMap.containsKey(docGuid)) {
	        		HashMap<Integer, String> nortLevelMap = documentLevelMap.get(docGuid);
	        		
	        		if(nortLevelMap.containsKey(nortFileLevel)) {
	        			// duplicate docGuid found and also exists in NORT Level map
	        			// use DOC GUID from NORT Level map
	        			docGuid = nortLevelMap.get(nortFileLevel);
	        		} else {
	        			// duplicate docGuid found but DOC GUID does not exist in this NORT Level
	        			// generate new docGuid to fix bug: CA Dwyer duplicate doc conflict.  Multiple extracts from
	        			// same content set produces same documents with different prelims.  This is a special case.
	        			// Duplicate documents within same content set can reuse the same document.
	        			docGuid = docGuid + "-" + nortFileLevel;
	        			nortLevelMap.put(nortFileLevel, docGuid);
	        			documentLevelMap.put(docGuid, nortLevelMap);
	        		}
	        	} else {
	        		// First time seeing the document
	        		HashMap<Integer, String> nortLevelMap = new HashMap<>();
	        		nortLevelMap.put(nortFileLevel,docGuid);
	        		documentLevelMap.put(docGuid, nortLevelMap);
	        	}
	        	currentNode.setDocumentGuid(docGuid);
	        } else if (currentXpath.equalsIgnoreCase(RANK)) {
	        	double rank = Double.valueOf(value);
	        	currentNode.setRank(rank);
	        } else if (currentXpath.equalsIgnoreCase(LABEL)) {
	        	currentNode.setLabel(value);
	        } else if (currentXpath.equalsIgnoreCase(NODE_TYPE)) {
	        	currentNode.setNodeType(value);
	        } else if (currentXpath.equalsIgnoreCase(GRAFT_POINT_FLAG)) {
	        	boolean isRootNode = false;
	        	if("Y".equalsIgnoreCase(value)) {
	        		isRootNode = true;
	        	}
	        	currentNode.setRootNode(isRootNode);
	        } else if (currentXpath.equalsIgnoreCase(N_VIEW)) {
	        	currentNode.getViews().add(value);
	        }
	    	tempVal = null;
    	}
    	
    	xpathStack.pop();
    }
    
    private void addCurrentNodeToMap() throws SAXException {
    	String endDateStr = currentNode.getEndDateStr();
		DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date endDate = null;
		try {
			endDate = formatter.parse(endDateStr);
		} catch (ParseException e) {
			LOG.debug("End date format error: " + endDateStr + " Expect end date format in yyyyMMddHHmmss.");
			throw new SAXException("End date format error: " + endDateStr + " Expect end date format in yyyyMMddHHmmss.");
		} catch (NullPointerException e) {
			LOG.debug("No end date was found for NORT GUID " + currentNode.getNortGuid());
			throw new SAXException("No end date was found for NORT GUID " + currentNode.getNortGuid());
		}
		
		// Only add nodes if they have not expired yet.
		if (endDate != null && endDate.after(cutoffDate)) {
			if(!currentNode.isDeletedNode()) {
				if(nortNodeMap.containsKey(currentNode.getNortGuid())) {
					duplicateNodes.add(currentNode);
				} else {
					nortNodeMap.put(currentNode.getNortGuid(), currentNode);
					// Save root nodes to return after parsing
					if (currentNode.isRootNode()) {
		    			roots.add(currentNode);
		    		} 
				}
    		} else {
    			LOG.debug("Novus NORT GUID " + currentNode.getNortGuid() + " not included because it has been deleted.");
    		}
		}
    }
}
