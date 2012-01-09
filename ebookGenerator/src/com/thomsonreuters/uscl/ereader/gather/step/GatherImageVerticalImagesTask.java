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
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * Fetch book images from the Image Vertical REST web service and save them
 * into a specified image destination directory.
 */
public class GatherImageVerticalImagesTask extends AbstractSbTasklet {
	//private static final Logger log = Logger.getLogger(AssembleEbook.class);
	private ImageService imageService;

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
		JobInstance jobInstance = jobExecution.getJobInstance();
		JobParameters jobParams = jobInstance.getJobParameters();
		
		// Assert the state of the filesystem image directory and expected input files
		File imageDestinationDirectory = new File(getRequiredStringProperty(jobExecutionContext,
												JobExecutionKey.EBOOK_GATHER_IMAGE_DYNAMIC_DIR_PATH));
		File imageGuidFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.EBOOK_GATHER_IMAGE_GUIDS_FILE_PATH));
		Assert.isTrue(imageDestinationDirectory.exists(),
						String.format("The image destination directory does not exist in the filesystem: " + imageDestinationDirectory,
						imageDestinationDirectory.getAbsolutePath()));
		Assert.isTrue(imageDestinationDirectory.canWrite(),
						String.format("The image destination directory is not writable: " + imageDestinationDirectory,
						imageDestinationDirectory.getAbsolutePath()));
		Assert.isTrue(imageGuidFile.exists(), "Image GUID text file does not exist: " + imageGuidFile);
		
		// Remove all existing image files from image destination directory, covers case of this step failing and restarting the step.
		removeAllFilesInDirectory(imageDestinationDirectory);

		// Create list of image guids gathered from a previous step and stored in a flat text file, one per line
		List<String> imageGuids = readImageGuidListFromFile(imageGuidFile);
		
		// Fetch the image metadata and file bytes
		long jobInstanceId = jobInstance.getId();
		String titleId = jobParams.getString(JobParameterKey.TITLE_ID_FULLY_QUALIFIED);
		imageService.fetchImages(imageGuids, imageDestinationDirectory, jobInstanceId, titleId);

		return ExitStatus.COMPLETED;
	}
	
	/**
	 * Reads the list of image GUID's from a flat file, organized one GUID per line.
	 * The file is assumed to have been previously created in another job step.
	 * @return a list of image GUID's for the book being generated.
	 */
	private static List<String> readImageGuidListFromFile(File imageGuidFile) throws IOException {
		List<String> imageGuidList = new ArrayList<String>();
		FileReader fileReader = new FileReader(imageGuidFile);
		try {
			BufferedReader reader = new BufferedReader(fileReader);
			String guid;
			while ((guid = reader.readLine()) != null) {
				if (StringUtils.isNotBlank(guid)) {
					imageGuidList.add(guid.trim());
				}
			}
		} finally {
			if (fileReader != null) {
				fileReader.close();
			}
		}
		return imageGuidList;
	}

	/**
	 * Delete all files in the specified directory.
	 * @param imageDirectory directory whose files will be removed
	 */
	private static void removeAllFilesInDirectory(File imageDirectory) {
		File[] files = imageDirectory.listFiles();
		for (File file : files) {
			file.delete();
		}
	}

	@Required
	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}
}
