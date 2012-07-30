/*
 * Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.domain;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.Assert;
import org.junit.Test;

public class JibxMarshallingTest  {
	
	
	@Test
	public void testGatherTocRequestMarshalling() {
		try {
			GatherTocRequest expected = new GatherTocRequest("someGuid", "TestCollectionName", new File("/temp"), null, null, true);
			String xml = marshal(expected, GatherTocRequest.class);
			GatherTocRequest actual = unmarshal(xml, GatherTocRequest.class);
			Assert.assertEquals(expected, actual);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGatherNortRequestMarshalling() {
		try {
//			GatherNortRequest expected = new GatherNortRequest("domain", "filter", new File("/temp"), null, new Long(1));
			GatherNortRequest expected = new GatherNortRequest("domain", "filter", new File("/temp"), new Date(), null, null, true);
			String xml = marshal(expected, GatherNortRequest.class);
			GatherNortRequest actual = unmarshal(xml, GatherNortRequest.class);
			Assert.assertEquals(expected, actual);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGatherDocRequestMarshalling() {
		try {
			String[] guidArray = { "a", "b", "c" };
			Collection<String>  guids = new ArrayList<String>(Arrays.asList(guidArray));
			String collectionName = "bogusCollname";
			File contentDir = new File("/foo");
			File metadataDir = new File("/bar");
			GatherDocRequest expected = new GatherDocRequest(guids, collectionName, contentDir, metadataDir, true);
			String xml = marshal(expected, GatherDocRequest.class);
			GatherDocRequest actual = unmarshal(xml, GatherDocRequest.class);
			Assert.assertEquals(expected, actual);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testGatherResponseMarshalling() {
		try {
			GatherResponse expected = new GatherResponse(999, "bogus error message");
			String xml = marshal(expected, GatherResponse.class);
			GatherResponse actual = unmarshal(xml, GatherResponse.class);
			Assert.assertEquals(expected, actual);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
	
	public static <T> String marshal(T expecedRequest, Class<T> type) throws JiBXException {
		IBindingFactory factory = BindingDirectory.getFactory(type);
		IMarshallingContext context = factory.createMarshallingContext();
		context.setIndent(2);
		StringWriter stringWriter = new StringWriter();
		context.setOutput(stringWriter);
		context.marshalDocument(expecedRequest);
		String xml = stringWriter.toString();
		return xml;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(String xml, Class<T> type) throws JiBXException {
		StringReader stringReader = new StringReader(xml);
		try {
			IBindingFactory factory = BindingDirectory.getFactory(type);
			IUnmarshallingContext context = factory.createUnmarshallingContext();
			T request = (T) context.unmarshalDocument(stringReader);
			return request;
		} finally {
			stringReader.close();
		}
	}
}
