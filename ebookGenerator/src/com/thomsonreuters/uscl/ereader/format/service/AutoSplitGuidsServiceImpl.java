package com.thomsonreuters.uscl.ereader.format.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.core.book.service.EBookAuditService;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.AutoSplitNodesHandler;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.SAXException;

@Slf4j
public class AutoSplitGuidsServiceImpl implements AutoSplitGuidsService {
    private BookDefinitionService bookDefinitionService;
    private PublishingStatsService publishingStatsService;
    private EBookAuditService eBookAuditService;

    private Map<String, String> splitGuidTextMap = new HashMap<String, String>();

    @Override
    public Map<String, String> getSplitGuidTextMap() {
        return splitGuidTextMap;
    }

    public void setSplitGuidTextMap(final Map<String, String> splitGuidTextMap) {
        this.splitGuidTextMap = splitGuidTextMap;
    }

    public EBookAuditService geteBookAuditService() {
        return eBookAuditService;
    }

    @Required
    public void seteBookAuditService(final EBookAuditService eBookAuditService) {
        this.eBookAuditService = eBookAuditService;
    }

    public PublishingStatsService getPublishingStatsService() {
        return publishingStatsService;
    }

    @Required
    public void setPublishingStatsService(final PublishingStatsService publishingStatsService) {
        this.publishingStatsService = publishingStatsService;
    }

    @Override
    public List<String> getAutoSplitNodes(
        final InputStream tocInputStream,
        final BookDefinition bookDefinition,
        final Integer tocNodeCount,
        final Long jobInstanceId,
        final boolean metrics) {
        try {
            List<String> splitTocGuidList = new ArrayList<>();

            //Check split documents in database in case we might have deleted in earlier steps
            final Collection<SplitDocument> persistedSplitDocuments =
                bookDefinitionService.findSplitDocuments(bookDefinition.getEbookDefinitionId());

            if (persistedSplitDocuments != null && persistedSplitDocuments.size() > 0 && !metrics) {
                for (final SplitDocument splitDocument : persistedSplitDocuments) {
                    splitTocGuidList.add(splitDocument.getTocGuid());
                }
                return splitTocGuidList;
            }

            final Integer thresholdValue = bookDefinition.getDocumentTypeCodes().getThresholdValue();
            final Integer thresholdPercent = bookDefinition.getDocumentTypeCodes().getThresholdPercent();
            final int partSize = getSizeforEachPart(thresholdValue, tocNodeCount);
            final AutoSplitNodesHandler autoSplitNodesFilter = new AutoSplitNodesHandler(partSize, thresholdPercent);
            autoSplitNodesFilter.parseInputStream(tocInputStream);

            splitGuidTextMap = autoSplitNodesFilter.getSplitTocTextMap();
            splitTocGuidList = autoSplitNodesFilter.getSplitTocGuidList();

            final List<SplitDocument> splitDocuments = new ArrayList<>();
            int parts = 1;
            for (final String node : splitTocGuidList) {
                parts++;
                final SplitDocument splitDocument = new SplitDocument();
                splitDocument.setBookDefinition(bookDefinition);
                splitDocument.setTocGuid(node);
                final String note = "part" + parts;
                splitDocument.setNote(note);
                splitDocuments.add(splitDocument);
            }

            if (!metrics) {
                bookDefinitionService
                    .saveSplitDocumentsforEBook(bookDefinition.getEbookDefinitionId(), splitDocuments, parts);
                final EbookAudit eBookAudit = publishingStatsService.findAuditInfoByJobId(jobInstanceId);
                final String splitDocumentsConcat =
                    maxString(concatString(splitDocuments), EbookAudit.MAX_CHARACTER_2048);
                eBookAuditService.updateSplitDocumentsAudit(eBookAudit, splitDocumentsConcat, parts);
            }

            return splitTocGuidList;
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException("Failed to configure SAX Parser when generating title manifest.", e);
        } catch (final SAXException e) {
            throw new RuntimeException("A SAXException occurred while generating the title manifest.", e);
        } catch (final IOException e) {
            throw new RuntimeException("An IOException occurred while generating the title manifest.", e);
        } catch (final GenericJDBCException e) {
            e.printStackTrace();
            throw new RuntimeException("An GenericJDBCException occurred while generating the title manifest.", e);
        }
    }

    private String maxString(final String buffer, final int maxCharacters) {
        return StringUtils.abbreviate(buffer.toString(), maxCharacters);
    }

    private String concatString(final Collection<?> collection) {
        final StringBuilder buffer = new StringBuilder();
        for (final Object item : collection) {
            buffer.append(item.toString());
            buffer.append(", ");
        }

        return buffer.toString();
    }

    public int getSizeforEachPart(final Integer thresholdValue, final Integer tocNodeCount) {
        int partSize = 0;
        final int parts = (tocNodeCount / thresholdValue) + 1;
        partSize = tocNodeCount / parts;
        log.debug("Total parts based on the node Size " + parts + ". Approximate split size " + partSize);
        return partSize;
    }

    public BookDefinitionService getBookDefinitionService() {
        return bookDefinitionService;
    }

    @Required
    public void setBookDefinitionService(final BookDefinitionService bookDefinitionService) {
        this.bookDefinitionService = bookDefinitionService;
    }
}
