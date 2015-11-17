/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.gather.restclient.service.GatherService;
import com.thomsonreuters.uscl.ereader.gather.util.ImgMetadataInfo;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;


/**
 * Fetch book images from the Image Vertical REST web service and save them
 * into a specified image destination directory.
 */
public class GatherImageVerticalImagesTask extends AbstractSbTasklet {
	//private static final Logger log = Logger.getLogger(GatherImageVerticalImagesTask.class);
	private ImageService imageService;
	private PublishingStatsService publishingStatsService;
	private GatherService gatherService;
	
	@Required
	public void setGatherService(GatherService service) 
	{
		this.gatherService = service;
	}

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
				
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		
		// Assert the state of the filesystem image directory and expected input files
		File dynamicImageDestinationDirectory = new File(getRequiredStringProperty(jobExecutionContext,
														 JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
		File imageGuidFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_TO_DOC_MANIFEST_FILE));
		Assert.isTrue(dynamicImageDestinationDirectory.exists(),
						String.format("The dynamic image destination directory does not exist in the filesystem: " + dynamicImageDestinationDirectory,
						dynamicImageDestinationDirectory.getAbsolutePath()));
		Assert.isTrue(dynamicImageDestinationDirectory.canWrite(),
						String.format("The dynamic image destination directory is not writable: " + dynamicImageDestinationDirectory,
						dynamicImageDestinationDirectory.getAbsolutePath()));
		Assert.isTrue(imageGuidFile.exists(), "The dynamic image GUID list text file does not exist: " + imageGuidFile +
						" - This file contains image GUID's, one per line, that are requested from the Image Vertical REST service.");
		
		// Fetch the image metadata and file bytes
		long jobInstanceId = jobInstance.getId();
		String titleId = bookDefinition.getFullyQualifiedTitleId();
		int imageGuidNum = 0;
		int retrievedCount = 0;
		
		String stepStatus = "Completed";
		try {
			// Remove all existing image files from image destination directory, covers case of this step failing and restarting the step.
			ImageServiceImpl.removeAllFilesInDirectory(dynamicImageDestinationDirectory);
			
			Set<String> imgGuidSet = readImageGuidsFromTextFile(imageGuidFile);
					
			imageGuidNum = imgGuidSet.size();
			
			if (imageGuidNum > 0){
				GatherImgRequest imgRequest = new GatherImgRequest(imageGuidFile,dynamicImageDestinationDirectory, jobInstanceId, bookDefinition.isFinalStage());
				GatherResponse gatherResponse = gatherService.getImg(imgRequest);
				
				if(gatherResponse.getImageMetadataList() != null)
				{
					for (ImgMetadataInfo metadata : gatherResponse.getImageMetadataList()){
						imageService.saveImageMetadata(metadata, jobInstanceId, titleId);
						
					}
				}
				retrievedCount = imageGuidNum;
				
				//gatherResponse.getNodeCount() missing image count
				if (gatherResponse.getMissingImgCount() > 0 || gatherResponse.getMissingMetadaCount() > 0){
					if (gatherResponse.getMissingImgCount() > 0){
						retrievedCount = imageGuidNum - gatherResponse.getNodeCount();
					}					
					throw new ImageException(String.format("Download of dynamic images failed because there were %d missing image(s) and %d missing metadata", gatherResponse.getMissingImgCount(), gatherResponse.getMissingMetadaCount() ));
					
				}
			}			
			
		} catch (ImageException e) {
			stepStatus = "Failed";
			throw e; 
		} catch (Exception e) {
			stepStatus = "Failed";
			throw e;
		} finally {
			PublishingStats jobstatsDoc = new PublishingStats();
			jobstatsDoc.setJobInstanceId(jobInstanceId);
			jobstatsDoc.setGatherImageExpectedCount(imageGuidNum);
			jobstatsDoc.setGatherImageRetrievedCount(retrievedCount);
			jobstatsDoc.setPublishStatus("getDynamicImages : " + stepStatus);
			publishingStatsService.updatePublishingStats(jobstatsDoc, StatsUpdateTypeEnum.GATHERIMAGE);
		}
		
		return ExitStatus.COMPLETED;
	}

	
	/**
	 * Reads the contents of a text file and return each line as an element in the returned list.
	 * The file is assumed to already exist.
	 * @file textFile the text file to process
	 * @return a set of text strings, representing each file of the specified file
	 */
	private static Set<String> readImageGuidsFromTextFile(File textFile) throws IOException {
		Set<String> imgGuidSet = new HashSet<String>();
		FileReader fileReader = new FileReader(textFile);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(fileReader);
			String textLine;
			while ((textLine = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(textLine)) {
					String[] imgGuids = textLine.split("\\|");
					if (imgGuids.length > 1)
					{
						String[] imgGuidsList = imgGuids[1].split(",");
						for (String imgGuid : imgGuidsList)
						{
						  imgGuidSet.add(imgGuid);
						}
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
		return imgGuidSet;
	}
	

	@Required
	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}


	@Required
	public void setPublishingStatsService(
			PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
}
