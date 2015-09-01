package com.thomsonreuters.uscl.ereader.gather.image.service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.thomsonreuters.uscl.ereader.gather.image.dao.ImageDao;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ServiceStatus;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadata;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadataResponse;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageResponse;

public class ImageServiceTest {
	//private static final Logger log = Logger.getLogger(ImageServiceTest.class);
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();
	private static final Long JOB_INSTANCE_ID = 1965l;
	private static final String TITLE_ID = "bogusTitleId";
	private static final String GUID = "junitBogusGuid";
	private static final String DOC_GUID = "dummyDocGuid";
	private static final int MAX_RETRIES = 3;
	private static final String MISSING_GUIDS_FILE_BASENAME = "missingImageGuidsFile.txt";
	private static final ImageMetadataEntityKey METADATA_PK = new ImageMetadataEntityKey(JOB_INSTANCE_ID, GUID, DOC_GUID);
	private URL SERVICE_CONTEXT_URL;
	private static String SERVICE_VERSION = "v1";
	private ImageDao mockImageDao;
	private ImageServiceImpl imageService;
	private RestTemplate mockSingletonRestTemplate;
	private ImageVerticalRestTemplateFactory mockImageVerticalRestTemplateFactory;
	
	private SingleImageMetadata SINGLE_IMAGE_METADATA;
	
	@Before
	public void setUp() throws Exception {
		SERVICE_CONTEXT_URL = new URL("http://bogus.imageservice.com/image");
		this.mockSingletonRestTemplate = EasyMock.createMock(RestTemplate.class);
		this.mockImageDao = EasyMock.createMock(ImageDao.class);
		this.mockImageVerticalRestTemplateFactory = EasyMock.createMock(ImageVerticalRestTemplateFactory.class);
		
		this.imageService = new ImageServiceImpl();
		imageService.setSingletonRestTemplate(mockSingletonRestTemplate);
		imageService.setImageVerticalRestServiceUrl(SERVICE_CONTEXT_URL);
		imageService.setSleepIntervalBetweenImages(500l);
		imageService.setUrlVersion(SERVICE_VERSION);
		imageService.setImageDao(mockImageDao);
		imageService.setImageVerticalRestTemplateFactory(mockImageVerticalRestTemplateFactory);
		imageService.setMissingImageGuidsFileBasename(MISSING_GUIDS_FILE_BASENAME);
		imageService.setImageServiceMaxRetries(MAX_RETRIES);
		imageService.setImageMetadataServiceMaxRetries(MAX_RETRIES);
		
		SINGLE_IMAGE_METADATA = new SingleImageMetadata();
		SINGLE_IMAGE_METADATA.setDimUnit("px");
		SINGLE_IMAGE_METADATA.setDpi(400l);
		SINGLE_IMAGE_METADATA.setGuid(GUID);
		SINGLE_IMAGE_METADATA.setHeight(1234l);
		SINGLE_IMAGE_METADATA.setSize(45678l);
		SINGLE_IMAGE_METADATA.setWidth(999l);
		SINGLE_IMAGE_METADATA.setMediaType(MediaType.IMAGE_PNG_VALUE);
	}

