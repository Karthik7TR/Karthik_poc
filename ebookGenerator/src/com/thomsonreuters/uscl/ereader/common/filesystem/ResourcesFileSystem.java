package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;
import java.util.Collection;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.jetbrains.annotations.NotNull;

/**
 *  Provides access to folders, which contain files for final archive
 */
public interface ResourcesFileSystem {
    /**
     * {@link com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem#getHtmlPagesDirectory}
     */
    @NotNull
    File getDocumentsDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem#getHtmlPagesDirectory/materialNumber}
     */
    @NotNull
    File getDocumentsDirectory(@NotNull BookStep step, String materialNumber);

    /**
     * {@link com.thomsonreuters.uscl.ereader.common.filesystem.ImageFileSystem#getImageDynamicDirectory}
     */
    @NotNull
    File getAssetsDirectory(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.assemble.step.CoverArtUtil#getCoverArt}
     */
    @NotNull
    File getArtwork(@NotNull BookStep step);

    /**
     * Returns common CSS file for all XPP books
     */
    @NotNull
    File getDocumentCss();

    /**
     * Special image to display TLRkey character
     */
    @NotNull
    File getTlrKeyImage();

    /**
     * {@link com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem#getFontsCssDirectory}
     */
    @NotNull
    Collection<File> getFontsCssFiles(@NotNull BookStep step);

    /**
     * {@link com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem#getFontsCssDirectory/materialNumber}
     */
    @NotNull
    Collection<File> getFontsCssFiles(@NotNull BookStep step, String materialNumber);
}
