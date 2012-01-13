/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.Assert;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.orchestrate.core.JobRunRequest;

public class JibxMarshallingTest  {
	
	
	@Test
	public void testGatherTocRequestMarshalling() {
		try {
			GatherTocRequest expecedRequest = new GatherTocRequest();
			expecedRequest.setCollectionName("TestCollectionName");
			expecedRequest.setGuid("TGuid");
			expecedRequest.setJobId(999);
			expecedRequest.setJobStartDate(new java.util.Date()) ;
			expecedRequest.setRootDirectory("TestRootDir");
			expecedRequest.setTitleId("TitleId");
			String strXml = marshal(expecedRequest);
			System.out.println("strXml ="+strXml);
			
			GatherTocRequest actual = unmarshal(strXml);
			Assert.assertEquals(expecedRequest, actual);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	public static String marshal(GatherTocRequest expecedRequest) throws JiBXException {
		IBindingFactory factory = BindingDirectory.getFactory(GatherTocRequest.class);
		IMarshallingContext context = factory.createMarshallingContext();
		context.setIndent(2);
		StringWriter stringWriter = new StringWriter();
		context.setOutput(stringWriter);
		context.marshalDocument(expecedRequest);
		String xml = stringWriter.toString();
		return xml;
	}
	
	public static GatherTocRequest unmarshal(String xml) throws JiBXException {
		StringReader stringReader = new StringReader(xml);
		try {
			IBindingFactory factory = BindingDirectory.getFactory(GatherTocRequest.class);
			IUnmarshallingContext context = factory.createUnmarshallingContext();
			GatherTocRequest request = (GatherTocRequest) context.unmarshalDocument(stringReader);
			return request;
		} finally {
			stringReader.close();
		}
	}
}
