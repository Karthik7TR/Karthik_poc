package com.thomsonreuters.uscl.ereader.format.links;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class CiteQueryAdapterTest {
    private static final String CITE_QUERY_SOURCE_CITE = "ebook";

    private CiteQueryAdapter citeQueryAdapter;

    @Test
    public void getCiteQueryLink() {
        final String linkElement =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        final String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        final String keyText = "";
        final String sourceCite = "Immigr. Proc. Handbook � 5:42";
        final String param1 = "originationContext=document";
        final String param2 = "";
        final String param3 = "";
        final String param4 = "transitionType=DocumentItem";
        final String urlString = citeQueryAdapter
            .GetCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLink1() {
        final String linkElement =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        final String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        final String keyText = "";
        final String sourceCite = "Immigr. Proc. Handbook � 5:42";
        final String param1 = "originationContext=document";
        final String urlString =
            citeQueryAdapter.GetCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite, param1);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLink2() {
        final String linkElement =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        final String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        final String keyText = "";
        final String sourceCite = "Immigr. Proc. Handbook � 5:42";
        final String param1 = "originationContext=document";
        final String param2 = "";

        final String urlString =
            citeQueryAdapter.GetCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite, param1, param2);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLink3() {
        final String linkElement =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        final String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        final String keyText = "";
        final String sourceCite = "Immigr. Proc. Handbook � 5:42";
        final String param1 = "originationContext=document";
        final String param2 = "";
        final String param3 = "";
        final String param4 = "transitionType=DocumentItem";
        final String urlString =
            citeQueryAdapter.GetCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLinkWithNode() {
        final String linkStr =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        final DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
        Node linkElement = null;

        try {
            final DocumentBuilder builder = fact.newDocumentBuilder();
            final Document doc = builder.parse(new ByteArrayInputStream(linkStr.getBytes("UTF-8")));

            linkElement = doc.getDocumentElement();

            //linkElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(linkStr.getBytes())).getDocumentElement();
        } catch (final Exception e) {
            System.out.println(e.getMessage());
        }

        final String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        final String keyText = "";
        final String sourceCite = "Immigr. Proc. Handbook � 5:42";
        final String param1 = "originationContext=document";
        final String param2 = "";
        final String param3 = "";
        final String param4 = "transitionType=DocumentItem";
        final String urlString = citeQueryAdapter
            .GetCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite, param1, param2, param3, param4);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLinkWithoutParameters() {
        final String linkElement =
            "<cite.query w-ref-type=\"DA\" w-normalized-cite=\"IMPHDMYREFCH5\" w-pub-number=\"135162\" ID=\"Ic6a6af900bbe11e1b1520000837bc6dd\">References</cite.query>";
        final String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        final String keyText = "";
        final String sourceCite = "Immigr. Proc. Handbook � 5:42";
        final String urlString = citeQueryAdapter.GetCiteQueryLink(linkElement, originatingDoc, keyText, sourceCite);

        Assert.isTrue(
            urlString.contains("originatingDoc=Iff5a5a9b7c8f11da9de6e47d6d5aa7a5"),
            "file content should have contained a hyperlink to WLN, but did not!");
    }

    @Test
    public void getCiteQueryLinkEmpty() {
        final String linkElement = "<cite.query w-seq-number=\"00062\" id=\"I0e1c8f2519c011ebbea6fcce6570c4b3\" w-src-number=\"0287018091\" w-pub-number=\"0000711\" w-serial-number=\"1969134013\" w-ref-type=\"RS\">Smith</cite.query>";
        final String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        final String urlString = citeQueryAdapter.GetCiteQueryLink(linkElement, originatingDoc, StringUtils.EMPTY, CITE_QUERY_SOURCE_CITE);

        Assert.isTrue(StringUtils.isEmpty(urlString));
    }

    @Test
    public void getCiteQueryLinkEmpty2() {
        final String linkElement = "<cite.query w-seq-number=\"00007\" id=\"I12f6c6f119c011ebbea6fcce6570c4b3\" w-src-number=\"0287018096\" w-pub-number=\"0000359\" w-serial-number=\"2030644789\" w-ref-type=\"RT\">infra</cite.query>";
        final String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        final String urlString = citeQueryAdapter.GetCiteQueryLink(linkElement, originatingDoc, StringUtils.EMPTY, CITE_QUERY_SOURCE_CITE);

        Assert.isTrue(StringUtils.isEmpty(urlString));
    }

    @Test
    public void getCiteQueryLinkInternal() {
        final String linkElement = "<cite.query w-normalized-cite=\"GAEVIDENCES8:8\" id=\"Ideebe97119c011eb9186f10f857b4088\" w-pub-number=\"160247\" w-ref-type=\"DA\">8:8</cite.query>";
        final String originatingDoc = "Iff5a5a9b7c8f11da9de6e47d6d5aa7a5";
        final String urlString = citeQueryAdapter.GetCiteQueryLink(linkElement, originatingDoc, StringUtils.EMPTY, CITE_QUERY_SOURCE_CITE);

        Assert.isTrue(StringUtils.isNotEmpty(urlString));
    }

    @Before
    public void setUp() throws Exception {
        citeQueryAdapter = new CiteQueryAdapter();
    }
}
