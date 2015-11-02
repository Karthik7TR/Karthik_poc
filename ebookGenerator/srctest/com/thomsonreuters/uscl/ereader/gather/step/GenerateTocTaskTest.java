package com.thomsonreuters.uscl.ereader.gather.step;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenerateTocTaskTest {
	private GenerateTocTask generateTocTask;
	List<String> splitGuidList = null;
	List<String> dupGuidList = null;

	@Before
	public void setUp() {
		this.generateTocTask = new GenerateTocTask();
	}

	@Test
	public void testNoDuplicateToc() {
		boolean thrown = false;
		try {
			splitGuidList = new ArrayList<String>();
			splitGuidList.add("abcd");
			generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
		} catch (Exception ex) {
			thrown = true;
		}
		Assert.assertEquals(false,thrown);

	}
	
	@Test
	public void testDuplicateToc() {
		boolean thrown = false;
		try {
			splitGuidList = new ArrayList<String>();
			splitGuidList.add("abcd");
			dupGuidList = new ArrayList<String>();
			dupGuidList.add("abcd");
			generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
		} catch (Exception ex) {
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void testManualSplitDuplcateToc() {
		boolean thrown = false;
		try {
			splitGuidList = new ArrayList<String>();
			splitGuidList.add("abcd");
			dupGuidList = new ArrayList<String>();
			dupGuidList.add("1234");
			generateTocTask.duplicateTocCheck(splitGuidList, dupGuidList);
		} catch (Exception ex) {
			thrown = true;
		}
		Assert.assertEquals(false,thrown);
	}
	
	@Test
	public void testAutoSplitDuplcateToc() {
		boolean thrown = false;
		try {
			dupGuidList = new ArrayList<String>();
			dupGuidList.add("1234");
			generateTocTask.duplicateTocCheck(null, dupGuidList);
		} catch (Exception ex) {
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	@Test
	public void testAutoSplitNoDuplcateToc() {
		boolean thrown = false;
		try {
			generateTocTask.duplicateTocCheck(null, null);
		} catch (Exception ex) {
			thrown = true;
		}
		Assert.assertEquals(false,thrown);
	}

}

