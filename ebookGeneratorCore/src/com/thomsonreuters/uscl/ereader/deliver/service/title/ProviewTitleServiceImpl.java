package com.thomsonreuters.uscl.ereader.deliver.service.title;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.thomsonreuters.uscl.ereader.core.book.model.BookTitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewClient;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.PublishedTitleParser;
import com.thomsonreuters.uscl.ereader.deliver.service.TitleInfoParser;
import com.thomsonreuters.uscl.ereader.proview.Doc;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProviewTitleServiceImpl implements ProviewTitleService
{
    private static final Logger LOG = LoggerFactory.getLogger(ProviewTitleServiceImpl.class);

    private final ProviewClient proviewClient;

    @Autowired
    public ProviewTitleServiceImpl(final ProviewClient proviewClient)
    {
        this.proviewClient = proviewClient;
    }

    @Override
    @Nullable
    public Version getLatestProviewTitleVersion(@NotNull final String fullyQualifiedTitleId)
    {
        final ProviewTitleInfo titleInfo = getLatestProviewTitleInfo(fullyQualifiedTitleId);
        final String version = titleInfo == null ? null : titleInfo.getVersion();
        return version == null ? null : new Version(version);
    }

    @Nullable
    private ProviewTitleInfo getLatestProviewTitleInfo(@NotNull final String fullyQualifiedTitleId)
    {
        ProviewTitleInfo titleInfo = null;
        try
        {
            final String singlePublishTitleResponse = proviewClient.getSinglePublishedTitle(fullyQualifiedTitleId);
            final ProviewTitleContainer titleContainer = new PublishedTitleParser()
                                                               .process(singlePublishTitleResponse)
                                                               .get(fullyQualifiedTitleId);
            titleInfo = titleContainer == null ? null : titleContainer.getLatestVersion();
        }
        catch (final ProviewException e)
        {
            LOG.info("Cannot get title info from ProView", e);
        }
        return titleInfo;
    }

    @Override
    @NotNull
    public List<Doc> getProviewTitleDocs(@NotNull final BookTitleId titleId)
    {
        try
        {
            final String titleInfoResponse = proviewClient.getTitleInfo(
                titleId.getTitleId(), titleId.getVersion().getFullVersion());
            return new TitleInfoParser().getDocuments(titleInfoResponse);
        }
        catch (final ProviewException e)
        {
            throw new RuntimeException("Cannot get title info from ProView", e);
        }
    }

    @Override
    @NotNull
    public List<BookTitleId> getPreviousTitles(@NotNull final Version previousVersion, @NotNull final String titleId)
    {
        final List<BookTitleId> titleIds = new ArrayList<>();
        String currentTitleId = titleId;
        Integer part = 2;
        boolean hasParts;
        do
        {
            titleIds.add(new BookTitleId(currentTitleId, previousVersion));
            try
            {
                currentTitleId = titleId + "_pt" + part++;
                hasParts = StringUtils.isNotBlank(proviewClient.getTitleInfo(currentTitleId, previousVersion.getFullVersion()));
            }
            catch (final ProviewException e)
            {
                if (e.getMessage().contains("FileNotFoundException"))
                {
                    hasParts = false;
                }
                else
                {
                    LOG.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        }
        while (hasParts);
        return titleIds;
    }

    @Override
    public boolean isMajorVersionPromotedToFinal(@NotNull final String fullyQualifiedTitleId, @NotNull final Version newVersion)
    {
        try
        {
            final String response = proviewClient.getTitleInfosByStatus(fullyQualifiedTitleId, "Final");
            final Pattern pattern = Pattern.compile(String.format("version=\"v%d\\.\\d+\"", newVersion.getMajorNumber()));
            return pattern.matcher(response).find();
        }
        catch (final ProviewException e)
        {
            throw new RuntimeException("Cannot get title info from ProView", e);
        }
    }
}
