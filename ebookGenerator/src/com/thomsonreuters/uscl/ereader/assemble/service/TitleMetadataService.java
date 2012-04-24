/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
/**
 * Implementors of this interface are responsible for marshalling & unmarshalling TitleMetadata.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public interface TitleMetadataService {
	
	public ArrayList<Asset> createAssets(final File imagesDirectory);
	public Artwork createArtwork(final File coverImage);
	public Asset createStylesheet(final File stylesheet);
	
	/**
	 * Creates a title manifest to be included within the assembled ebook.
	 * 
	 * @param titleManifest the title manifest (title.xml) to create.
	 * @param tocXml the TOC structure from which the &lt;toc&gt; &amp; &lt;docs&gt; portions of the manifest are to be derived.
	 */
	public void generateTitleManifest(final OutputStream titleManifest, final InputStream tocXml, final TitleMetadata titleMetadata, final Long jobInstanceId, final File documentsDirectory);
}
