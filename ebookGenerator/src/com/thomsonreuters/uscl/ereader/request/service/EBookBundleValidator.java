package com.thomsonreuters.uscl.ereader.request.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.request.EBookBundle;
import com.thomsonreuters.uscl.ereader.request.EBookRequestException;
import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class EBookBundleValidator
{
    public void validateBundleDirectory(final File bundleDir) throws EBookRequestException
    {
        Assert.notNull(bundleDir);
        if (!bundleDir.exists())
        {
            throw new EBookRequestException(XPPConstants.ERROR_BUNDLE_NOT_FOUND + bundleDir.getAbsolutePath());
        }
        final List<File> contents = Arrays.asList(bundleDir.listFiles());

        // TODO update as more of the directory structure is determined
        validateDir(contents, new File(bundleDir, XPPConstants.FILE_ASSETS_DIRECTORY));
        validateDir(contents, new File(bundleDir, XPPConstants.FILE_PDF_DIRECTORY));
        validateDir(contents, new File(bundleDir, XPPConstants.FILE_XPP_DIRECTORY));
        validateFile(contents, new File(bundleDir, XPPConstants.FILE_BUNDLE_XML));
    }

    private void validateDir(final List<File> bundleFiles, final File target) throws EBookRequestException
    {
        if (!bundleFiles.contains(target) || !target.isDirectory())
        {
            throw new EBookRequestException(target.getName() + " directory must exist");
        }
    }

    private void validateFile(final List<File> bundleFiles, final File target) throws EBookRequestException
    {
        if (!bundleFiles.contains(target) || !target.isFile())
        {
            throw new EBookRequestException(target.getName() + " must exist");
        }
    }

    public void validateBundleXml(final EBookBundle bundle) throws EBookRequestException
    {
        if (bundle == null)
            throw new EBookRequestException("bundle must not be null");

        // TODO update as more of the bundle.xml information is determined
        if (!StringUtils.isNotBlank(bundle.getProductTitle()))
            throw new EBookRequestException("ProductTitle must not be blank");
        if (!StringUtils.isNotBlank(bundle.getProductType()))
            throw new EBookRequestException("ProductType must not be blank");
    }
}
