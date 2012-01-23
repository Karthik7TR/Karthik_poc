package com.thomsonreuters.uscl.ereader.gather.services;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.gather.domain.EBookToc;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;
import com.westgroup.novus.productapi.TOC;
import com.westgroup.novus.productapi.TOCNode;

public class TocServiceTest {
	private static final String COLLECTION_NAME = "w_an_rcc_cajur_toc";
	private NovusFactory mockNovusFactory;
	private Novus mockNovus;
	private TOC mockToc;
	private TocServiceImpl tocService;
	
	@Before
	public void setUp() {
		this.mockNovusFactory = EasyMock.createMock(NovusFactory.class);
		this.mockNovus = EasyMock.createMock(Novus.class);
		this.mockToc = EasyMock.createMock(TOC.class);
		
		// The object under test
		this.tocService = new TocServiceImpl();
		tocService.setNovusFactory(mockNovusFactory);
	}
	
	@Test
	public void testGetTocDataFromNovus( )
	{
		try {
			// Record expected calls
			EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
			EasyMock.expect(mockNovus.getTOC()).andReturn(mockToc);
			mockToc.setCollection(COLLECTION_NAME);
			mockToc.setShowChildrenCount(true);
			TOCNode[] tocNodes = { };
			EasyMock.expect(mockToc.getRootNodes()).andReturn(tocNodes);
			mockNovus.shutdownMQ();
	
			// Set up for replay
			EasyMock.replay(mockNovusFactory);
			EasyMock.replay(mockNovus);
			EasyMock.replay(mockToc);
			List<EBookToc> tocList = null;
			tocList = tocService.findTableOfContents("I7b3ec600675a11da90ebf04471783734",COLLECTION_NAME);
			Assert.assertNotNull(tocList);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}
	
//	@Test (expected=GatherException.class)
//	public void testGetTocDataFromNovus_Exception( ) throws Exception {
//		// Record expected calls
//		EasyMock.expect(mockNovusFactory.createNovus()).andReturn(mockNovus);
//		EasyMock.expect(mockNovus.getTOC()).andThrow(new NovusException("xxx"));
//		EasyMock.replay(mockNovusFactory);
//		EasyMock.replay(mockNovus);
//		
//		List<EBookToc> tocList = tocService.findTableOfContents("Dummy_Guid_for_Exception_test","Dummy_Collection_name");
//	}
}
