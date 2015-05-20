package com.thomsonreuters.uscl.ereader.gather.step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.item.ExecutionContext;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.format.service.SplitBookTocParseService;
import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import com.thomsonreuters.uscl.ereader.orchestrate.core.tasklet.AbstractSbTasklet;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public class GenerateSplitTocTask extends AbstractSbTasklet {
	// TODO: Use logger API to get Logger instance to job-specific appender.
	private static final Logger LOG = Logger.getLogger(GenerateSplitTocTask.class);
	private PublishingStatsService publishingStatsService;

	private SplitBookTocParseService splitBookTocParseService;
	private static final Long ANALYTICAL = new Long(1);

	private DocMetadataService docMetadataService;

	private FileHandlingHelper fileHandlingHelper;

	private Map<String, DocumentInfo> documentInfoMap = new HashMap<String, DocumentInfo>();
	
	// retrieve list of all transformed files
	List<File> transformedDocFiles = new ArrayList<File>();

	@Override
	public ExitStatus executeStep(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		PublishingStats jobstats = new PublishingStats();
		String publishStatus = "Completed";

		JobInstance jobInstance = getJobInstance(chunkContext);
		Long jobInstanceId = jobInstance.getId();

		ExecutionContext jobExecutionContext = getJobExecutionContext(chunkContext);
		BookDefinition bookDefinition = (BookDefinition) jobExecutionContext.get(JobExecutionKey.EBOOK_DEFINITON);
		List<SplitDocument> splitDocuments = (ArrayList<SplitDocument>) bookDefinition.getSplitDocuments();
		String splitTocFilePath = jobExecutionContext.getString(JobExecutionKey.FORMAT_SPLITTOC_FILE);
		String tocXmlFile = getRequiredStringProperty(jobExecutionContext, JobExecutionKey.GATHER_TOC_FILE);
		String transformDirectory = getRequiredStringProperty(jobExecutionContext,
				JobExecutionKey.FORMAT_TRANSFORMED_DIR);
		File transformDir = new File(transformDirectory);
		String titleBreakLabel = null;

		InputStream tocXml = null;
		OutputStream splitTocXml = null;

		DocumentTypeCode documentTypeCode = bookDefinition.getDocumentTypeCodes();

		titleBreakLabel = getTitleBreakLabel(documentTypeCode, bookDefinition.getEbookNames());

		try {
			tocXml = new FileInputStream(tocXmlFile);
			splitTocXml = new FileOutputStream(splitTocFilePath);
			List<String> splitTocGuidList = new ArrayList<String>();

			for (SplitDocument splitDocument : splitDocuments) {
				splitTocGuidList.add(splitDocument.getTocGuid());
			}

			generateAndUpdateSplitToc(tocXml, splitTocXml, splitTocGuidList, titleBreakLabel, transformDir, jobInstanceId);
			
			

		} catch (Exception e) {
			publishStatus = "Failed";
			throw (e);
		} finally {
			if (tocXml != null) {
				tocXml.close();
			}
			if (splitTocXml != null) {
				splitTocXml.close();
			}
			jobstats.setJobInstanceId(jobInstanceId);
			jobstats.setPublishStatus("generateSplitToc : " + publishStatus);
			publishingStatsService.updatePublishingStats(jobstats, StatsUpdateTypeEnum.GENERAL);
		}
		return ExitStatus.COMPLETED;
	}

	public void generateAndUpdateSplitToc(InputStream tocXml, OutputStream splitTocXml, List<String> splitTocGuidList,
			String titleBreakLabel, File transformDir, Long jobInstanceId) throws Exception {

		LOG.debug("generateSplitBookToc---------------------------------------------------------");
		documentInfoMap = splitBookTocParseService.generateSplitBookToc(tocXml, splitTocXml, splitTocGuidList, titleBreakLabel);
		
		System.out.println("documentInfoMap-3--"+documentInfoMap.size());

		if (transformDir == null || !transformDir.isDirectory()) {
			throw new IllegalArgumentException("transformDir must be a directory, not null or a regular file.");
		}

		try {
			fileHandlingHelper.getFileList(transformDir, transformedDocFiles);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("No transformed files were found in the specified directory "
					+ transformDir.getAbsolutePath(), e);
		}
		
		// Update docmetadata
		for (File docFile : transformedDocFiles) {
			String fileName = docFile.getName();
			String guid = fileName.substring(0, fileName.indexOf("."));
			if (documentInfoMap.containsKey(guid)) {
				DocumentInfo documentInfo = documentInfoMap.get(guid);
				documentInfo.setDocSize(new Long(docFile.length()));
				documentInfoMap.put(guid, documentInfo);
			}
		}
		
		docMetadataService.updateSplitBookFields(jobInstanceId, documentInfoMap);
		
	}

	public String getTitleBreakLabel(DocumentTypeCode documentTypeCode, List<EbookName> names) {

		StringBuffer mainTitle = new StringBuffer();
		String series = "";

		for (EbookName name : names) {
			if (name.getSequenceNum() == 1) {
				mainTitle.append(name.getBookNameText());
				if (documentTypeCode == null
						|| (documentTypeCode != null && !documentTypeCode.getId().equals(ANALYTICAL))) {
					return mainTitle.append(" part").toString();
				}
			}
			// Add series if the content type is Analytical or by default it
			// should be main title.
			else if (name.getSequenceNum() == 2) {
				series = "-" + name.getBookNameText();
			}
		}

		mainTitle.append(series).append(" part");

		LOG.debug("titlebreak label" + mainTitle.toString());

		return mainTitle.toString();

	}

	public void setfileHandlingHelper(FileHandlingHelper fileHandlingHelper) {
		this.fileHandlingHelper = fileHandlingHelper;
	}

	public PublishingStatsService getPublishingStatsService() {
		return publishingStatsService;
	}

	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

	public SplitBookTocParseService getSplitBookTocParseService() {
		return splitBookTocParseService;
	}

	public void setSplitBookTocParseService(SplitBookTocParseService splitBookTocParseService) {
		this.splitBookTocParseService = splitBookTocParseService;
	}

	public Map<String, DocumentInfo> getDocumentInfoMap() {
		return documentInfoMap;
	}

	public void setDocumentInfoMap(Map<String, DocumentInfo> documentInfoMap) {
		this.documentInfoMap = documentInfoMap;
	}
	

	public DocMetadataService getDocMetadataService() {
		return docMetadataService;
	}

	public void setDocMetadataService(DocMetadataService docMetadataService) {
		this.docMetadataService = docMetadataService;
	}


}
