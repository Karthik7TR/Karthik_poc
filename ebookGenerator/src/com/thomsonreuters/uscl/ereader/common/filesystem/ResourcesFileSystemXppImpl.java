package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.assemble.step.CoverArtUtil;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("resourcesFileSystemXpp")
public class ResourcesFileSystemXppImpl implements ResourcesFileSystem {
    @Resource(name = "xppFormatFileSystem")
    private XppFormatFileSystem xppFormatFileSystem;
    @Resource(name = "imageFileSystem")
    private ImageFileSystem imageFileSystem;
    @Resource(name = "coverArtUtil")
    private CoverArtUtil coverArtUtil;
    @Value("${xpp.document.css}")
    private File documentCssFile;
    @Value("${xpp.tlrkey.image}")
    private File tlrKeyImage;

    @NotNull
    @Override
    public File getDocumentsDirectory(@NotNull final BookStep step) {
        return getDocumentsDirectory(step, null);
    }

    @NotNull
    @Override
    public File getDocumentsDirectory(@NotNull final BookStep step, final String materialNumber) {
        return Optional.ofNullable(materialNumber)
            .map(material -> xppFormatFileSystem.getDirectory(step, XppFormatFileSystemDir.UNESCAPE_DIR, material))
            .orElseGet(() -> xppFormatFileSystem.getDirectory(step, XppFormatFileSystemDir.UNESCAPE_DIR));
    }

    @NotNull
    @Override
    public File getAssetsDirectory(@NotNull final BookStep step) {
        return imageFileSystem.getImageDynamicDirectory(step);
    }

    @NotNull
    @Override
    public File getArtwork(@NotNull final BookStep step) {
        return coverArtUtil.getCoverArt(step.getBookDefinition());
    }

    @NotNull
    @Override
    public File getDocumentCss() {
        return documentCssFile;
    }

    @NotNull
    @Override
    public File getTlrKeyImage() {
        return tlrKeyImage;
    }

    @NotNull
    @Override
    public Collection<File> getFontsCssFiles(@NotNull final BookStep step) {
        return getFontsCssFiles(step, null);
    }

    @NotNull
    @Override
    public Collection<File> getFontsCssFiles(@NotNull final BookStep step, final String materialNumber) {
        final File cssDir = Optional.ofNullable(materialNumber)
            .map(material -> xppFormatFileSystem.getFontsCssDirectory(step, material))
            .orElseGet(() -> xppFormatFileSystem.getFontsCssDirectory(step));
        return FileUtils.listFiles(cssDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    }
}
