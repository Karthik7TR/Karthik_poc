package com.thomsonreuters.uscl.ereader.common.filesystem;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("xppUnpackFileSystem")
public class XppUnpackFileSystemImpl implements XppUnpackFileSystem
{
    @Resource(name = "gatherFileSystem")
    private GatherFileSystem gatherFileSystem;

    //TODO: temp usage to static xpp source directory
    @Value("${xpp.sample.xppTemp.directory}")
    private String xppTempDirectory;

    @Override
    public File getXppUnpackDirectory(final BookStep step)
    {
//        return new File(gatherFileSystem.getGatherRootDirectory(step), "XppUnpack");
        return new File(xppTempDirectory, "XppUnpack");

    }

    @Override
    public String getXppAssetsDirectory(final BookStep step)
    {
        return new File(getXppUnpackDirectory(step), "assets").getAbsolutePath();
    }
}
