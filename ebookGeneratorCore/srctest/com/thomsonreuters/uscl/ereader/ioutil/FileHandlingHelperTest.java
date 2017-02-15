package com.thomsonreuters.uscl.ereader.ioutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * File Handling Utility test.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public final class FileHandlingHelperTest
{
    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();

    private FileExtensionFilter fileExtFilter;
    private FileHandlingHelper fileHandler;
    private final String testExtension = ".xml";
    private final String testExtension2 = ".txt";

    private File emptyDir;
    private File testDir;

    protected File xmlFile;
    protected File xmlFile2;

    @Before
    public void setUp() throws Exception
    {
        fileExtFilter = new FileExtensionFilter();
        fileHandler = new FileHandlingHelper();

        //set up Transformed directories
        emptyDir = testFiles.newFolder("ExtFilterTestEmptyDir");
        emptyDir.mkdir();

        testDir = testFiles.newFolder("ExtFilterTestDir");
        testDir.mkdir();

        xmlFile = new File(testDir, "xmlFile1.xml");
        xmlFile.createNewFile();
        xmlFile2 = new File(testDir, "xmlFile2.xml");
        xmlFile2.createNewFile();

        final File txtFile = new File(testDir, "txtFile.txt");
        txtFile.createNewFile();
        final File htmlFile = new File(testDir, "htmlFile.html");
        htmlFile.createNewFile();
    }

    @Test(expected = IllegalStateException.class)
    public void testFilterSetEnforcement()
    {
        final List<File> fileList = new ArrayList<>();
        try
        {
            fileHandler.getFileList(testDir, fileList);
        }
        catch (final FileNotFoundException e)
        {
            fail("Test threw FileNotFoundException when IllegalStateException was expected.");
        }
    }

    @Test
    public void testFileNotFoundFromEmptyDirectory()
    {
        final List<File> fileList = new ArrayList<>();
        try
        {
            fileExtFilter.setAcceptedFileExtensions(new String[] {testExtension});
            fileHandler.setFilter(fileExtFilter);
            fileHandler.getFileList(emptyDir, fileList);
            fail("Test did not throw a FileNotFoundException when expected.");
        }
        catch (final IllegalStateException e)
        {
            fail("Test threw IllegalStateException when FileNotFoundException was expected.");
        }
        catch (final FileNotFoundException e)
        {
            //Intentionally left blank
        }
    }

    @Test
    public void testXMLFileRetrieval() throws Exception
    {
        final List<File> fileList = new ArrayList<>();

        fileExtFilter.setAcceptedFileExtensions(new String[] {testExtension});
        fileHandler.setFilter(fileExtFilter);
        fileHandler.getFileList(testDir, fileList);

        assertEquals(2, fileList.size());
    }

    @Test
    public void testMultipleExtFileRetrieval() throws Exception
    {
        final List<File> fileList = new ArrayList<>();

        fileExtFilter.setAcceptedFileExtensions(new String[] {testExtension, testExtension2});
        fileHandler.setFilter(fileExtFilter);
        fileHandler.getFileList(testDir, fileList);

        assertEquals(3, fileList.size());
    }
}
