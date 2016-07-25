/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.gather.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.MediaType;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import com.thomsonreuters.uscl.ereader.gather.util.ImageConverter;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import com.westgroup.novus.productapi.BLOB;
import com.westgroup.novus.productapi.Find;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

public class NovusImgServiceImpl implements NovusImgService {

	// "missing_image_guids.txt"
	private String missingImageGuidsFileBasename;
	private NovusFactory novusFactory;
	private NovusUtility novusUtility;
	/** Milliseconds to sleep between each meta-data/bytes fetch */
	private long sleepIntervalBetweenImages;

	private static final Logger Log = LogManager.getLogger(NovusImgServiceImpl.class);	

	int missingImageCount;
	int missingMetadataCount;

	private static final String TIF = "tif";
	private static final String PING_EXTENTION = "png";
	private static final String PING_FORMAT = "PNG";
	private static final String[] KNOWN_IMG_FORMATS = { "PNG", "TIF", "JPG", "GIF", "BMP" };
	private List<String> uniqueImageGuids ;

	public List<String> getUniqueImageGuids() {
		return uniqueImageGuids;
	}

	public void setUniqueImageGuids(List<String> uniqueImageGuids) {
		this.uniqueImageGuids = uniqueImageGuids;
	}

	/**
	 * Reads imageGuids from file Format/doc-to-image-manifest.txt and gets
	 * images from Novus and writes images to Gather/Images/Dynamic directory.
	 */
	public GatherResponse getImagesFromNovus(File imgToDocManifestFile, File dynamicImageDirectory, boolean isFinalStage) {

		try {
			// Create list of image guids gathered from a previous step and
			// stored in a flat text file, one per line
			Map<String, String> imgDocGuidMap = readLinesFromTextFile(imgToDocManifestFile);
			return fetchImages(imgDocGuidMap, dynamicImageDirectory, isFinalStage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return new GatherResponse();
	}

	public GatherResponse fetchImages(final Map<String, String> imgDocGuidMap, File imageDestinationDirectory,
			boolean isFinalStage) throws Exception {

		GatherResponse gatherResponse = new GatherResponse();
		Novus novus = null;
		try {
			novus = novusFactory.createNovus(isFinalStage);
		} catch (NovusException e) {
			GatherException ge = new GatherException("Novus error occurred while creating Novus object " + e,
					GatherResponse.CODE_NOVUS_ERROR);
			throw ge;
		}

		ArrayList<ImgMetadataInfo> imageMetadataList = new ArrayList<ImgMetadataInfo>();
		File missingImagesFile = new File(imageDestinationDirectory.getParent(), missingImageGuidsFileBasename);
		FileOutputStream stream = new FileOutputStream(missingImagesFile);
		Writer fileWriter = new OutputStreamWriter(stream, "UTF-8");
		missingImageCount = 0;
		missingMetadataCount = 0;
		try {

			Find find = novus.getFind();
			find.setResolveIncludes(true);
			
			uniqueImageGuids = new ArrayList<String>();

			// Iterate the image GUID's and first fetch image data and then
			// download the image bytes
			for (String docGuid : imgDocGuidMap.keySet()) {
				List<String> deDupImagesArray = new ArrayList<String>();
				String imgGuidList = imgDocGuidMap.get(docGuid);

				if (imgGuidList != null) {
					String[] imgDocsArray = imgGuidList.split(",");
					for (String imgGuid : imgDocsArray) {
						if (!deDupImagesArray.contains(imgGuid)) {
							
							 ImgMetadataInfo imgMetadataInfo = getImagesAndMetadata(find, imgGuid, fileWriter, docGuid,
									imageDestinationDirectory);	
							if (imgMetadataInfo != null) {
								imageMetadataList.add(imgMetadataInfo);
							}

							// Intentionally pause between invocations of the
							// Image Vertical REST service as not to pound on it
							Thread.sleep(sleepIntervalBetweenImages);
							// Add the image guid to the unique list
							deDupImagesArray.add(imgGuid);

						}// end of for-loop
					}
				} // end of for-loop
			}

		} finally {
			novus.shutdownMQ();
			gatherResponse.setImageMetadataList(imageMetadataList);
			gatherResponse.setMissingImgCount(missingImageCount);
			fileWriter.close();
		}
		return gatherResponse;
	}

	

	public ImgMetadataInfo getImagesAndMetadata(Find find, String imageGuid, Writer missingImageFileWriter, String docGuid,
			File imageDirectory) throws GatherException, IOException {
		final Integer imgRetryCount = new Integer(novusUtility.getImgRetryCount());
		Integer novusRetryCounter = 0;
		String collection = null;
		ImgMetadataInfo imgMetadataInfo = null;
		boolean missingImage = true;

		while (novusRetryCounter < imgRetryCount) {
			missingImage = true;
			try {

				BLOB blob = find.getBLOB(collection, imageGuid);
				String mimeType = blob.getMimeType();
				String imgMetadata = blob.getMetaData();

				if (!isEmpty(mimeType) && !isEmpty(imgMetadata) ) {
					
					SAXParserFactory parserFactory = SAXParserFactory.newInstance();
					parserFactory.setNamespaceAware(true);
					XMLReader reader = parserFactory.newSAXParser().getXMLReader();
					ImageMetadataHandler imageMetadataHandler = new ImageMetadataHandler();
					reader.setContentHandler(imageMetadataHandler);
					reader.parse(new InputSource(new StringReader(imgMetadata)));
					imgMetadataInfo = imageMetadataHandler.getImgMetadataInfo();

					MediaType mediaType = MediaType.valueOf(mimeType);

					String extension = null;					
					File imageFile = null;

					FileOutputStream fileOuputStream = null;
					try {
						if (mediaType.getType().equals("image") && mediaType.getSubtype().equals(TIF)) {
							imageFile = new File(imageDirectory, imageGuid + "." + PING_EXTENTION);
							extension = PING_EXTENTION;
							ImageConverter.convertByteImg(blob.getContents(), imageFile.getAbsolutePath(), PING_FORMAT);
						} else {
							imageFile = new File(imageDirectory, imageGuid + "." + mediaType.getSubtype());
							fileOuputStream = new FileOutputStream(imageFile);
							fileOuputStream.write(blob.getContents());
							extension = mediaType.getSubtype();
						}
						if (mediaType.getType().equals("image")
								&& !ArrayUtils.contains(KNOWN_IMG_FORMATS, mediaType.getSubtype().toUpperCase())) {
							Log.debug(" Unfamiliar Image format " + mediaType.getSubtype() + " found for imageGuid "
									+ imageGuid);
						}

						imgMetadataInfo.setMimeType(mediaType.getType() + "/" + extension);
						imgMetadataInfo.setSize(imageFile.length());
						imgMetadataInfo.setDocGuid(docGuid);
						imgMetadataInfo.setImgGuid(imageGuid);

					} catch (IOException ex) {
						Log.error("Failed while writing the image for imageGuid " + imageGuid);
						GatherException ge = new GatherException("IMG IO Exception for imageGuid " + imageGuid, ex,
								GatherResponse.CODE_FILE_ERROR);
						throw ge;
					} finally {
						if (fileOuputStream != null) {
							fileOuputStream.close();
						}
					}
					
					missingImage = false;

				}
				else{
					Log.error("MimeType/Metadata is null for image Guid "+imageGuid+". Throwing NOVUS exception explicitly");
					throw new Exception("NOVUS Exception");
				}

				break;
			} catch (final Exception exception) {
				try {
					Log.error("Exception ocuured while retreiving image for imageGuid " + imageGuid);
					novusRetryCounter = novusUtility.handleException(exception, novusRetryCounter, imgRetryCount);
				}  catch (Exception e) {
					Log.error("Exception in handleException() call from getImagesAndMetadata() " + e.getMessage());
					novusRetryCounter++;
				}
			}

		}
		
		if (missingImage) {
			Log.error("Could not find dynamic image in NOVUS for imageGuid " + imageGuid);
			if(!uniqueImageGuids.contains(imageGuid)){
				missingImageCount++;
				uniqueImageGuids.add(imageGuid);
			}
			
			writeFailedImageGuidToFile(missingImageFileWriter, imageGuid, docGuid);
		}

		return imgMetadataInfo;

	}

	private static void writeFailedImageGuidToFile(Writer missingImageFileWriter, String imageGuid, String docGuid)
			throws IOException {
		missingImageFileWriter.write(imageGuid + "," + docGuid);
		missingImageFileWriter.write("\n");
	}
	
	public boolean isEmpty(String string){
		if(string == null){
			return true;
		}
		else{
			return StringUtils.isEmpty(string.trim());
		}
	}

	/**
	 * Reads the contents of a text file and return each line as an element in
	 * the returned list. The file is assumed to already exist.
	 * 
	 * @file textFile the text file to process
	 * @return a map of text strings, representing each file of the specified
	 *         file
	 */
	private static Map<String, String> readLinesFromTextFile(File textFile) throws IOException {
		Map<String, String> imgDocGuidMap = new HashMap<String, String>();
		FileReader fileReader = new FileReader(textFile);
		BufferedReader reader = new BufferedReader(fileReader);
		try {

			String textLine;
			while ((textLine = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(textLine)) {
					String[] imgGuids = textLine.split("\\|");
					if (imgGuids.length > 1) {
						imgDocGuidMap.put(imgGuids[0].trim(), imgGuids[1]);
					} else {
						imgDocGuidMap.put(imgGuids[0].trim(), null);
					}
				}
			}
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
			if (reader != null) {
				reader.close();
			}
		}
		return imgDocGuidMap;
	}

	@Required
	public void setMissingImageGuidsFileBasename(String basename) {
		this.missingImageGuidsFileBasename = basename;
	}

	@Required
	public void setNovusFactory(NovusFactory factory) {
		this.novusFactory = factory;
	}

	@Required
	public void setNovusUtility(NovusUtility novusUtil) {
		this.novusUtility = novusUtil;
	}

	@Required
	public void setSleepIntervalBetweenImages(long interval) {
		this.sleepIntervalBetweenImages = interval;
	}

}
