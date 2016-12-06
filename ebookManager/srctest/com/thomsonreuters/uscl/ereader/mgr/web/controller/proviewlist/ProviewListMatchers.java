/*
 * Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved.
 * Proprietary and Confidential information of TRGR. Disclosure, Use or
 * Reproduction without the written authorization of TRGR is prohibited
 */
package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;

@SuppressWarnings("null")
public class ProviewListMatchers {

	public static ProviewTitleContainer container(List<ProviewTitleInfo> titles) {
		ProviewTitleContainer container = new ProviewTitleContainer();
		container.setProviewTitleInfos(titles);
		return container;
	}

	public static ProviewTitleInfo titleInfo(String version, String status) {
		ProviewTitleInfo proviewTitleInfo = new ProviewTitleInfo();
		proviewTitleInfo.setVersion(version);
		proviewTitleInfo.setStatus(status);
		return proviewTitleInfo;
	}

	public static ProviewTitle title(String version, String status) {
		return new ProviewTitle(titleInfo(version, status), false, false);
	}

	public static Matcher<ProviewTitle> isTitle(final boolean canRemove, final boolean canPromote) {
		return new BaseMatcher<ProviewTitle>() {
			@Override
			public boolean matches(final Object item) {
				final ProviewTitle title = (ProviewTitle) item;
				return canRemove == title.isCanRemove() && canPromote == title.isCanPromote();
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("canRemove should be ").appendValue(canRemove);
				description.appendText("; canPromote should be ").appendValue(canPromote);
			}

			@Override
			public void describeMismatch(final Object item, final Description description) {
				final ProviewTitle title = (ProviewTitle) item;
				description.appendText("canRemove was ").appendValue(title.isCanRemove());
				description.appendText("; canPromote was ").appendValue(title.isCanPromote());
			}
		};
	}
}
