package com.thomsonreuters.uscl.ereader.common.step;

import java.io.File;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.jetbrains.annotations.NotNull;

public interface BookStep extends BaseStep
{
    @NotNull
    BookDefinition getBookDefinition();

    @NotNull
    Long getBookDefinitionId();

    @NotNull
    String getBookVersion();

    @NotNull
    String getUserName();

    @NotNull
    String getHostName();

    @NotNull
    String getEnvironment();

    @NotNull
    Date getSubmitTimestamp();

    @NotNull
    File getWorkDirectory();

    @NotNull
    File getAssembleDirectory();

    @NotNull
    File getAssembleTitleDirectory();

    @NotNull
    File getAssembleSplitTitleDirectory(String splitTitleId);

    @NotNull
    File getAssembleAssetsDirectory();

    @NotNull
    File getAssembleDocumentsDirectory();

    @NotNull
    File getAssembledBookFile();

    @NotNull
    File getAssembledSplitTitleFile(String splitTitleId);

    @NotNull
    File getTitleXml();
}
