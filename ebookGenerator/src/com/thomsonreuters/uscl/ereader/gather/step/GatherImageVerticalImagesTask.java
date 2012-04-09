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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * Fetch book images from the Image Vertical REST web service and save them
 * into a specified image destination directory.
 */
public class GatherImageVerticalImagesTask extends AbstractSbTasklet {
	//private static final Logger log = Logger.getLogger(GatherImageVerticalImagesTask.class);
	private ImageService imageService;

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		JobInstance jobInstance = getJobInstance(chunkContext);
		JobParameters jobParams = jobInstance.getJobParameters();
		
		BookDefinition bookDefinition = (BookDefinition)jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		
		// Assert the state of the filesystem image directory and expected input files
		File dynamicImageDestinationDirectory = new File(getRequiredStringProperty(jobExecutionContext,
														 JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
		File imageGuidFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_DYNAMIC_GUIDS_FILE));
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
		List<String> imageGuids = readLinesFromTextFile(imageGuidFile);
		
		// Fetch the image metadata and file bytes
		long jobInstanceId = jobInstance.getId();
		String titleId = bookDefinition.getFullyQualifiedTitleId();
		imageService.fetchImageVerticalImages(imageGuids, dynamicImageDestinationDirectory, jobInstanceId, titleId);

		return ExitStatus.COMPLETED;
	}
	
	/**
	 * Reads the contents of a text file and return each line as an element in the returned list.
	 * The file is assumed to already exist.
	 * @file textFile the text file to process
	 * @return a list of text strings, representing each file of the specified file
	 */
	public static List<String> readLinesFromTextFile(File textFile) throws IOException {
		List<String> lineList = new ArrayList<String>();
		FileReader fileReader = new FileReader(textFile);
		try {
			BufferedReader reader = new BufferedReader(fileReader);
			String textLine;
			while ((textLine = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(textLine)) {
					lineList.add(textLine.trim());
				}
			}
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}
		return lineList;
	}

	@Required
	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}
}
