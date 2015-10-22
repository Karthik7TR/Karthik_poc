package com.thomsonreuters.uscl.ereader.gather.services;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.TOC;
import com.westgroup.novus.productapi.TOCNode;


public class TocServiceTest {
	private static final String COLLECTION_NAME = "w_an_rcc_cajur_toc";
	private static final String TOC_GUID = "I7b3ec600675a11da90ebf04471783734";
	private static final boolean IS_FINAL_STAGE = true;
	private NovusFactory mockNovusFactory;
	private Novus mockNovus;
	private TOC mockToc;
	private TOCNode mockTocRootNode;
	private TOCNode mockTocNode;
	private TocServiceImpl tocService;
	private File tocDir = null;
	private static Logger LOG = Logger.getLogger(TocServiceTest.class);
	private NovusUtility mockNovusUtility;
	private ExcludeDocument mockExcludeDocument;
	private GatherResponse gatherResponse;
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void setUp() throws Exception {
		this.mockNovusFactory = EasyMock.createMock(NovusFactory.class);
		this.mockNovus = EasyMock.createMock(Novus.class);
		this.mockToc = EasyMock.createMock(TOC.class);
		this.mockTocNode = EasyMock.createMock(TOCNode.class);
//		mockTocRootNode = new TOCNode[]{mockTocNode};
		this.mockTocRootNode = EasyMock.createMock(TOCNode.class);
		this.mockNovusUtility = EasyMock.createMock(NovusUtility.class);
		this.mockExcludeDocument = EasyMock.createMock(ExcludeDocument.class);

		// The object under test
		this.tocService = new TocServiceImpl();
		tocService.setNovusFactory(mockNovusFactory);
		tocService.setNovusUtility(mockNovusUtility);		
		tocDir = temporaryFolder.newFolder("junit_toc");
	}
	
