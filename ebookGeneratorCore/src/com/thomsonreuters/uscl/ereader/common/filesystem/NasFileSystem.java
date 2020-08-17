package com.thomsonreuters.uscl.ereader.common.filesystem;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface NasFileSystem {
    @NotNull
    File getStaticContentDirectory();

    @NotNull
    File getCoverImagesDirectory();

    @NotNull
    File getFrontMatterImagesDirectory();

    @NotNull
    File getFrontMatterCssDirectory();

    @NotNull
    File getFrontMatterUsclPdfDirectory();

    @NotNull
    File getFrontMatterCwPdfDirectory();

    @NotNull
    File getPilotBookCsvDirectory();

    @NotNull
    File getCodesWorkbenchRootDir();

    @NotNull
    File getFrontMatterCssFile();

    @NotNull
    File getIsbnFileDir();

    @NotNull
    File getIsbnFileArchiveDir();
}
