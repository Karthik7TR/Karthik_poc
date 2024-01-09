package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.FormatConstants;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.ANCHOR;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.HREF;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.INNER;

/**
 * Filter that handles various Anchor "<a>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLUnlinkInternalLinksFilter extends XMLFilterImpl {
    private Set<String> nameAnchors;
    private Map<String, Set<String>> targetAnchors;
    private Map<String, String> anchorDupTargets;
    private List<String> unlinkDocMetadataList;
    private Map<String, DocMetadata> docMetadataKeyedByProViewId;
    private DocMetadata unlinkDocMetadata;
    // TRUE represents valid anchors, FALSE represents a removed anchor
    private ArrayDeque<Object> openAnchors = new ArrayDeque<>();
    private String currentGuid;

    private Pattern pattern = Pattern.compile("^er:#([a-zA-Z0-9_]+)/[a-zA-Z0-9_]+$");

    public String getCurrentGuid() {
        return currentGuid;
    }

    public void setCurrentGuid(final String currentGuid) {
        this.currentGuid = currentGuid;
    }

    public void setTargetAnchors(final Map<String, Set<String>> targetAnchors) {
        this.targetAnchors = targetAnchors;
    }

    public Map<String, Set<String>> getTargetAnchors() {
        return targetAnchors;
    }

    public void setAnchorDupTargets(final Map<String, String> anchorDupTargets) {
        this.anchorDupTargets = anchorDupTargets;
    }

    public Map<String, String> getAnchorDupTargets() {
        return anchorDupTargets;
    }

    public List<String> getUnlinkDocMetadataList() {
        return unlinkDocMetadataList;
    }

    public void setUnlinkDocMetadataList(final List<String> unlinkDocMetadataList) {
        this.unlinkDocMetadataList = unlinkDocMetadataList;
    }

    public DocMetadata getUnlinkDocMetadata() {
        return unlinkDocMetadata;
    }

    public Map<String, DocMetadata> getDocMetadataKeyedByProViewId() {
        return docMetadataKeyedByProViewId;
    }

    public void setDocMetadataKeyedByProViewId(final Map<String, DocMetadata> docMetadataKeyedByProViewId) {
        this.docMetadataKeyedByProViewId = docMetadataKeyedByProViewId;
    }

    public void setUnlinkDocMetadata(final DocMetadata unlinkDocMetadata) {
        this.unlinkDocMetadata = unlinkDocMetadata;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        if (ANCHOR.equalsIgnoreCase(qName)) {
            if (atts != null
                && atts.getValue(HREF) != null
                && atts.getValue(HREF).startsWith(FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT)
                && atts.getValue(HREF).contains("/")
                && atts.getValue(INNER) == null
            ) {
                String guid = currentGuid;

                String attsHrefValue = atts.getValue(HREF);
                // hrefLink value without split title
                attsHrefValue = FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT
                    + StringUtils.substring(attsHrefValue, attsHrefValue.indexOf('#'));

                // Get the string list after # regex '/'
                final String[] guidList = attsHrefValue.split("/");

                if (guidList.length > 1) {
                    guid = guidList[0].substring(4);
                }

                if (targetAnchors != null) {
                    nameAnchors = targetAnchors.get(guid);
                }

                if (nameAnchors != null && nameAnchors.contains(attsHrefValue)) {
                    if (anchorDupTargets != null && anchorDupTargets.containsKey(attsHrefValue)) {
                        // change href REPLACEWITH existing anchor

                        for (final Entry<String, String> dupTarget : anchorDupTargets.entrySet()) {
                            if (dupTarget.getKey().contains(attsHrefValue)) {
                                attsHrefValue = dupTarget.getValue();
                                if (!dupTarget.getValue().contains("_"))
                                    break; // give priority to guid without _
                            }
                        }
                        final AttributesImpl newAtts = new AttributesImpl(atts);
                        String newAttsHrefValue = newAtts.getValue(HREF);
                        // hrefLink value without split title
                        final String splitTitle = StringUtils.substring(
                            newAttsHrefValue,
                            newAttsHrefValue.indexOf("er:") + 3,
                            newAttsHrefValue.indexOf('#'));
                        newAttsHrefValue = FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT
                            + StringUtils.substring(newAttsHrefValue, newAttsHrefValue.indexOf('#'));

                        if (attsHrefValue != null
                            && newAtts.getIndex(HREF) >= 0
                            && !attsHrefValue.equals(newAttsHrefValue)) {
                            final int indexHrefId = newAtts.getIndex(HREF);
                            // Add split title to the new link if exists
                            if (splitTitle.length() > 0) {
                                attsHrefValue = FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT
                                    + splitTitle
                                    + StringUtils.substring(attsHrefValue, attsHrefValue.indexOf('#'));
                            }
                            newAtts.setAttribute(indexHrefId, "", "", HREF, "CDATA", attsHrefValue);
                        }
                        super.startElement(uri, localName, qName, newAtts);
                        openAnchors.push(true); // valid link denoted by TRUE
                    } else {
                        // remove anchor with no target.
                        openAnchors.push(false); // bad link denoted by FALSE

                        // write out link information for email report
                        if (unlinkDocMetadataList == null) {
                            unlinkDocMetadataList = new ArrayList<>();
                        }

                        final StringBuilder sbDocMetadata = new StringBuilder();
                        if (unlinkDocMetadata != null) {
                            sbDocMetadata.append(unlinkDocMetadata.getDocUuid());
                        } else {
                            sbDocMetadata.append(currentGuid);
                        }

                        sbDocMetadata.append(",");
                        sbDocMetadata.append(toCsvString(unlinkDocMetadata,
                            () -> unlinkDocMetadata.getDocFamilyUuid(),
                            () -> unlinkDocMetadata.getNormalizedFirstlineCite(),
                            () -> unlinkDocMetadata.getSerialNumber(),
                            () -> unlinkDocMetadata.getCollectionName()));

                        sbDocMetadata.append(",");
                        final String link = atts.getValue(HREF);
                        sbDocMetadata.append(link);

                        final Matcher matcher = pattern.matcher(link);
                        if (matcher.find()) {
                            final String proViewId = matcher.group(1);
                            final DocMetadata targetDocMetadata = docMetadataKeyedByProViewId.get(proViewId);
                            if (targetDocMetadata != null) {
                                sbDocMetadata.append(",");
                                sbDocMetadata.append(toCsvString(
                                    targetDocMetadata.getDocUuid(),
                                    targetDocMetadata.getDocFamilyUuid(),
                                    targetDocMetadata.getNormalizedFirstlineCite(),
                                    targetDocMetadata.getSerialNumber()));
                            }
                        }

                        unlinkDocMetadataList.add(sbDocMetadata.toString());
                    }
                } else {
                    super.startElement(uri, localName, qName, atts);
                    openAnchors.push(true);
                }
            } else {
                super.startElement(uri, localName, qName, atts);
                openAnchors.push(true);
            }
        } else {
            super.startElement(uri, localName, qName, atts);
        }
    }

    private String toCsvString(final DocMetadata metadata, final Supplier<Object>... funcs) {
        final Object[] values;
        if (metadata != null) {
            values = Stream.of(funcs)
                .map(Supplier::get)
                .toArray(Object[]::new);
        } else {
            values = new Object[funcs.length];
        }
        return toCsvString(values);
    }

    private String toCsvString(final Object... values) {
        return Stream.of(values)
            .map(Optional::ofNullable)
            .map(o -> o.map(Object::toString).orElse(""))
            .map(String::trim)
            .collect(Collectors.joining(","));
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (ANCHOR.equalsIgnoreCase(qName)) {
            // If the latest anchor was valid (TRUE) add the </a> if it was removed (FALSE) don't.
            if ((boolean) openAnchors.pop()) {
                super.endElement(uri, localName, qName);
            }
        } else {
            super.endElement(uri, localName, qName);
        }
    }
}
