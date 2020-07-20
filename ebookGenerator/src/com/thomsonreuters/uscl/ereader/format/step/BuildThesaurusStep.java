package com.thomsonreuters.uscl.ereader.format.step;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianTopicCode;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.CanadianTopicCodeService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.batch.core.ExitStatus;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@SendFailureNotificationPolicy(FailureNotificationType.GENERATOR)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GENERAL)
public class BuildThesaurusStep extends BookStepImpl {
    private static final String THESAURUS = "thesaurus";
    private static final String ENTRY = "entry";
    private static final String LEAD = "lead";
    private static final String COUNT = "count";

    @Resource(name = "formatFileSystem")
    private FormatFileSystem formatFileSystem;

    @Autowired
    private JsoupService jsoup;

    @Autowired
    private CanadianTopicCodeService canadianTopicCodeService;

    @Override
    public ExitStatus executeStep() throws Exception {
        Map<String, List<CanadianTopicCode>> topicKeys = getTopicKeysMap();

        if (!topicKeys.isEmpty()) {
            buildThesaurus(topicKeys);
        }

        return ExitStatus.COMPLETED;
    }

    private void buildThesaurus(final Map<String, List<CanadianTopicCode>> topicKeys) {
        final File outputDir = formatFileSystem.getFormatDirectory(this);
        final Document document = jsoup.parseXml(THESAURUS);
        document.outputSettings().prettyPrint(false);

        fillThesaurusEntries(topicKeys, document);

        jsoup.saveDocument(outputDir, formatFileSystem.getThesaurusXml(this).getName(), document);
        setJobExecutionProperty(JobExecutionKey.WITH_THESAURUS, Boolean.TRUE);
    }

    private Map<String, List<CanadianTopicCode>> getTopicKeysMap() {
        return buildTopicKeysMap(canadianTopicCodeService.findAllCanadianTopicCodesForTheBook(getJobInstanceId()));
    }

    private Map<String, List<CanadianTopicCode>> buildTopicKeysMap(final List<CanadianTopicCode> canadianTopicCodes) {
        return canadianTopicCodes.stream().collect(groupingBy(CanadianTopicCode::getTopicKey, TreeMap::new, toList()));
    }

    private void fillThesaurusEntries(final Map<String, List<CanadianTopicCode>> topicKeys, final Document document) {
        final Element thesaurus = document.selectFirst(THESAURUS);
        topicKeys.forEach((topicKey, docs) ->
                thesaurus.appendChild(buildEntryElement(topicKey, docs))
        );
    }

    private Element buildEntryElement(final String topicKey, final List<CanadianTopicCode> docs) {
        Element entry = new Element(ENTRY);
        Element lead = buildTextElement(LEAD, topicKey);
        Element count = buildTextElement(COUNT, String.valueOf(docs.size()));
        entry.appendChild(lead);
        entry.appendChild(count);
        return entry;
    }

    private Element buildTextElement(final String tag, final String text) {
        Element textElement = new Element(tag);
        textElement.appendText(text);
        return textElement;
    }
}
