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
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;

/**
 * Extract NORT nodes from NORT xml file generated from Codes Workbench
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class NovusNortFileParser extends DefaultHandler {
	private static final Logger LOG = Logger.getLogger(NovusNortFileParser.class);
	
	private static final String RELATIONSHIP = "n-relationship"; 
	private static final String RELBASE = "n-relbase"; 
	private static final String RELTARGET = "n-reltarget"; 
	private static final String START_DATE = "n-start-date"; 
	private static final String END_DATE = "n-end-date"; 
	private static final String DOC_GUID = "n-doc-guid"; 
	private static final String RANK = "n-rank"; 
	private static final String LABEL = "n-label"; 
	private static final String NODE_TYPE = "node-type"; 
	private static final String GRAFT_POINT_FLAG = "graft-point-flag";
	private static final String N_VIEW = "n-view";
	private Date cutoffDate;
	
	private HashMap<String, RelationshipNode> nortNodeMap = new HashMap<String, RelationshipNode>();
	
	private RelationshipNode root;
	private RelationshipNode currentNode;
	private StringBuffer tempVal = null;
	
	public NovusNortFileParser(Date cutoffDate) {
		super();
		this.cutoffDate = cutoffDate;
	}
	
	public RelationshipNode parseDocument(File nortFile) throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
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
		
		if(root != null) {
			createParentChildRelationships();
		} else {
			LOG.debug("No root node found for file " + nortFile.getAbsolutePath());
			throw new SAXException("No root node found for file " + nortFile.getAbsolutePath());
		}
		
		return root;
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
        if (qName.equalsIgnoreCase(RELATIONSHIP)) {
        	currentNode = new RelationshipNode();
        }
        tempVal = new StringBuffer();
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

    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException {
    	if (qName.equalsIgnoreCase(RELATIONSHIP)) {
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
		    		nortNodeMap.put(currentNode.getNortGuid(), currentNode);
		    		if (currentNode.isRootNode()) {
		        		root = currentNode;
		    		} 
	    		} else {
	    			LOG.debug("Novus NORT GUID " + currentNode.getNortGuid() + " not included because it has been deleted.");
	    		}
    			
    		}
        } 
    	if(tempVal.length() != 0) {
    		String value = tempVal.toString();
	    	if (qName.equalsIgnoreCase(RELBASE)) {
	    		LOG.debug("Parsing Novus NORT GUID " + value);
	        	currentNode.setNortGuid(value);
	        } else if (qName.equalsIgnoreCase(RELTARGET)) {
	        	currentNode.setParentNortGuid(value);
	        } else if (qName.equalsIgnoreCase(START_DATE)) {
		        currentNode.setStartDateStr(value);
	        } else if (qName.equalsIgnoreCase(END_DATE)) {
		        currentNode.setEndDateStr(value);
	        } else if (qName.equalsIgnoreCase(DOC_GUID)) {
	        	currentNode.setDocumentGuid(value);
	        } else if (qName.equalsIgnoreCase(RANK)) {
	        	double rank = Double.valueOf(value);
	        	currentNode.setRank(rank);
	        } else if (qName.equalsIgnoreCase(LABEL)) {
	        	currentNode.setLabel(value);
	        } else if (qName.equalsIgnoreCase(NODE_TYPE)) {
	        	currentNode.setNodeType(value);
	        } else if (qName.equalsIgnoreCase(GRAFT_POINT_FLAG)) {
	        	boolean isRootNode = false;
	        	if("Y".equalsIgnoreCase(value)) {
	        		isRootNode = true;
	        	}
	        	currentNode.setRootNode(isRootNode);
	        } else if (qName.equalsIgnoreCase(N_VIEW)) {
	        	currentNode.getViews().add(value);
	        }
    	}
    }
}
