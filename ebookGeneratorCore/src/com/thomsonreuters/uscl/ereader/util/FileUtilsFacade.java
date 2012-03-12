/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.util;

import java.io.File;
import java.io.IOException;

/**
 * Provides a facility for various operations on the FileSystem and abstracts low-level I/O from the rest of the application. 
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public interface FileUtilsFacade {

	public void copyFile(File sourceFile, File destinationFile) throws IOException;
}
