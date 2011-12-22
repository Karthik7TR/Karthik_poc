/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.deliver.exception;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * @author u0081674
 *
 */
public class ProviewExceptionTest {
	
	private ProviewException proviewException;
	
	@Before
	public void setUp() {
		proviewException = new ProviewException("YARR!", new RuntimeException("PIRATES!"));
	}
	
	@Test
	public void testProviewExceptionHappyPath() throws Exception {
		assertTrue("YARR!".equals(proviewException.getMessage()));
		assertTrue("PIRATES!".equals(proviewException.getCause().getMessage()));
	}
	
	@Test
	public void testProviewExceptionObjectEquality() throws Exception {
		ProviewException exceptionOne = new ProviewException("YARR!");
		ProviewException exceptionTwo = new ProviewException("PIRATES!");
		assertTrue(exceptionOne != exceptionTwo);
	}
}