	@Test
	public void testGetTocDataFromNovus() throws Exception {

		File tocFile = new File(tocDir, "TOC"+COLLECTION_NAME+TOC_GUID+EBConstants.XML_FILE_EXTENSION);

		// Record expected calls
		EasyMock.expect(mockNovusFactory.createNovus(IS_FINAL_STAGE)).andReturn(mockNovus);
		EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("3").anyTimes();		
		EasyMock.expect(mockNovus.getTOC()).andReturn(mockToc);
		mockToc.setCollection(COLLECTION_NAME);
		mockToc.setShowChildrenCount(true);
		TOCNode[] children = new TOCNode[]{};

		mockTocRootNode = mockTocNode;
		
		children = getChildNodes(5, 'a', 999).toArray(new TOCNode[]{});
//		EasyMock.expect(mockToc.getRootNodes()).andReturn(mockTocRootNode);
		EasyMock.expect(mockToc.getNode(TOC_GUID)).andReturn(mockTocRootNode);
		EasyMock.expect(mockTocNode.getName()).andReturn(" &lt; Root &amp;  &quot; Node&apos;s &gt; ").times(2); 
		EasyMock.expect(mockTocNode.getDocGuid()).andReturn(null).anyTimes();
		EasyMock.expect(mockTocNode.getGuid()).andReturn("tocGuid").anyTimes();
		EasyMock.expect(mockTocNode.getChildrenCount()).andReturn(5).anyTimes();
		EasyMock.expect(mockTocNode.getChildren()).andReturn(children).anyTimes();
	
		
		//EasyMock.expect(mockToc.getRootNodes()).andReturn(tocNodes);
		mockNovus.shutdownMQ();
		tocDir.mkdirs();

		// Set up for replay
		EasyMock.replay(mockNovusFactory);
		EasyMock.replay(mockNovus);
		EasyMock.replay(mockToc);
		EasyMock.replay(mockTocNode);
		EasyMock.replay(mockNovusUtility);		

		try {
			tocService.findTableOfContents(TOC_GUID, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		finally {
			// Temporary file will clean up after itself.
		}
		
		String tocFileContents = readFileAsString(tocFile);
		LOG.debug("tocFileContents =" + tocFileContents);
		assertTrue(tocFileContents != null);
		
		StringBuffer expectedTocContent = new StringBuffer(1000);

		expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		expectedTocContent.append("<EBook>\r\n");
		expectedTocContent.append("<EBookToc><Name> &lt; Root &amp;  &quot; Node&apos;s &gt; </Name><Guid>tocGuid</Guid>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 0a</Name><Guid>TOC_UUID_0a</Guid><DocumentGuid>UUID_0a</DocumentGuid>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 0b</Name><Guid>TOC_UUID_0b</Guid><DocumentGuid>UUID_0b</DocumentGuid></EBookToc>\r\n");
		expectedTocContent.append("</EBookToc>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 1a</Name><Guid>TOC_UUID_1a</Guid><DocumentGuid>UUID_1a</DocumentGuid></EBookToc>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 2a</Name><Guid>TOC_UUID_2a</Guid><DocumentGuid>UUID_2a</DocumentGuid></EBookToc>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 3a</Name><Guid>TOC_UUID_3a</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 4a</Name><Guid>TOC_UUID_4a</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
		expectedTocContent.append("</EBookToc>\r\n");
		expectedTocContent.append("</EBook>\r\n");
		LOG.debug("expectedTocContent =" + expectedTocContent.toString());

		Assert.assertEquals(expectedTocContent.toString(), tocFileContents);
		EasyMock.verify(mockNovusFactory);
		EasyMock.verify(mockNovus);
		EasyMock.verify(mockToc);
	}
	
	@Test
	public void testGetTocDataFromNovusWithNoName() throws Exception {

		File tocFile = new File(tocDir, "TOC"+COLLECTION_NAME+TOC_GUID+EBConstants.XML_FILE_EXTENSION);

		// Record expected calls
		EasyMock.expect(mockNovusFactory.createNovus(IS_FINAL_STAGE)).andReturn(mockNovus);
		EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("3").anyTimes();		
		EasyMock.expect(mockNovus.getTOC()).andReturn(mockToc);
		mockToc.setCollection(COLLECTION_NAME);
		mockToc.setShowChildrenCount(true);

		mockTocRootNode = mockTocNode;
		
		EasyMock.expect(mockToc.getNode(TOC_GUID)).andReturn(mockTocRootNode);
		EasyMock.expect(mockTocNode.getName()).andReturn(null).times(2); // No Name node
		EasyMock.expect(mockTocNode.getDocGuid()).andReturn(null).anyTimes();
		EasyMock.expect(mockTocNode.getGuid()).andReturn("tocGuid").anyTimes();
		EasyMock.expect(mockTocNode.getChildrenCount()).andReturn(0).anyTimes();
		EasyMock.expect(mockTocNode.getChildren()).andReturn(null).anyTimes();
	
		
		mockNovus.shutdownMQ();
		tocDir.mkdirs();

		// Set up for replay
		EasyMock.replay(mockNovusFactory);
		EasyMock.replay(mockNovus);
		EasyMock.replay(mockToc);
		EasyMock.replay(mockTocNode);
		EasyMock.replay(mockNovusUtility);		

	
		try {
			tocService.findTableOfContents(TOC_GUID, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
		} 
		catch (Exception e)
		{
			LOG.debug(e.getMessage());
			Assert.assertEquals("Failed with empty node Name for guid tocGuid", e.getMessage());
		}
		finally {
			// Temporary file will clean up after itself.
		}
		
		EasyMock.verify(mockNovusFactory);
		EasyMock.verify(mockNovus);
		EasyMock.verify(mockToc);
	}
	
	@Test
	public void testGetTocDataSkipNodesFromNovus() throws Exception {

		File tocFile = new File(tocDir, "TOC"+COLLECTION_NAME+TOC_GUID+EBConstants.XML_FILE_EXTENSION);

		// Record expected calls
		EasyMock.expect(mockNovusFactory.createNovus(IS_FINAL_STAGE)).andReturn(mockNovus);
		EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("1").anyTimes();		
		EasyMock.expect(mockNovus.getTOC()).andReturn(mockToc);
		mockToc.setCollection(COLLECTION_NAME);
		mockToc.setShowChildrenCount(true);
		TOCNode[] children = new TOCNode[]{};
        PublishingStats jobstats = new PublishingStats();
        jobstats.setJobInstanceId((long) 1);
        jobstats.setGatherTocDocCount(1);
        jobstats.setGatherTocNodeCount(1);
        jobstats.setPublishStatus("TEST");
       

		mockTocRootNode = mockTocNode;
		
		children = getChildNodes(5, 'a', 1).toArray(new TOCNode[]{});
//		EasyMock.expect(mockToc.getRootNodes()).andReturn(mockTocRootNode);
		EasyMock.expect(mockToc.getNode(TOC_GUID)).andReturn(mockTocRootNode);
		EasyMock.expect(mockTocNode.getName()).andReturn(" &lt; Root &amp;  &quot; Node&apos;s &gt; ").times(2); 
		EasyMock.expect(mockTocNode.getDocGuid()).andReturn(null).anyTimes();
		EasyMock.expect(mockTocNode.getGuid()).andReturn("tocGuid").anyTimes();
		EasyMock.expect(mockTocNode.getChildrenCount()).andReturn(5).anyTimes();
		EasyMock.expect(mockTocNode.getChildren()).andReturn(children).anyTimes();
	
		
		//EasyMock.expect(mockToc.getRootNodes()).andReturn(tocNodes);
		mockNovus.shutdownMQ();
		tocDir.mkdirs();

		// Set up for replay
		EasyMock.replay(mockNovusFactory);
		EasyMock.replay(mockNovus);
		EasyMock.replay(mockToc);
		EasyMock.replay(mockTocNode);
		EasyMock.replay(mockNovusUtility);		
		try {
			gatherResponse = tocService.findTableOfContents(TOC_GUID, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		finally {
			// Temporary file will clean up after itself.
		}
		LOG.debug(gatherResponse);
		assertTrue(gatherResponse.docCount == 4);
		assertTrue(gatherResponse.nodeCount == 7);
		
		String tocFileContents = readFileAsString(tocFile);
		LOG.debug("tocFileContents =" + tocFileContents);
		assertTrue(tocFileContents != null);
		
		StringBuffer expectedTocContent = new StringBuffer(1000);

		expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		expectedTocContent.append("<EBook>\r\n");
		expectedTocContent.append("<EBookToc><Name> &lt; Root &amp;  &quot; Node&apos;s &gt; </Name><Guid>tocGuid</Guid>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 0a</Name><Guid>TOC_UUID_0a</Guid><DocumentGuid>UUID_0a</DocumentGuid>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 0b</Name><Guid>TOC_UUID_0b</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
		expectedTocContent.append("</EBookToc>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 1a</Name><Guid>TOC_UUID_1a</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 2a</Name><Guid>TOC_UUID_2a</Guid><DocumentGuid>UUID_2a</DocumentGuid></EBookToc>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 3a</Name><Guid>TOC_UUID_3a</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
		expectedTocContent.append("<EBookToc><Name>Child 4a</Name><Guid>TOC_UUID_4a</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
		expectedTocContent.append("</EBookToc>\r\n");
		expectedTocContent.append("</EBook>\r\n");
		LOG.debug("expectedTocContent =" + expectedTocContent.toString());

		Assert.assertEquals(expectedTocContent.toString(), tocFileContents);
		EasyMock.verify(mockNovusFactory);
		EasyMock.verify(mockNovus);
		EasyMock.verify(mockToc);
	}
	
	@Test (expected=GatherException.class)
	public void testGetTocDataWithNovusException() throws Exception {
		File tocFile = new File(tocDir, "FAIL"+COLLECTION_NAME+TOC_GUID+EBConstants.XML_FILE_EXTENSION);

		// Record expected calls
		EasyMock.expect(mockNovusFactory.createNovus(IS_FINAL_STAGE)).andReturn(mockNovus);
		EasyMock.expect(mockNovusUtility.getDocRetryCount()).andReturn("3").times(2);
		EasyMock.expect(mockNovusUtility.getNortRetryCount()).andReturn("3").times(2);
		EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("3").times(2);			
		EasyMock.expect(mockNovus.getTOC()).andReturn(mockToc);
		mockToc.setCollection(COLLECTION_NAME);
		mockToc.setShowChildrenCount(true);
		mockNovus.shutdownMQ();
		EasyMock.expect(mockToc.getNode(TOC_GUID)).andThrow(new MockNovusException());

		// Replay
		EasyMock.replay(mockNovusFactory);
		EasyMock.replay(mockNovus);
		EasyMock.replay(mockToc);
		EasyMock.replay(mockNovusUtility);		
		
		try {
			tocService.findTableOfContents(TOC_GUID, COLLECTION_NAME, tocFile, null, null, IS_FINAL_STAGE, null, 0);
		} 
		finally
		{
			FileUtils.deleteQuietly(tocFile);
		}

		EasyMock.verify(mockNovusFactory);
		EasyMock.verify(mockNovus);
		EasyMock.verify(mockToc);
		EasyMock.replay(mockTocNode);
	}
	
	/**
	 * Creates child nodes.this is a helper method.
	 * If you change the number of children, be sure to modify the fillTOCNodes above.
	 * @param maxChildren number of children to create
	 * @return List of TOCNodes with child expects set.
	 * @throws java.io.IOException
	 */
	private List<TOCNode> getChildNodes(int maxChildren, char prefix, int skipDoc) throws Exception {
		List<TOCNode> childNodes = new ArrayList<TOCNode>();
		
		for (int i=0; i< maxChildren; i++) {
			
			TOCNode child = EasyMock.createMock(TOCNode.class);
			EasyMock.expect(child.getName()).andReturn("Child " + i + prefix).anyTimes();
			if (i == skipDoc )
			{
				EasyMock.expect(child.getDocGuid()).andReturn(null).times(2);
			}
			else
			{
				EasyMock.expect(child.getDocGuid()).andReturn("UUID_" + i+ prefix).times(2);
			}
			EasyMock.expect(child.getGuid()).andReturn("TOC_UUID_" + i+ prefix).times(2);

			if(i==0 && prefix == 'a')
			{
				TOCNode[] tocChildren = getChildNodes(1, 'b', skipDoc-1).toArray(new TOCNode[]{});
				EasyMock.expect(child.getChildren()).andReturn(tocChildren).anyTimes();
				EasyMock.expect(child.getChildrenCount()).andReturn(1).anyTimes();
			}
			else
			{
				EasyMock.expect(child.getChildrenCount()).andReturn(0).anyTimes();
				EasyMock.expect(child.getChildren()).andReturn(null).anyTimes();
			}
			
			EasyMock.replay(child);
			childNodes.add(child);
		}
		return childNodes;
	}
	
	@Ignore
		public void testGetTocDataExcludedDocsFromNovus() throws Exception {
	
			File tocFile = new File(tocDir, "TOC"+COLLECTION_NAME+TOC_GUID+EBConstants.XML_FILE_EXTENSION);
	
			// Record expected calls
			EasyMock.expect(mockNovusFactory.createNovus(IS_FINAL_STAGE)).andReturn(mockNovus);
			EasyMock.expect(mockNovusUtility.getTocRetryCount()).andReturn("1").anyTimes();		
			EasyMock.expect(mockNovus.getTOC()).andReturn(mockToc);
			mockToc.setCollection(COLLECTION_NAME);
			mockToc.setShowChildrenCount(true);
			TOCNode[] children = new TOCNode[]{};
	        PublishingStats jobstats = new PublishingStats();
	        jobstats.setJobInstanceId((long) 1);
	        jobstats.setGatherTocDocCount(1);
	        jobstats.setGatherTocNodeCount(1);
	        jobstats.setPublishStatus("TEST");
	       
	
			mockTocRootNode = mockTocNode;
			
			children = getChildNodes(5, 'a', 1).toArray(new TOCNode[]{});
	//		EasyMock.expect(mockToc.getRootNodes()).andReturn(mockTocRootNode);
			EasyMock.expect(mockToc.getNode(TOC_GUID)).andReturn(mockTocRootNode);
			EasyMock.expect(mockTocNode.getName()).andReturn(" &lt; Root &amp;  &quot; Node&apos;s &gt; ").times(2); 
			EasyMock.expect(mockTocNode.getDocGuid()).andReturn(null).anyTimes();
			EasyMock.expect(mockTocNode.getGuid()).andReturn("tocGuid").anyTimes();
			EasyMock.expect(mockTocNode.getChildrenCount()).andReturn(5).anyTimes();
			EasyMock.expect(mockTocNode.getChildren()).andReturn(children).anyTimes();
		
			
			//EasyMock.expect(mockToc.getRootNodes()).andReturn(tocNodes);
			mockNovus.shutdownMQ();
			tocDir.mkdirs();
	
			// Set up for replay
			EasyMock.replay(mockNovusFactory);
			EasyMock.replay(mockNovus);
			EasyMock.replay(mockToc);
			EasyMock.replay(mockTocNode);
			EasyMock.replay(mockNovusUtility);		
			try {
				ArrayList<ExcludeDocument> excludeDocuments = new ArrayList();
				mockExcludeDocument.setDocumentGuid("UUID_1a");
				excludeDocuments.add(mockExcludeDocument);
				gatherResponse = tocService.findTableOfContents(TOC_GUID, COLLECTION_NAME, tocFile, excludeDocuments, null, IS_FINAL_STAGE, null, 0);
			} catch (Exception e) {
				e.printStackTrace();
				Assert.fail(e.getMessage());
			}
			finally {
				// Temporary file will clean up after itself.
			}
			LOG.debug(gatherResponse);
			assertTrue(gatherResponse.docCount == 3);
			assertTrue(gatherResponse.nodeCount == 7);
			
			String tocFileContents = readFileAsString(tocFile);
			LOG.debug("tocFileContents =" + tocFileContents);
			assertTrue(tocFileContents != null);
			
			StringBuffer expectedTocContent = new StringBuffer(1000);
	
			expectedTocContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
			expectedTocContent.append("<EBook>\r\n");
			expectedTocContent.append("<EBookToc><Name> &lt; Root &amp;  &quot; Node&apos;s &gt; </Name><Guid>tocGuid</Guid>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 0a</Name><Guid>TOC_UUID_0a</Guid><DocumentGuid>UUID_0a</DocumentGuid>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 0b</Name><Guid>TOC_UUID_0b</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
			expectedTocContent.append("</EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 1a</Name><Guid>TOC_UUID_1a</Guid><MissingDocument></MissingDocument></EBookToc>\r\n");
//			expectedTocContent.append("<EBookToc><Name>Child 2a</Name><Guid>TOC_UUID_2a</Guid><DocumentGuid>UUID_2a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 3a</Name><Guid>TOC_UUID_3a</Guid><DocumentGuid>UUID_3a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("<EBookToc><Name>Child 4a</Name><Guid>TOC_UUID_4a</Guid><DocumentGuid>UUID_4a</DocumentGuid></EBookToc>\r\n");
			expectedTocContent.append("</EBookToc>\r\n");
			expectedTocContent.append("</EBook>\r\n");
			LOG.debug("expectedTocContent =" + expectedTocContent.toString());
	
			Assert.assertEquals(expectedTocContent.toString(), tocFileContents);
			EasyMock.verify(mockNovusFactory);
			EasyMock.verify(mockNovus);
			EasyMock.verify(mockToc);
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
