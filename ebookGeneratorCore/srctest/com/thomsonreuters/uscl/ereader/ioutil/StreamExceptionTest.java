package com.thomsonreuters.uscl.ereader.ioutil;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public final class StreamExceptionTest
{
    @Test
    public void testCreate()
    {
        assertNotNull(new StreamException(new RuntimeException()));
    }
}
