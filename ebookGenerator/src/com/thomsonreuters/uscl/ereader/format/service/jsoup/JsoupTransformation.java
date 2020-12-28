package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jsoup.nodes.Document;

import java.io.File;

public interface JsoupTransformation {
    default void preparationsBeforeAll(BookStep bookStep) {
    }

    default void preparations(Document document) {
    }

    void transform(File file, Document document, BookStep bookStep);

    default void clear(BookStep bookStep) {
    }
}
