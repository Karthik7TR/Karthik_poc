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
			final JobRunRequest requestToMarshal = JobRunRequest.createStartRequest("fooJob", Thread.MAX_PRIORITY, "jblow", "jblow@bogusaddr.com");
//System.out.println("To Marshal: " + requestToMarshal);
			String xml = requestToMarshal.marshal();
			Assert.assertNotNull(xml);
			Assert.assertTrue(xml.length() > 0);
			JobRunRequest unmarshalledRequest = JobRunRequest.unmarshal(xml);
//System.out.println("Unmarshalled: " + unmarshalledRequest);
			Assert.assertEquals(requestToMarshal.getThreadPriority(), unmarshalledRequest.getThreadPriority());
			Assert.assertEquals(requestToMarshal.getJobName(), unmarshalledRequest.getJobName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
