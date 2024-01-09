package com.thomsonreuters.uscl.ereader.deliver.exception;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * @author u0081674
 *
 */
public final class ProviewExceptionTest {
    private ProviewException proviewException;

    @Before
    public void setUp() {
        proviewException = new ProviewException("YARR!", new RuntimeException("PIRATES!"));
    }

    @Test
    public void testProviewExceptionHappyPath() {
        assertTrue("YARR!".equals(proviewException.getMessage()));
        assertTrue("PIRATES!".equals(proviewException.getCause().getMessage()));
    }

    @Test
    public void testProviewExceptionObjectEquality() {
        final ProviewException exceptionOne = new ProviewException("YARR!");
        final ProviewException exceptionTwo = new ProviewException("PIRATES!");
        assertTrue(exceptionOne != exceptionTwo);
    }
}
