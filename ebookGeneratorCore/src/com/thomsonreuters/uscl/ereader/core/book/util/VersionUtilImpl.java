/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.core.book.util;

import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class VersionUtilImpl implements VersionUtil
{
	@Override
	public boolean isMajorUpdate(@NotNull final Version current, @NotNull final Version next)
	{
		Assert.notNull(current);
		Assert.notNull(next);

		final boolean newMajorNumberIsGraterByOne = next.getMajorNumber() == current.getMajorNumber() + 1;
		final boolean newMinorNumberIsZero = next.getMinorNumber() == 0;
		return newMajorNumberIsGraterByOne && newMinorNumberIsZero;
	}
}
