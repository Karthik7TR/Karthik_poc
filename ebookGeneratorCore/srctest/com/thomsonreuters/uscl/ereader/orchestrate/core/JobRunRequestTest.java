/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.orchestrate.core;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class JobRunRequestTest  {

	@Test
	public void testJobRequestMarshalling() {
		try {
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("foo", "abc");
			params.put("bar", "def");
			final JobRunRequest requestToMarshal = JobRunRequest.create("fooBook", "jblow", "jblow@bogusaddr.com");
//System.out.println("To Marshal: " + requestToMarshal);
			String xml = requestToMarshal.marshal();
//System.out.println(xml);			
			Assert.assertNotNull(xml);
			Assert.assertTrue(xml.length() > 0);
			JobRunRequest unmarshalledRequest = JobRunRequest.unmarshal(xml);
//System.out.println("Unmarshalled: " + unmarshalledRequest);
			Assert.assertEquals(requestToMarshal.getJobName(), unmarshalledRequest.getJobName());
			Assert.assertEquals(requestToMarshal.getBookId(), unmarshalledRequest.getBookId());
			Assert.assertEquals(requestToMarshal.getUserName(), unmarshalledRequest.getUserName());
			Assert.assertEquals(requestToMarshal.getUserEmail(), unmarshalledRequest.getUserEmail());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
