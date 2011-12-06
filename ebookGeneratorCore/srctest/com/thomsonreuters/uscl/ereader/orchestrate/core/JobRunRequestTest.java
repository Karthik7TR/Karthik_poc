package com.thomsonreuters.uscl.ereader.orchestrate.core;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class JobRunRequestTest  {

	@Test
	public void testJobRequestMarshalling() {
		try {
			HashMap<String,String> params = new HashMap<String,String>();
			String bookVersion = "1.1";
			params.put("foo", "abc");
			params.put("bar", "def");
			final JobRunRequest requestToMarshal = JobRunRequest.create("fooBook", "TR's wonderful fluff & stuff book!", bookVersion, "jblow", "jblow@bogusaddr.com");
//System.out.println("To Marshal: " + requestToMarshal);
			String xml = requestToMarshal.marshal();
//System.out.println(xml);			
			Assert.assertNotNull(xml);
			Assert.assertTrue(xml.length() > 0);
			JobRunRequest unmarshalledRequest = JobRunRequest.unmarshal(xml);
//System.out.println("Unmarshalled: " + unmarshalledRequest);
			Assert.assertEquals(requestToMarshal.getJobName(), unmarshalledRequest.getJobName());
			Assert.assertEquals(requestToMarshal.getBookCode(), unmarshalledRequest.getBookCode());
			Assert.assertEquals(requestToMarshal.getBookTitle(), unmarshalledRequest.getBookTitle());
			Assert.assertEquals(requestToMarshal.getBookVersion(), unmarshalledRequest.getBookVersion());
			Assert.assertEquals(requestToMarshal.getUserName(), unmarshalledRequest.getUserName());
			Assert.assertEquals(requestToMarshal.getUserEmail(), unmarshalledRequest.getUserEmail());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
