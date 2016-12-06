/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitNodeInfo;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;

public class BookTitlesUtilImpl implements BookTitlesUtil {

	@Override
	public boolean isSplitBook(@NotNull BookDefinition book, @NotNull Version version) {
		Assert.notNull(book);
		Assert.notNull(version);
		
		//TODO replace with lambda when Java 8 will be available
		String versionToCompare = version.getVersionWithoutPrefix();
		for (SplitNodeInfo splitPart : book.getSplitNodes()) {
			String partVersion = splitPart.getBookVersionSubmitted();
			if (versionToCompare.equals(partVersion)) {
				return true;
			}
		}
		return false;
	}

}
