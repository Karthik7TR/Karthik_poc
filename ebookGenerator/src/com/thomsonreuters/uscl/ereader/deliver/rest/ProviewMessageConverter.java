/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * This class is responsible for serializing the gzipped tarball
 * into the body of the HTTP request being sent to ProView.
 * 
 * <p><i>Used only during PUT (publish) operations.</i></p>
 */
public class ProviewMessageConverter<T> extends AbstractHttpMessageConverter<File> {

	@Override
	protected File readInternal(Class<? extends File> arg0,
			HttpInputMessage arg1) throws IOException,
			HttpMessageNotReadableException {
		
		return null;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return Boolean.TRUE;
	}

	@Override
	protected void writeInternal(File fileToSend, HttpOutputMessage httpOutputMessage)
			throws IOException, HttpMessageNotWritableException {
		
		try {
			IOUtils.copy(new FileInputStream(fileToSend), httpOutputMessage.getBody());
		}
		catch (IOException e) {
			throw new HttpMessageNotWritableException("Could not write HTTP message.", e);
		}
		
	}

}
