package com.thomsonreuters.uscl.ereader.xpp.unpackbundle.step;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.common.notification.step.FailureNotificationType;
import com.thomsonreuters.uscl.ereader.common.notification.step.SendFailureNotificationPolicy;
import com.thomsonreuters.uscl.ereader.common.publishingstatus.step.SavePublishingStatusPolicy;
import com.thomsonreuters.uscl.ereader.common.service.compress.CompressService;
import com.thomsonreuters.uscl.ereader.common.step.BookStepImpl;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.request.service.XppBundleArchiveService;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.ExitStatus;

/**
 *Task step to unpack archive with bundle files.
 *The task defines current material number of print component,
 *finds associated with this component archive file, unpacks it in proper folder.
 *Then the task validates the concurrence of source zip file structure and
 *unzipped folder and log the result
 */

@SendFailureNotificationPolicy(FailureNotificationType.XPP)
@SavePublishingStatusPolicy
public class UnpackBundleTask extends BookStepImpl
{
    @Resource(name = "xppGatherFileSystem")
    private XppGatherFileSystem xppGatherFileSystem;
    @Resource(name = "zipService")
    private CompressService zipService;
    @Resource(name = "xppBundleArchiveService")
    private XppBundleArchiveService xppBundleArchiveService;
    @Resource(name = "gzipService")
    private CompressService gzipService;

    @Override
    public ExitStatus executeStep() throws Exception
    {
        xppGatherFileSystem.getXppBundlesDirectory(this).mkdirs();
        unpackBundles();
        unmarshalBundleXmlFiles();
        return ExitStatus.COMPLETED;
    }

    private void unpackBundles() throws Exception
    {
        final BookDefinition bookDefinition = getBookDefinition();
        for (final PrintComponent printComponent : bookDefinition.getPrintComponents())
        {
            final String currentMaterialNumber = printComponent.getMaterialNumber();

            final XppBundleArchive xppBundleArchive =
                xppBundleArchiveService.findByMaterialNumber(currentMaterialNumber);
            final File targetArchive = xppBundleArchive.getEBookSrcFile();

            final File currentBundleDirectory =
                xppGatherFileSystem.getXppBundleMaterialNumberDirectory(this, currentMaterialNumber);

            unpackBundle(targetArchive, currentBundleDirectory);
        }
    }

    private void unpackBundle(final File targetArchive, final File currentBundleDirectory) throws Exception
    {
        switch (ArchiveType.getTypeByName(targetArchive.getName()))
        {
            case ZIP:
                zipService.decompress(targetArchive, currentBundleDirectory);
                break;
            case TAR_GZ:
                final String bundleName = StringUtils.substringBefore(targetArchive.getName(), ".tar.gz");
                gzipService.decompress(targetArchive, currentBundleDirectory, bundleName + "/");
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void unmarshalBundleXmlFiles() throws JAXBException
    {
        final Unmarshaller unmarshaller = JAXBContext.newInstance(XppBundle.class).createUnmarshaller();
        final List<XppBundle> xppBundleList = new ArrayList<>();

        for (final File element : xppGatherFileSystem.getAllBundleXmls(this))
        {
            xppBundleList.add((XppBundle) unmarshaller.unmarshal(element));
        }

        setJobExecutionProperty(JobParameterKey.XPP_BUNDLES, xppBundleList);
    }

    private enum ArchiveType
    {
        ZIP(".zip"),
        TAR_GZ(".tar.gz");

        private final String extension;

        ArchiveType(final String extension)
        {
            this.extension = extension;
        }

        private static ArchiveType getTypeByName(final String archiveName)
        {
            for (final ArchiveType type : values())
            {
                if (archiveName.endsWith(type.extension))
                {
                    return type;
                }
            }
            throw new IllegalArgumentException("Not supported archive type: " + archiveName);
        }
    }
}
