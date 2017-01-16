package com.thomsonreuters.uscl.ereader.orchestrate.engine.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thomsonreuters.uscl.ereader.jms.dao.RequestLogDao;
import com.thomsonreuters.uscl.ereader.jms.dao.RequestLogDaoImpl;
import com.thomsonreuters.uscl.ereader.jms.handler.EBookRequest;

public class EBookRequestValidatorTest {
	
	private EBookRequestValidator validator;
	private RequestLogDao requestDao;
	
	private String requestId = "ed4abfa40ee548388d39ecad55a0daaa";
	private String bundleHash;
	private Date requestDate = new Date();
	private File fileLocation = new File("srctest/com/thomsonreuters/uscl/ereader/orchestrate/engine/service/EBookRequestValidatorTest.java");
	
	@Before
	public void setUp() throws IOException {
		this.requestDao = EasyMock.createMock(RequestLogDaoImpl.class);
		this.validator = new EBookRequestValidator();
		this.validator.setRequestLogDao(requestDao);
		
		this.bundleHash = DigestUtils.md5Hex(new FileInputStream(fileLocation));
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testHappyPath() {
		EasyMock.expect(requestDao.findByRequestId(requestId)).andReturn(null);
		EasyMock.replay(requestDao);
		
		boolean thrown = false;
		try {
			validator.validate(createRequest());
		} catch (Exception e){
			e.printStackTrace();
			thrown = true;
		}
		Assert.assertTrue(!thrown);
	}
	
	@Test
	public void testIncompleteRequest() {
		
		boolean thrown = false;
		String errorMessage = "";
		try {
			validator.validate(new EBookRequest());
		} catch (Exception e){
			thrown = true;
			errorMessage = e.getMessage();
		}
		Assert.assertTrue(thrown);
		Assert.assertTrue(errorMessage.contains(EBookRequestValidator.ERROR_INCOMPLETE_REQUEST));
	}
	
	@Test
	public void testDuplicateRequest() {
		EasyMock.expect(requestDao.findByRequestId(requestId)).andReturn(createRequest());
		EasyMock.replay(requestDao);
		
		boolean thrown = false;
		String errorMessage = "";
		try {
			validator.validate(createRequest());
		} catch (Exception e){
			thrown = true;
			errorMessage = e.getMessage();
		}
		Assert.assertTrue(thrown);
		Assert.assertTrue(errorMessage.contains(EBookRequestValidator.ERROR_DUPLICATE_REQUEST));
	}
	
	@Test
	public void testFileNotFound() {
		fileLocation = new File("definitely_not_valid.bad");
		EasyMock.expect(requestDao.findByRequestId(requestId)).andReturn(null);
		EasyMock.replay(requestDao);
		
		boolean thrown = false;
		String errorMessage = "";
		try {
			validator.validate(createRequest());
		} catch (Exception e){
			thrown = true;
			errorMessage = e.getMessage();
		}
		Assert.assertTrue(thrown);
		Assert.assertTrue(errorMessage.contains(EBookRequestValidator.ERROR_EBOOK_NOT_FOUND));
	}
	
	@Test
	public void testBadHash() {
		bundleHash = "12345";
		EasyMock.expect(requestDao.findByRequestId(requestId)).andReturn(null);
		EasyMock.replay(requestDao);
		
		boolean thrown = false;
		String errorMessage = "";
		try {
			validator.validate(createRequest());
		} catch (Exception e){
			thrown = true;
			errorMessage = e.getMessage();
		}
		Assert.assertTrue(thrown);
		Assert.assertTrue(errorMessage.contains(EBookRequestValidator.ERROR_BAD_HASH));
	}
	
	private EBookRequest createRequest() {
		EBookRequest request = new EBookRequest();
		request.setVersion("1.0");
		request.setMessageId(requestId);
		request.setBundleHash(bundleHash);
		request.setDateTime(requestDate);
		request.setEBookSrcFile(fileLocation);
		return request;
	}
}