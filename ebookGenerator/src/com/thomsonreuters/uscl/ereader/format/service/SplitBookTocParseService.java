package com.thomsonreuters.uscl.ereader.format.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.format.step.DocumentInfo;

public interface SplitBookTocParseService {

	public Map<String,DocumentInfo> generateSplitBookToc(final InputStream tocXml, final OutputStream splitTocXml,
			final List<String> splitTocGuidList, final String titleBreakLabel);

}
