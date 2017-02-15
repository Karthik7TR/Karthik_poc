package com.thomsonreuters.uscl.ereader.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class XMLXpathEvaluatorTest
{
    @Before
    public void setUp()
    {
        //Intentionally left blank
    }

    @After
    public void tearDown()
    {
        //Intentionally left blank
    }

    @Test
    public void testExtractWithDom() throws ParserConfigurationException, SAXException, IOException
    {
        final String xmlstring = "<parent><child attr='attr1'><prop>propvalue</prop></child></parent>";
        final ByteArrayInputStream xml = new ByteArrayInputStream(xmlstring.getBytes());
        final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document dom = builder.parse(xml);

        final XMLXpathEvaluator extractor = new XMLXpathEvaluator(dom);
        Assert.assertEquals("propvalue", extractor.evaluate("/parent/child/prop"));
        Assert.assertEquals("attr1", extractor.evaluate("/parent/child/@attr"));
    }

    @Test
    public void testExtract() throws ParserConfigurationException, SAXException, IOException
    {
        final String xmlstring = "<parent><child attr='attr1'><prop>propvalue</prop></child></parent>";
        final XMLXpathEvaluator extractor = new XMLXpathEvaluator(xmlstring);
        Assert.assertEquals("propvalue", extractor.evaluate("/parent/child/prop"));
        Assert.assertEquals("attr1", extractor.evaluate("/parent/child/@attr"));
    }

    @Test
    public void testExtractMoreCases() throws Exception
    {
        final String xmlstring = "<parent><child attr='attr1'><prop>propvalue</prop></child></parent>";

        final XMLXpathEvaluator extractor = new XMLXpathEvaluator(xmlstring);
        Assert.assertEquals(null, extractor.evaluate("/parent/abc/prop"));
        Assert.assertEquals(null, extractor.evaluate("/parent/child/@abc"));

        final List<String> list = extractor.evaluateList("/parent/child");
        Assert.assertEquals(1, list.size());

        final Node node = extractor.evaluateNode("/parent/child");
        Assert.assertNotNull(node);
    }

    @Test
    public void testExtractBadXpath() throws Exception
    {
        final String xmlstring = "<parent><child attr='attr1'><prop>propvalue</prop></child></parent>";

        final XMLXpathEvaluator extractor = new XMLXpathEvaluator(xmlstring);
        Assert.assertEquals(null, extractor.evaluate("/parent/abc=123/prop"));

        final List<String> list = extractor.evaluateList("/parent/child=123");
        Assert.assertEquals(0, list.size());

        final Node node = extractor.evaluateNode("/parent/child=123");
        Assert.assertEquals(null, node);
    }

    @Test
    public void testExtractToString() throws Exception
    {
        final String xmlstring = "<parent><child attr='attr1'><prop>propvalue</prop></child></parent>";

        final XMLXpathEvaluator extractor = new XMLXpathEvaluator(xmlstring);

        final String expected =
            "<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n<parent><child attr=\"attr1\"><prop>propvalue</prop></child></parent>";
        Assert.assertEquals(expected, extractor.toXml());
    }

    @Test
    public void testExtractNodeList() throws ParserConfigurationException, SAXException, IOException
    {
        final String xmlstring = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<group id=\"uscl/grouptest\" status=\"Review\"><name>Group Test</name><type>standard</type><headtitle>uscl/an/book_lohisplitnodeinfo/v1</headtitle>"
            + "<members><subgroup heading=\"2014\"><title>uscl/an/book_lohisplitnodeinfo/v1</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v1</title></subgroup>"
            + "<subgroup heading=\"2015\"><title>uscl/an/book_lohisplitnodeinfo/v2</title><title>uscl/an/book_lohisplitnodeinfo_pt2/v2</title></subgroup></members></group>";

        final XMLXpathEvaluator extractor = new XMLXpathEvaluator(xmlstring);
        final NodeList subGroups = extractor.evaluateNodeList("group/members/subgroup");
        Assert.assertEquals(2, subGroups.getLength());
    }
}
