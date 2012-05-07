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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.thomsonreuters.uscl.ereader.gather.image.domain.ImageException;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.apache.log4j.Logger;


/**
 * Fetch book images from the Image Vertical REST web service and save them
 * into a specified image destination directory.
 */
public class GatherImageVerticalImagesTask extends AbstractSbTasklet {
	private static final Logger log = Logger.getLogger(GatherImageVerticalImagesTask.class);
	private ImageService imageService;
	private PublishingStatsService publishingStatsService;

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
		
		// Remove all existing image files from image destination directory, covers case of this step failing and restarting the step.
		ImageServiceImpl.removeAllFilesInDirectory(dynamicImageDestinationDirectory);

		// Create list of image guids gathered from a previous step and stored in a flat text file, one per line
		Map<String,String> imgDocGuidMap = readLinesFromTextFile(imageGuidFile);
				
		// Fetch the image metadata and file bytes
		long jobInstanceId = jobInstance.getId();
		String titleId = bookDefinition.getFullyQualifiedTitleId();
		int imageGuidNum = imgDocGuidMap.size();
		try {
			
			imageService.fetchImageVerticalImages(imgDocGuidMap, dynamicImageDestinationDirectory, jobInstanceId, titleId);
			updateImageRetrivalStats(imageGuidNum, jobInstanceId,null);

			
		} catch (ImageException e) {
			
			updateImageRetrivalStats(imageGuidNum, jobInstanceId,e);
			throw e; 
		}
		
		return ExitStatus.COMPLETED;
	}



	private void updateImageRetrivalStats(int imageGuidsSize,
			long jobInstanceId,ImageException e) {

		Long jobInstanceLong = jobInstanceId;
		int retrievedCound = imageGuidsSize;
		
		if(null == e){
			retrievedCound = imageGuidsSize;;
		}else{
			retrievedCound = getMissingGuidsCount(e);
		}
		
		PublishingStats jobstatsDoc = new PublishingStats();
		jobstatsDoc.setJobInstanceId(jobInstanceLong);
		jobstatsDoc.setGatherImageExpectedCount(imageGuidsSize);
		jobstatsDoc.setGatherImageRetrievedCount(retrievedCound);
		publishingStatsService.updatePublishingStats(jobstatsDoc, StatsUpdateTypeEnum.GATHERIMAGE);
	}
	
	
	
	private int getMissingGuidsCount(ImageException e) {
		int missingGuidNo = 0;
		if(e != null ){
			String stringMessage = e.getMessage();
			Pattern intsOnly = Pattern.compile("\\d+"); 
			Matcher makeMatch = intsOnly.matcher(stringMessage); 
			makeMatch.find(); 
			String inputInt = makeMatch.group(); 
			missingGuidNo = Integer.parseInt(inputInt);
		}
		return missingGuidNo;
	}	
	/**
	 * Reads the contents of a text file and return each line as an element in the returned list.
	 * The file is assumed to already exist.
	 * @file textFile the text file to process
	 * @return a list of text strings, representing each file of the specified file
	 */
	public static Map<String,String> readLinesFromTextFile(File textFile) throws IOException {
		Map<String,String> imgDocGuidMap = new HashMap<String,String>();
		FileReader fileReader = new FileReader(textFile);
		try {
			BufferedReader reader = new BufferedReader(fileReader);
			String textLine;
			while ((textLine = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(textLine)) {
					String[] imgGuids = textLine.split("\\|");
					if (imgGuids.length > 1)
					{
					  imgDocGuidMap.put(imgGuids[0].trim(),imgGuids[1]);
					}
					else {
						imgDocGuidMap.put(imgGuids[0].trim(),null);
					}
				}
			}
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}
		return imgDocGuidMap;
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
