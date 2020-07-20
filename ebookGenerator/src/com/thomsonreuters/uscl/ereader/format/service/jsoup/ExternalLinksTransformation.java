package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Getter
@Setter
public class ExternalLinksTransformation implements JsoupTransformation {
    private static String HREF = "href";
    private static String DATA_LINK_HREF = "data-link-href";
    @Value("${westlaw.url}")
    private String westlawUrl;
    @Value("${westlaw.canada.url}")
    private String westlawCanadaUrl;

    @Override
    public void preparations(final Document document) {

    }

    @Override
    public void transform(final String fileName, final Document document, final BookStep bookStep) {
        if (bookStep.getBookDefinition().isCwBook()) {
            getAttributesWithUrlValue().forEach(attribute -> document.getElementsByAttributeValueContaining(attribute, westlawUrl)
                    .forEach(element -> {
                        String url = element.attr(attribute).replace(westlawUrl, westlawCanadaUrl);
                        element.attr(attribute, url);
                    }));
        }
    }

    private List<String> getAttributesWithUrlValue() {
        return Arrays.asList(HREF, DATA_LINK_HREF);
    }
}
