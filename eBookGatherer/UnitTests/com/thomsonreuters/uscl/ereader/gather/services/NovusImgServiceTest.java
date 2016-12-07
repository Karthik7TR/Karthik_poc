package com.thomsonreuters.uscl.ereader.gather.services;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import com.westgroup.novus.productapi.BLOB;
import com.westgroup.novus.productapi.Find;
import com.westgroup.novus.productapi.Novus;
/**
 * @deprecated Should be removed after related integration test will be fixed
 */
public class NovusImgServiceTest {
	
	@Rule
	public TemporaryFolder tempDirectory = new TemporaryFolder();
	
	NovusUtility novusUtility;
	NovusImgServiceImpl novusImgService;
	Map<String,String> imgDocGuidMap;
	private static boolean IS_FINAL_STAGE = true;
	
	
	File temporaryDirectory;
	private NovusFactory mockNovusFactory;
	private Novus mockNovus;
	private Find mockFinder;
	private NovusUtility mockNovusUtility;
	
	
	@Before
	public void setUp() throws Exception{
		
		this.mockNovusFactory = EasyMock.createMock(NovusFactory.class);
		this.mockNovus = EasyMock.createMock(Novus.class);
		this.mockFinder = EasyMock.createMock(Find.class);
		this.mockNovusUtility = EasyMock.createMock(NovusUtility.class);
		
		novusImgService = new NovusImgServiceImpl();
		novusImgService.setNovusFactory(mockNovusFactory);
		novusImgService.setNovusUtility(mockNovusUtility);
		
		this.novusUtility = new NovusUtility();			
		novusUtility.setImgRetryCount("3");
		novusImgService.setNovusUtility(novusUtility);
		tempDirectory.create();
		temporaryDirectory = tempDirectory.newFolder("temp");
		imgDocGuidMap = new HashMap<String,String>();
		novusImgService.setMissingImageGuidsFileBasename("missing_doc.txt");
	}
	
	@Test
	public void testString(){
		String str = null;
		String str1 = new String();
		String str2 = new String(" ");
		String str3 = "new string";
		assertTrue(novusImgService.isEmpty(str));
		assertTrue(novusImgService.isEmpty(str1));
		assertTrue(novusImgService.isEmpty(str2));
		assertTrue(!novusImgService.isEmpty(str3));
	}
	
	@Test
	public void testFetchImages() throws Exception{
		
		EasyMock.expect(mockNovusFactory.createNovus(IS_FINAL_STAGE)).andReturn(mockNovus);
		EasyMock.expect(mockNovusUtility.getNortRetryCount()).andReturn("3").times(2);			
		EasyMock.expect(mockNovus.getFind()).andReturn(mockFinder);
		
		EasyMock.expect(mockFinder.getBLOB(null, "IMGGUID")).andReturn(null).times(3);			
		
		mockFinder.setResolveIncludes(true);
		mockNovus.shutdownMQ();

		// Set up for replay
		EasyMock.replay(mockNovusFactory);
		EasyMock.replay(mockNovusUtility);			
		EasyMock.replay(mockNovus);
		EasyMock.replay(mockFinder);
		
		
		imgDocGuidMap.put("DOCGUID","IMGGUID,");
		GatherResponse gatherResponse = novusImgService.fetchImages(imgDocGuidMap, temporaryDirectory, IS_FINAL_STAGE);
		
		assertTrue(gatherResponse.getMissingImgCount()==1);
		assertTrue(gatherResponse.getImageMetadataList().size() == 0);
		System.out.println(gatherResponse.getErrorMessage()+" : "+gatherResponse.getErrorCode());
		
		EasyMock.verify(mockNovusFactory);
		EasyMock.verify(mockNovus);
		EasyMock.verify(mockFinder);
	}
	
	@Test
	public void testgetImagesAndData() throws Exception{
		BLOB blob = null;
				
		EasyMock.expect(mockFinder.getBLOB(null, "IMGGUID")).andReturn(blob).times(3);		
	
		
		mockFinder.setResolveIncludes(true);
		mockNovus.shutdownMQ();

		EasyMock.replay(mockNovusUtility);			
		EasyMock.replay(mockNovus);
		EasyMock.replay(mockFinder);
		
		
		File missingImagesFile = new File(temporaryDirectory, "missing_doc.txt");
		FileOutputStream stream = new FileOutputStream(missingImagesFile);
		Writer fileWriter = new OutputStreamWriter(stream, "UTF-8");
		novusImgService.setUniqueImageGuids(new ArrayList<String>());
		
		ImgMetadataInfo imgMetadataInfo = novusImgService.getImagesAndMetadata(mockFinder, "IMGGUID", fileWriter, "DOCGUID", temporaryDirectory);
				
		assertTrue(imgMetadataInfo==null);
	}
	
	@Test
	public void testgetImagesWithException() throws Exception{
		
		EasyMock.expect(mockFinder.getBLOB(null, "IMGGUID")).andThrow( new RuntimeException("NOVUS")).times(3);		
	
		
		mockFinder.setResolveIncludes(true);
		mockNovus.shutdownMQ();

		EasyMock.replay(mockNovusUtility);
		EasyMock.replay(mockFinder);
		boolean thrown = false; 
		
		File missingImagesFile = new File(temporaryDirectory, "missing_doc.txt");
		FileOutputStream stream = new FileOutputStream(missingImagesFile);
		Writer fileWriter = new OutputStreamWriter(stream, "UTF-8");
		try{
			novusImgService.getImagesAndMetadata(mockFinder, "IMGGUID", fileWriter, "DOCGUID", temporaryDirectory);
		}
		catch(Exception ex){
			thrown = false;
		}
		//Missing guids will be written to file
		assertTrue(!thrown);
	}
	
	@Test
	public void testgetImagesAndDataEx() throws Exception{
		
		EasyMock.expect(mockFinder.getBLOB(null, "IMGGUID")).andReturn(null).times(3);		
	
		
		mockFinder.setResolveIncludes(true);
		mockNovus.shutdownMQ();

		EasyMock.replay(mockNovusUtility);
		EasyMock.replay(mockFinder);
		boolean thrown = false; 
		
		File missingImagesFile = new File(temporaryDirectory, "missing_doc.txt");
		FileOutputStream stream = new FileOutputStream(missingImagesFile);
		Writer fileWriter = new OutputStreamWriter(stream, "UTF-8");
		try{
			novusImgService.setUniqueImageGuids(new ArrayList<String>());
			novusImgService.getImagesAndMetadata(mockFinder, "IMGGUID", fileWriter, "DOCGUID", temporaryDirectory);
		}
		catch(Exception ex){
			thrown = true;
		}
		//Missing guids will be written to file
		assertTrue(!thrown);
	}
	
	

}
