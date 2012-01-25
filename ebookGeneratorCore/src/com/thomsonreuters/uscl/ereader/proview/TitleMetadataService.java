/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Implementors of this interface are responsible for marshalling & unmarshalling TitleMetadata.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public interface TitleMetadataService {
	public void writeToStream(TitleMetadata titleMetadata, OutputStream outputStream);
	public void writeToFile(TitleMetadata titleMetadata, File destinationFile);
	public TitleMetadata readFromStream(InputStream inputStream);
	public TitleMetadata readFromFile(File titleMetadataFile);
	
	public ArrayList<Author> createAuthors(final String delimitedAuthorsString);
	public ArrayList<Asset> createAssets(final File imagesDirectory);
	public Artwork createArtwork(final File coverImage);
	public ArrayList<Doc> createDocuments(final File gatheredDocumentsFolder);
	public ArrayList<TocEntry> createTableOfContents(final File gatheredTableOfContents);
	public ArrayList<TocEntry> createTableOfContents(final InputStream gatheredTableOfContentsInputStream);
	public Asset createStylesheet(final File stylesheet);
}
