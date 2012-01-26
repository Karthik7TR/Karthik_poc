/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.util;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;

public class EBookTocXmlHelperTest {
	private static Logger log = Logger.getLogger(EBookTocXmlHelperTest.class);
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	/**
	 * create dummy 
	 */
	@Test
	public void testGetTocDataFromNovus() {
		
		//TODO: change this file path to point diffrent location other than project root.
		//TODO: need set up test context file in right way.
		File tocFilePath = new File(temporaryFolder.getRoot(), "ebook.xml");
		String tocContent = null;
		List<EBookToc> ebookTocList = new ArrayList<EBookToc>();
		EBookToc eBookToc_1 = new EBookToc();
//		eBookToc_1.setName("<header>Test &quot; Toc 1</header>");
		eBookToc_1.setName("<header>Test &quot; Toc § 1</header>");
		ebookTocList.add(eBookToc_1);

		EBookToc eBookToc_2 = new EBookToc();
		eBookToc_2.setName("<heading><cite query='attr'/>Test Toc 2 Hawai&apos;i <bold>50</bold><eos/></heading>");
		ebookTocList.add(eBookToc_2);

		List<EBookToc> ebookTocInnerList = new ArrayList<EBookToc>();

		EBookToc eBookToc_Inner = new EBookToc();
		eBookToc_Inner.setName("<heading><cite query='attr'/>Test Toc 3 inner</heading>");
		eBookToc_Inner.setDocGuid("docGuid");
		ebookTocInnerList.add(eBookToc_Inner);
		
		EBookToc eBookToc_3 = new EBookToc();
		eBookToc_3.setName("&lt; Test Toc &amp; 3 &gt;");
		eBookToc_3.setChildren(ebookTocInnerList);
		eBookToc_3.setChildrenCount(1);
		ebookTocList.add(eBookToc_3);

		try {
			EBookTocXmlHelper.processTocListToCreateEBookTOC(ebookTocList,
					tocFilePath);
			tocContent = readFileAsString(tocFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug("Exception being caught");
		}
		
		log.debug("tocContent =" + tocContent);
		assertTrue(tocContent != null);
		
		StringBuffer expectedTocContent = new StringBuffer(1000);

		expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		expectedTocContent.append("<EBook>\r\n");
		expectedTocContent.append(" <EBookToc>\r\n");
		expectedTocContent.append("  <Name>Test &quot; Toc § 1</Name>\r\n");
		expectedTocContent.append(" </EBookToc>\r\n");
		expectedTocContent.append(" <EBookToc>\r\n");
		expectedTocContent.append("  <Name>Test Toc 2 Hawai&apos;i 50</Name>\r\n");
		expectedTocContent.append(" </EBookToc>\r\n");
		expectedTocContent.append(" <EBookToc>\r\n");
		expectedTocContent.append("  <Name>&lt; Test Toc &amp; 3 &gt;</Name>\r\n");
		expectedTocContent.append("  <EBookToc>\r\n");
		expectedTocContent.append("   <Name>Test Toc 3 inner</Name>\r\n");
		expectedTocContent.append("   <DocumentGuid>docGuid</DocumentGuid>\r\n");
		expectedTocContent.append("  </EBookToc>\r\n");
		expectedTocContent.append(" </EBookToc>\r\n");
		expectedTocContent.append("</EBook>\r\n");
		log.debug("expectedTocContent =" + expectedTocContent.toString());

		Assert.assertEquals(expectedTocContent.toString(), tocContent);
	}
	
	/**
	 * Reads specified file and returns in string format.this is a helper method.
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	private static String readFileAsString(File filePath) throws Exception {
	    StringBuffer buffer = new StringBuffer();
	    FileInputStream fis =
	    		new FileInputStream(filePath);
	    try {
	        InputStreamReader isr =
	            new InputStreamReader(fis, "UTF8");
	        Reader in = new BufferedReader(isr);
	        int ch;
	        while ((ch = in.read()) > -1) {
	                buffer.append((char)ch);
	        }
	        in.close();
	        return buffer.toString();
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	    	fis.close();
	    }
	}


}
