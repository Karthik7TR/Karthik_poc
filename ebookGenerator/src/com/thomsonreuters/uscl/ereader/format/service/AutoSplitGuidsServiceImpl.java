package com.thomsonreuters.uscl.ereader.format.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.SAXException;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.AutoSplitNodesHandler;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;

public class AutoSplitGuidsServiceImpl implements AutoSplitGuidsService {

	BookDefinitionService bookDefinitionService;
	PublishingStatsService publishingStatsService;
	EBookAuditService eBookAuditService;

	private static final Logger LOG = Logger.getLogger(AutoSplitGuidsServiceImpl.class);
	private Map<String, String> splitGuidTextMap = new HashMap<String, String>();

	public Map<String, String> getSplitGuidTextMap() {
		return splitGuidTextMap;
	}

	public void setSplitGuidTextMap(Map<String, String> splitGuidTextMap) {
		this.splitGuidTextMap = splitGuidTextMap;
	}

	public EBookAuditService geteBookAuditService() {
		return eBookAuditService;
	}

	@Required
	public void seteBookAuditService(EBookAuditService eBookAuditService) {
		this.eBookAuditService = eBookAuditService;
	}

	public PublishingStatsService getPublishingStatsService() {
		return publishingStatsService;
	}

	@Required
	public void setPublishingStatsService(PublishingStatsService publishingStatsService) {
		this.publishingStatsService = publishingStatsService;
	}

	public List<String> getAutoSplitNodes(InputStream tocInputStream, BookDefinition bookDefinition,
			Integer tocNodeCount, Long jobInstanceId, boolean metrics) {
		try {

			List<String> splitTocGuidList = new ArrayList<String>();

			//Check split documents in database in case we might have deleted in earlier steps
			List<SplitDocument> persistedSplitDocuments = bookDefinitionService.findSplitDocuments(bookDefinition
					.getEbookDefinitionId());

			if (persistedSplitDocuments != null && persistedSplitDocuments.size() > 0 && !metrics) {
				for (SplitDocument splitDocument : persistedSplitDocuments) {
					splitTocGuidList.add(splitDocument.getTocGuid());
				}
				return splitTocGuidList;
			}
			
			Integer thresholdValue = bookDefinition.getDocumentTypeCodes().getThresholdValue();
			Integer thresholdPercent = bookDefinition.getDocumentTypeCodes().getThresholdPercent();
			int partSize = getSizeforEachPart(thresholdValue, tocNodeCount);			
			AutoSplitNodesHandler autoSplitNodesFilter = new AutoSplitNodesHandler(partSize, thresholdPercent);
			autoSplitNodesFilter.parseInputStream(tocInputStream);
			
			this.splitGuidTextMap = autoSplitNodesFilter.getSplitTocTextMap();
			splitTocGuidList = autoSplitNodesFilter.getSplitTocGuidList();

			List<SplitDocument> splitDocuments = new ArrayList<SplitDocument>();
			int parts = 1;
			for (String node : splitTocGuidList) {
				parts++;
				SplitDocument splitDocument = new SplitDocument();
				splitDocument.setBookDefinition(bookDefinition);
				splitDocument.setTocGuid(node);
				String note = "part" + parts;
				splitDocument.setNote(note);
				splitDocuments.add(splitDocument);
			}
			

			if (!metrics) {
				bookDefinitionService.saveSplitDocumentsforEBook(bookDefinition.getEbookDefinitionId(), splitDocuments,
						parts);
				EbookAudit eBookAudit = publishingStatsService.findAuditInfoByJobId(jobInstanceId);
				String splitDocumentsConcat = maxString(concatString(splitDocuments), EbookAudit.MAX_CHARACTER_2048);
				eBookAuditService.updateSplitDocumentsAudit(eBookAudit, splitDocumentsConcat, parts);
			}

			return splitTocGuidList;

		} catch (ParserConfigurationException e) {
			throw new RuntimeException("Failed to configure SAX Parser when generating title manifest.", e);
		} catch (SAXException e) {
			throw new RuntimeException("A SAXException occurred while generating the title manifest.", e);
		} catch (IOException e) {
			throw new RuntimeException("An IOException occurred while generating the title manifest.", e);
		} catch (GenericJDBCException e) {
			e.printStackTrace();
			throw new RuntimeException("An GenericJDBCException occurred while generating the title manifest.", e);
		}
	}

	private String maxString(String buffer, int maxCharacters) {
		return StringUtils.abbreviate(buffer.toString(), maxCharacters);
	}

	private String concatString(Collection<?> collection) {
		StringBuilder buffer = new StringBuilder();
		for (Object item : collection) {
			buffer.append(item.toString());
			buffer.append(", ");
		}

		return buffer.toString();
	}

	public int getSizeforEachPart(Integer thresholdValue, Integer tocNodeCount) {
		int partSize = 0;
		int parts = (tocNodeCount / thresholdValue) + 1;
		partSize = tocNodeCount / parts;
		LOG.debug("Total parts based on the node Size "+parts+". Approximate split size "+partSize);
		return partSize;

	}

	public BookDefinitionService getBookDefinitionService() {
		return bookDefinitionService;
	}

	@Required
	public void setBookDefinitionService(BookDefinitionService bookDefinitionService) {
		this.bookDefinitionService = bookDefinitionService;
	}

}
