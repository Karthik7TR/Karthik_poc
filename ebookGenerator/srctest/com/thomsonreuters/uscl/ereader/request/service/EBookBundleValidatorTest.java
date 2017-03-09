package com.thomsonreuters.uscl.ereader.request.service;

import java.io.File;
import java.io.IOException;

import com.thomsonreuters.uscl.ereader.request.EBookBundle;
import com.thomsonreuters.uscl.ereader.request.EBookRequestException;
import com.thomsonreuters.uscl.ereader.request.RequestConstants;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class EBookBundleValidatorTest
{
    private EBookBundleValidator validator;
    private File tempRootDir;

    @Before
    public void setUp() throws IOException
    {
        validator = new EBookBundleValidator();

        tempRootDir = new File(System.getProperty("java.io.tmpdir") + this.getClass().getName());
        tempRootDir.mkdir();

        initialize_Directory(tempRootDir);
    }

    @After
    public void tearDown() throws IOException
    {
        FileUtils.deleteDirectory(tempRootDir);
    }

    @Test
    public void testValidateDirectory()
    {
        try
        {
            validator.validateBundleDirectory(tempRootDir);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception encountered: " + e.getMessage());
        }
    }

    @Test
    public void testValidateDirectory_nullInput()
    {
        try
        {
            validator.validateBundleDirectory(null);

            Assert.fail("Should thrown EBookRequestException");
        }
        catch (final IllegalArgumentException e)
        {
            Assert.assertTrue(e.getMessage().contains("not be null"));
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception encountered: " + e.getMessage());
        }
    }

    @Test
    public void testValidateDirectory_noAssetsDir()
    {
        try
        {
            new File(tempRootDir, RequestConstants.FILE_ASSETS_DIRECTORY).delete();
            validator.validateBundleDirectory(tempRootDir);

            Assert.fail("Should thrown EBookRequestException");
        }
        catch (final EBookRequestException e)
        {
            Assert.assertEquals("assets directory must exist", e.getMessage());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception encountered: " + e.getMessage());
        }
    }

    @Test
    public void testValidateDirectory_noPDFDir()
    {
        try
        {
            new File(tempRootDir, RequestConstants.FILE_PDF_DIRECTORY).delete();
            validator.validateBundleDirectory(tempRootDir);

            Assert.fail("Should thrown EBookRequestException");
        }
        catch (final EBookRequestException e)
        {
            Assert.assertEquals("PDF directory must exist", e.getMessage());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception encountered: " + e.getMessage());
        }
    }

    @Test
    public void testValidateDirectory_noXPPDir()
    {
        try
        {
            new File(tempRootDir, RequestConstants.FILE_XPP_DIRECTORY).delete();
            validator.validateBundleDirectory(tempRootDir);

            Assert.fail("Should thrown EBookRequestException");
        }
        catch (final EBookRequestException e)
        {
            Assert.assertEquals("XPP directory must exist", e.getMessage());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception encountered: " + e.getMessage());
        }
    }

    @Test
    public void testValidateDirectory_noBundleXML()
    {
        try
        {
            new File(tempRootDir, RequestConstants.FILE_BUNDLE_XML).delete();
            validator.validateBundleDirectory(tempRootDir);

            Assert.fail("Should thrown EBookRequestException");
        }
        catch (final EBookRequestException e)
        {
            Assert.assertEquals("bundle.xml must exist", e.getMessage());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception encountered: " + e.getMessage());
        }
    }

    private void initialize_Directory(final File rootDir) throws IOException
    {
        new File(rootDir, RequestConstants.FILE_ASSETS_DIRECTORY).mkdir();
        new File(rootDir, RequestConstants.FILE_PDF_DIRECTORY).mkdir();
        new File(rootDir, RequestConstants.FILE_XPP_DIRECTORY).mkdir();
        new File(rootDir, RequestConstants.FILE_BUNDLE_XML).createNewFile();
    }

    @Test
    public void testValidateBundleXml()
    {
        final EBookBundle bundle = new EBookBundle();
        bundle.setProductTitle("title");
        bundle.setProductType("type");
        try
        {
            validator.validateBundleXml(bundle);
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception encountered: " + e.getMessage());
        }
    }

    @Test
    public void testValidateBundleXml_null()
    {
        try
        {
            validator.validateBundleXml(null);

            Assert.fail("Should thrown IllegalArgumentException");
        }
        catch (final EBookRequestException e)
        {
            Assert.assertTrue(e.getMessage().contains("not be null"));
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception encountered: " + e.getMessage());
        }
    }

    @Test
    public void testValidateBundleXml_noTitle()
    {
        final EBookBundle bundle = new EBookBundle();
        bundle.setProductType("type");
        try
        {
            validator.validateBundleXml(bundle);

            Assert.fail("Should thrown IllegalArgumentException");
        }
        catch (final EBookRequestException e)
        {
            Assert.assertEquals("ProductTitle must not be blank", e.getMessage());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception encountered: " + e.getMessage());
        }
    }

    @Test
    public void testValidateBundleXml_noType()
    {
        final EBookBundle bundle = new EBookBundle();
        bundle.setProductTitle("title");
        try
        {
            validator.validateBundleXml(bundle);

            Assert.fail("Should thrown IllegalArgumentException");
        }
        catch (final EBookRequestException e)
        {
            Assert.assertEquals("ProductType must not be blank", e.getMessage());
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            Assert.fail("Unexpected exception encountered: " + e.getMessage());
        }
    }
}
