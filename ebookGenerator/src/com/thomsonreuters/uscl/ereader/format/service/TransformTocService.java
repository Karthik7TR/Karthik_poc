package com.thomsonreuters.uscl.ereader.format.service;

import org.jsoup.nodes.Document;

public interface TransformTocService {
    void transformToc(Document tocDocument);
}
