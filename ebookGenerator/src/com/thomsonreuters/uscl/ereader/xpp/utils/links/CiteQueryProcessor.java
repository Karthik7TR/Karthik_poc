package com.thomsonreuters.uscl.ereader.xpp.utils.links;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.trgr.cobalt.util.urlbuilder.CiteQuery;
import com.trgr.cobalt.util.urlbuilder.Container;
import com.trgr.cobalt.util.urlbuilder.ContainerAwareUrlBuilderFactoryBean;
import com.trgr.cobalt.util.urlbuilder.Parameter;
import com.trgr.cobalt.util.urlbuilder.UrlBuilder;
import com.trgr.cobalt.util.urlbuilder.UrlBuilderInput;

public final class CiteQueryProcessor
{
    private static final String ORIGINATION_CONTEXT = "ebook";
    private static final String HOST = "https://1.next.westlaw.com";

    public static String getLink(final String citeQueryTag) throws Exception
    {
        final CiteQuery citeQueryObj = new CiteQuery(Container.COBALT.name());
        final UrlBuilderInput urlInput =
            citeQueryObj.getCiteQueryLink(citeQueryTag, getDocumentGuid(citeQueryTag), ORIGINATION_CONTEXT);
        final UrlBuilder urlBuilder = new ContainerAwareUrlBuilderFactoryBean().getObject();

        if (urlInput == null)
        {
            throw new Exception("Unable to process cite query");
        }

        final List<Parameter> params = urlInput.getParameters();
        return HOST + urlBuilder.createUrl(Container.COBALT.name(), urlInput.getUrlTemplateName(), params);
    }

    private static String getDocumentGuid(final String citeQueryTag) throws Exception
    {
        final Element citeElement =
            (Element) getDocumentFromString(citeQueryTag).getElementsByTagName("cite.query").item(0);
        return citeElement.getAttribute("ID");
    }

    private static Document getDocumentFromString(final String xml) throws Exception
    {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    private CiteQueryProcessor()
    {
        //intentionally left empty
    }
}
