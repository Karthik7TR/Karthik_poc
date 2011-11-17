package com.thomsonreuters.uscl.ereader.orchestrate.core;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class JobControlRequestTest  {

	@Test
	public void testJobRequestMarshalling() {
		try {
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("foo", "abc");
			params.put("bar", "def");
			final JobControlRequest requestToMarshal = JobControlRequest.createStartRequest("fooJob", Thread.MAX_PRIORITY);
//System.out.println("To Marshal: " + requestToMarshal);
			String xml = requestToMarshal.marshal();
System.out.println(xml);  // DEBUG
			JobControlRequest unmarshalledRequest = JobControlRequest.unmarshal(xml);
//System.out.println("Unmarshalled: " + unmarshalledRequest);
			assertEquals(requestToMarshal.getThreadPriority(), unmarshalledRequest.getThreadPriority());
			assertEquals(requestToMarshal.getJobName(), unmarshalledRequest.getJobName());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
