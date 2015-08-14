package com.thomsonreuters.uscl.ereader.deliver.step;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;

public class ArchiveBookTest {
	
	private ArchiveBook archiveBook;
	private String currentVersion;
	private SplitNodeInfo splitNodeInfo;
	@Before
	public void setUp() throws Exception
	{
		archiveBook = new ArchiveBook();
		currentVersion = "V1";
		splitNodeInfo = new SplitNodeInfo();
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(new Long(1));
		splitNodeInfo.setBookDefinition(bookDefinition);
		splitNodeInfo.setBookVersionSubmitted("V1");
		splitNodeInfo.setSpitBookTitle("splitBookTitle");
		splitNodeInfo.setSplitNodeGuid("splitNodeGuid");
	}
	
	@After
	public void tearDown() throws Exception
	{
		
	}
	
	@Test
	public void testEmptyPersistedList(){
		List<SplitNodeInfo> currentsplitNodeList = new ArrayList<SplitNodeInfo>();
		List<SplitNodeInfo> persistedSplitNodes = new ArrayList<SplitNodeInfo>();
		boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);
		assertTrue(!actual);
	}
	
	@Test
	public void testEqualLists(){
		List<SplitNodeInfo> currentsplitNodeList = new ArrayList<SplitNodeInfo>();
		currentsplitNodeList.add(splitNodeInfo);
		List<SplitNodeInfo> persistedSplitNodes = new ArrayList<SplitNodeInfo>();
		persistedSplitNodes.add(splitNodeInfo);
		boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);		
		assertTrue(actual);
	}
	
	@Test
	public void testSyncList1(){
		List<SplitNodeInfo> currentsplitNodeList = new ArrayList<SplitNodeInfo>();
		currentsplitNodeList.add(splitNodeInfo);
		List<SplitNodeInfo> persistedSplitNodes = new ArrayList<SplitNodeInfo>();
		boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);		
		assertTrue(!actual);
	}
	
	@Test
	public void testSyncList2(){
		List<SplitNodeInfo> currentsplitNodeList = new ArrayList<SplitNodeInfo>();
		List<SplitNodeInfo> persistedSplitNodes = null;
		boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);		
		assertTrue(!actual);
	}
	
	@Test
	public void testSyncList3(){
		List<SplitNodeInfo> currentsplitNodeList = new ArrayList<SplitNodeInfo>();
		List<SplitNodeInfo> persistedSplitNodes = new ArrayList<SplitNodeInfo>();
		splitNodeInfo.setBookVersionSubmitted("V0");
		persistedSplitNodes.add(splitNodeInfo);
		boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);		
		assertTrue(!actual);
	}
	
	@Test
	public void testSyncList6(){
		List<SplitNodeInfo> currentsplitNodeList = new ArrayList<SplitNodeInfo>();
		List<SplitNodeInfo> persistedSplitNodes = new ArrayList<SplitNodeInfo>();
		SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(new Long(1));
		splitNodeInfo2.setBookDefinition(bookDefinition);
		splitNodeInfo2.setBookVersionSubmitted("V1");
		splitNodeInfo2.setSpitBookTitle("splitBookTitle");
		splitNodeInfo2.setSplitNodeGuid("splitNodeGuid");
		boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);		
		assertTrue(!actual);
	}
		
	
	
	/**
	 * If currentNodes has less Rows than previousNodes and versions are same
	 * then the new list should get only currentNodes
	 */
	@Test
	public void testSyncList4(){
		List<SplitNodeInfo> currentsplitNodeList = new ArrayList<SplitNodeInfo>();
		List<SplitNodeInfo> persistedSplitNodes = new ArrayList<SplitNodeInfo>();
		persistedSplitNodes.add(splitNodeInfo);
		SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(new Long(1));
		splitNodeInfo2.setBookDefinition(bookDefinition);
		splitNodeInfo2.setBookVersionSubmitted("V1");
		splitNodeInfo2.setSpitBookTitle("splitBookTitle2");
		splitNodeInfo2.setSplitNodeGuid("splitNodeGuid");
		persistedSplitNodes.add(splitNodeInfo2);
		boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);		
		assertTrue(actual);
	}
	
	/**
	 * If versions are different
	 * then the new list should get currentNodes and previousNodes
	 */
	@Test
	public void testSyncList5(){
		List<SplitNodeInfo> currentsplitNodeList = new ArrayList<SplitNodeInfo>();
		currentsplitNodeList.add(splitNodeInfo);
		List<SplitNodeInfo> persistedSplitNodes = new ArrayList<SplitNodeInfo>();
		SplitNodeInfo splitNodeInfo2 = new SplitNodeInfo();
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setEbookDefinitionId(new Long(1));
		splitNodeInfo2.setBookDefinition(bookDefinition);
		splitNodeInfo2.setBookVersionSubmitted("V0");
		splitNodeInfo2.setSpitBookTitle("splitBookTitle");
		splitNodeInfo2.setSplitNodeGuid("splitNodeGuid");
		persistedSplitNodes.add(splitNodeInfo2);
		boolean actual = archiveBook.hasChanged(persistedSplitNodes, currentsplitNodeList, currentVersion);		
		assertTrue(!actual);
	}

}
