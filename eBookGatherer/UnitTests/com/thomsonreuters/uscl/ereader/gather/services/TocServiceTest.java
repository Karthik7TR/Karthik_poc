package com.thomsonreuters.uscl.ereader.gather.services;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.TOC;
import com.westgroup.novus.productapi.TOCNode;


public class TocServiceTest {
	private static final String COLLECTION_NAME = "w_an_rcc_cajur_toc";
	private static final String TOC_GUID = "I7b3ec600675a11da90ebf04471783734";
	private NovusFactory mockNovusFactory;
	private Novus mockNovus;
	private TOC mockToc;
	private TOCNode mockTocNode;
	private TocServiceImpl tocService;
	
	@Before
	public void setUp() throws Exception {
		this.mockNovusFactory = EasyMock.createMock(NovusFactory.class);
		this.mockNovus = EasyMock.createMock(Novus.class);
		this.mockToc = EasyMock.createMock(TOC.class);
		this.mockTocNode = EasyMock.createMock(TOCNode.class);
		
		// The object under test
		this.tocService = new TocServiceImpl();
		tocService.setNovusFactory(mockNovusFactory);
	}
	
	@Test
	public void testGetTocDataFromNovus() throws Exception {
		
		// Record expected calls
		EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
		EasyMock.expect(mockNovus.getTOC()).andReturn(mockToc);
		mockToc.setCollection(COLLECTION_NAME);
		mockToc.setShowChildrenCount(true);
		TOCNode[] tocNodes = { };
		EasyMock.expect(mockToc.getNode(TOC_GUID)).andReturn(mockTocNode);
		EasyMock.expect(mockTocNode.getGuid()).andReturn(TOC_GUID);
		EasyMock.expect(mockTocNode.getName()).andReturn("someName");
		EasyMock.expect(mockTocNode.getDocGuid()).andReturn("someDocGuid");
		EasyMock.expect(mockTocNode.getParentGuid()).andReturn("someParentGuid");
		EasyMock.expect(mockTocNode.getMetadata()).andReturn("someMetadata");
		EasyMock.expect(mockTocNode.getChildrenCount()).andReturn(1).times(2);
		EasyMock.expect(mockTocNode.getChildren()).andReturn(tocNodes);
		//EasyMock.expect(mockToc.getRootNodes()).andReturn(tocNodes);
		mockNovus.shutdownMQ();

		// Set up for replay
		EasyMock.replay(mockNovusFactory);
		EasyMock.replay(mockNovus);
		EasyMock.replay(mockToc);
		EasyMock.replay(mockTocNode);
		try {
			List<EBookToc> tocList = null;
			tocList = tocService.findTableOfContents(TOC_GUID,COLLECTION_NAME);
			Assert.assertNotNull(tocList);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		EasyMock.verify(mockNovusFactory);
		EasyMock.verify(mockNovus);
		EasyMock.verify(mockToc);
	}
	
	@Test (expected=GatherException.class)
	public void testGetTocDataWithNovusException() throws Exception {

		// Record expected calls
		EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
		EasyMock.expect(mockNovus.getTOC()).andReturn(mockToc);
		mockToc.setCollection(COLLECTION_NAME);
		mockToc.setShowChildrenCount(true);
		mockNovus.shutdownMQ();
		EasyMock.expect(mockToc.getNode(TOC_GUID)).andThrow(new MockNovusException());

		// Replay
		EasyMock.replay(mockNovusFactory);
		EasyMock.replay(mockNovus);
		EasyMock.replay(mockToc);
		
		tocService.findTableOfContents(TOC_GUID, COLLECTION_NAME);
		
		EasyMock.verify(mockNovusFactory);
		EasyMock.verify(mockNovus);
		EasyMock.verify(mockToc);
		EasyMock.replay(mockTocNode);
	}
}
