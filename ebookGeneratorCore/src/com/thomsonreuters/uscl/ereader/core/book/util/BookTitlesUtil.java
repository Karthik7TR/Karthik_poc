/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.util;

import org.jetbrains.annotations.NotNull;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;

/**
 * Util methods for split book titles
 * 
 * @author Ilia Bochkarev UC220946
 *
 */
public interface BookTitlesUtil {

	/**
	 * Is specified version of book is split
	 * @param book book definition
	 * @param version version of book
	 * @return split book flag
	 */
	boolean isSplitBook(@NotNull BookDefinition book, @NotNull Version version);
}
