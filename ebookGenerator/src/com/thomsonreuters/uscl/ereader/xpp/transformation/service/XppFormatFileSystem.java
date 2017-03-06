package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 * Provides access to files and directories specific for XPP transformation steps
 */
public interface XppFormatFileSystem extends FormatFileSystem
{
    /**
     * Returns split book directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}{@code /Original}
     */
    @NotNull
    File getOriginalDirectory(@NotNull BookStep step);

    /**
     * Returns split book directory:
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem#getWorkDirectory workDirectory}
     * {@code /}{@link com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem#getFormatDirectory Format}
     * {@code /}{@link getOriginalDirectory Original}{@code /fileName.original}
     */
    @NotNull
    File getOriginalFile(@NotNull BookStep step, @NotNull String xppFileName);
}
