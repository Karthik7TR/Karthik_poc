package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thomsonreuters.uscl.ereader.format.FormatConstants;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various Anchor "<a>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLUnlinkInternalLinksFilter extends XMLFilterImpl
{
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

    public String getCurrentGuid()
    {
        return currentGuid;
    }

    public void setCurrentGuid(final String currentGuid)
    {
        this.currentGuid = currentGuid;
    }

    public void setTargetAnchors(final Map<String, Set<String>> targetAnchors)
    {
        this.targetAnchors = targetAnchors;
    }

    public Map<String, Set<String>> getTargetAnchors()
    {
        return targetAnchors;
    }

    public void setAnchorDupTargets(final Map<String, String> anchorDupTargets)
    {
        this.anchorDupTargets = anchorDupTargets;
    }

    public Map<String, String> getAnchorDupTargets()
    {
        return anchorDupTargets;
    }

    public List<String> getUnlinkDocMetadataList()
    {
        return unlinkDocMetadataList;
    }

    public void setUnlinkDocMetadataList(final List<String> unlinkDocMetadataList)
    {
        this.unlinkDocMetadataList = unlinkDocMetadataList;
    }

    public DocMetadata getUnlinkDocMetadata()
    {
        return unlinkDocMetadata;
    }

    public Map<String, DocMetadata> getDocMetadataKeyedByProViewId()
    {
        return docMetadataKeyedByProViewId;
    }

    public void setDocMetadataKeyedByProViewId(final Map<String, DocMetadata> docMetadataKeyedByProViewId)
    {
        this.docMetadataKeyedByProViewId = docMetadataKeyedByProViewId;
    }

    public void setUnlinkDocMetadata(final DocMetadata unlinkDocMetadata)
    {
        this.unlinkDocMetadata = unlinkDocMetadata;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException
    {
        if (qName.equalsIgnoreCase("a"))
        {
            if (atts != null
                && atts.getValue("href") != null
                && atts.getValue("href").startsWith(FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT)
                && atts.getValue("href").contains("/"))
            {
                String guid = currentGuid;

                String attsHrefValue = atts.getValue("href");
                // hrefLink value without split title
                attsHrefValue = FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT
                    + StringUtils.substring(attsHrefValue, attsHrefValue.indexOf("#"));

                // Get the string list after # regex '/'
                final String[] guidList = attsHrefValue.split("/");

                if (guidList.length > 1)
                {
                    guid = guidList[0].substring(4);
                }

                if (targetAnchors != null)
                {
                    nameAnchors = targetAnchors.get(guid);
                }

                if (nameAnchors != null && nameAnchors.contains(attsHrefValue))
                {
                    if (anchorDupTargets != null && anchorDupTargets.containsKey(attsHrefValue))
                    {
                        // change href REPLACEWITH existing anchor

                        for (final Entry<String, String> dupTarget : anchorDupTargets.entrySet())
                        {
                            if (dupTarget.getKey().contains(attsHrefValue))
                            {
                                attsHrefValue = dupTarget.getValue();
                                if (!dupTarget.getValue().contains("_"))
                                    break; // give priority to guid without _
                            }
                        }
                        final AttributesImpl newAtts = new AttributesImpl(atts);
                        String newAttsHrefValue = newAtts.getValue("href");
                        // hrefLink value without split title
                        final String splitTitle = StringUtils.substring(
                            newAttsHrefValue,
                            newAttsHrefValue.indexOf("er:") + 3,
                            newAttsHrefValue.indexOf("#"));
                        newAttsHrefValue = FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT
                            + StringUtils.substring(newAttsHrefValue, newAttsHrefValue.indexOf("#"));

                        if (attsHrefValue != null
                            && newAtts.getIndex("href") >= 0
                            && !attsHrefValue.equals(newAttsHrefValue))
                        {
                            final int indexHrefId = newAtts.getIndex("href");
                            // Add split title to the new link if exists
                            if (splitTitle.length() > 0)
                            {
                                attsHrefValue = FormatConstants.PROVIEW_ASSERT_REFERENCE_PREFIX_SPLIT
                                    + splitTitle
                                    + StringUtils.substring(attsHrefValue, attsHrefValue.indexOf("#"));
                            }
                            newAtts.setAttribute(indexHrefId, "", "", "href", "CDATA", attsHrefValue);
                        }
                        super.startElement(uri, localName, qName, newAtts);
                        openAnchors.push(true); // valid link denoted by TRUE
                    }
                    else
                    {
                        // remove anchor with no target.
                        openAnchors.push(false); // bad link denoted by FALSE

                        // write out link information for email report
                        if (unlinkDocMetadataList == null)
                        {
                            unlinkDocMetadataList = new ArrayList<>();
                        }

                        final StringBuffer sbDocMetadata = new StringBuffer();
                        if (unlinkDocMetadata != null)
                        {
                            sbDocMetadata.append(unlinkDocMetadata.getDocUuid());
                        }
                        else
                        {
                            sbDocMetadata.append(currentGuid);
                        }
                        sbDocMetadata.append(",");
                        if (unlinkDocMetadata.getDocFamilyUuid() != null)
                        {
                            sbDocMetadata.append(unlinkDocMetadata.getDocFamilyUuid());
                        }
                        sbDocMetadata.append(",");
                        if (unlinkDocMetadata.getNormalizedFirstlineCite() != null)
                        {
                            sbDocMetadata.append(unlinkDocMetadata.getNormalizedFirstlineCite());
                        }
                        sbDocMetadata.append(",");
                        if (unlinkDocMetadata.getSerialNumber() != null)
                        {
                            sbDocMetadata.append(unlinkDocMetadata.getSerialNumber());
                        }
                        sbDocMetadata.append(",");
                        if (unlinkDocMetadata.getCollectionName() != null)
                        {
                            sbDocMetadata.append(unlinkDocMetadata.getCollectionName());
                        }
                        sbDocMetadata.append(",");
                        final String link = atts.getValue("href");
                        sbDocMetadata.append(link);

                        final Matcher matcher = pattern.matcher(link);
                        if (matcher.find())
                        {
                            final String proViewId = matcher.group(1);
                            final DocMetadata targetDocMetadata = docMetadataKeyedByProViewId.get(proViewId);
                            if (targetDocMetadata != null)
                            {
                                sbDocMetadata.append(",");
                                if (StringUtils.isNotBlank(targetDocMetadata.getDocUuid()))
                                {
                                    sbDocMetadata.append(targetDocMetadata.getDocUuid());
                                }
                                sbDocMetadata.append(",");
                                if (StringUtils.isNotBlank(targetDocMetadata.getDocFamilyUuid()))
                                {
                                    sbDocMetadata.append(targetDocMetadata.getDocFamilyUuid());
                                }
                                sbDocMetadata.append(",");
                                if (StringUtils.isNotBlank(targetDocMetadata.getNormalizedFirstlineCite()))
                                {
                                    sbDocMetadata.append(targetDocMetadata.getNormalizedFirstlineCite());
                                }
                                sbDocMetadata.append(",");
                                if (targetDocMetadata.getSerialNumber() != null)
                                {
                                    sbDocMetadata.append(targetDocMetadata.getSerialNumber());
                                }
                            }
                        }

                        unlinkDocMetadataList.add(sbDocMetadata.toString());
                    }
                }
                else
                {
                    super.startElement(uri, localName, qName, atts);
                    openAnchors.push(true);
                }
            }
            else
            {
                super.startElement(uri, localName, qName, atts);
                openAnchors.push(true);
            }
        }
        else
        {
            super.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException
    {
        super.characters(buf, offset, len);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException
    {
        if (qName.equalsIgnoreCase("a"))
        {
            // If the latest anchor was valid (TRUE) add the </a> if it was removed (FALSE) don't.
            if ((boolean) openAnchors.pop())
            {
                super.endElement(uri, localName, qName);
            }
        }
        else
        {
            super.endElement(uri, localName, qName);
        }
    }
}
