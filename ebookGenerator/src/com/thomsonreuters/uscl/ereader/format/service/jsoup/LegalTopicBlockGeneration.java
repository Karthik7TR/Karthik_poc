package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianDigest;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.CanadianDigestService;
import com.thomsonreuters.uscl.ereader.util.NormalizationRulesUtil;
import org.apache.commons.io.FilenameUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.KEY_DOC_GUID_TO_TOPIC_MAP;

@Service
public class LegalTopicBlockGeneration implements JsoupTransformation {
    private static final String CO_PARAGRAPH = ".co_paragraph";
    private static final String CO_TITLE = ".co_title";
    private static final String DIV = "div";
    private static final String SPAN = "span";
    private static final String LEGAL_TOPIC = "legalTopic-";
    private static final String LEGAL_TOPIC_TABLE_SELECTOR = "div[data-link-href] :contains(Legal Topic)";
    private static final String DASH = "—";
    private static final String CLASSIFNUM_INJECTION = "— %s —";
    private final static String STATIC_PART =
            "<label for=\"%1$s\" class=\"abr-clas-label noprint\">" +
                    "<img alt=\"Legal topic information\" inline=\"1\" src=\"er:#legal_topic\"/>" +
                    "</label>" +
                    "<input name=\"%1$s\" id=\"%1$s\" type=\"checkbox\" class=\"abr-clas-input noprint\"/>" +
                    "<div class=\"abr-clas-hdiv noprint\">\n" +
                    "<p style=\"margin-bottom:0em\" class=\"i2\">\n" +
                    "<span style=\"overflow-wrap:break-word\" class=\"fs-small\"><b><i>Browse Legal Topics on Westlaw Canada for more on these topics:</i></b></span>\n" +
                    "</p>\n" +
                    "%2$s" +
                    "</div>";
    private final static String DYNAMIC_PART = "<p style=\"margin-top:0em\" class=\"i2\">\n" +
            "            <span style=\"overflow-wrap:break-word\" class=\"fs-small\"><a href=\"%s\" class=\"tr_op_url\" target=\"_blank\">%s</a></span>\n" +
            "          </p>\n";

    @Autowired
    private CanadianDigestService canadianDigestService;

    @Override
    public void preparationsBeforeAll(final BookStep bookStep) {
        bookStep.setJobExecutionProperty(KEY_DOC_GUID_TO_TOPIC_MAP, createDocGuidToTopicMap(bookStep.getJobInstanceId()));
    }

    @Override
    public void transform(final File file, final Document document, final BookStep bookStep) {
        final String docGuid = FilenameUtils.removeExtension(file.getName());
        final Map<String, List<CanadianDigest>> docGuidToTopicMap = getGuidToTopicMapFromStep(bookStep);
        if (docGuidToTopicMap.containsKey(docGuid)) {
            final String legalTopic = buildLegalTopic(docGuidToTopicMap.get(docGuid), docGuid);
            Stream.of(getLegalTopicPlaceholder(document),
                    Optional.ofNullable(document.selectFirst(CO_TITLE)),
                    Optional.ofNullable(document.selectFirst(CO_PARAGRAPH)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElse(document.selectFirst(DIV))
                    .after(legalTopic);
        }
    }

    @Override
    public void clear(final BookStep bookStep) {
        bookStep.getJobExecutionContext().remove(KEY_DOC_GUID_TO_TOPIC_MAP);
    }

    private Optional<Element> getLegalTopicPlaceholder(final Document document) {
        final Element placeholder = new Element(SPAN);
        return Optional.ofNullable(document.selectFirst(LEGAL_TOPIC_TABLE_SELECTOR))
                .map(element -> {
                    element.parent().after(placeholder).remove();
                    return placeholder;
                });
    }

    private String buildLegalTopic(final List<CanadianDigest> legalTopicMetadata, final String docGuid) {
        final String links = legalTopicMetadata.stream()
                .map(item -> {
                    String linkText = NormalizationRulesUtil.replaceHyphenToDash(item.getClassification())
                            .replaceFirst(DASH, String.format(CLASSIFNUM_INJECTION, item.getClassifnum()));
                    return String.format(DYNAMIC_PART, getUrl(item.getClassifnum()), linkText);
                })
                .collect(Collectors.joining());
        return String.format(STATIC_PART, LEGAL_TOPIC + docGuid, links);
    }

    private String getUrl(final String classifnum) {
        return "http://nextcanada.westlaw.com/Browse/Home/Taxonomy/TaxonomyTOC/" +
                classifnum +
                "/View.html?transitionType=Default&amp;contextData=(sc.Default)&amp;vr=3.0&amp;rs=cblt1.0";
    }

    private Map<String, List<CanadianDigest>> createDocGuidToTopicMap(final Long jobInstanceId) {
        return canadianDigestService.findAllByJobInstanceId(jobInstanceId).stream()
                .collect(Collectors.groupingBy(CanadianDigest::getDocUuid));
    }

    private Map<String, List<CanadianDigest>> getGuidToTopicMapFromStep(final BookStep bookStep) {
        return (Map<String, List<CanadianDigest>>) bookStep.getJobExecutionProperty(KEY_DOC_GUID_TO_TOPIC_MAP);
    }
}
