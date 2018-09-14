package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.exception.EBookException;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.service.BookDefinitionService;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.service.PublishingStatsService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("bookFileSystem")
public class BookFileSystemImpl implements BookFileSystem {
    @Value("${root.work.directory}")
    private File rootWorkDirectory;
    @Resource(name = "environmentName")
    private String environmentName;
    @Resource(name = "publishingStatsService")
    private PublishingStatsService publishingStatsService;
    @Resource(name = "bookDefinitionService")
    private BookDefinitionService bookDefinitionService;

    @NotNull
    @Override
    public File getWorkDirectory(@NotNull final BookStep step) {
        return buildFile(step.getSubmitTimestamp(), step.getBookDefinition().getTitleId(), step.getJobInstanceId());
    }

    @NotNull
    @Override
    public File getWorkDirectoryByJobId(@NotNull final Long jobInstanceId) {
        final PublishingStats stats = Optional.ofNullable(publishingStatsService.findPublishingStatsByJobId(jobInstanceId))
            .orElseThrow(() -> new EBookException("Can not find jobInstanceId " + jobInstanceId));
        final BookDefinition bookDefinition = bookDefinitionService.findBookDefinitionByEbookDefId(stats.getEbookDefId());
        return buildFile(stats.getJobSubmitTimestamp(), bookDefinition.getTitleId(), jobInstanceId);
    }

    private File buildFile(final Date date, final String titleId, final Long jobInstanceId) {
        final String dynamicPath =
            String.format("%s/%s/%s/%s/%d", environmentName, CoreConstants.DATA_DIR, getDateBasedDirName(date), titleId, jobInstanceId);
        return new File(rootWorkDirectory, dynamicPath);
    }

    public static String getDateBasedDirName(final Date date) {
        return new SimpleDateFormat(CoreConstants.DIR_DATE_FORMAT).format(date);
    }
}
