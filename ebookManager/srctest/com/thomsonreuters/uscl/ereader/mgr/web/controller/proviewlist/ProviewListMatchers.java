package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.List;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ProviewListMatchers
{
    public static ProviewTitleContainer container(final List<ProviewTitleInfo> titles)
    {
        final ProviewTitleContainer container = new ProviewTitleContainer();
        container.setProviewTitleInfos(titles);
        return container;
    }

    public static ProviewTitleInfo titleInfo(final String version, final String status)
    {
        final ProviewTitleInfo proviewTitleInfo = new ProviewTitleInfo();
        proviewTitleInfo.setVersion(version);
        proviewTitleInfo.setStatus(status);
        return proviewTitleInfo;
    }

    public static ProviewTitle title(final String version, final String status)
    {
        return new ProviewTitle(titleInfo(version, status), false, false);
    }

    public static Matcher<ProviewTitle> isTitle(final boolean canRemove, final boolean canPromote)
    {
        return new BaseMatcher<ProviewTitle>()
        {
            @Override
            public boolean matches(final Object item)
            {
                final ProviewTitle title = (ProviewTitle) item;
                return canRemove == title.isCanRemove() && canPromote == title.isCanPromote();
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("canRemove should be ").appendValue(canRemove);
                description.appendText("; canPromote should be ").appendValue(canPromote);
            }

            @Override
            public void describeMismatch(final Object item, final Description description)
            {
                final ProviewTitle title = (ProviewTitle) item;
                description.appendText("canRemove was ").appendValue(title.isCanRemove());
                description.appendText("; canPromote was ").appendValue(title.isCanPromote());
            }
        };
    }
}
