/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.image.service;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.thomsonreuters.uscl.ereader.gather.image.domain.Header;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ServiceStatus;
import com.thomsonreuters.uscl.ereader.gather.image.domain.SingleImageMetadata;
import com.thomsonreuters.uscl.ereader.gather.image.domain.TraceInformation;

/**
 * Tests of the JSON objects returned from the Image Vertical RESTful web service and their marshaling and unmarshaling.
 */
public class ImageVerticalJsonTest  {

	@Test
	public void testAcceptedMediaTypes() {
		MediaType type = MediaType.valueOf("image/png");
		Assert.assertNotNull(type);
		Assert.assertEquals("image", type.getType());
		Assert.assertEquals("png", type.getSubtype());
		type = MediaType.valueOf("image/tif");
		Assert.assertNotNull(type);
	}
	
	/**
	 * Test that JSON marshalling is working correctly.
	 * This is the body of the HTTP response from the Image vertical RESTful web service.
	 */
	@Test
	public void testHeaderObject() {
		String expectedJson = "{\"AuthenticationToken\":\"authToken\",\"ContextualInformation\":\"contextInfo\",\"ProductIdentifier\":\"productId\",\"SessionToken\":null,\"SlideInformation\":null,\"UserHostIpAddress\":\"11.22.33.44\",\"Version\":\"999\"}";
		try {
			// Unmarshal JSON into a java object and make sure properties are correct
			Header header = JsonUtils.fromJson(expectedJson, Header.class);
			Assert.assertEquals("authToken", header.getAuthenticationToken());
			Assert.assertEquals("contextInfo", header.getContextualInformation());
			Assert.assertEquals("productId", header.getProductIdentifier());
			Assert.assertEquals(null, header.getSessionToken());
			Assert.assertEquals(null, header.getSlideInformation());
			Assert.assertEquals("11.22.33.44", header.getUserHostIpAddress());
			Assert.assertEquals("999", header.getVersion());
			
			// Convert the object back to a JSON string and make sure it is what we started with
			String actualJson = JsonUtils.toJson(header);
			Assert.assertEquals(expectedJson, actualJson);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testServiceStatusObject() {
		String expectedStartTimeString = "asdfkweoasdf";
		String expectedJson = "{\"ElapsedTime\":200,\"StartTime\":\""+expectedStartTimeString+"\",\"StatusCode\":123,\"StatusDescription\":\"statusDescription\"}";
		try {
			ServiceStatus serviceStatus = JsonUtils.fromJson(expectedJson, ServiceStatus.class);
			Assert.assertEquals(200, serviceStatus.getElapsedTime());
			Assert.assertEquals(expectedStartTimeString, serviceStatus.getStartTime());
			Assert.assertEquals(123, serviceStatus.getStatusCode());
			Assert.assertEquals("statusDescription", serviceStatus.getDescription());

			// Convert the object back to a JSON string and make sure it is what we started with
			String actualJson = JsonUtils.toJson(serviceStatus);
			Assert.assertEquals(expectedJson, actualJson);// TODO: what is format of returned date? a long int or other?
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testTraceInformationObject() {
		String expectedJson = "{\"ExecutionType\":\"executionType\",\"ParentGuid\":\"parentGuid\",\"Product\":\"product\",\"RootGuid\":\"rootGuid\",\"ServerInformation\":\"serverInformation\",\"SessionGuid\":\"sessionGuid\",\"TransactionGuid\":\"transactionGuid\",\"UserGuid\":\"userGuid\"}";
		try {
			TraceInformation traceInfo = JsonUtils.fromJson(expectedJson, TraceInformation.class);
			Assert.assertEquals("executionType", traceInfo.getExecutionType());
			Assert.assertEquals("parentGuid", traceInfo.getParentGuid());
			Assert.assertEquals("product", traceInfo.getProduct());
			Assert.assertEquals("rootGuid", traceInfo.getRootGuid());
			Assert.assertEquals("serverInformation", traceInfo.getServerInformation());
			Assert.assertEquals("sessionGuid", traceInfo.getSessionGuid());
			Assert.assertEquals("transactionGuid", traceInfo.getTransactionGuid());
			Assert.assertEquals("userGuid", traceInfo.getUserGuid());

			// Convert the object back to a JSON string and make sure it is what we started with
			String actualJson = JsonUtils.toJson(traceInfo);
			Assert.assertEquals(expectedJson, actualJson);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	@Test
	public void testSingleImageMetadataObject() {
		String expectedJson = "{\"ByteCount\":70790,\"ContentDatabase\":null,\"DPI\":400,\"DimensionUnit\":\"px\",\"Height\":1644,\"ImageGuid\":\"IA31BCD5F18364C9BBDCD008012AFBF02\",\"MimeType\":\"image\\/png\",\"PageCount\":null,\"RoyaltyCode\":null,\"Width\":1568}";
		try {
			SingleImageMetadata metadata = JsonUtils.fromJson(expectedJson, SingleImageMetadata.class);
			Assert.assertEquals("IA31BCD5F18364C9BBDCD008012AFBF02", metadata.getGuid());
			Assert.assertEquals(MediaType.IMAGE_PNG_VALUE, metadata.getMediaType().toString());
			Assert.assertEquals(new Long(70790), metadata.getSize());
			Assert.assertEquals("px", metadata.getDimUnit());
			Assert.assertEquals(new Long(1644), metadata.getHeight());
			Assert.assertEquals(new Long(1568), metadata.getWidth());
			Assert.assertEquals(new Long(400), metadata.getDpi());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
