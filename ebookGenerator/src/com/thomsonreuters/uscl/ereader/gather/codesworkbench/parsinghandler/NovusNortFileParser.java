package com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.XpathStack;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Extract NORT nodes from NORT xml file generated from Codes Workbench
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
@Slf4j
public class NovusNortFileParser extends DefaultHandler {

    private static final String N_LOAD = "/n-load";
    private static final String RELATIONSHIP = "/n-relationship" + N_LOAD;
    private static final String RELBASE = "/n-relbase" + RELATIONSHIP;
    private static final String RELTARGET = "/n-reltarget" + RELATIONSHIP;
    private static final String NORT_PAYLOAD = "/n-nortpayload/n-relpayload" + RELATIONSHIP;

    private static final String START_DATE = "/n-start-date" + NORT_PAYLOAD;
    private static final String END_DATE = "/n-end-date" + NORT_PAYLOAD;
    private static final String PUB_TAGGED_HEADING = "/pub-tagged-heading" + NORT_PAYLOAD;
    private static final String DOC_GUID = "/n-doc-guid" + NORT_PAYLOAD;
    private static final String RANK = "/n-rank" + NORT_PAYLOAD;
    private static final String LABEL = "/heading/n-label" + NORT_PAYLOAD;
    private static final String NODE_TYPE = "/node-type" + NORT_PAYLOAD;
    private static final String GRAFT_POINT_FLAG = "/graft-point-flag" + NORT_PAYLOAD;
    private static final String N_VIEW = "/n-view" + NORT_PAYLOAD;
    private Date cutoffDate;
    private XpathStack xpathStack;
    private final int nortFileLevel;

    // in CWB it is called "relationship", not "node"
    private List<RelationshipNode> nortNodes = new LinkedList<>();
    private Map<String, Map<Integer, String>> documentLevelMap;

    private List<RelationshipNode> roots = new ArrayList<>();
    private RelationshipNode currentNode;
    private StringBuffer tempVal;

    public NovusNortFileParser(
        final Date cutoffDate,
        final int nortFileLevel,
        final Map<String, Map<Integer, String>> documentMap) {
        super();
        this.cutoffDate = cutoffDate;
        this.nortFileLevel = nortFileLevel;
        documentLevelMap = documentMap;
        xpathStack = new XpathStack();
    }

    public List<RelationshipNode> parseDocument(final File nortFile)
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        // get a factory
        final SAXParserFactory spf = SAXParserFactory.newInstance();

        try (InputStream inputStream = new FileInputStream(nortFile);
            Reader reader = new InputStreamReader(inputStream, "UTF-8")) {
            // get a new instance of parser
            final SAXParser sp = spf.newSAXParser();

            final InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");
            sp.parse(is, this);
        }

        if (roots.size() > 0) {
            createParentChildRelationships();
        } else {
            log.error("No root node(s) found in file " + nortFile.getAbsolutePath());
            throw new SAXException("No root node(s) found in file " + nortFile.getAbsolutePath());
        }

