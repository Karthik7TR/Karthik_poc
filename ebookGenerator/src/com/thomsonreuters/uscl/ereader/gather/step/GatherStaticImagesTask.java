/*
* Copyright 2011: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.util.List;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageService;
import com.thomsonreuters.uscl.ereader.gather.image.service.ImageServiceImpl;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;

/**
 * Fetch static book images from a filesystem tree and copy them to the holding destination directory.
 */
public class GatherStaticImagesTask extends AbstractSbTasklet {
	//private static final Logger log = Logger.getLogger(GatherStaticImagesTask.class);
	private ImageService imageService;

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		
		// Assert the state of the filesystem image directory and expected input files
		File staticImageDestinationDirectory = new File(getRequiredStringProperty(jobExecutionContext,
														JobExecutionKey.IMAGE_STATIC_DEST_DIR));
		File manifestFile = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_STATIC_MANIFEST_FILE));
		Assert.isTrue(staticImageDestinationDirectory.exists(),
						String.format("The static image destination directory does not exist in the filesystem: " + staticImageDestinationDirectory,
						staticImageDestinationDirectory.getAbsolutePath()));
		Assert.isTrue(staticImageDestinationDirectory.canWrite(),
						String.format("The static image destination directory is not writable: " + staticImageDestinationDirectory,
						staticImageDestinationDirectory.getAbsolutePath()));
		Assert.isTrue(manifestFile.exists(), "The static image manifest file does not exist: " + manifestFile +
						" - This file contains static image basenames (no directory path info), one per line, that are copied to a destination directory.");
		
		// Remove all existing image files from the static image destination directory, covers case of this step failing and restarting the step.
		ImageServiceImpl.removeAllFilesInDirectory(staticImageDestinationDirectory);

		// Read the image file basenames, one per line from the manifest file
		List<String> basenames = GatherImageVerticalImagesTask.readLinesFromTextFile(manifestFile);
		
		// Copy all the static image files from their location in the tree to the destination directory
		imageService.fetchStaticImages(basenames, staticImageDestinationDirectory);
		
		return ExitStatus.COMPLETED;
	}
	


	@Required
	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}
}
