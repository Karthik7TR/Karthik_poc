/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * Concrete implementation of TitleMetadataService.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TitleMetadataServiceImpl implements TitleMetadataService {

	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.proview.TitleMetadataService#writeToStream(com.thomsonreuters.uscl.ereader.proview.TitleMetadata, java.io.OutputStream)
	 */
	public void writeToStream(TitleMetadata titleMetadata, OutputStream outputStream) {
		
		try{
			marshalTitleMetadata(titleMetadata, outputStream);
		}
		catch(JiBXException e) {
			throw new RuntimeException("An error occurred while marshalling titleMetadata to output stream.", e);
		}
	}


	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.proview.TitleMetadataService#writeToFile(com.thomsonreuters.uscl.ereader.proview.TitleMetadata, java.io.File)
	 */
	public void writeToFile(TitleMetadata titleMetadata, File destinationFile) {
		if (null == destinationFile) {
			throw new IllegalArgumentException("destinationFile must not be null!");
		}
		if (null == titleMetadata) {
			throw new IllegalArgumentException("titleMetadata must not be null!");
		}
		
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
			marshalTitleMetadata(titleMetadata, fileOutputStream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("An error occurred while writing TitleMetadata to destination file: " + destinationFile.getName(), e);
		} catch(JiBXException e) {
			throw new RuntimeException("An error occurred while marshalling titleMetadata to output stream.", e);
		}

	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.proview.TitleMetadataService#readFromStream(java.io.InputStream)
	 */
	public TitleMetadata readFromStream(InputStream inputStream) {

		return null;
	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.proview.TitleMetadataService#readFromFile(java.io.File)
	 */
	public TitleMetadata readFromFile(File titleMetadataFile) {

		return null;
	}

	private void marshalTitleMetadata(TitleMetadata titleMetadata,
			OutputStream outputStream) throws JiBXException {
		IBindingFactory bfact = 
				BindingDirectory.getFactory(TitleMetadata.class);
		IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
		mctx.marshalDocument(titleMetadata, "UTF-8", null, outputStream);
	}
}
