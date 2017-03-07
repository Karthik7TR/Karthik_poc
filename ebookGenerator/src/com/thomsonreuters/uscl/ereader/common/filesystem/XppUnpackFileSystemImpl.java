package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.springframework.stereotype.Component;

@Component("xppUnpackFileSystem")
public class XppUnpackFileSystemImpl implements XppUnpackFileSystem
{
    @Resource(name = "gatherFileSystem")
    private GatherFileSystem gatherFileSystem;

    @Override
    public File getXppUnpackDirectory(final BookStep step)
    {
        return new File(gatherFileSystem.getGatherRootDirectory(step), "XppUnpack");
    }

    @Override
    public String getXppAssetsDirectory(final BookStep step)
    {
        return new File(getXppUnpackDirectory(step), "assets").getAbsolutePath();
    }
}
