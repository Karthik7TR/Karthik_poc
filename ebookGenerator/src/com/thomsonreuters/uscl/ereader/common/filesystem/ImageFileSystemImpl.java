package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.springframework.stereotype.Component;

@Component("imageFileSystem")
public class ImageFileSystemImpl implements ImageFileSystem {
    @Resource(name = "gatherFileSystem")
    private GatherFileSystem gatherFileSystem;

    @Override
    public File getImageRootDirectory(final BookStep step) {
        return new File(gatherFileSystem.getGatherRootDirectory(step), "Images");
    }

    @Override
    public File getImageDynamicDirectory(final BookStep step) {
        return new File(getImageRootDirectory(step), "Dynamic");
    }
}
