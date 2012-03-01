/*
 * Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.format.links;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;


public class CiteQueryAdapterTest
{
    CiteQueryAdapter citeQueryAdapter;

    @Test
    public void getCiteQueryLink()
    {
        String linkElement =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        String keyText = "";
        String sourceCite = "Immigr. Proc. Handbook § 5:42";
        String param1 = "originationContext=document";
        String param2 = "";
        String param3 = "";
        String param4 = "transitionType=DocumentItem";
        String urlString =
            citeQueryAdapter.GetCiteQueryLink(
                linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLink1()
    {
        String linkElement =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        String keyText = "";
        String sourceCite = "Immigr. Proc. Handbook § 5:42";
        String param1 = "originationContext=document";
        String urlString =
            citeQueryAdapter.GetCiteQueryLink(
                linkElement, originatingDoc, keyText, sourceCite, param1);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLink2()
    {
        String linkElement =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        String keyText = "";
        String sourceCite = "Immigr. Proc. Handbook § 5:42";
        String param1 = "originationContext=document";
        String param2 = "";

        String urlString =
            citeQueryAdapter.GetCiteQueryLink(
                linkElement, originatingDoc, keyText, sourceCite, param1, param2);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLink3()
    {
        String linkElement =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        String keyText = "";
        String sourceCite = "Immigr. Proc. Handbook § 5:42";
        String param1 = "originationContext=document";
        String param2 = "";
        String param3 = "";
        String param4 = "transitionType=DocumentItem";
        String urlString =
            citeQueryAdapter.GetCiteQueryLink(
                linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLinkWithNode()
    {
        String linkStr =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        Node linkElement = null;

        try
        {
            DocumentBuilder builder = fact.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(linkStr.getBytes("UTF-8")));

            linkElement = doc.getDocumentElement();

            //linkElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(linkStr.getBytes())).getDocumentElement();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        String keyText = "";
        String sourceCite = "Immigr. Proc. Handbook § 5:42";
        String param1 = "originationContext=document";
        String param2 = "";
        String param3 = "";
        String param4 = "transitionType=DocumentItem";
        String urlString =
            citeQueryAdapter.GetCiteQueryLink(
                linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLinkWithoutParameters()
    {
        String linkElement =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        String keyText = "";
        String sourceCite = "Immigr. Proc. Handbook § 5:42";
        String urlString =
            citeQueryAdapter.GetCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Before
    public void setUp() throws Exception
    {
        citeQueryAdapter = new CiteQueryAdapter();
    }
}
