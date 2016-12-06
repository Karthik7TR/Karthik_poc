/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book;

import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;

@SuppressWarnings("null")
public class BookMatchers {

	@NotNull
	public static BookDefinition book(String titleId) {
		BookDefinition bookDefinition = new BookDefinition();
		bookDefinition.setFullyQualifiedTitleId(titleId);
		return bookDefinition;
	}

	@NotNull
	public static TitleId titleId(String titleId) {
		return new TitleId(titleId);
	}

	@NotNull
	public static Version version(String version) {
		return new Version(version);
	}

	@NotNull
	public static SplitNodeInfo splitNode(BookDefinition book, String titleId, String version) {
		SplitNodeInfo splitNodeInfo = new SplitNodeInfo();
		splitNodeInfo.setBookDefinition(book);
		splitNodeInfo.setSpitBookTitle(titleId);
		splitNodeInfo.setBookVersionSubmitted(version);
		return splitNodeInfo;
	}

	@NotNull
	public static Set<SplitNodeInfo> splitNodes(SplitNodeInfo... nodes) {
		Set<SplitNodeInfo> splitNodes = new HashSet<>();
		for (SplitNodeInfo node : nodes) {
			splitNodes.add(node);
		}
		return splitNodes;
	}
}
