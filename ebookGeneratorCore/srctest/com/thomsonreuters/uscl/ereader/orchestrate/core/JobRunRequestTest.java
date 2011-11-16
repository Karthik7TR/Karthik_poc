package com.thomsonreuters.uscl.ereader.orchestrate.core;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JobRunRequestTest  {

	@Test
	public void testRequestMarshal() {
		try {
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("foo", "abc");
			params.put("bar", "def");
			JobRunRequest requestToMarshal = JobRunRequest.createStartRequest("fooJob", Thread.MAX_PRIORITY);
System.out.println("To Marshal: " + requestToMarshal);
			String xml = requestToMarshal.marshal();
System.out.println(xml);  // DEBUG
			JobRunRequest unmarshalledRequest = JobRunRequest.unmarshal(xml);
System.out.println("Unmarshalled: " + unmarshalledRequest);
			assertEquals(requestToMarshal.getThreadPriority() , unmarshalledRequest.getThreadPriority());
			assertEquals(requestToMarshal.getJobName() , unmarshalledRequest.getJobName());
			//assertEquals(requestToMarshal.getJobParams() , unmarshalledRequest.getJobParams());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
