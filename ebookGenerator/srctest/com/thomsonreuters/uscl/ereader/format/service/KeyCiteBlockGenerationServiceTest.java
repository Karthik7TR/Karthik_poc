package com.thomsonreuters.uscl.ereader.format.service;

import java.io.InputStream;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;


public class KeyCiteBlockGenerationServiceTest {
	
	private KeyCiteBlockGenerationServiceImpl service;
	private DocMetadataService mockDocMetadataService;
	DocMetadata mockDocMetadata;
	String titleId;
    long jobId;
    String docGuid;
	
	/**
	 * Generic setup for all the tests.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {

		mockDocMetadata = EasyMock.createMock(DocMetadata.class);
	    mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		this.mockDocMetadataService = EasyMock.createMock(DocMetadataService.class);
		this.service = new KeyCiteBlockGenerationServiceImpl();
		service.setDocMetadataService(mockDocMetadataService);	
		service.setHostname("http://www.westlaw.com");
		service.setMudparamrs("ebbb3.0");
		service.setMudparamvr("3.0");
		titleId="uscl/an/IMPH";
		jobId=101;
		docGuid="I770806320bbb11e1948492503fc0d37f";
		
	}
	
	
	@Test
	public void testGetKeyCite(){
		
				
		DocMetadata docMetaData = new DocMetadata();
		
		
		EasyMock.expect(mockDocMetadataService.findDocMetadataByPrimaryKey(titleId, new Long(101), docGuid)).andReturn(docMetaData);
		EasyMock.replay(mockDocMetadataService);
		
		InputStream keyCiteStream =null;
		try {
			keyCiteStream = service.getKeyCiteInfo(titleId, jobId, docGuid);
			System.out.println(keyCiteStream);
		} catch (EBookFormatException e) {
			e.printStackTrace();
		}
		EasyMock.verify(mockDocMetadataService);
		Assert.assertNotNull(keyCiteStream);
		
	}

}
