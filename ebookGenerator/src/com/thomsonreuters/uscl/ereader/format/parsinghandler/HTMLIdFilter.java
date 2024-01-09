package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter that handles various Anchor "<a>" tags and transforms them as needed.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLIdFilter extends XMLFilterImpl {
    private String currentGuid;
    private String familyGuid;
    private Set<String> nameAnchors;
    private Map<String, Set<String>> targetAnchors;
    private Map<String, Set<String>> dupTargetAnchors;
    private Map<String, String> dupGuids;
    private int anchorAddedCntr;
    private int goodStartCntr;
    private List<Integer> goodCntrList = new ArrayList<>();

    public String getCurrentGuid() {
        return currentGuid;
    }

    public void setCurrentGuid(final String currentGuid) {
        this.currentGuid = currentGuid;
    }

    public String getFamilyGuid() {
        return familyGuid;
    }

    public void setFamilyGuid(final String familyGuid) {
        this.familyGuid = familyGuid;
    }

    public void setTargetAnchors(final Map<String, Set<String>> targetAnchors) {
        this.targetAnchors = targetAnchors;
    }

    public Map<String, Set<String>> getTargetAnchors() {
        return targetAnchors;
    }

    public void setDupTargetAnchors(final Map<String, Set<String>> dupTargetAnchors) {
        this.dupTargetAnchors = dupTargetAnchors;
    }

    public Map<String, Set<String>> getDupTargetAnchors() {
        return dupTargetAnchors;
    }

    public void setDupGuids(final Map<String, String> dupGuids) {
        this.dupGuids = dupGuids;
    }

    public Map<String, String> getDupGuids() {
        return dupGuids;
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        boolean bCreateDupAnchor = false;
        String guid = currentGuid; // proviewId or guid
        if (atts != null && atts.getValue("id") != null) {
            final String[] guidList = atts.getValue("id").split("/");
            if (guidList.length > 1) {
                guid = guidList[0];
            }

            if (targetAnchors != null) {
                nameAnchors = targetAnchors.get(guid);
            }
            // add anchor id after <span id="co_footnote_I6a32d800568d11e199970000837bc6dd"> like
            // <a name="er:#I38680d13677511dc8ebc0000837214a9/co_footnote_I6a32d800568d11e199970000837bc6dd">
            final String fullyQualifiedId = "er:#" + guid + "/" + atts.getValue("id");

            //determine if should be duplicate guid instead!
            if (((nameAnchors != null && !nameAnchors.contains(fullyQualifiedId)) || nameAnchors == null)
                && dupGuids != null
                && dupGuids.containsKey(guid)) {
                for (final String dupProviewId : dupGuids.keySet()) {
                    final String famGuid = dupGuids.get(dupProviewId);
                    if (famGuid.equals(familyGuid) && !guid.equals(dupProviewId)) {
                        final String fullyQualifiedDupId = "er:#" + dupProviewId + "/" + atts.getValue("id");
                        final Set<String> nameDupAnchors = targetAnchors.get(dupProviewId);

                        if (nameDupAnchors != null && nameDupAnchors.contains(fullyQualifiedDupId)) {
                            bCreateDupAnchor = true;

                            // Create a file with guid, fullyQualified and fullyQualifiedDupId
                            Set<String> hs = new HashSet<>();
                            if (dupTargetAnchors != null && dupTargetAnchors.get(guid) != null) {
                                hs = dupTargetAnchors.get(guid);
                            } else if (dupTargetAnchors == null) {
                                dupTargetAnchors = new HashMap<>();
                            }
                            hs.add(fullyQualifiedDupId + "REPLACEWITH" + fullyQualifiedId);
                            dupTargetAnchors.put(guid, hs);
                        }
                    }
                }
                if (bCreateDupAnchor) {
                    // Found a match!
                    // Add anchor with fullyQualified but don't remove from list so we can change
                    // the anchor href in the unlink step
                    final AttributesImpl newAtts = new AttributesImpl();

                    newAtts.addAttribute("", "", "name", "CDATA", atts.getValue("id"));

                    if (anchorAddedCntr > 0) {
                        goodCntrList.add(goodStartCntr);
                        goodStartCntr = 0;
                    }
                    anchorAddedCntr++;
                    super.startElement(uri, localName, qName, atts);
                    super.startElement(uri, localName, "a", newAtts);
                }
            }
            if (nameAnchors != null && nameAnchors.contains(fullyQualifiedId)) {
                // insert missing named anchor
                final AttributesImpl newAtts = new AttributesImpl();

                newAtts.addAttribute("", "", "name", "CDATA", atts.getValue("id"));

                if (anchorAddedCntr > 0) {
                    goodCntrList.add(goodStartCntr);
                    goodStartCntr = 0;
                }
                anchorAddedCntr++;
                // remove from list
                nameAnchors.remove(fullyQualifiedId);
                targetAnchors.get(guid).remove(fullyQualifiedId);
                // write existing id
                super.startElement(uri, localName, qName, atts);
                super.startElement(uri, localName, "a", newAtts);
            } else if (!bCreateDupAnchor) {
                super.startElement(uri, localName, qName, atts);
                if (anchorAddedCntr > 0) {
                    goodStartCntr++;
                }
            }
        } else {
            super.startElement(uri, localName, qName, atts);
            if (anchorAddedCntr > 0) {
                goodStartCntr++;
            }
        }
    }

    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException {
        super.characters(buf, offset, len);
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (anchorAddedCntr > 0) {
            if (goodStartCntr == 0) {
                super.endElement(uri, localName, "a");
                anchorAddedCntr--;
                if (anchorAddedCntr >= 1) {
                    final int lastIdx = goodCntrList.size() - 1;
                    goodStartCntr = goodCntrList.get(lastIdx);
                    goodCntrList.remove(lastIdx);
                }
            } else {
                goodStartCntr--;
            }
        }
        super.endElement(uri, localName, qName);
    }
}
