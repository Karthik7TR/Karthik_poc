package com.thomsonreuters.uscl.ereader.request.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import com.thomsonreuters.uscl.ereader.request.XPPConstants;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class XppMessageValidatorTest
{
    private XppMessageValidator validator;

    private static final String FILE_CONTENTS = "file contents";
    private String requestId = "ed4abfa40ee548388d39ecad55a0daaa";
    private String bundleHash;
    private Long materialNumber = 1L;
    private Date requestDate = new Date();
    private File mockEbookSrcFile;

    @Before
    public void setUp() throws IOException
    {
        validator = new XppMessageValidator();
        mockEbookSrcFile = initMockEbookSrcFile();

        bundleHash = DigestUtils.md5Hex(new FileInputStream(mockEbookSrcFile));
        System.out.println(bundleHash);
    }

    private File initMockEbookSrcFile() throws IOException
    {
        final File tempFile = File.createTempFile("mockEbookSrcFile", "tmp");
        FileUtils.writeStringToFile(tempFile, FILE_CONTENTS);
        return tempFile;
    }

    @After
    public void tearDown()
    {
        mockEbookSrcFile.delete();
    }

    @Test
    public void testHappyPath()
    {
        boolean thrown = false;
        try
        {
            validator.validate(createRequest());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            thrown = true;
        }
        Assert.assertTrue(!thrown);
    }

    @Test
    public void testIncompleteRequest()
    {
        boolean thrown = false;
        String errorMessage = "";
        try
        {
            validator.validate(new XppBundleArchive());
        }
        catch (final Exception e)
        {
            thrown = true;
            errorMessage = e.getMessage();
        }
        Assert.assertTrue(thrown);
        Assert.assertTrue(errorMessage.contains(XPPConstants.ERROR_INCOMPLETE_REQUEST));
    }

    @Test
    public void testFileNotFound()
    {
        mockEbookSrcFile = new File("definitely_not_valid.bad");

        boolean thrown = false;
        String errorMessage = "";
        try
        {
            validator.validate(createRequest());
        }
        catch (final Exception e)
        {
            thrown = true;
            errorMessage = e.getMessage();
        }
        Assert.assertTrue(thrown);
        Assert.assertTrue(errorMessage.contains(XPPConstants.ERROR_BUNDLE_NOT_FOUND));
    }

    @Test
    public void testBadHash()
    {
        bundleHash = "12345";

        boolean thrown = false;
        String errorMessage = "";
        try
        {
            validator.validate(createRequest());
        }
        catch (final Exception e)
        {
            thrown = true;
            errorMessage = e.getMessage();
        }
        Assert.assertTrue(thrown);
        Assert.assertTrue(errorMessage.contains(XPPConstants.ERROR_BAD_HASH));
    }

    private XppBundleArchive createRequest()
    {
        final XppBundleArchive request = new XppBundleArchive();
        request.setVersion("1.0");
        request.setMessageId(requestId);
        request.setMaterialNumber(materialNumber);
        request.setBundleHash(bundleHash);
        request.setDateTime(requestDate);
        request.setEBookSrcFile(mockEbookSrcFile);
        return request;
    }
}
