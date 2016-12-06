/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import org.jetbrains.annotations.NotNull;

import com.thomsonreuters.uscl.ereader.deliver.service.TitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.TitleInfoDecorator;

public class ProviewTitle extends TitleInfoDecorator {
	private static final String STATUS_REVIEW = "Review";
	
	private boolean canRemove;
	private boolean canPromoteBook;

	public ProviewTitle(@NotNull TitleInfo titleInfo, boolean canRemove, boolean canPromoteBook) {
		super(titleInfo);
		this.canRemove = canRemove;
		this.canPromoteBook = canPromoteBook;
	}
	
	public boolean isCanRemove() {
		return canRemove;
	}

	public boolean isCanPromote() {
		return canPromoteBook && STATUS_REVIEW.equals(getStatus());
	}
}