        // Sort root nodes based on node rank
        final List<RelationshipNode> rootList = new ArrayList<>(roots);
        Collections.sort(rootList);
        return rootList;
    }

    private void createParentChildRelationships() {
        for (final RelationshipNode curNode : nortNodes) {
            nortNodes.stream()
                .filter(isParentNodeOf(curNode))
                .findFirst()
                .ifPresent(parent -> {
                    parent.getChildNodes()
                        .add(curNode);
                    curNode.setParentNode(parent);
                });
        }
    }

    private Predicate<? super RelationshipNode> isParentNodeOf(final RelationshipNode otherNode) {
        return node -> node.getNortGuid().equals(otherNode.getParentNortGuid());
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes atts)
        throws SAXException {
        xpathStack.push(qName);
        final String currentXpath = xpathStack.toXPathString();
        if (currentXpath.equalsIgnoreCase(RELATIONSHIP)) {
            currentNode = new RelationshipNode();
        }
        if (inExtractXpath(currentXpath)) {
            tempVal = new StringBuffer();
        }
    }

    @Override
    public void characters(final char[] buf, final int offset, final int len) throws SAXException {
        if (tempVal != null) {
            for (int i = offset; i < offset + len; i++) {
                tempVal.append(buf[i]);
            }
        }
    }

    private boolean inExtractXpath(final String xpath) {
        boolean extractPath = false;
        switch (xpath) {
        case RELBASE:
        case RELTARGET:
        case START_DATE:
        case END_DATE:
        case DOC_GUID:
        case RANK:
        case LABEL:
        case NODE_TYPE:
        case GRAFT_POINT_FLAG:
        case N_VIEW:
        case PUB_TAGGED_HEADING:
            extractPath = true;
            break;
        default:
            extractPath = false;
        }
        return extractPath;
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        final String currentXpath = xpathStack.toXPathString();
        if (currentXpath.equalsIgnoreCase(RELATIONSHIP)) {
            addCurrentNodeToMap();
        }

        if (inExtractXpath(currentXpath)) {
            final String value = tempVal.toString();
            if (currentXpath.equalsIgnoreCase(RELBASE)) {
                log.debug("Parsing Novus NORT GUID " + value);
                currentNode.setNortGuid(value);
            } else if (currentXpath.equalsIgnoreCase(RELTARGET)) {
                currentNode.setParentNortGuid(value);
            } else if (currentXpath.equalsIgnoreCase(START_DATE)) {
                currentNode.setStartDateStr(value);
            } else if (currentXpath.equalsIgnoreCase(END_DATE)) {
                currentNode.setEndDateStr(value);
            } else if (currentXpath.equalsIgnoreCase(PUB_TAGGED_HEADING)) {
                if (StringUtils.isNotBlank(value)) {
                    final boolean pubTaggedHeadingExists = (value.equalsIgnoreCase("Y") ? true : false);
                    currentNode.setPubTaggedHeadingExists(pubTaggedHeadingExists);
                }
            } else if (currentXpath.equalsIgnoreCase(DOC_GUID)) {
                String docGuid = value;
                if (documentLevelMap.containsKey(docGuid)) {
                    final Map<Integer, String> nortLevelMap = documentLevelMap.get(docGuid);

                    if (nortLevelMap.containsKey(nortFileLevel)) {
                        // duplicate docGuid found and also exists in NORT Level map
                        // use DOC GUID from NORT Level map
                        docGuid = nortLevelMap.get(nortFileLevel);
                    } else {
                        // duplicate docGuid found but DOC GUID does not exist in this NORT Level
                        // generate new docGuid to fix bug: CA Dwyer duplicate doc conflict.  Multiple extracts from
                        // same content set produces same documents with different prelims.  This is a special case.
                        // Duplicate documents within same content set can reuse the same document.
                        if (docGuid.contains("-")) {
                            docGuid = docGuid + nortFileLevel;
                        } else {
                            docGuid = docGuid + "-" + nortFileLevel;
                        }

                        nortLevelMap.put(nortFileLevel, docGuid);
                        documentLevelMap.put(docGuid, nortLevelMap);
                    }
                } else {
                    // First time seeing the document
                    final Map<Integer, String> nortLevelMap = new HashMap<>();
                    nortLevelMap.put(nortFileLevel, docGuid);
                    documentLevelMap.put(docGuid, nortLevelMap);
                }
                currentNode.setDocumentGuid(docGuid);
            } else if (currentXpath.equalsIgnoreCase(RANK)) {
                final double rank = Double.valueOf(value);
                currentNode.setRank(rank);
            } else if (currentXpath.equalsIgnoreCase(LABEL)) {
                currentNode.setLabel(value);
            } else if (currentXpath.equalsIgnoreCase(NODE_TYPE)) {
                currentNode.setNodeType(value);
            } else if (currentXpath.equalsIgnoreCase(GRAFT_POINT_FLAG)) {
                boolean isRootNode = false;
                if ("Y".equalsIgnoreCase(value)) {
                    isRootNode = true;
                }
                currentNode.setRootNode(isRootNode);
            } else if (currentXpath.equalsIgnoreCase(N_VIEW)) {
                currentNode.getViews().add(value);
            }
            tempVal = null;
        }

        xpathStack.pop();
    }

    private void addCurrentNodeToMap() throws SAXException {
        currentNode.setNortRank(nortFileLevel);
        final String endDateStr = currentNode.getEndDateStr();
        final DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date endDate = null;
        try {
            endDate = formatter.parse(endDateStr);
        } catch (final ParseException e) {
            log.debug("End date format error: " + endDateStr + " Expect end date format in yyyyMMddHHmmss.");
            throw new SAXException(
                "End date format error: " + endDateStr + " Expect end date format in yyyyMMddHHmmss.");
        } catch (final NullPointerException e) {
            log.debug("No end date was found for NORT GUID " + currentNode.getNortGuid());
            throw new SAXException("No end date was found for NORT GUID " + currentNode.getNortGuid());
        }

        // Only add nodes if they have not expired yet.
        if (endDate != null && endDate.after(cutoffDate)) {
            if (!currentNode.isDeletedNode()) {
                nortNodes.add(currentNode);
                // Save root nodes to return after parsing
                if (currentNode.isRootNode()) {
                    roots.add(currentNode);
                }
            } else {
                log.debug(
                    "Novus NORT GUID " + currentNode.getNortGuid() + " not included because it has been deleted.");
            }
        }
    }
}
