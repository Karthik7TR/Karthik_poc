package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.CLASS;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.HREF;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.ID;
import static com.thomsonreuters.uscl.ereader.core.MarkupConstants.NAME;

@Service
public class InnerLinksTransformation implements JsoupTransformation {
    private static final String ANCHOR_TEMPLATE = "co_link_codessectionanchor_";
    private static final String LINK_TEMPLATE = "co_link_codessectioncontents_";
    private static final String ANCHOR_SELECTOR = String.format("[id^=%s]", ANCHOR_TEMPLATE);
    private static final String LINK_ID_SELECTOR = String.format("[id^=%s]", LINK_TEMPLATE);
    private static final String CODES_SECTION_ID_GROUP = "codesSectionId";
    private static final String SHARP_SIGN = "#";
    private static final String WESTLAW = "westlaw";
    private static final Pattern LINK_ID_PATTERN = Pattern.compile(String.format("%s[0-9]+_id_(?<%s>.*)", LINK_TEMPLATE, CODES_SECTION_ID_GROUP));

    @Override
    public void transform(final File file, final Document document, final BookStep bookStep) {
        cleanUpAnchors(document);
        cleanUpLinks(document);
    }

    private void cleanUpAnchors(final Document document) {
        document.select(ANCHOR_SELECTOR).forEach(anchor -> {
            String anchorName = anchor.attr(ID);
            anchor.removeAttr(ID);
            anchor.removeAttr(CLASS);
            anchor.removeAttr(HREF);
            anchor.text(StringUtils.EMPTY);
            anchor.attr(NAME, anchorName);
        });
    }

    private void cleanUpLinks(final Document document) {
        document.select(LINK_ID_SELECTOR).forEach(linkMark -> {
            Element mainLink = linkMark.nextElementSibling();
            if (mainLink != null) {
                String id = linkMark.attr(ID);
                String href = mainLink.attr(HREF);
                Matcher matcher = LINK_ID_PATTERN.matcher(id);
                if (isWestlawLink(href) && matcher.matches()) {
                    mainLink.attr(HREF, SHARP_SIGN + ANCHOR_TEMPLATE + matcher.group(CODES_SECTION_ID_GROUP));
                }
            }
            linkMark.remove();
        });
    }

    private boolean isWestlawLink(final String href) {
        return href.contains(WESTLAW);
    }
}
