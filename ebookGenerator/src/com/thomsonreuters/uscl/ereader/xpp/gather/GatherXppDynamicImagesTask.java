package com.thomsonreuters.uscl.ereader.xpp.gather;

import java.io.File;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.step.GatherDynamicImagesTask;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;

/**
 * Gather dynamic images task for XPP pathway.
 */
@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy(StatsUpdateTypeEnum.GATHERIMAGE)
public class GatherXppDynamicImagesTask extends GatherDynamicImagesTask {
    @Resource(name = "xppGatherFileSystem")
    private XppGatherFileSystem xppGatherFileSystem;

    @Override
    protected GatherImgRequest constructGatherImageRequest(
        final File dynamicImageDestinationDirectory,
        final File imageGuidFile,
        final long jobInstanceId) {
        final GatherImgRequest imgRequest =
            super.constructGatherImageRequest(dynamicImageDestinationDirectory, imageGuidFile, jobInstanceId);
        imgRequest.setXppSourceImageDirectory(xppGatherFileSystem.getXppAssetsDirectories(this));
        imgRequest.setXpp(true);
        return imgRequest;
    }
}
