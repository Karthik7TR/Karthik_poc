/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.thomsonreuters.uscl.ereader.format.domain.XSLTMapperEntity;

/**
 * Filter that extracts content type for XSL transformation
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XSLMapperParser extends DefaultHandler {
	private static final Logger LOG = Logger.getLogger(XSLMapperParser.class);
	
	private static final String CONTENT_TYPE = "ContentType";
	private static final String COLLECTION_GROUP = "CollectionGroup";
	private static final String STYLE_SHEET = "Stylesheet";
	private static final String CONTENT_TYPE_NAME = "ContentTypeName";
	private static final String COLLECTION_NAME = "CollectionName";
	private static final String ATTRIBUTE = "Attribute";
	private static final String X_PATH_VALUE = "XPathValue";
	
	private HashMap<String,String> contentTypes = new HashMap<String, String>();
	
	private XSLTMapperEntity xsltMapperEntity;
	
	//keyed map used to search for the corresponding XSLTMapperEntity.
	private Map<String, String> xsltFileNameByCollectionName = new HashMap<String, String>();
	
	public XSLMapperParser() {
		super();
	}
	
	public Map<String, String> parseDocument(File mapperFile) throws Exception {

		InputStream inputStream = new FileInputStream(mapperFile);
		// get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			// get a new instance of parser
			SAXParser sp = spf.newSAXParser();

			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			sp.parse(is, this);
			// printData();
		} finally {
			inputStream.close();
		}
		return xsltFileNameByCollectionName;
	}
	
	public Map<String, String> getXSLTMapperEntityByCollectionName() {
		return xsltFileNameByCollectionName;
	}

	@Override
    public void startElement(
        final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException
    {
        if (qName.equalsIgnoreCase(CONTENT_TYPE))
        {
        	String contentTypeName = atts.getValue(CONTENT_TYPE_NAME);
        	String styleSheet = atts.getValue(STYLE_SHEET);
        	
        	// Key is ContentTypeName in ContentType element from XML file
        	contentTypes.put(contentTypeName, styleSheet);
        }
        else if (qName.equalsIgnoreCase(COLLECTION_GROUP))
        {
        	String collectionName = atts.getValue(COLLECTION_NAME);
        	String contentTypeName = atts.getValue(CONTENT_TYPE_NAME);
        	
        	xsltMapperEntity = new XSLTMapperEntity();
        	xsltMapperEntity.setCollection(collectionName);
        	xsltMapperEntity.setCONTENT_TYPE(contentTypeName);
        	xsltMapperEntity.setXSLT(contentTypes.get(contentTypeName));
        	
        }
        else if (qName.equalsIgnoreCase(ATTRIBUTE))
        {
        	if (xsltMapperEntity != null) {
        		xsltMapperEntity.setDOC_TYPE(atts.getValue(X_PATH_VALUE));
        	}
        }
        
        super.startElement(uri, localName, qName, atts);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
        throws SAXException
    {
    	
    	if (qName.equalsIgnoreCase(COLLECTION_GROUP))
        {
    		String collectionName = xsltMapperEntity.getCollection();
    		String docType = xsltMapperEntity.getDOC_TYPE();
    		String xslFileName = xsltMapperEntity.getXSLT();
    		
    		if (StringUtils.isNotBlank(docType))
			{
				xsltFileNameByCollectionName.put(collectionName + " " + docType, xslFileName);
			}
			else
			{
				xsltFileNameByCollectionName.put(collectionName, xslFileName);
			}
    		
    		//LOG.debug(collectionName + ";" + docType + ";"+ xsltMapperEntity.getCONTENT_TYPE() + ";"+ xslFileName);
    		
        	xsltMapperEntity = null;
        }
    	
        super.endElement(uri, localName, qName);
    }
}