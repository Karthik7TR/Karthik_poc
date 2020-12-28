package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class RemoveGapsBetweenChaptersService implements JsoupTransformation {
    private static final String COMMENTARY_ENHANCEMENT_CLASS = "co_commentaryEnhancement";
    private static final String REMOVE_MIN_HEIGHT_CLASS = "remove-min-height";

    @Override
    public void transform(final File file, final Document document, final BookStep bookStep) {
        if (bookStep.getBookDefinition().isPrintPageNumbers()) {
            document.getElementsByClass(COMMENTARY_ENHANCEMENT_CLASS).forEach(element ->
                    element.addClass(REMOVE_MIN_HEIGHT_CLASS));
        }
    }
}
