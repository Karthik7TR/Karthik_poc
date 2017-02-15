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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
public class XMLXpathEvaluator
{
    private static final Logger logger = LogManager.getLogger(XMLXpathEvaluator.class);
    private XPath xpath;
    private Document domDocument;

    /**
     * A DOM is provided
     *
     * @param doc
     */
    public XMLXpathEvaluator(final Document doc)
    {
        domDocument = doc;

        final XPathFactory xpf = XPathFactory.newInstance();
        xpath = xpf.newXPath();
    }

    /**
     * @param xml
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public XMLXpathEvaluator(final InputStream xml)
        throws ParserConfigurationException, SAXException, IOException
    {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        final DocumentBuilder db = dbf.newDocumentBuilder();
        db.setEntityResolver(new EntityResolver()
        {
            @Override
            public InputSource resolveEntity(final String publicId, final String systemId) throws SAXException, IOException
            {
                return new InputSource(new StringReader(""));
            }
        });

        domDocument = db.parse(xml);

        // create XPath
        final XPathFactory xpf = XPathFactory.newInstance();
        xpath = xpf.newXPath();
    }

    public XMLXpathEvaluator(final String xml)
        throws ParserConfigurationException, SAXException, IOException
    {
        // literally every single client of this class was parsing a String, so I moved the
        // InputStream conversion here.
        this(IOUtils.toInputStream(xml, "UTF-8"));
    }

    /**
     * @param xpathExpression an expression that contains a single entry.  For multiple entries, the first
     *   one is always returned
     * @return
     */
    public String evaluate(final String xpathExpression)
    {
        String data = null;
        try
        {
            data = (String) xpath.evaluate(xpathExpression, domDocument, XPathConstants.STRING);
            if ("".equals(data))
            {
                data = null;
            }
        }
        catch (final XPathExpressionException e)
        {
            logger.warn(xpathExpression + " not processed properly, returning null.", e);
        }
        return data;
    }

    /**
     * Resolves to a multiple string values
     *
     * @param xpathExpression an expression that usually can contain multiple entries
     * @return list of Strings, or empty list if none found.
     */
    public List<String> evaluateList(final String xpathExpression)
    {
        final List<String> data = new ArrayList<>();
        try
        {
            final NodeList nodelist = (NodeList) xpath.evaluate(xpathExpression, domDocument, XPathConstants.NODESET);
            if (nodelist.getLength() > 0)
            {
                for (int i = 0; i < nodelist.getLength(); i++)
                {
                    final Node node = nodelist.item(i);
                    final String eval = xpath.evaluate(".", node);
                    if (!"".equals(eval))
                    {
                        data.add(eval);
                    }
                }
            }
        }
        catch (final XPathExpressionException e)
        {
            logger.warn(xpathExpression + " not processed properly, returning nothing.", e);
        }
        return data;
    }

    /**
     * Resolves to the Node for a given expression
     *
     * @param xpathExpression an expression that usually can contain single entry
     * @return
     */
    public Node evaluateNode(final String xpathExpression)
    {
        Node node = null;
        try
        {
            node = (Node) xpath.evaluate(xpathExpression, domDocument, XPathConstants.NODE);
        }
        catch (final XPathExpressionException e)
        {
            logger.warn(xpathExpression + " not processed properly, returning null.", e);
        }
        return node;
    }

    /**
     * Resolves to the Node for a given expression
     *
     * @param xpathExpression an expression that usually can contain multiple entries
     * @return
     */
    public NodeList evaluateNodeList(final String xpathExpression)
    {
        NodeList nodeList = null;
        try
        {
            nodeList = (NodeList) xpath.evaluate(xpathExpression, domDocument, XPathConstants.NODESET);
        }
        catch (final XPathExpressionException e)
        {
            logger.warn(xpathExpression + " not processed properly, returning null.", e);
        }
        return nodeList;
    }

    public String toXml() throws Exception
    {
        try
        {
            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();
            return writer.writeToString(domDocument);
        }
        catch (final Exception e)
        {
            throw new Exception("Error XML from DOM.", e);
        }
    }

    public Document getDomDocument()
    {
        return domDocument;
    }

    public void setDomDocument(final Document domDocument)
    {
        this.domDocument = domDocument;
    }
}
