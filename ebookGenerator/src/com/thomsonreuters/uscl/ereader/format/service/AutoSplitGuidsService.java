package com.thomsonreuters.uscl.ereader.format.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;

public interface AutoSplitGuidsService {

	public List<String> getAutoSplitNodes(InputStream tocInputStream, BookDefinition bookDefinition,
			Integer tocNodeCount, Long jobInstanceId, boolean metrics, Map<String, String> splitGuidTextMap);

	public Map<String, String> getSplitGuidTextMap();

}
