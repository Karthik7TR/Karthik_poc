package com.thomsonreuters.uscl.ereader.xpp.gather;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.XppUnpackFileSystem;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTask;

/**
 * Gather dynamic images task for XPP pathway.
 */
public class GatherXppDynamicImagesTask extends GatherDynamicImagesTask
{
    @Resource(name = "xppUnpackFileSystem")
    private XppUnpackFileSystem xppUnpackFileSystem;

    @Override
    protected GatherImgRequest constructGatherImageRequest(
        final File dynamicImageDestinationDirectory,
        final File imageGuidFile,
        final long jobInstanceId)
    {
        final GatherImgRequest imgRequest = super.constructGatherImageRequest(dynamicImageDestinationDirectory, imageGuidFile, jobInstanceId);
        imgRequest.setXppSourceImageDirectory(xppUnpackFileSystem.getXppAssetsDirectory(this));
        imgRequest.setXpp(true);
        return imgRequest;
    }

}
