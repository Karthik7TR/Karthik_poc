/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.assemble.step.AssembleEbook;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * Fetch book images from the Image Vertical REST web service and save them
 * into a specified image destination directory.
 */
public class FetchImagesTask extends AbstractSbTasklet {
	private static final Logger log = Logger.getLogger(AssembleEbook.class);
	private ImageService imageService;

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		StepContext stepContext = chunkContext.getStepContext();
		StepExecution stepExecution = stepContext.getStepExecution();
		JobExecution jobExecution = stepExecution.getJobExecution();
//		ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
		JobInstance jobInstance = jobExecution.getJobInstance();
		JobParameters jobParams = jobInstance.getJobParameters();
		
		String guid = jobParams.getString(JobParameterKey.ROOT_TOC_GUID);
		File imageDestinationDirectory = new File(jobParams.getString(JobExecutionKey.EBOOK_IMAGE_DIRECTORY_PATH));
		Assert.isTrue(imageDestinationDirectory.exists(),
					  String.format("The image destination directory (%s) exists in the filesystem",
							  		imageDestinationDirectory.getAbsolutePath()));

//TODO		imageService.fetchImages(guid);
		
// TODO: handle/verify response result and process non happy path scenarios

		return ExitStatus.COMPLETED;
	}

	@Required
	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}
}
