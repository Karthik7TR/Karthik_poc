/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Wraps a stream and provides xpath expression capability.
 * 
 * Note that the entity resolver is disabled during parsing
 */
public class XMLXpathEvaluator {
    private static final Logger logger = LogManager.getLogger(XMLXpathEvaluator.class);
    private XPath xpath;
    private Document domDocument;
    
    /**
     * A DOM is provided
     * 
     * @param doc
     */
    public XMLXpathEvaluator(Document doc) {
        this.domDocument = doc;
        
        XPathFactory xpf = XPathFactory.newInstance();
        xpath = xpf.newXPath();
    }

    /**
     * @param xml
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public XMLXpathEvaluator(InputStream xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                return new InputSource(new StringReader(""));
            }
        });
        
        domDocument = db.parse(xml);
        
        // create XPath
        XPathFactory xpf = XPathFactory.newInstance();
        xpath = xpf.newXPath();
    }
    
    public XMLXpathEvaluator(String xml) throws ParserConfigurationException, SAXException, IOException {
        // literally every single client of this class was parsing a String, so I moved the
        // InputStream conversion here.
        this(IOUtils.toInputStream(xml, "UTF-8"));
    }

    /**
     * @param xpathExpression an expression that contains a single entry.  For multiple entries, the first
     *   one is always returned
     * @return
     */
    public String evaluate(String xpathExpression) {
        String data = null;
        try {
            data = (String) xpath.evaluate(xpathExpression, domDocument, XPathConstants.STRING);
            if (data == "") {
                data = null;
            }
        } catch (XPathExpressionException e) {
            logger.warn(xpathExpression + " not processed properly, returning null.");
        }
        return data;
    }

    /**
     * Resolves to a multiple string values
     * 
     * @param xpathExpression an expression that usually can contain multiple entries
     * @return list of Strings, or empty list if none found.
     */
    public List<String> evaluateList(String xpathExpression) {
        List<String> data = new ArrayList<String>();
        try {
            NodeList nodelist = (NodeList) xpath.evaluate(xpathExpression, domDocument, XPathConstants.NODESET);
            if (nodelist.getLength() > 0) {
                for (int i = 0; i < nodelist.getLength(); i++) {
                    Node node = nodelist.item(i);
                    String eval = xpath.evaluate(".", node);
                    if (eval != "") {
                        data.add(eval);
                    }
                }
            }
        } catch (XPathExpressionException e) {
            logger.warn(xpathExpression + " not processed properly, returning nothing.");
        }
        return data;
    }
    
    
    /**
     * Resolves to the Node for a given expression
     * 
     * @param xpathExpression an expression that usually can contain single entry
     * @return
     */
    public Node evaluateNode(String xpathExpression) {
        Node node = null;
        try {
            node = (Node) xpath.evaluate(xpathExpression, domDocument, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            logger.warn(xpathExpression + " not processed properly, returning null.");
        }
        return node;
    }
    
    /**
     * Resolves to the Node for a given expression
     * 
     * @param xpathExpression an expression that usually can contain multiple entries
     * @return
     */
    public NodeList evaluateNodeList(String xpathExpression) {
        NodeList nodeList = null;
        try {
            nodeList = (NodeList) xpath.evaluate(xpathExpression, domDocument, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.warn(xpathExpression + " not processed properly, returning null.");
        }
        return nodeList;
    }
    
    public String toXml() throws Exception {
        try {
            DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            LSSerializer writer = impl.createLSSerializer();
            return writer.writeToString(domDocument);
        } catch (Exception e) {
            throw new Exception("Error XML from DOM.", e);
        }
    }

    public Document getDomDocument() {
        return domDocument;
    }

    public void setDomDocument(Document domDocument) {
        this.domDocument = domDocument;
    }
}
