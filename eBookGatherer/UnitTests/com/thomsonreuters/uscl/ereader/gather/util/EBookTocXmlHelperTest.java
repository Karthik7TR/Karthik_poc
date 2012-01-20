/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.util;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;

public class EBookTocXmlHelperTest {

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
		eBookToc_1.setName("Test Toc 1");
		eBookToc_1.setGuid("Test Guid 1");
		eBookToc_1.setParentGuid("Test Toc ParentGuid 1");
		eBookToc_1.setMetadata("Test Toc Metadata 1");
		ebookTocList.add(eBookToc_1);

		EBookToc eBookToc_2 = new EBookToc();
		eBookToc_2.setName("Test Toc 2");
		eBookToc_2.setGuid("Test Guid 2");
		eBookToc_2.setParentGuid("Test Toc ParentGuid 2");
		eBookToc_2.setMetadata("Test Toc Metadata 2");
		ebookTocList.add(eBookToc_2);

		List<EBookToc> ebookTocInnerList = new ArrayList<EBookToc>();

		EBookToc eBookToc_Inner = new EBookToc();
		eBookToc_Inner.setName("Test Toc 3 inner");
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
			EBookTocXmlHelper.processTocListToCreateEBookTOC(ebookTocList,
					tocFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception being caught");
		}
		try {
			tocContent = readFileAsString(tocFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("tocContent =" + tocContent);
		assertTrue(tocContent != null);

		//TODO: need to verify toc xml structure. by parsing xml.
	}
	
	/**
	 * Reads specified file and returns in string format.this is a helper method.
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	private static String readFileAsString(File filePath)
			throws java.io.IOException {
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

}
