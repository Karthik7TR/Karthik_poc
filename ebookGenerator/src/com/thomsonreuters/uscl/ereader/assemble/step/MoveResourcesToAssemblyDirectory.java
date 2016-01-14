/*
* Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved.
* Proprietary and Confidential information of TRGR. Disclosure, Use or
* Reproduction without the written authorization of TRGR is prohibited
*/
package com.thomsonreuters.uscl.ereader.assemble.step;

import java.io.File;
import java.io.IOException;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

/**
 * This step is responsible for moving resources, identified by well-known JobExecutionKeys, to the assembly directory
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 *
 */
public class MoveResourcesToAssemblyDirectory extends AbstractSbTasklet {
	

	
	/**
	 * To update publishingStatsService table.
	 */
	private PublishingStatsService publishingStatsService;
	
	private MoveResourcesUtil moveResourcesUtil;
	
	/* (non-Javadoc)
	 * @see com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet#executeStep(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public ExitStatus executeStep(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		Long jobId = getJobInstance(chunkContext).getId();
		PublishingStats jobstats = new PublishingStats();
	    jobstats.setJobInstanceId(jobId);
	    String publishStatus = "Completed";
		
	    try 
	    {
			File ebookDirectory = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey  .EBOOK_DIRECTORY));
			File assetsDirectory = createAssetsDirectory(ebookDirectory);
			File artworkDirectory = createArtworkDirectory(ebookDirectory);
			File documentsDirectory = createDocumentsDirectory(ebookDirectory);
			
			File frontMatter = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_FRONT_MATTER_HTML_DIR));
			moveResourcesUtil.copySourceToDestination(frontMatter, documentsDirectory);
			
			File transformedDocsDir= new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.FORMAT_DOCUMENTS_READY_DIRECTORY_PATH));
			moveResourcesUtil.copySourceToDestination(transformedDocsDir, documentsDirectory);
			
			//Images
			File dynamicImagesDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_DYNAMIC_DEST_DIR));
			File staticImagesDir = new File(getRequiredStringProperty(jobExecutionContext, JobExecutionKey.IMAGE_STATIC_DEST_DIR));
			moveResourcesUtil.copySourceToDestination(dynamicImagesDir, assetsDirectory);
			moveResourcesUtil.copySourceToDestination(staticImagesDir, assetsDirectory);
			
			
			
			moveResourcesUtil.moveCoverArt(jobExecutionContext, artworkDirectory);
			moveResourcesUtil.moveFrontMatterImages(jobExecutionContext, assetsDirectory,true);		
			moveResourcesUtil.moveStylesheet(assetsDirectory);
	    }
	    catch (Exception e)
	    {
	    	publishStatus = "Failed";
	    	throw (e);
	    }
		finally 
		{
		    jobstats.setPublishStatus("moveResourcesToAssemblyDirectory: " + publishStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}
			
		return ExitStatus.COMPLETED;
	}

	private File createDocumentsDirectory(File ebookDirectory) throws IOException {
		return new File(ebookDirectory, "documents");
	}

	private File createArtworkDirectory(File ebookDirectory) {
		File artworkDirectory = new File(ebookDirectory, "artwork");
		artworkDirectory.mkdirs();
		return artworkDirectory;
	}

	private File createAssetsDirectory (final File parentDirectory) {
		File assetsDirectory = new File(parentDirectory, "assets");
		return assetsDirectory;
	}
	

	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}
	
	public MoveResourcesUtil getMoveResourcesUtil() {
		return moveResourcesUtil;
	}

	@Required
	public void setMoveResourcesUtil(MoveResourcesUtil moveResourcesUtil) {
		this.moveResourcesUtil = moveResourcesUtil;
	}
}
