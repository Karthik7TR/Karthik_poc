package com.thomsonreuters.uscl.ereader.format.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

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
			Integer tocNodeCount, Long jobInstanceId, boolean metrics, Map<String, String> splitGuidTextMap) {
		try {

			List<String> splitTocGuidList = new ArrayList<String>();

			List<SplitDocument> persistedSplitDocuments = bookDefinitionService.findSplitDocuments(bookDefinition
					.getEbookDefinitionId());

			if (persistedSplitDocuments != null && persistedSplitDocuments.size() > 0 && !metrics) {
				for (SplitDocument splitDocument : persistedSplitDocuments) {
					splitTocGuidList.add(splitDocument.getTocGuid());
				}
				return splitTocGuidList;
			}

			// update this has been removed for test util
			Integer thresholdValue = bookDefinition.getDocumentTypeCodes().getThresholdValue();
			Integer thresholdPercent = bookDefinition.getDocumentTypeCodes().getThresholdPercent();
			int partSize = getSizeforEachPart(thresholdValue, tocNodeCount);

			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			saxParserFactory.setNamespaceAware(Boolean.TRUE);
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			AutoSplitNodesHandler autoSplitNodesFilter = new AutoSplitNodesHandler(partSize, thresholdPercent);
			autoSplitNodesFilter.setParent(xmlReader);

			Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
			props.setProperty("omit-xml-declaration", "yes");

			Serializer serializer = SerializerFactory.getSerializer(props);

			autoSplitNodesFilter.setContentHandler(serializer.asContentHandler());

			Reader reader = new InputStreamReader(tocInputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			autoSplitNodesFilter.parse(is);

			if (splitGuidTextMap == null) {
				splitGuidTextMap = new HashMap<String, String>();
			}

			splitGuidTextMap = autoSplitNodesFilter.getSplitTocTextMap();
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

			this.splitGuidTextMap = splitGuidTextMap;

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
