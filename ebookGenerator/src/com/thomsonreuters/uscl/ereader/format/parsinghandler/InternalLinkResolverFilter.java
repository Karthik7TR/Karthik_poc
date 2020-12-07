package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.io.File;
import java.util.Optional;
import com.thomsonreuters.uscl.ereader.format.service.InternalLinkResolverService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * A SAX Filter that detects whether or not an anchor points to a resource included within an
 * eBook.<p>If there is a match, the URL is converted to ProView format &lt;a
 * href="er:#id"&gt;anchor text&lt;/a&gt;.</p>
 *
 * @author <a href="mailto:ravi.nandikolla@thomsonreuters.com">Ravi Nandikolla</a> c139353
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 * @author <a href="mailto:dong.kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class InternalLinkResolverFilter extends XMLFilterImpl {
    private static final String ANCHOR_ELEMENT = "a";
    private static final String HREF = "href";
    private static final String HTTP = "http";
    private DocumentMetadataAuthority documentMetadataAuthority;
    private File docsGuidFile;
    private String docGuid;
    private String version;

    private InternalLinkResolverService internalLinkResolver;

    private InternalLinkResolverFilter(final DocumentMetadataAuthority documentMetadataAuthority) {
        this.documentMetadataAuthority = Optional.ofNullable(documentMetadataAuthority)
            .orElseThrow(() -> new IllegalArgumentException(
                "Cannot create instances of InternalLinkResolverFilter without a DocumentMetadataAuthority"));
    }

    public InternalLinkResolverFilter(final InternalLinkResolverService internalLinkResolver,
                                      final DocumentMetadataAuthority documentMetadataAuthority,
                                      final File docsGuidFile,
                                      final String docGuid,
                                      final String version) {
        this(documentMetadataAuthority);
        this.docsGuidFile = docsGuidFile;
        this.internalLinkResolver = internalLinkResolver;
        this.docGuid = docGuid;
        this.version = version;
    }

    @Override
    public void startElement(final String uri, final String localname, final String qName, final Attributes attributes)
        throws SAXException {
        final String resourceUrl = attributes.getValue(HREF);

        if (StringUtils.isNotEmpty(resourceUrl) && resourceUrl.contains(HTTP) && ANCHOR_ELEMENT.equals(qName)) {
            final Attributes resolvedAttributes =
                    internalLinkResolver.resolveResourceUrlReference(documentMetadataAuthority,
                            resourceUrl, attributes, docsGuidFile, docGuid, version);
            super.startElement(uri, localname, qName, resolvedAttributes);
        } else {
            super.startElement(uri, localname, qName, attributes);
        }
    }
}
