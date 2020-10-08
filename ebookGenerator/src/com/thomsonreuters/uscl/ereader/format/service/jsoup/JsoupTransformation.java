package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jsoup.nodes.Document;

public interface JsoupTransformation {
    default void preparationsBeforeAll(BookStep bookStep) {
    }

    default void preparations(Document document) {
    }

    void transform(String fileName, Document document, BookStep bookStep);

    default void clear(BookStep bookStep) {
    }
}
