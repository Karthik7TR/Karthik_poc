/*
 * StreamException.java
 *
 * Created on: Oct 30, 2010 by: davicar
 *
 * Copyright 2010 Thomson Reuters Global Resources.  All Rights Reserved.
 * 
 * Proprietary and Confidential information of TRGR.
 * Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited.
 */
package com.thomsonreuters.uscl.ereader.ioutil;

import java.io.IOException;

public class StreamException extends IOException
{
    /**
     * Constructor.
     *
     * @param e a Throwable object.
     */
    public StreamException(Throwable e)
    {
        super(e);
    }

}