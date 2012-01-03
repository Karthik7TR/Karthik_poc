/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Implementors of this interfact are responsible for marshalling & unmarshalling TitleMetadata.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public interface TitleMetadataService {
	public void writeToStream(TitleMetadata titleMetadata, OutputStream outputStream);
	public void writeToFile(TitleMetadata titleMetadata, File destinationFile);
	public TitleMetadata readFromStream(InputStream inputStream);
	public TitleMetadata readFromFile(File titleMetadataFile);
}
