/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

import com.thomsonreuters.uscl.ereader.gather.codesworkbench.domain.RelationshipNode;
import com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler.NovusNortFileParser;

/**
 * Unit test to validate Nort XML parsing.
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568.
 */
public class NovusNortFileParserTest {
	@Rule
	public TemporaryFolder testFiles = new TemporaryFolder();
	private File cwbDir;
	private NovusNortFileParser parser;
	
	@Before
	public void setUp() throws IOException {
		cwbDir = testFiles.newFolder("cwb");
		Date date = new Date();
		parser = new NovusNortFileParser(date);
	}
	
	@Test
	public void testNoneExistingFile() throws Exception {
		File nort = new File(cwbDir, "none.xml");
		try {
			parser.parseDocument(nort);
			fail("Test should throw FileNotFoundException");
		} catch (FileNotFoundException e) {
			//expected exception thrown
		}
	}
	
	@Test
	public void testInvalidXML() throws UnsupportedEncodingException, IOException, ParserConfigurationException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "asdasd");
		try {
			parser.parseDocument(nort);
			fail("Test should throw SAXException");
		} catch (SAXException e) {
			//expected exception thrown
		}
	}
	
	@Test
	public void testNoRootNode() throws UnsupportedEncodingException, IOException, ParserConfigurationException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load><n-relationship guid=\"N6E4FBD50582711DB99C8C2EC4C695390\" control=\"ADD\">"
				+ "<n-end-date>20071202235959</n-end-date></n-relationship></n-load>");
		try {
			parser.parseDocument(nort);
			fail("Test should throw SAXException");
		} catch (SAXException e) {
			//expected exception thrown
		}
	}
	
	@Test
	public void testRootFound() throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
				+ "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
				+ "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
				+ "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
				+ "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
				+ "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
				+ "</n-nortpayload></n-relpayload></n-relationship></n-load>");
		
		List<RelationshipNode> roots = parser.parseDocument(nort);
		RelationshipNode root = roots.get(0);
		
		assertEquals(0, root.getChildNodes().size());
		assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
		assertEquals(null, root.getDocumentGuid());
		assertEquals("gradehead", root.getNodeType());
		assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
		assertEquals("20050217000000", root.getStartDateStr());
		assertEquals("20970101235959", root.getEndDateStr());
		assertEquals(null , root.getParentNortGuid());
		assertEquals(1.0 , root.getRank(), 0.0001);

	}
	
	@Test
	public void testRootAndChild() throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
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
				+ "<heading>TITLE 42—PUBLIC HEALTH</heading></n-label><node-type>gradehead</node-type>"
				+ "<term>TOCID(NAEBD884087-FE11D98CEAD-D8B3DFE30E4)</term></n-nortpayload></n-relpayload>"
				+ "</n-relationship></n-load>");
		
		List<RelationshipNode> roots = parser.parseDocument(nort);
		RelationshipNode root = roots.get(0);
		
		assertEquals(1, root.getChildNodes().size());
		assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
		assertEquals(null, root.getDocumentGuid());
		assertEquals("gradehead", root.getNodeType());
		assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
		assertEquals("20050217000000", root.getStartDateStr());
		assertEquals("20970101235959", root.getEndDateStr());
		assertEquals(false, root.getPubTaggedHeadingExists());
		assertEquals(null , root.getParentNortGuid());
		assertEquals(1.0 , root.getRank(), 0.0001);

	}
	
	@Test
	public void testLabel() throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
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
				+ "<heading>TITLE 42—PUBLIC HEALTH</heading></n-label><node-type>gradehead</node-type>"
				+ "<term>TOCID(NAEBD884087-FE11D98CEAD-D8B3DFE30E4)</term></n-nortpayload></n-relpayload>"
				+ "</n-relationship></n-load>");
		
		List<RelationshipNode> roots = parser.parseDocument(nort);
		RelationshipNode root = roots.get(0);
		
		assertEquals(1, root.getChildNodes().size());
		assertEquals("CODE OF FEDERAL REGULATIONS other text ending", root.getLabel());
		assertEquals(null, root.getDocumentGuid());
		assertEquals("gradehead", root.getNodeType());
		assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
		assertEquals("20050217000000", root.getStartDateStr());
		assertEquals("20970101235959", root.getEndDateStr());
		assertEquals(true, root.getPubTaggedHeadingExists());
		assertEquals(null , root.getParentNortGuid());
		assertEquals(1.0 , root.getRank(), 0.0001);

	}
	
	@Test
	public void testDeletedChildNode() throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
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
				+ "<heading>TITLE 42—PUBLIC HEALTH</heading></n-label><node-type>gradehead</node-type>"
				+ "<term>TOCID(NAEBD884087-FE11D98CEAD-D8B3DFE30E4)</term><n-view>DELER_StaTX_DBAS</n-view></n-nortpayload>"
				+ "</n-relpayload></n-relationship></n-load>");
		
		List<RelationshipNode> roots = parser.parseDocument(nort);
		RelationshipNode root = roots.get(0);
		
		assertEquals(0, root.getChildNodes().size());
		assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
		assertEquals(null, root.getDocumentGuid());
		assertEquals("gradehead", root.getNodeType());
		assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
		assertEquals("20050217000000", root.getStartDateStr());
		assertEquals("20970101235959", root.getEndDateStr());
		assertEquals(null , root.getParentNortGuid());
		assertEquals(1.0 , root.getRank(), 0.0001);

	}
	
	@Test
	public void testDeletedChildNode2() throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
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
				+ "<heading>TITLE 42—PUBLIC HEALTH</heading></n-label><node-type>gradehead</node-type>"
				+ "<term>TOCID(NAEBD884087-FE11D98CEAD-D8B3DFE30E4)</term><n-view>TAXDEL</n-view></n-nortpayload>"
				+ "</n-relpayload></n-relationship></n-load>");
		
		List<RelationshipNode> roots = parser.parseDocument(nort);
		RelationshipNode root = roots.get(0);
		
		assertEquals(0, root.getChildNodes().size());
		assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
		assertEquals(null, root.getDocumentGuid());
		assertEquals("gradehead", root.getNodeType());
		assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
		assertEquals("20050217000000", root.getStartDateStr());
		assertEquals("20970101235959", root.getEndDateStr());
		assertEquals(null , root.getParentNortGuid());
		assertEquals(1.0 , root.getRank(), 0.0001);

	}
	
	@Test
	public void testMultipleRoots() throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
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
		
		List<RelationshipNode> roots = parser.parseDocument(nort);
		assertEquals(2, roots.size());
		
		RelationshipNode root = roots.get(0);
		assertEquals(0, root.getChildNodes().size());
		assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
		assertEquals(null, root.getDocumentGuid());
		assertEquals("gradehead", root.getNodeType());
		assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
		assertEquals("20050217000000", root.getStartDateStr());
		assertEquals("20970101235959", root.getEndDateStr());
		assertEquals(null , root.getParentNortGuid());
		assertEquals(1.0 , root.getRank(), 0.0001);
		
		RelationshipNode root2 = roots.get(1);
		assertEquals(0, root2.getChildNodes().size());
		assertEquals("CODE OF FEDERAL REGULATIONS 2", root2.getLabel());
		assertEquals(null, root2.getDocumentGuid());
		assertEquals("gradehead", root2.getNodeType());
		assertEquals("N11111111111111111111111111111111", root2.getNortGuid());
		assertEquals("20050217000000", root2.getStartDateStr());
		assertEquals("20970101235959", root2.getEndDateStr());
		assertEquals(null , root2.getParentNortGuid());
		assertEquals(2.0 , root2.getRank(), 0.0001);

	}
	
	@Test
	public void testMultipleIdenticalRoots() throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
				+ "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
				+ "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
				+ "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
				+ "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
				+ "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
				+ "</n-nortpayload></n-relpayload></n-relationship><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
				+ "<n-relbase>N156AF7107C8011D9BF2BB0A94FBB0D8D</n-relbase><n-reltype>TOC</n-reltype><n-relpayload>"
				+ "<n-nortpayload><n-view>WlAdcCf</n-view><n-start-date>20050217000000</n-start-date>"
				+ "<n-end-date>20970101235959</n-end-date><n-rank>1.0</n-rank><n-label>"
				+ "<heading>CODE OF FEDERAL REGULATIONS</heading></n-label><node-type>gradehead</node-type>"
				+ "<graft-point-flag>Y</graft-point-flag><term>TOCID(N156AF7107C-8011D9BF2BB-0A94FBB0D8D)</term>"
				+ "</n-nortpayload></n-relpayload></n-relationship></n-load>");
		
		try {
			parser.parseDocument(nort);
			fail("Test should throw SAXException");
		} catch (SAXException e) {
			//expected exception thrown
			String expectedError = "Duplicate NORT node(s) found: N156AF7107C8011D9BF2BB0A94FBB0D8D";
			if(!e.getMessage().contains(expectedError)) {
				fail("Wrong failure message");
			}
		}
	}
	
	@Test
	public void testChangeRankRoots() throws UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load><n-relationship guid=\"N932693C2A30011DE8D7A0023AE540669\" control=\"ADD\">"
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
		
		List<RelationshipNode> roots = parser.parseDocument(nort);
		assertEquals(2, roots.size());
		
		RelationshipNode root2 = roots.get(0);
		assertEquals(0, root2.getChildNodes().size());
		assertEquals("CODE OF FEDERAL REGULATIONS 2", root2.getLabel());
		assertEquals(null, root2.getDocumentGuid());
		assertEquals("gradehead", root2.getNodeType());
		assertEquals("N11111111111111111111111111111111", root2.getNortGuid());
		assertEquals("20050217000000", root2.getStartDateStr());
		assertEquals("20970101235959", root2.getEndDateStr());
		assertEquals(null , root2.getParentNortGuid());
		assertEquals(1.0 , root2.getRank(), 0.0001);
		
		RelationshipNode root = roots.get(1);
		assertEquals(0, root.getChildNodes().size());
		assertEquals("CODE OF FEDERAL REGULATIONS", root.getLabel());
		assertEquals(null, root.getDocumentGuid());
		assertEquals("gradehead", root.getNodeType());
		assertEquals("N156AF7107C8011D9BF2BB0A94FBB0D8D", root.getNortGuid());
		assertEquals("20050217000000", root.getStartDateStr());
		assertEquals("20970101235959", root.getEndDateStr());
		assertEquals(null , root.getParentNortGuid());
		assertEquals(2.0 , root.getRank(), 0.0001);
	}
	
	private void addContentToFile(File file, String text) {
		try {
			FileWriter fileOut = new FileWriter(file);
			fileOut.write(text);
			fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
