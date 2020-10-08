package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Getter
@Setter
public class ExternalLinksTransformation implements JsoupTransformation {
    private static final String SERIAL_NUM_GROUP = "serialNumberGroup";
    private static final String SERIAL_NUM_SHORT_GROUP = "serialNumberShortGroup";
    private static final String SERIAL_NUM_REGEX = ".+serNum=(?<%s>[0]*(?<%s>[0-9]{10}))($|[^0-9].*)";
    private static final Pattern URL_SERIAL_NUM_PATTERN = Pattern.compile(String.format(SERIAL_NUM_REGEX, SERIAL_NUM_GROUP, SERIAL_NUM_SHORT_GROUP));
    private static String HREF = "href";
    private static String DATA_LINK_HREF = "data-link-href";
    @Value("${westlaw.url}")
    private String westlawUrl;
    @Value("${westlaw.canada.url}")
    private String westlawCanadaUrl;

    @Override
    public void transform(final String fileName, final Document document, final BookStep bookStep) {
        if (bookStep.getBookDefinition().isCwBook()) {
            getAttributesWithUrlValue().forEach(attribute -> document.getElementsByAttributeValueContaining(attribute, westlawUrl)
                    .forEach(element -> {
                        String url = replaceWestlawLocation(element.attr(attribute));
                        url = cutLeadingZeroesInSerialNum(url);
                        element.attr(attribute, url);
                    }));
        }
    }

    private String replaceWestlawLocation(final String url) {
        return url.replace(westlawUrl, westlawCanadaUrl);
    }

    private String cutLeadingZeroesInSerialNum(final String url) {
        final Matcher matcher = URL_SERIAL_NUM_PATTERN.matcher(url);
        return matcher.find()
                ? replaceByShortSerialNum(url, matcher)
                : url;
    }

    private String replaceByShortSerialNum(final String url, final Matcher matcher) {
        final String serialNumber = matcher.group(SERIAL_NUM_GROUP);
        final String serialNumberShort = matcher.group(SERIAL_NUM_SHORT_GROUP);
        return serialNumber.length() > serialNumberShort.length()
                ? url.replace(serialNumber, serialNumberShort)
                : url;
    }

    private List<String> getAttributesWithUrlValue() {
        return Arrays.asList(HREF, DATA_LINK_HREF);
    }
}
