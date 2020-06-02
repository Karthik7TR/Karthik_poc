package com.thomsonreuters.uscl.ereader.format.service.jsoup;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianDigest;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;

public interface JsoupTransformation {
    void preparations(Document document);
    void transform(String fileName, Document document, BookStep bookStep, Map<String, List<CanadianDigest>> map);
}
