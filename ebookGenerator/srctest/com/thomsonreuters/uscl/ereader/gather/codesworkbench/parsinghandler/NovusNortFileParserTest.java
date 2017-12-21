package com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

/**
 * Unit test to validate Nort XML parsing.
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568.
 */
public final class NovusNortFileParserTest {
    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();
    private File cwbDir;
    private NovusNortFileParser parser;
    private Map<String, Map<Integer, String>> documentLevelMap;
    private Integer documentLevel = 1;

    @Before
    public void setUp() throws IOException {
        cwbDir = testFiles.newFolder("cwb");
        final Date date = new Date();
        documentLevelMap = new HashMap<>();
        parser = new NovusNortFileParser(date, documentLevel, documentLevelMap);
    }

    @Test
    public void testNoneExistingFile() throws Exception {
        final File nort = new File(cwbDir, "none.xml");
        try {
            parser.parseDocument(nort);
            fail("Test should throw FileNotFoundException");
        } catch (final FileNotFoundException e) {
            //expected exception thrown
        }
    }

    @Test
    public void testInvalidXML() throws UnsupportedEncodingException, IOException, ParserConfigurationException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(nort, "asdasd");
        try {
            parser.parseDocument(nort);
            fail("Test should throw SAXException");
        } catch (final SAXException e) {
            //expected exception thrown
        }
    }

    @Test
    public void testNoRootNode() throws UnsupportedEncodingException, IOException, ParserConfigurationException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N6E4FBD50582711DB99C8C2EC4C695390\" control=\"ADD\">"
                + "<n-end-date>20071202235959</n-end-date></n-relationship></n-load>");
        try {
            parser.parseDocument(nort);
            fail("Test should throw SAXException");
        } catch (final SAXException e) {
            //expected exception thrown
        }
    }

    @Test
    public void testRootFound()
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
                + "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship></n-load>");

        final List<RelationshipNode> roots = parser.parseDocument(nort);
        final RelationshipNode root = roots.get(0);

        assertEquals(0, root.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
        assertEquals(null, root.getDocumentGuid());
        assertEquals("gradehead", root.getNodeType());
        assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
        assertEquals("20050217000000", root.getStartDateStr());
        assertEquals("20970101235959", root.getEndDateStr());
        assertEquals(null, root.getParentNortGuid());
        assertEquals(1.0, root.getRank(), 0.0001);
    }

    @Test
    public void testRootAndChild()
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
                + "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship><n-relationship guid=\"NAEC7C17087FE11D98CEADD8B3DFE30E4\" "
                + "control=\"ADD\"><n-relbase>NAEBD884087FE11D98CEADD8B3DFE30E4</n-relbase>"
                + "<n-reltarget>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-reltarget><n-reltype>TOC</n-reltype>"
                + "<n-relpayload><n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>2031.0</n-rank><n-label>"
                + "<heading>TITLE 42ï¿½PUBLIC HEALTH</heading></n-label><node-type>gradehead</node-type>"
                + "<term>TOCID(NAEBD884087-FE11D98CEAD-D8B3DFE30E4)</term></n-nortpayload></n-relpayload>"
                + "</n-relationship></n-load>");

        final List<RelationshipNode> roots = parser.parseDocument(nort);
        final RelationshipNode root = roots.get(0);

        assertEquals(1, root.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
        assertEquals(null, root.getDocumentGuid());
        assertEquals("gradehead", root.getNodeType());
        assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
        assertEquals("20050217000000", root.getStartDateStr());
        assertEquals("20970101235959", root.getEndDateStr());
        assertEquals(false, root.getPubTaggedHeadingExists());
        assertEquals(null, root.getParentNortGuid());
        assertEquals(1.0, root.getRank(), 0.0001);
    }

    @Test
    public void testLabel()
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><pub-tagged-heading>Y</pub-tagged-heading><n-rank>1.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS <cite.query>other text</cite.query> ending</heading><a>not here</a><section-heading type=\"statute\">"
                + "<section.designator>2308.</section.designator><cite.query w-ref-type=\"VQ\" "
                + "w-normalized-cite=\"NB4CA2840AF-F711D8803AE-0632FEDDFBF\" w-pub-number=\"1000546\">Implied warranties</cite.query>"
                + "</section-heading><citation-heading>15 USCA USCA USCA 2308</citation-heading></n-label>"
                + "<node-type>gradehead</node-type><graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship><n-relationship guid=\"NAEC7C17087FE11D98CEADD8B3DFE30E4\" "
                + "control=\"ADD\"><n-relbase>NAEBD884087FE11D98CEADD8B3DFE30E4</n-relbase>"
                + "<n-reltarget>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-reltarget><n-reltype>TOC</n-reltype>"
                + "<n-relpayload><n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>2031.0</n-rank><n-label>"
                + "<heading>TITLE 42ï¿½PUBLIC HEALTH</heading></n-label><node-type>gradehead</node-type>"
                + "<term>TOCID(NAEBD884087-FE11D98CEAD-D8B3DFE30E4)</term></n-nortpayload></n-relpayload>"
                + "</n-relationship></n-load>");

        final List<RelationshipNode> roots = parser.parseDocument(nort);
        final RelationshipNode root = roots.get(0);

        assertEquals(1, root.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS other text ending", root.getLabel());
        assertEquals(null, root.getDocumentGuid());
        assertEquals("gradehead", root.getNodeType());
        assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
        assertEquals("20050217000000", root.getStartDateStr());
        assertEquals("20970101235959", root.getEndDateStr());
        assertEquals(true, root.getPubTaggedHeadingExists());
        assertEquals(null, root.getParentNortGuid());
        assertEquals(1.0, root.getRank(), 0.0001);
    }

    @Test
    public void testDeletedChildNode()
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
                + "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship><n-relationship guid=\"NAEC7C17087FE11D98CEADD8B3DFE30E4\" "
                + "control=\"ADD\"><n-relbase>NAEBD884087FE11D98CEADD8B3DFE30E4</n-relbase>"
                + "<n-reltarget>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-reltarget><n-reltype>TOC</n-reltype>"
                + "<n-relpayload><n-nortpayload><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>2031.0</n-rank><n-label>"
                + "<heading>TITLE 42ï¿½PUBLIC HEALTH</heading></n-label><node-type>gradehead</node-type>"
                + "<term>TOCID(NAEBD884087-FE11D98CEAD-D8B3DFE30E4)</term><n-view>DELER_StaTX_DBAS</n-view></n-nortpayload>"
                + "</n-relpayload></n-relationship></n-load>");

        final List<RelationshipNode> roots = parser.parseDocument(nort);
        final RelationshipNode root = roots.get(0);

        assertEquals(0, root.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
        assertEquals(null, root.getDocumentGuid());
        assertEquals("gradehead", root.getNodeType());
        assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
        assertEquals("20050217000000", root.getStartDateStr());
        assertEquals("20970101235959", root.getEndDateStr());
        assertEquals(null, root.getParentNortGuid());
        assertEquals(1.0, root.getRank(), 0.0001);
    }

    @Test
    public void testDeletedChildNode2()
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
                + "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship><n-relationship guid=\"NAEC7C17087FE11D98CEADD8B3DFE30E4\" "
                + "control=\"ADD\"><n-relbase>NAEBD884087FE11D98CEADD8B3DFE30E4</n-relbase>"
                + "<n-reltarget>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-reltarget><n-reltype>TOC</n-reltype>"
                + "<n-relpayload><n-nortpayload><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>2031.0</n-rank><n-label>"
                + "<heading>TITLE 42ï¿½PUBLIC HEALTH</heading></n-label><node-type>gradehead</node-type>"
                + "<term>TOCID(NAEBD884087-FE11D98CEAD-D8B3DFE30E4)</term><n-view>TAXDEL</n-view></n-nortpayload>"
                + "</n-relpayload></n-relationship></n-load>");

        final List<RelationshipNode> roots = parser.parseDocument(nort);
        final RelationshipNode root = roots.get(0);

        assertEquals(0, root.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
        assertEquals(null, root.getDocumentGuid());
        assertEquals("gradehead", root.getNodeType());
        assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
        assertEquals("20050217000000", root.getStartDateStr());
        assertEquals("20970101235959", root.getEndDateStr());
        assertEquals(null, root.getParentNortGuid());
        assertEquals(1.0, root.getRank(), 0.0001);
    }

    @Test
    public void testMultipleRoots()
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
                + "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540668\" control=\"ADD\">"
                + "<n-relbase>N11111111111111111111111111111111</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>2.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS 2</heading></n-label><node-type>gradehead</node-type>"
                + "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship></n-load>");

        final List<RelationshipNode> roots = parser.parseDocument(nort);
        assertEquals(2, roots.size());

        final RelationshipNode root = roots.get(0);
        assertEquals(0, root.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
        assertEquals(null, root.getDocumentGuid());
        assertEquals("gradehead", root.getNodeType());
        assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
        assertEquals("20050217000000", root.getStartDateStr());
        assertEquals("20970101235959", root.getEndDateStr());
        assertEquals(null, root.getParentNortGuid());
        assertEquals(1.0, root.getRank(), 0.0001);

        final RelationshipNode root2 = roots.get(1);
        assertEquals(0, root2.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS 2", root2.getLabel());
        assertEquals(null, root2.getDocumentGuid());
        assertEquals("gradehead", root2.getNodeType());
        assertEquals("N11111111111111111111111111111111", root2.getNortGuid());
        assertEquals("20050217000000", root2.getStartDateStr());
        assertEquals("20970101235959", root2.getEndDateStr());
        assertEquals(null, root2.getParentNortGuid());
        assertEquals(2.0, root2.getRank(), 0.0001);
    }

    @Test
    public void testMultipleIdenticalRoots()
        throws Exception {
        //given
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20052017000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
                + "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20052018000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
                + "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship></n-load>");
        //when
        final List<RelationshipNode> nodes = parser.parseDocument(nort);
        //then
        assertEquals(nodes.size(), 2);
        final RelationshipNode currentEffective = nodes.get(0);
        final RelationshipNode futureEffective = nodes.get(1);
        assertEquals(currentEffective.getNortGuid(),
            futureEffective.getNortGuid());
        assertTrue(!currentEffective.equals(futureEffective));
    }

    @Test
    public void testChangeRankRoots()
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>2.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
                + "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540668\" control=\"ADD\">"
                + "<n-relbase>N11111111111111111111111111111111</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
                + "<heading>CODE OF FEDERAL REGULATIONS 2</heading></n-label><node-type>gradehead</node-type>"
                + "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship></n-load>");

        final List<RelationshipNode> roots = parser.parseDocument(nort);
        assertEquals(2, roots.size());

        final RelationshipNode root2 = roots.get(0);
        assertEquals(0, root2.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS 2", root2.getLabel());
        assertEquals(null, root2.getDocumentGuid());
        assertEquals("gradehead", root2.getNodeType());
        assertEquals("N11111111111111111111111111111111", root2.getNortGuid());
        assertEquals("20050217000000", root2.getStartDateStr());
        assertEquals("20970101235959", root2.getEndDateStr());
        assertEquals(null, root2.getParentNortGuid());
        assertEquals(1.0, root2.getRank(), 0.0001);

        final RelationshipNode root = roots.get(1);
        assertEquals(0, root.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
        assertEquals(null, root.getDocumentGuid());
        assertEquals("gradehead", root.getNodeType());
        assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
        assertEquals("20050217000000", root.getStartDateStr());
        assertEquals("20970101235959", root.getEndDateStr());
        assertEquals(null, root.getParentNortGuid());
        assertEquals(2.0, root.getRank(), 0.0001);
    }

    @Test
    public void testDocumentGuidFirstTime()
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final File nort = new File(cwbDir, "none.xml");
        final String docGuid = "N7EB9F7C08D7011D8A785F88B1CCF3D4B";
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-doc-guid>"
                + docGuid
                + "</n-doc-guid>"
                + "<n-rank>1.0</n-rank><n-label><heading>CODE OF FEDERAL REGULATIONS</heading></n-label>"
                + "<node-type>gradehead</node-type><graft-point-flag>Y</graft-point-flag>"
                + "<term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship></n-load>");

        final List<RelationshipNode> roots = parser.parseDocument(nort);
        final RelationshipNode root = roots.get(0);

        assertEquals(0, root.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
        assertEquals(docGuid, root.getDocumentGuid());
        assertEquals("gradehead", root.getNodeType());
        assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
        assertEquals("20050217000000", root.getStartDateStr());
        assertEquals("20970101235959", root.getEndDateStr());
        assertEquals(false, root.getPubTaggedHeadingExists());
        assertEquals(null, root.getParentNortGuid());
        assertEquals(1.0, root.getRank(), 0.0001);
    }

    @Test
    public void testDuplicateDocumentSameLevel()
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final File nort = new File(cwbDir, "none.xml");
        final String docGuid = "N7EB9F7C08D7011D8A785F88B1CCF3D4B";
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-doc-guid>"
                + docGuid
                + "</n-doc-guid>"
                + "<n-rank>1.0</n-rank><n-label><heading>CODE OF FEDERAL REGULATIONS</heading></n-label>"
                + "<node-type>gradehead</node-type><graft-point-flag>Y</graft-point-flag>"
                + "<term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship></n-load>");

        final Map<Integer, String> levelMap = new HashMap<>();
        levelMap.put(documentLevel, docGuid);
        documentLevelMap.put(docGuid, levelMap);
        final List<RelationshipNode> roots = parser.parseDocument(nort);
        final RelationshipNode root = roots.get(0);

        assertEquals(0, root.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
        assertEquals(docGuid, root.getDocumentGuid());
        assertEquals("gradehead", root.getNodeType());
        assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
        assertEquals("20050217000000", root.getStartDateStr());
        assertEquals("20970101235959", root.getEndDateStr());
        assertEquals(false, root.getPubTaggedHeadingExists());
        assertEquals(null, root.getParentNortGuid());
        assertEquals(1.0, root.getRank(), 0.0001);
    }

    @Test
    public void testDuplicateDocumentDifferentLevel()
        throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
        final File nort = new File(cwbDir, "none.xml");
        final String docGuid = "N7EB9F7C08D7011D8A785F88B1CCF3D4B";
        addContentToFile(
            nort,
            "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
                + "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
                + "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
                + "<n-end-date>20970101235959</n-end-date><n-doc-guid>"
                + docGuid
                + "</n-doc-guid>"
                + "<n-rank>1.0</n-rank><n-label><heading>CODE OF FEDERAL REGULATIONS</heading></n-label>"
                + "<node-type>gradehead</node-type><graft-point-flag>Y</graft-point-flag>"
                + "<term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
                + "</n-nortpayload></n-relpayload></n-relationship></n-load>");

        final Map<Integer, String> levelMap = new HashMap<>();
        levelMap.put(documentLevel + 1, docGuid);
        documentLevelMap.put(docGuid, levelMap);
        final List<RelationshipNode> roots = parser.parseDocument(nort);
        final RelationshipNode root = roots.get(0);

        assertEquals(0, root.getChildNodes().size());
        assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
        assertEquals(docGuid + "-" + documentLevel, root.getDocumentGuid());
        assertEquals("gradehead", root.getNodeType());
        assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
        assertEquals("20050217000000", root.getStartDateStr());
        assertEquals("20970101235959", root.getEndDateStr());
        assertEquals(false, root.getPubTaggedHeadingExists());
        assertEquals(null, root.getParentNortGuid());
        assertEquals(1.0, root.getRank(), 0.0001);
    }

    private void addContentToFile(final File file, final String text) {
        try (FileWriter fileOut = new FileWriter(file)) {
            fileOut.write(text);
            fileOut.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
