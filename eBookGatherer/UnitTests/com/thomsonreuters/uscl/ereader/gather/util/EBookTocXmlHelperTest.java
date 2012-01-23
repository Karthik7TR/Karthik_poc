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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.util.EBookTocXmlHelper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class EBookTocXmlHelperTest {

	@Autowired
	private EBookTocXmlHelper eBookTocXmlHelper;
	/**
	 * create dummy 
	 */
	@Test
	public void testGetTocDataFromNovus() {
		
		//TODO: change this file path to point diffrent location other than project root.
		//TODO: need set up test context file in right way.
		File tocFilePath = new File("ebook.xml");
		String tocContent = null;
		List<EBookToc> ebookTocList = new ArrayList<EBookToc>();
		EBookToc eBookToc_1 = new EBookToc();
		eBookToc_1.setName("<header>Test &quot; Toc § 1</header>");
		eBookToc_1.setGuid("Test Guid 1");
		eBookToc_1.setParentGuid("Test Toc ParentGuid 1");
		eBookToc_1.setMetadata("<md.term>  &lt; Test &amp; Toc Metadata 1 &gt; </md.term>");
		ebookTocList.add(eBookToc_1);

		EBookToc eBookToc_2 = new EBookToc();
		eBookToc_2.setName("<heading><cite query='attr'/>Test Toc 2 Hawai&apos;i <bold>50</bold><eos/></heading>");
		eBookToc_2.setGuid("Test Guid 2");
		eBookToc_2.setParentGuid("Test Toc ParentGuid 2");
		eBookToc_2.setMetadata("Test Toc Metadata 2");
		ebookTocList.add(eBookToc_2);

		List<EBookToc> ebookTocInnerList = new ArrayList<EBookToc>();

		EBookToc eBookToc_Inner = new EBookToc();
		eBookToc_Inner.setName("<heading><cite query='attr'/>Test Toc 3 inner</heading>");
		eBookToc_Inner.setGuid("Test Guid 3 inner ");
		eBookToc_Inner.setParentGuid("Test Toc ParentGuid 3 inner");
		eBookToc_Inner.setMetadata("Test Toc Metadata 3 inner");
		ebookTocInnerList.add(eBookToc_Inner);
		
		EBookToc eBookToc_3 = new EBookToc();
		eBookToc_3.setName("Test Toc 3");
		eBookToc_3.setGuid("Test Guid 3");
		eBookToc_3.setParentGuid("Test Toc ParentGuid 3");
		eBookToc_3.setMetadata("Test Toc Metadata 3");
		eBookToc_3.setChildren(ebookTocInnerList);
		eBookToc_3.setChildrenCount(1);
		ebookTocList.add(eBookToc_3);

		try {
			eBookTocXmlHelper.processTocListToCreateEBookTOC(ebookTocList,
					tocFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception being caught");
		}
		
			tocContent = readFileAsString(tocFilePath);

		System.out.println("tocContent =" + tocContent);
		assertTrue(tocContent != null);
		
		StringBuffer expectedTocContent = new StringBuffer(1000);

		expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		expectedTocContent.append("<EBook>\r\n");
		expectedTocContent.append("    <EBookToc>\r\n");
		expectedTocContent.append("        <Name>Test &quot; Toc § 1</Name>\r\n");
		expectedTocContent.append("        <Guid>Test Guid 1</Guid>\r\n");
		expectedTocContent.append("        <ParentGuid>Test Toc ParentGuid 1</ParentGuid>\r\n");
		expectedTocContent.append("        <DocumentGuid/>\r\n");
		expectedTocContent.append("        <Metadata>  &lt; Test &amp; Toc Metadata 1 &gt; </Metadata>\r\n");
		expectedTocContent.append("    </EBookToc>\r\n");
		expectedTocContent.append("    <EBookToc>\r\n");
		expectedTocContent.append("        <Name>Test Toc 2 Hawai&apos;i 50</Name>\r\n");
		expectedTocContent.append("        <Guid>Test Guid 2</Guid>\r\n");
		expectedTocContent.append("        <ParentGuid>Test Toc ParentGuid 2</ParentGuid>\r\n");
		expectedTocContent.append("        <DocumentGuid/>\r\n");
		expectedTocContent.append("        <Metadata>Test Toc Metadata 2</Metadata>\r\n");
		expectedTocContent.append("    </EBookToc>\r\n");
		expectedTocContent.append("    <EBookToc>\r\n");
		expectedTocContent.append("        <Name>Test Toc 3</Name>\r\n");
		expectedTocContent.append("        <Guid>Test Guid 3</Guid>\r\n");
		expectedTocContent.append("        <ParentGuid>Test Toc ParentGuid 3</ParentGuid>\r\n");
		expectedTocContent.append("        <DocumentGuid/>\r\n");
		expectedTocContent.append("        <Metadata>Test Toc Metadata 3</Metadata>\r\n");
		expectedTocContent.append("        <EBookToc>\r\n");
		expectedTocContent.append("            <Name>Test Toc 3 inner</Name>\r\n");
		expectedTocContent.append("            <Guid>Test Guid 3 inner </Guid>\r\n");
		expectedTocContent.append("            <ParentGuid>Test Toc ParentGuid 3 inner</ParentGuid>\r\n");
		expectedTocContent.append("            <DocumentGuid/>\r\n");
		expectedTocContent.append("            <Metadata>Test Toc Metadata 3 inner</Metadata>\r\n");
		expectedTocContent.append("        </EBookToc>\r\n");
		expectedTocContent.append("    </EBookToc>\r\n");
		expectedTocContent.append("</EBook>\r\n");
		System.out.println("expectedTocContent =" + expectedTocContent.toString());


		assertTrue(tocContent.equals(expectedTocContent.toString()));


		//TODO: need to verify toc xml structure. by parsing xml.
	}
	
	/**
	 * Reads specified file and returns in string format.this is a helper method.
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
//	private static String readFileAsString(File filePath)
//			throws java.io.IOException {
//		StringBuffer fileData = new StringBuffer(1000);
//		BufferedReader reader = new BufferedReader(new FileReader(filePath));
//		char[] buf = new char[1024];
//		int numRead = 0;
//		while ((numRead = reader.read(buf)) != -1) {
//			String readData = String.valueOf(buf, 0, numRead);
//			fileData.append(readData);
//			buf = new char[1024];
//		}
//		reader.close();
//		return fileData.toString();
//	}
	private static String readFileAsString(File filePath) {
	    StringBuffer buffer = new StringBuffer();
	    try {
	        FileInputStream fis =
	            new FileInputStream(filePath);
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
	    }
	}


}
