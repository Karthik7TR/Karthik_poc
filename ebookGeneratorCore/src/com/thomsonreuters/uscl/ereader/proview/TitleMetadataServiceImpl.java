/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.proview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import com.thomsonreuters.uscl.ereader.gather.TableOfContents;
import com.thomsonreuters.uscl.ereader.ioutil.EntityEncodedInputStream;
import com.thomsonreuters.uscl.ereader.jibx.EntityPreservingCharacterEscaper;



/**
 * TitleMetadataService is responsible for manipulating and persisting title metadata.
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TitleMetadataServiceImpl implements TitleMetadataService {

	private static final Logger LOG = Logger.getLogger(TitleMetadataServiceImpl.class);
	private static final String STYLESHEET_ID = "css";
	//These FilenameFilter instances are de-facto singletons. As there is only ONE instance of TitleMetadataServiceImpl in the Spring Application Context.
	private final ImageFilter IMAGE_FILTER = new ImageFilter(); 
	private final DocumentFilter DOCUMENT_FILTER = new DocumentFilter();
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.proview.TitleMetadataService#writeToStream(com.thomsonreuters.uscl.ereader.proview.TitleMetadata, java.io.OutputStream)
	 */
	public void writeToStream(TitleMetadata titleMetadata, OutputStream outputStream) {
		if (titleMetadata == null) {
			throw new IllegalArgumentException("Title metadata must not be null!");
		}
		if (outputStream == null) {
			throw new IllegalArgumentException("OutputStream must not be null!");
		}
		
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
		TitleMetadata titleMetadata = null;
		try {
			titleMetadata = unmarshalTitleMetadata(inputStream);
		}
		catch (JiBXException e){
			throw new RuntimeException("Could not unmarshal titleMetadata from inputStream: " + inputStream, e);
		}
		return titleMetadata;
	}

	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.proview.TitleMetadataService#readFromFile(java.io.File)
	 */
	public TitleMetadata readFromFile(File titleMetadataFile) {
		TitleMetadata titleMetadata = null;
		try {
			titleMetadata = unmarshalTitleMetadata(new FileInputStream(titleMetadataFile));
		}
		catch (JiBXException e){
			throw new RuntimeException("Could not unmarshal titleMetadata from file: " + titleMetadataFile.getAbsolutePath(), e);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Could not unmarshal titleMetadata from file. ", e);
		}
		return titleMetadata;
	}

	private void marshalTitleMetadata(TitleMetadata titleMetadata,
			OutputStream outputStream) throws JiBXException {
		IBindingFactory bfact = 
				BindingDirectory.getFactory(TitleMetadata.class);
		IMarshallingContext mctx = bfact.createMarshallingContext();
		
		mctx.setOutput(outputStream, "UTF-8");
		mctx.marshalDocument(titleMetadata);
		
		IOUtils.closeQuietly(outputStream);
	}
	
	private TitleMetadata unmarshalTitleMetadata(InputStream inputStream) throws JiBXException{
		IBindingFactory bfact = 
				BindingDirectory.getFactory(TitleMetadata.class);
		IUnmarshallingContext unmtcx = bfact.createUnmarshallingContext();
		return (TitleMetadata) unmtcx.unmarshalDocument(inputStream, "UTF-8");
	}


	@Override
	public ArrayList<Asset> createAssets(final File imagesDirectory) {
		if (null == imagesDirectory || !imagesDirectory.isDirectory()) {
			throw new IllegalArgumentException("Images Directory must not be null and must be a directory that exists [" + imagesDirectory + "].");
		}
		
		List<File> images = Arrays.asList(imagesDirectory.listFiles());
		ArrayList<Asset> assets = new ArrayList<Asset>();
		for (File image : images) {
			String filename = image.getName();
			Asset asset = new Asset(StringUtils.substringBeforeLast(filename, "."), filename);
			assets.add(asset);
		}
		
		return assets;		
	}


	@Override
	public Artwork createArtwork(final File coverImage) {
		if (null == coverImage || !coverImage.exists()) {
			throw new IllegalArgumentException("coverImage must not be null and must be a directory that exists [" + coverImage + "].");
		}
	
		Artwork coverArt = new Artwork(coverImage.getName());
		return coverArt;
	}


	@Override
	public ArrayList<Doc> createDocuments(final File documentsFolder) {
		if (null == documentsFolder || !documentsFolder.isDirectory()) {
			throw new IllegalArgumentException("Documents Directory must not be null and must be a directory that exists [" + documentsFolder + "].");
		}
		
		List<File> documents = Arrays.asList(documentsFolder.listFiles());
		ArrayList<Doc> docs = new ArrayList<Doc>();
		for (File document : documents) {
			String filename = document.getName();
			Doc doc = new Doc(StringUtils.substringBeforeLast(filename, "."), filename);
			docs.add(doc);
		}
		
		return docs;
		
	}

	/**
	 * Returns an ArrayList of Author objects created based on values taken from a tokenized String.
	 * @param authors the tokenized String that represents one or more authors
	 * @return ArrayList of Author objects.
	 */
	public ArrayList<Author> createAuthors(final String authorsProperty) {
		List<String> authornameList = Arrays.asList(authorsProperty.split("\\|"));
		ArrayList<Author> authors = new ArrayList<Author>();
		for(String authorName : authornameList) {
			authors.add(new Author(authorName.trim()));
		}
		return authors;
	}

	@Override
	public ArrayList<TocEntry> createTableOfContents(final File gatheredTableOfContents) {
		TableOfContents tableOfContents = null;
		ArrayList<TocEntry> tocEntries = null;
		InputStream gatheredTocInputStream;
		IUnmarshallingContext unmtcx;
		try {
			gatheredTocInputStream = new FileInputStream(gatheredTableOfContents);
			IBindingFactory bfact = 
					BindingDirectory.getFactory(TableOfContents.class);
			unmtcx = bfact.createUnmarshallingContext();
			tableOfContents = (TableOfContents) unmtcx.unmarshalDocument(gatheredTocInputStream, "UTF-8");
			tocEntries = tableOfContents.getTocEntries();
		}
		catch(FileNotFoundException e){
			throw new RuntimeException("The file " + gatheredTableOfContents + " did not exist!", e);
		}
		catch(JiBXException e){
			throw new RuntimeException("Failed to unmarshal TocEntry objects from gathered toc file: " + gatheredTableOfContents.getAbsolutePath(), e);
		}
		return tocEntries;
	}
	
	@Override
	public Asset createStylesheet(final File stylesheet) {
		return new Asset(STYLESHEET_ID, stylesheet.getName());
	}
	
	private class ImageFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			boolean result = (StringUtils.endsWithIgnoreCase(name,"png") || StringUtils.endsWithIgnoreCase(name,"svg")) ? true : false;
			return result;
		}
	}
	
	private class DocumentFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			boolean result = (StringUtils.endsWithIgnoreCase(name,"htm") || StringUtils.endsWithIgnoreCase(name,"html")) ? true : false;
			return result;
		}
	}

	@Override
	public ArrayList<TocEntry> createTableOfContents(
			InputStream gatheredTableOfContentsInputStream) {
		TableOfContents tableOfContents = null;
		ArrayList<TocEntry> tocEntries = null;
		IUnmarshallingContext unmtcx;
		try {
			IBindingFactory bfact = 
					BindingDirectory.getFactory(TableOfContents.class);
			unmtcx = bfact.createUnmarshallingContext();
			tableOfContents = (TableOfContents) unmtcx.unmarshalDocument(gatheredTableOfContentsInputStream, "UTF-8");
			tocEntries = tableOfContents.getTocEntries();
		}
		catch(JiBXException e){
			throw new RuntimeException("Failed to unmarshal TocEntry objects from input stream: " + gatheredTableOfContentsInputStream, e);
		}
		return tocEntries;
	}
}
