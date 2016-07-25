/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */

package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.FileWriterWithEncoding;
 import org.apache.log4j.LogManager; import org.apache.log4j.Logger;
import org.springframework.http.MediaType;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntity;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageMetadataEntityKey;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;

/**
 * Defines the service that generates the ImageMetadata xml block that is appended
 * to the Novus document to help the transform embed images properly during the
 * transformation process.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public class GenerateImageMetadataBlockServiceImpl implements
		GenerateImageMetadataBlockService {
	
	private static final Logger LOG = LogManager.getLogger(GenerateImageMetadataBlockServiceImpl.class);

	private ImageService imgService;

	public void setimgService(ImageService imgService)
	{
		this.imgService = imgService;
	}

	/**
	 * Using the document to image manifest as a blue print the service generates one
	 * file per document of ImageMetadata blocks for all images embedded in the documents.
	 * If there are no images embedded an empty file will be created.
	 * 
	 * @param docToImgManifest manifest of what images are embedded in each document
	 * @param targetDirectory directory to which the generated Image Metadata blocks will be writen to
	 * @param jobInstanceId job instance id assigned to the current job run
	 * 
	 * @return number of ImageMetadata block files generated
	 */
	@Override
	public int generateImageMetadata(final File docToImgManifest, 
			final File targetDirectory, final long jobInstanceId) 
		throws EBookFormatException
	{
		LOG.info("Generating ImageMetadata block files for each document...");
		
		Map<String, List<String>> docImgMap = new HashMap<String, List<String>>();
		readDocToImgMap(docToImgManifest, docImgMap);
		int numDocsGenerated = 0;

		for (String docGuid : docImgMap.keySet())
		{
			numDocsGenerated++;
			File imgMetadataFile = new File(targetDirectory, docGuid + ".imgMeta");
			
			StringBuffer imageMetadataBlocks = new StringBuffer();
			
			imageMetadataBlocks.append("<ImageMetadata>");
			for (String imgId : docImgMap.get(docGuid))
			{
				appendImageBlock(imageMetadataBlocks, jobInstanceId, imgId, docGuid);
			}
			imageMetadataBlocks.append("</ImageMetadata>");
			
			createImageMetadataFile(imgMetadataFile, imageMetadataBlocks);
		}

		LOG.info("Generated ImageMetadata block files for " + numDocsGenerated + " documents.");
		
		return numDocsGenerated;
	}
	
	/**
	 * Writes the generated ImageMetadata string to file.
	 * 
	 * @param output file to which the generated ImageMetadataBlock will be output
	 * @param xmlText the ImageMetadata xml
	 * 
	 * @throws EBookFormatException if unable to write to specified file
	 */
	protected void createImageMetadataFile(File output, StringBuffer xmlText)
		throws EBookFormatException
	{
		BufferedWriter writer = null;
		
		try
		{
			writer = new BufferedWriter(new FileWriterWithEncoding(output, "UTF-8"));
			
			writer.append(xmlText.toString());
		}
		catch (IOException e)
		{
			String message = "Could not write out ImageMetadata to following file: " + 
					output.getAbsolutePath();
			LOG.error(message);
			throw new EBookFormatException(message, e);
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch (IOException e)
			{
				LOG.error("Unable to close generated ImageMetadata file.", e);
			}
		}
	}
	
	/**
	 * Appends the ImageMetadata for the passed in image guid to the passed in StringBuffer.
	 * 
	 * @param imgBlocks StringBuffer containing all the built up ImageMetadata blocks
	 * @param jobId job instance id used for image metadata lookup
	 * @param imgGuid guid for the ImageMetadata block to be added to the StringBuffer
	 * 
	 * @throws EBookFormatException if image metadata for the passed in image guid is not found
	 */
	protected void appendImageBlock(StringBuffer imgBlocks, long jobId, String imgGuid, String docGuid)
		throws EBookFormatException
	{
		ImageMetadataEntityKey key = new ImageMetadataEntityKey(jobId, imgGuid, docGuid);
		ImageMetadataEntity imgMetadata = imgService.findImageMetadata(key);
		if (imgMetadata == null)
		{
			String message = "Could not find the image metadata for the following image guid: " + imgGuid;
			LOG.error(message);
			throw new EBookFormatException(message);
		}
		
		imgBlocks.append("<n-metadata guid=\"");
		imgBlocks.append(imgGuid);
		imgBlocks.append("\" ttype=\"\">");
		imgBlocks.append("<md.block>");
		imgBlocks.append("<md.image.format>"); 
		imgBlocks.append(imgMetadata.getMediaType().toString()); 
		imgBlocks.append("</md.image.format>");
		imgBlocks.append("<md.image.block>");
		imgBlocks.append("<md.image.bytes>");
		imgBlocks.append(imgMetadata.getSize());
		imgBlocks.append("</md.image.bytes>");
		imgBlocks.append("<md.image.dpi>");
		imgBlocks.append(imgMetadata.getDpi());
		imgBlocks.append("</md.image.dpi>");
		imgBlocks.append("<md.image.height>");
		imgBlocks.append(imgMetadata.getHeight());
		imgBlocks.append("</md.image.height>");
		imgBlocks.append("<md.image.width>");
		imgBlocks.append(imgMetadata.getWidth());
		imgBlocks.append("</md.image.width>");
		imgBlocks.append("<md.image.units>");
		imgBlocks.append(imgMetadata.getDimUnits());
		imgBlocks.append("</md.image.units>");		
		imgBlocks.append("</md.image.block>");
		imgBlocks.append("</md.block>");
		imgBlocks.append("<md.image.renderType>");
		if(imgMetadata.getMediaType().equals(new MediaType("application", "pdf")))
		{
			imgBlocks.append("PDFLink");
		}
		else if (imgMetadata.getMediaType().equals(MediaType.IMAGE_GIF) ||
				imgMetadata.getMediaType().equals(MediaType.IMAGE_JPEG) ||
				imgMetadata.getMediaType().equals(MediaType.IMAGE_PNG))
		{
			imgBlocks.append("Image");
		}
		else
		{
			String message = "Encountered unexpected " + imgMetadata.getMediaType().toString() + 
					" media type associated with " + imgGuid + " image.";
			LOG.error(message);
			throw new EBookFormatException(message);
		}
		imgBlocks.append("</md.image.renderType>");		
		imgBlocks.append("</n-metadata>");
	}

	/**
	 * Reads in the mapping of images in each document and generates an in memory map used to build
	 * the ImageMetadata blocks.
	 * 
	 * @param docToImg file that contains the mapping of images in each document
	 * @param docToImgMap map that will be loaded of all images embedded in the documents
	 * 
	 * @throws EBookFormatException 
	 */
	protected void readDocToImgMap(File docToImg, Map<String, List<String>> docToImgMap)
		throws EBookFormatException
	{
		BufferedReader reader = null;
		try
		{
			LOG.info("Reading in Doc to Image map file...");
			int numDocs = 0;
			int numImgs = 0;
			reader = new BufferedReader(new FileReader(docToImg));
			String input = reader.readLine();
			while (input != null)
			{
				numDocs++;
				String[] line = input.split("\\|", -1);
				if (!line[1].equals(""))
				{
					String[] images = line[1].split(",");
					List<String> imgSet = new ArrayList<String>(Arrays.asList(images));
					numImgs = numImgs + imgSet.size();
					docToImgMap.put(line[0], imgSet);
				}
				else
				{
					docToImgMap.put(line[0], new ArrayList<String>());
				}
				input = reader.readLine();
			}
			LOG.info("Generated a map for " + numDocs + " DOCs with " + numImgs + " image references.");
		}
		catch(IOException e)
		{
			String message = "Could not read the DOC to Image map file: " + 
					docToImg.getAbsolutePath();
			LOG.error(message);
			throw new EBookFormatException(message, e);
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException e)
			{
				LOG.error("Unable to close Document to Image mapping file reader.", e);
			}
		}
	}
}
