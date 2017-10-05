package com.thomsonreuters.uscl.ereader.format.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;

public interface SplitBookTocParseService {
    Map<String, DocumentInfo> generateSplitBookToc(
        InputStream tocXml,
        OutputStream splitTocXml,
        List<String> splitTocGuidList,
        String splitTitleId);
}
