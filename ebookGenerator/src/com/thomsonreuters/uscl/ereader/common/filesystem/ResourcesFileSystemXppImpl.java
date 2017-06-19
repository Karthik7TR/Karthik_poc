package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.assemble.step.CoverArtUtil;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("resourcesFileSystemXpp")
public class ResourcesFileSystemXppImpl implements ResourcesFileSystem
{
    @Resource(name = "xppFormatFileSystem")
    private XppFormatFileSystem xppFormatFileSystem;

    @Resource(name = "imageFileSystem")
    private ImageFileSystem imageFileSystem;

    @Resource(name = "coverArtUtil")
    private CoverArtUtil coverArtUtil;

    @Value("${xpp.document.css}")
    private File documentCssFile;

    @NotNull
    @Override
    public File getDocumentsDirectory(@NotNull final BookStep step)
    {
        return xppFormatFileSystem.getHtmlPagesDirectory(step);
    }

    @NotNull
    @Override
    public File getAssetsDirectory(@NotNull final BookStep step)
    {
        return imageFileSystem.getImageDynamicDirectory(step);
    }

    @NotNull
    @Override
    public File getArtwork(@NotNull final BookStep step)
    {
        return coverArtUtil.getCoverArt(step.getBookDefinition());
    }

    @NotNull
    @Override
    public File getDocumentCss()
    {
        return documentCssFile;
    }
}
