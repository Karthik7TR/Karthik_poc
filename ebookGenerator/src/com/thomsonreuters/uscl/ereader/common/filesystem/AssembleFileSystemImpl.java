package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobExecutionKey;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component("assembleFileSystem")
public class AssembleFileSystemImpl implements AssembleFileSystem
{
    @Resource(name = "bookFileSystem")
    private BookFileSystem bookFileSystem;

    @Override
    public File getAssembleDirectory(@NotNull final BookStep step)
    {
        return new File(bookFileSystem.getWorkDirectory(step), "Assemble");
    }

    @Override
    public File getTitleDirectory(@NotNull final BookStep step)
    {
        final BookDefinition bookDefinition = step.getBookDefinition();
        return new File(getAssembleDirectory(step), bookDefinition.getTitleId());
    }

    @Override
    public File getTitleXml(@NotNull final BookStep step)
    {
        return new File(getTitleDirectory(step), "title.xml");
    }

    @Override
    public File getSplitTitleDirectory(@NotNull final BookStep step, @NotNull final String splitTitleId)
    {
        final String titleId = StringUtils.substringAfterLast(splitTitleId, "/");
        return new File(getAssembleDirectory(step), titleId);
    }

    @Override
    public File getAssetsDirectory(@NotNull final BookStep step)
    {
        return new File(getTitleDirectory(step), "assets");
    }

    @Override
    public File getDocumentsDirectory(@NotNull final BookStep step)
    {
        return new File(getTitleDirectory(step), "documents");
    }

    @Override
    public File getAssembledBookFile(@NotNull final BookStep step)
    {
        final BookDefinition bookDefinition = step.getBookDefinition();
        return new File(
            bookFileSystem.getWorkDirectory(step),
            bookDefinition.getTitleId() + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
    }

    @Override
    public File getAssembledSplitTitleFile(@NotNull final BookStep step, @NotNull final String splitTitleId)
    {
        final String titleId = StringUtils.substringAfterLast(splitTitleId, "/");
        return new File(bookFileSystem.getWorkDirectory(step), titleId + JobExecutionKey.BOOK_FILE_TYPE_SUFFIX);
    }
}
