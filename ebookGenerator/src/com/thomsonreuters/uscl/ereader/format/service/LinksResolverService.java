package com.thomsonreuters.uscl.ereader.format.service;

import com.thomsonreuters.uscl.ereader.common.filesystem.GatherFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import java.io.File;
import java.util.Collection;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
public class LinksResolverService {
    private static final String HREF = "href";
    private static final String CLASS = "class";
    private static final String CDATA = "CDATA";
    @Autowired
    private CiteQueryService citeQueryService;
    @Autowired
    private DocMetadataService docMetadataService;
    @Autowired
    private InternalLinkResolverService internalLinkResolverService;
    @Autowired
    private GatherFileSystem gatherFileSystem;

    public void transformCiteQueries(final Element section, final String docGuid,
                                     final BookStep step) {
        Collection<Element> links = resolveCiteQueryLinks(section, docGuid);
        resolveInternalLinks(docGuid, links, step);
    }

    private Collection<Element> resolveCiteQueryLinks(final Element footnotesSectionToAppend, final String fileUuid) {
        return citeQueryService.transformCiteQueries(footnotesSectionToAppend, fileUuid);
    }

    private void resolveInternalLinks( final String docGuid, final Collection<Element> links, final BookStep step) {
        final Long jobId = step.getJobInstanceId();
        final File docsGuidFile = gatherFileSystem.getGatherDocGuidsFile(step);
        final String version = step.getBookVersionString();
        final DocumentMetadataAuthority documentMetadataAuthority = docMetadataService.findAllDocMetadataForTitleByJobId(jobId);

        links.forEach(link -> resolveInternalLink(link, documentMetadataAuthority, docsGuidFile, docGuid, version));
    }

    private void resolveInternalLink(final Element link,
                                     final DocumentMetadataAuthority documentMetadataAuthority,
                                     final File docsGuidFile,
                                     final String docGuid,
                                     final String version) {
        String resourceUrl = link.attr(HREF);
        Attributes attributes = convertJsoupToSaxAttributes(link);

        try {
            Attributes resolvedAttributes = internalLinkResolverService.resolveResourceUrlReference(
                            documentMetadataAuthority,
                            resourceUrl,
                            attributes,
                            docsGuidFile,
                            docGuid,
                            version);
            setJsoupAttribute(CLASS, resolvedAttributes, link);
            setJsoupAttribute(HREF, resolvedAttributes, link);
        } catch (Exception e) {
            log.debug("Can't convert to internal link {}.xml::{}", docGuid, resourceUrl);
        }
    }

    private Attributes convertJsoupToSaxAttributes(final Element link) {
        AttributesImpl attributes = new AttributesImpl();
        link.attributes().asList()
                .forEach(attribute -> attributes.addAttribute(StringUtils.EMPTY, attribute.getKey(), attribute.getKey(), CDATA, attribute.getValue()));
        return attributes;
    }

    private void setJsoupAttribute(final String attrName, final Attributes resolvedAttributes, final Element link) {
        ofNullable(resolvedAttributes.getValue(attrName)).ifPresent(value -> link.attr(attrName, value));
    }

}
