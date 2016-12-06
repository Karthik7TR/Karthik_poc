/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;

public interface ProviewTitleListService {

	/**
	 * Get list of titles to display on 'Proview list' page 
	 * @param titleInfos list of titles from ProView
	 * @param book book definition
	 * @return list of titles to display
	 */
	@NotNull
	List<ProviewTitle> getProviewTitles(@NotNull List<ProviewTitleInfo> titleInfos, @Nullable BookDefinition book);
	
	/**
	 * Get book definition of head book 
	 * @param titleId full title id for volume
	 * @return book definition
	 */
	@Nullable
	BookDefinition getBook(@NotNull TitleId titleId);
}
