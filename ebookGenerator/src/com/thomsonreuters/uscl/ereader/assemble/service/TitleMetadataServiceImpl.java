/*
* Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.EntityDecodedOutputStream;
import com.thomsonreuters.uscl.ereader.ioutil.EntityEncodedInputStream;
import com.thomsonreuters.uscl.ereader.proview.Artwork;
import com.thomsonreuters.uscl.ereader.proview.Asset;
import com.thomsonreuters.uscl.ereader.proview.TitleMetadata;
import com.thomsonreuters.uscl.ereader.util.FileUtilsFacade;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;



/**
 * TitleMetadataService is responsible for retrieving data from various sources in order to produce the title manifest for an eBook.
 * 
 * <p>A tight coupling between this service and its collaborators exists due to constraints imposed by ProView on publishers.
 * Should ProView ever support duplicate document IDs in a TOC, multiple TOC entries that refer to the same document ID, or other data scenarios this implementation
 * will probably become much, much simpler.  As it stands at the time of implementation, the collaborators responsible for decisions based on documents
 * within this book are dependencies at this level for reasons of encapsulation, performance, and necessity.</p> 
 * 
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
public class TitleMetadataServiceImpl implements TitleMetadataService {

	//private static final Logger LOG = Logger.getLogger(TitleMetadataServiceImpl.class);
	private static final String STYLESHEET_ID = "css";
	//These FilenameFilter instances are de-facto singletons. As there is only ONE instance of TitleMetadataServiceImpl in the Spring Application Context.
//	private final ImageFilter IMAGE_FILTER = new ImageFilter(); 
//	private final DocumentFilter DOCUMENT_FILTER = new DocumentFilter();
	private DocMetadataService docMetadataService;
	private PlaceholderDocumentService placeholderDocumentService;
	private FileUtilsFacade fileUtilsFacade;
	private UuidGenerator uuidGenerator;

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
	public Asset createStylesheet(final File stylesheet) {
		return new Asset(STYLESHEET_ID, stylesheet.getName());
	}

	@Override
	public void generateTitleManifest(final OutputStream titleManifest, final InputStream tocXml, final TitleMetadata titleMetadata, final Long jobInstanceId, final File documentsDirectory) {
		SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				
		try {
			
			saxParserFactory.setNamespaceAware(Boolean.TRUE);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			
			Map<String, String> familyGuidMap = docMetadataService.findDistinctFamilyGuidsByJobId(jobInstanceId);
			
			TitleManifestFilter titleManifestFilter = new TitleManifestFilter(titleMetadata, familyGuidMap, uuidGenerator, documentsDirectory, fileUtilsFacade, placeholderDocumentService);
			titleManifestFilter.setParent(xmlReader);
									
			Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
			props.setProperty("omit-xml-declaration", "yes");
			
			Serializer serializer = SerializerFactory.getSerializer(props);
			serializer.setOutputStream(new EntityDecodedOutputStream(titleManifest, true));
			
			titleManifestFilter.setContentHandler(serializer.asContentHandler());
			titleManifestFilter.parse(new InputSource(new EntityEncodedInputStream(tocXml)));
			
		} 
		catch (ParserConfigurationException e) {
			throw new RuntimeException("Failed to configure SAX Parser when generating title manifest.", e);
		}
		catch (SAXException e) {
			throw new RuntimeException("A SAXException occurred while generating the title manifest.", e);
		}
		catch (IOException e) {
			throw new RuntimeException("An IOException occurred while generating the title manifest.", e);
		}
	}
	
	public void setUuidGenerator(UuidGenerator uuidGenerator) {
		this.uuidGenerator = uuidGenerator;
	}

	public void setDocMetadataService(DocMetadataService docMetadataService) {
		this.docMetadataService = docMetadataService;
	}

	public void setFileUtilsFacade(FileUtilsFacade fileUtilsFacade) {
		this.fileUtilsFacade = fileUtilsFacade;
	}
	
	public void setPlaceholderDocumentService(PlaceholderDocumentService placeholderDocumentService) {
		this.placeholderDocumentService = placeholderDocumentService;
	}
	

}
