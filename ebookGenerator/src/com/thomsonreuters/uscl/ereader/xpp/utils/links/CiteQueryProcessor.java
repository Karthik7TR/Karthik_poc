package com.thomsonreuters.uscl.ereader.xpp.utils.links;

import java.io.StringReader;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.trgr.cobalt.util.urlbuilder.CiteQuery;
import com.trgr.cobalt.util.urlbuilder.Container;
import com.trgr.cobalt.util.urlbuilder.ContainerAwareUrlBuilderFactoryBean;
import com.trgr.cobalt.util.urlbuilder.UrlBuilder;
import com.trgr.cobalt.util.urlbuilder.UrlBuilderInput;

@Component
public final class CiteQueryProcessor {
    private static final String ORIGINATION_CONTEXT = "ebook";
    private static final String HOST = "https://1.next.westlaw.com";

    private final UrlBuilder urlBuilder;
    private final CiteQuery citeQuery;

    public CiteQueryProcessor() throws Exception {
        urlBuilder = new ContainerAwareUrlBuilderFactoryBean().getObject();
        citeQuery = new CiteQuery(Container.COBALT.name());
    }

    public String getLink(final String citeQueryTag) throws Exception {
        return Optional.ofNullable(getUrlBuilderInput(citeQueryTag))
            .map(this::getUrl)
            .orElse(null);
    }

    private UrlBuilderInput getUrlBuilderInput(final String citeQueryTag) throws Exception {
        final String fixedCiteQueryTag = citeQueryTag.replace("&", "&amp;");
        return citeQuery.getCiteQueryLink(fixedCiteQueryTag, getDocumentGuid(fixedCiteQueryTag), ORIGINATION_CONTEXT);
    }

    private String getUrl(final UrlBuilderInput input) {
        return HOST +  urlBuilder.createUrl(Container.COBALT.name(), input.getUrlTemplateName(), input.getParameters());
    }

    private String getDocumentGuid(final String citeQueryTag) throws Exception {
        final Element citeElement =
            (Element) getDocumentFromString(citeQueryTag).getElementsByTagName("cite.query").item(0);
        return citeElement.getAttribute("ID");
    }

    private Document getDocumentFromString(final String xml) throws Exception {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }
}
