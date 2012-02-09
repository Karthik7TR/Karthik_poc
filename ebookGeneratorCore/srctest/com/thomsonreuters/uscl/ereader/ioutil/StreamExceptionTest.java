/*
* StreamExceptionTest
* 
* Created on: 1/13/11 By: u0009398
* 
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
*
* Proprietary and Confidential information of TRGR. 
* Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.
*/
package com.thomsonreuters.uscl.ereader.ioutil;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class StreamExceptionTest
{
    @Test
    public void testCreate()
    {
        assertNotNull(new StreamException(new RuntimeException()));
    }
}
