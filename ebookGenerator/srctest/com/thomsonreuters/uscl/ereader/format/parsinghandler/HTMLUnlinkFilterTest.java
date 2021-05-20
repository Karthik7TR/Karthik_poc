package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Test various HTMLUnlinkInternalLinksFilter data scenarios.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public final class HTMLUnlinkFilterTest {
    private HTMLUnlinkInternalLinksFilter unlinkFilter;
    private Serializer serializer;
    private final String currentGuid = "ABC1234";
    private final String foundAnchor = "er:#ABC1234/foundAnchor";
    private final String foundAnchorId = "foundAnchor";

    @Before
    public void setUp() throws Exception {
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        final SAXParser saxParser = factory.newSAXParser();
        final Map<String, Set<String>> targetAnchors = new HashMap<>();
        final Set<String> hs = new HashSet<>();
        hs.add(foundAnchor);
        hs.add(foundAnchor + "new");
        targetAnchors.put(currentGuid, hs);
        final List<String> unlinkDocMetadataList = new ArrayList<>();
        final DocMetadata unlinkDocMetadata = new DocMetadata();
        unlinkFilter = new HTMLUnlinkInternalLinksFilter();
        unlinkFilter.setParent(saxParser.getXMLReader());
        unlinkFilter.setCurrentGuid(currentGuid);
        unlinkFilter.setTargetAnchors(targetAnchors);
        unlinkFilter.setUnlinkDocMetadataList(unlinkDocMetadataList);
        unlinkFilter.setUnlinkDocMetadata(unlinkDocMetadata);

        final Map<String, DocMetadata> docMetadataKeyedByProViewId = new HashMap<>();
        final DocMetadata docMeta = new DocMetadata();
        docMeta.setDocUuid("docUuid");
        docMeta.setDocFamilyUuid("familyUuid");
        docMeta.setNormalizedFirstlineCite("FirstlineCite");
        docMeta.setSerialNumber(127L);
        docMetadataKeyedByProViewId.put(currentGuid, docMeta);
        unlinkFilter.setDocMetadataKeyedByProViewId(docMetadataKeyedByProViewId);
        final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
        props.setProperty("omit-xml-declaration", "yes");
        serializer = SerializerFactory.getSerializer(props);
    }

    @After
    public void tearDown() {
        serializer = null;
        unlinkFilter = null;
    }

    /**
     * Helper method that sets up the repeating pieces of each test and modifies the ImageService values that are
     * returned along with the input and output.
     *
     * @param inputXML input string for the test.
     * @param expectedResult the expected output for the specified input string.
     */
    public void testHelper(final String inputXML, final String expectedResult) throws Exception {
        try (final ByteArrayInputStream input = new ByteArrayInputStream(inputXML.getBytes());
            ByteArrayOutputStream output = new ByteArrayOutputStream();) {

            serializer.setOutputStream(output);

            unlinkFilter.setContentHandler(serializer.asContentHandler());
            unlinkFilter.parse(new InputSource(input));

            final String result = output.toString();

            assertEquals(expectedResult, result);
        }
    }

    @Test
    public void testRemoveAnchorTagFromId() throws Exception {
        final String xmlTestStr = "<test><a href=\"" + foundAnchor + "\"><sup>1</sup></a></test>";
        final String expectedResult = "<test><sup>1</sup></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testRemoveConsecutiveNestedAnchorTag() throws Exception {
        final String xmlTestStr = "<test><div><a href=\"er:abc/efg\"><span id=\""
            + foundAnchorId
            + "notinlist\" class=\"KG\"><a href=\""
            + foundAnchor
            + "\"><strong>1<br/>break</strong></a><a href=\""
            + foundAnchor
            + "\"><strong>1<br/>break</strong></a></span></a></div></test>";
        final String expectedResult = "<test><div><a href=\"er:abc/efg\"><span id=\""
            + foundAnchorId
            + "notinlist\" class=\"KG\"><strong>1<br/>break</strong><strong>1<br/>break</strong></span></a></div></test>";
        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testRemoveAnchorTagFromIdTwice() throws Exception {
        final String xmlTestStr = "<test><a href=\""
            + foundAnchor
            + "\"><sup>1</sup></a><a href=\""
            + foundAnchor
            + "\"><sup>2</sup></a></test>";
        final String expectedResult = "<test><sup>1</sup><sup>2</sup></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testRemoveAnchorTagFromIdWithEmbededTags() throws Exception {
        final String xmlTestStr = "<test><sup id=\""
            + foundAnchorId
            + "\"><a href=\""
            + foundAnchor
            + "\"><strong>1</strong></a></sup></test>";
        final String expectedResult = "<test><sup id=\"" + foundAnchorId + "\"><strong>1</strong></sup></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testRemoveAnchorTagFromIdWithExtraTags() throws Exception {
        final String xmlTestStr = "<test><div>divVal</div><sup id=\""
            + foundAnchorId
            + "\"><a href=\""
            + foundAnchor
            + "\"><strong>1</strong></a></sup></test>";
        final String expectedResult =
            "<test><div>divVal</div><sup id=\"" + foundAnchorId + "\"><strong>1</strong></sup></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testRemoveAnchorTagFromIdWithExtraEmbeddedTags() throws Exception {
        final String xmlTestStr = "<test><div><div>divVal</div><sup id=\""
            + foundAnchorId
            + "\"><a href=\""
            + foundAnchor
            + "\"><strong>1</strong></a></sup></div></test>";
        final String expectedResult =
            "<test><div><div>divVal</div><sup id=\"" + foundAnchorId + "\"><strong>1</strong></sup></div></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testRemoveAnchorTagFromTwoIdWithExtraEmbeddedTags() throws Exception {
        final String xmlTestStr = "<test><div><div>divVal</div><sup id=\""
            + foundAnchorId
            + "\"><a href=\""
            + foundAnchor
            + "\"><span id=\""
            + foundAnchorId
            + "\" class=\"KG\"><strong>1</strong></span></a></sup></div></test>";
        final String expectedResult = "<test><div><div>divVal</div><sup id=\""
            + foundAnchorId
            + "\"><span id=\""
            + foundAnchorId
            + "\" class=\"KG\"><strong>1</strong></span></sup></div></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testRemoveAnchorTagFromTwoIdDifferentNamesNested() throws Exception {
        final String xmlTestStr = "<test><div><div>divVal</div><sup id=\""
            + foundAnchorId
            + "\"><a href=\""
            + foundAnchor
            + "\"><span id=\""
            + foundAnchorId
            + "new\" class=\"KG\"><a href=\""
            + foundAnchor
            + "new\"><strong>1</strong></a></span></a></sup></div></test>";
        final String expectedResult = "<test><div><div>divVal</div><sup id=\""
            + foundAnchorId
            + "\"><span id=\""
            + foundAnchorId
            + "new\" class=\"KG\"><strong>1</strong></span></sup></div></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testRemoveAnchorTagFromTwoIdDifferentNames() throws Exception {
        final String xmlTestStr = "<test><div><div>divVal</div><sup id=\""
            + foundAnchorId
            + "\"><a href=\""
            + foundAnchor
            + "\">SupText</a></sup><span id=\""
            + foundAnchorId
            + "new\" class=\"KG\"><a href=\""
            + foundAnchor
            + "new\"><strong>1</strong></a></span></div></test>";
        final String expectedResult = "<test><div><div>divVal</div><sup id=\""
            + foundAnchorId
            + "\">SupText</sup><span id=\""
            + foundAnchorId
            + "new\" class=\"KG\"><strong>1</strong></span></div></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testDoNotRemoveAnchorTagFromSpanNotInTargetList() throws Exception {
        final String xmlTestStr = "<test><div><div>divVal</div><sup id=\""
            + foundAnchorId
            + "\"><a href=\""
            + foundAnchor
            + "\"><span id=\""
            + foundAnchorId
            + "notinlist\" class=\"KG\"><a href=\""
            + foundAnchor
            + "notinlist\"><strong>1<br/>break</strong></a></span></a></sup></div></test>";
        final String expectedResult = "<test><div><div>divVal</div><sup id=\""
            + foundAnchorId
            + "\"><span id=\""
            + foundAnchorId
            + "notinlist\" class=\"KG\"><a href=\""
            + foundAnchor
            + "notinlist\"><strong>1<br/>break</strong></a></span></sup></div></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testRemoveAnchorTagWithNestingTags() throws Exception {
        final String xmlTestStr = "<test><div><div>divVal</div><strong><sup id=\""
            + foundAnchorId
            + "\"><a href=\""
            + foundAnchor
            + "\"><strong><span id=\""
            + foundAnchorId
            + "new\" class=\"KG\"><a href=\""
            + foundAnchor
            + "new\">text</a></span></strong><strong>1<br/>break</strong></a></sup></strong></div></test>";
        final String expectedResult = "<test><div><div>divVal</div><strong><sup id=\""
            + foundAnchorId
            + "\"><strong><span id=\""
            + foundAnchorId
            + "new\" class=\"KG\">text</span></strong><strong>1<br/>break</strong></sup></strong></div></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testNoIdField() throws Exception {
        final String xmlTestStr = "<test><div>divVal</div><strong>1</strong></test>";
        final String expectedResult = "<test><div>divVal</div><strong>1</strong></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testReplaceAnchorTagFromId() throws Exception {
        final Map<String, String> anchorDupTargets = new HashMap<>();
        anchorDupTargets.put(foundAnchor, foundAnchor + "new");
        unlinkFilter.setAnchorDupTargets(anchorDupTargets);

        final String xmlTestStr = "<test><a href=\"" + foundAnchor + "\"><sup>1</sup></a></test>";
        final String expectedResult = "<test><a href=\"" + foundAnchor + "new\"><sup>1</sup></a></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testReplaceAnchorTagFromIdMultiple() throws Exception {
        final Map<String, String> anchorDupTargets = new HashMap<>();
        anchorDupTargets.put(foundAnchor, foundAnchor + "_new2");
        anchorDupTargets.put(foundAnchor, foundAnchor + "new");
        unlinkFilter.setAnchorDupTargets(anchorDupTargets);

        final String xmlTestStr = "<test><a href=\"" + foundAnchor + "\"><sup>1</sup></a></test>";
        final String expectedResult = "<test><a href=\"" + foundAnchor + "new\"><sup>1</sup></a></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testNOReplaceAnchorTagFromIdMultiple() throws Exception {
        final Map<String, String> anchorDupTargets = new HashMap<>();
        anchorDupTargets.put(foundAnchor + "new", foundAnchor + "_new2");
        unlinkFilter.setAnchorDupTargets(anchorDupTargets);

        final String xmlTestStr = "<test><a href=\"" + foundAnchor + "\"><sup>1</sup></a></test>";
        final String expectedResult = "<test><sup>1</sup></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    @Test
    public void testUnlinkDocMetadataNull() throws Exception {
        unlinkFilter.setUnlinkDocMetadataList(null);
        unlinkFilter.setUnlinkDocMetadata(null);
        final String xmlTestStr = "<test><a href=\"" + foundAnchor + "\"><sup>1</sup></a></test>";
        final String expectedResult = "<test><sup>1</sup></test>";

        testHelper(xmlTestStr, expectedResult);

        assertEquals(1, unlinkFilter.getUnlinkDocMetadataList().size());
        assertEquals("ABC1234,,,,,er:#ABC1234/foundAnchor,docUuid,familyUuid,FirstlineCite,127", unlinkFilter.getUnlinkDocMetadataList().get(0));
    }

    @Test
    public void testUnlinkDocMetadataNotEmpty() throws Exception {
        unlinkFilter.setUnlinkDocMetadata(initDocMetadata());
        final String xmlTestStr = "<test><a href=\"" + foundAnchor + "\"><sup>1</sup></a></test>";
        final String expectedResult = "<test><sup>1</sup></test>";

        testHelper(xmlTestStr, expectedResult);

        assertEquals(1, unlinkFilter.getUnlinkDocMetadataList().size());
        assertEquals("docUuid,docFamilyUuid,normalizedFirstlineCite,1,collectionName,er:#ABC1234/foundAnchor,docUuid,familyUuid,FirstlineCite,127", unlinkFilter.getUnlinkDocMetadataList().get(0));
    }

    @Test
    public void testUnlinkDocMetadataBlankLines() throws Exception {
        final DocMetadata unlinkDocMetadata = initDocMetadata();
        unlinkDocMetadata.setDocFamilyUuid("    ");

        unlinkFilter.setUnlinkDocMetadata(unlinkDocMetadata);
        final String xmlTestStr = "<test><a href=\"" + foundAnchor + "\"><sup>1</sup></a></test>";
        final String expectedResult = "<test><sup>1</sup></test>";

        testHelper(xmlTestStr, expectedResult);

        assertEquals(1, unlinkFilter.getUnlinkDocMetadataList().size());
        assertEquals("docUuid,,normalizedFirstlineCite,1,collectionName,er:#ABC1234/foundAnchor,docUuid,familyUuid,FirstlineCite,127", unlinkFilter.getUnlinkDocMetadataList().get(0));
    }

    @Test
    public void testEmptyCurrentGuidDocMetadata() throws Exception {
        unlinkFilter.getDocMetadataKeyedByProViewId().put(currentGuid, new DocMetadata());
        unlinkFilter.setUnlinkDocMetadata(null);
        final String xmlTestStr = "<test><a href=\"" + foundAnchor + "\"><sup>1</sup></a></test>";
        final String expectedResult = "<test><sup>1</sup></test>";

        testHelper(xmlTestStr, expectedResult);

        assertEquals(1, unlinkFilter.getUnlinkDocMetadataList().size());
        assertEquals("ABC1234,,,,,er:#ABC1234/foundAnchor,,,,", unlinkFilter.getUnlinkDocMetadataList().get(0));
    }

    @Test
    public void testDontRemoveInnerAnchor() throws Exception {
        final String xmlTestStr = "<test><a href=\""
                + foundAnchor
                + "\" inner=\"true\">1</a></test>";
        final String expectedResult =
                "<test><a href=\"" + foundAnchor + "\" inner=\"true\">1</a></test>";

        testHelper(xmlTestStr, expectedResult);
    }

    private DocMetadata initDocMetadata() {
        final DocMetadata docMetadata = new DocMetadata();
        docMetadata.setDocUuid("docUuid");
        docMetadata.setDocFamilyUuid("docFamilyUuid");
        docMetadata.setNormalizedFirstlineCite("normalizedFirstlineCite");
        docMetadata.setSerialNumber(1L);
        docMetadata.setCollectionName("collectionName");
        return docMetadata;
    }
}
