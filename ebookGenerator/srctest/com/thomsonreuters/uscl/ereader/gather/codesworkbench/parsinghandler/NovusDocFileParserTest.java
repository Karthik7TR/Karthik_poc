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
import java.util.HashMap;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;

/**
 * Unit test to validate Novus Doc XML parsing.
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568.
 */
public class NovusDocFileParserTest {
	@Rule
	public TemporaryFolder testFiles = new TemporaryFolder();
	private File cwbDir;
	private NovusDocFileParser parser;
	private GatherResponse gatherResponse;
	private HashMap<String, Integer> docGuidsMap;
	
	private Integer numberOfDocuments = 0;
	
	@Before
	public void setUp() throws IOException {
		cwbDir = testFiles.newFolder("cwb");
		docGuidsMap = new HashMap<String, Integer>();
		docGuidsMap.put("1", 1);
		docGuidsMap.put("2", 1);
		numberOfDocuments = docGuidsMap.size();
		gatherResponse = new GatherResponse();
		parser = new NovusDocFileParser("collectionName", docGuidsMap, cwbDir, cwbDir, gatherResponse, 0);
	}
	
	@Test
	public void testNoneExistingFile() {
		File nort = new File(cwbDir, "none.xml");
		
		try {
			parser.parseXML(nort);
			fail("Test should throw GatherException: File Not Found error");
		} catch (GatherException e) {
			//expected exception thrown
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInvalidXML() {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "asdasd");
		try {
			parser.parseXML(nort);
			fail("Test should throw GatherException");
		} catch (GatherException e) {
			//expected exception thrown
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDocFound() throws GatherException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load>"
				+ "<n-document guid=\"1\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
				+ "<n-document guid=\"2\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
				+ "</n-load>");
		
		parser.parseXML(nort);
		
		Assert.assertEquals(numberOfDocuments.intValue(), gatherResponse.getDocCount());
	}
	
	@Test
	public void testDocFoundWithDuplicates() throws GatherException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load>"
				+ "<n-document guid=\"1\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
				+ "<n-document guid=\"2\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
				+ "<n-document guid=\"1\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
				+ "<n-document guid=\"2\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
				+ "</n-load>");
		
		parser.parseXML(nort);
		
		Assert.assertEquals(numberOfDocuments.intValue(), gatherResponse.getDocCount());
	}
	
	@Test
	public void testDocFoundWithExtra() throws GatherException {
		File nort = new File(cwbDir, "none.xml");
		addContentToFile(nort, "<n-load>"
				+ "<n-document guid=\"1\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
				+ "<n-document guid=\"2\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
				+ "<n-document guid=\"3\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
				+ "<n-document guid=\"4\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
				+ "</n-load>");
		
		parser.parseXML(nort);
		
		Assert.assertEquals(numberOfDocuments.intValue(), gatherResponse.getDocCount());
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