	@Test
	public void testFetchImageVerticalImages() {
		try {
			File imageDirectory = temporaryFolder.getRoot();
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatusCode(0);
			
			SingleImageMetadataResponse metadataResponse = new SingleImageMetadataResponse();
			metadataResponse.setServiceStatus(serviceStatus);
			metadataResponse.setImageMetadata(SINGLE_IMAGE_METADATA);
			
			ImageVerticalRestTemplate mockImageVerticalRestTemplate = EasyMock.createMock(ImageVerticalRestTemplate.class);
			EasyMock.expect(mockSingletonRestTemplate.getForObject(ImageServiceImpl.SINGLE_IMAGE_METADATA_URL_PATTERN,
							SingleImageMetadataResponse.class,
							SERVICE_CONTEXT_URL.toString(), SERVICE_VERSION, GUID)).andReturn(metadataResponse);
			EasyMock.expect(mockImageVerticalRestTemplateFactory.create(imageDirectory, GUID, SINGLE_IMAGE_METADATA.getMediaType())).andReturn(mockImageVerticalRestTemplate);
			EasyMock.expect(mockImageVerticalRestTemplate.getForObject(ImageServiceImpl.SINGLE_IMAGE_URL_PATTERN,
							SingleImageResponse.class,
							SERVICE_CONTEXT_URL.toString(), SERVICE_VERSION, GUID)).andReturn(null);
			EasyMock.replay(mockSingletonRestTemplate);
			EasyMock.replay(mockImageVerticalRestTemplateFactory);
			EasyMock.replay(mockImageVerticalRestTemplate);
			
			// Invoke the object under test
			List<String> guids = new ArrayList<String>();
			guids.add(GUID);
			Map<String,String> imgDocGuidMap = new HashMap<String,String>();
			imgDocGuidMap.put("123456789654645645",GUID);
			imageService.fetchImageVerticalImages(imgDocGuidMap, imageDirectory, JOB_INSTANCE_ID, TITLE_ID);
			
			// Ensure all expected mock object methods were called in the right order
			EasyMock.verify(mockSingletonRestTemplate);
			EasyMock.verify(mockImageVerticalRestTemplateFactory);
			EasyMock.verify(mockImageVerticalRestTemplate);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testFetchImageVerticalDownloadFailure() {
		try {
			File imageDirectory = temporaryFolder.getRoot();
			ServiceStatus serviceStatus = new ServiceStatus();
			serviceStatus.setStatusCode(0);
			
			SingleImageMetadataResponse metadataResponse = new SingleImageMetadataResponse();
			metadataResponse.setServiceStatus(serviceStatus);
			metadataResponse.setImageMetadata(SINGLE_IMAGE_METADATA);
			
			ImageVerticalRestTemplate mockImageVerticalRestTemplate = EasyMock.createMock(ImageVerticalRestTemplate.class);
			EasyMock.expect(mockSingletonRestTemplate.getForObject(ImageServiceImpl.SINGLE_IMAGE_METADATA_URL_PATTERN,
							SingleImageMetadataResponse.class,
							SERVICE_CONTEXT_URL.toString(), SERVICE_VERSION, GUID)).andReturn(metadataResponse);
			EasyMock.expect(mockImageVerticalRestTemplateFactory.create(imageDirectory, GUID, SINGLE_IMAGE_METADATA.getMediaType())).andReturn(mockImageVerticalRestTemplate);
			EasyMock.expect(mockImageVerticalRestTemplate.getForObject(ImageServiceImpl.SINGLE_IMAGE_URL_PATTERN,
							SingleImageResponse.class,
							SERVICE_CONTEXT_URL.toString(), SERVICE_VERSION, GUID))
							.andThrow(new RestClientException("Bogus image download error")).times(MAX_RETRIES);
			EasyMock.replay(mockSingletonRestTemplate);
			EasyMock.replay(mockImageVerticalRestTemplateFactory);
			EasyMock.replay(mockImageVerticalRestTemplate);
			
			// Invoke the object under test
			List<String> guids = new ArrayList<String>();
			guids.add(GUID);
			Map<String,String> imgDocGuidMap = new HashMap<String,String>();
			imgDocGuidMap.put("123456789",GUID);
			imageService.fetchImageVerticalImages(imgDocGuidMap, imageDirectory, JOB_INSTANCE_ID, TITLE_ID);
			
			File missingGuidsFile = new File(imageDirectory, MISSING_GUIDS_FILE_BASENAME);
			Assert.assertTrue("Missing image guids file exists", missingGuidsFile.exists());
			Assert.assertTrue("Missing image guids file is not empty", missingGuidsFile.length() > 0);
			
			// Ensure all expected mock object methods were called in the right order
			EasyMock.verify(mockSingletonRestTemplate);
			EasyMock.verify(mockImageVerticalRestTemplateFactory);
			EasyMock.verify(mockImageVerticalRestTemplate);
		} catch (Exception e) {
			Assert.assertTrue(e instanceof ImageException);
		}
	}

	@Test
	public void testFetchImageMetadata() {
		EasyMock.expect(mockSingletonRestTemplate.getForObject(ImageServiceImpl.SINGLE_IMAGE_METADATA_URL_PATTERN,
										SingleImageMetadataResponse.class,
										SERVICE_CONTEXT_URL.toString(), SERVICE_VERSION, GUID
										)).andReturn(null);
		EasyMock.replay(mockSingletonRestTemplate);
		
		try {
			imageService.fetchImageVerticalImageMetadata(GUID, null, "123456789");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
		
		EasyMock.verify(mockSingletonRestTemplate);
		
	}	
	/**
	 * Test the mapping of a SingleImageMetadataResponse (from the Image Vertical REST service) to a persistable meta-data entity.
	 */
	@Test
	public void testMapImageMetadataResponseToEntity() {

		
		SingleImageMetadataResponse metadataResponse = new SingleImageMetadataResponse();
		metadataResponse.setImageMetadata(SINGLE_IMAGE_METADATA);
		ImageMetadataEntity entity = ImageServiceImpl.createImageMetadataEntity(metadataResponse,
											JOB_INSTANCE_ID,  TITLE_ID, DOC_GUID);
		Assert.assertEquals(JOB_INSTANCE_ID, entity.getPrimaryKey().getJobInstanceId());
		Assert.assertEquals(SINGLE_IMAGE_METADATA.getGuid(), entity.getPrimaryKey().getImageGuid());
		Assert.assertEquals(TITLE_ID, entity.getTitleId());
		Assert.assertEquals(SINGLE_IMAGE_METADATA.getDimUnit(), entity.getDimUnits());
		Assert.assertEquals(SINGLE_IMAGE_METADATA.getDpi(), entity.getDpi());
		Assert.assertEquals(SINGLE_IMAGE_METADATA.getHeight(), entity.getHeight());
		Assert.assertEquals(SINGLE_IMAGE_METADATA.getWidth(), entity.getWidth());
		Assert.assertEquals(SINGLE_IMAGE_METADATA.getSize(), entity.getSize());
	}
	
	@Test
	public void testSaveSingleImageMetadataResponse() {
		SingleImageMetadata imageMetadata = new SingleImageMetadata();
		
		SingleImageMetadataResponse metadataResponse = new SingleImageMetadataResponse();
		metadataResponse.setImageMetadata(imageMetadata);
		EasyMock.expect(mockImageDao.saveImageMetadata((ImageMetadataEntity) EasyMock.anyObject())).andReturn(METADATA_PK);
		EasyMock.replay(mockImageDao);
		
		ImageMetadataEntityKey pk = imageService.saveImageMetadata(metadataResponse, METADATA_PK.getJobInstanceId(), TITLE_ID, DOC_GUID);
		Assert.assertNotNull(pk);
		Assert.assertEquals(METADATA_PK, pk);
		
		EasyMock.verify(mockImageDao);	
	}
	
	@Test
	public void testSaveImageMetadataEntity() {
		ImageMetadataEntity entity = new ImageMetadataEntity(METADATA_PK, "titleId",
				   									100l, 200l, 41234l, 1111l, "px", MediaType.IMAGE_PNG);
		EasyMock.expect(mockImageDao.saveImageMetadata(entity)).andReturn(METADATA_PK);
		EasyMock.replay(mockImageDao);
		
		ImageMetadataEntityKey pk = imageService.saveImageMetadata(entity);
		Assert.assertNotNull(pk);
		Assert.assertEquals(METADATA_PK, pk);
		
		EasyMock.verify(mockImageDao);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testFindImageMetadata() {
		long jobInstanceId = System.currentTimeMillis();
		EasyMock.expect(mockImageDao.findImageMetadata(jobInstanceId)).andReturn(Collections.EMPTY_LIST);
		EasyMock.replay(mockImageDao);
		
		List<ImageMetadataEntity> list = imageService.findImageMetadata(jobInstanceId);
		Assert.assertNotNull(list);
		Assert.assertEquals(0, list.size());
		
		EasyMock.verify(mockImageDao);
	}
}
