package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test the Image Parser Service.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869
 */
public final class XMLImageParserServiceTest
{
    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();

    protected XMLImageParserServiceImpl imgParserService;

    protected List<String> guidList;
    protected Map<String, List<String>> docToImgMap;

    protected File xmlDir;

    protected File imgListFile;
    protected File docToImgMapFile;
    protected File xmlFile;
    protected File xmlFile2;
    protected File xmlFile3Sorted;
    protected File invalidXmlFile;
    protected File emptyXmlFile;

    private String xmlText = "<primary.notes><para><bop /><bos /><paratext>The Sentencing Table "
        + "used to determine the guideline range follows:</paratext>"
        + "<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9905\" /></image.block>"
        + "<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9906\" /></image.block>"
        + "<eos /><eop /></para></primary.notes>";

    private String xmlText2 = "<primary.notes><para><bop /><bos /><paratext>The Sentencing Table "
        + "used to determine the guideline range follows:</paratext>"
        + "<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9905\" /></image.block>"
        + "<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9906\" /></image.block>"
        + "<eos /><eop /></para></primary.notes>";

    private String xmlText3 = "<primary.notes><para><bop /><bos /><paratext>The Sentencing Table "
        + "used to determine the guideline range follows:</paratext>"
        + "<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9905\" /></image.block>"
        + "<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9906\" /></image.block>"
        + "<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9907\" /></image.block>"
        + "<image.block><image.link target=\"I1d163990094d11e085f5891ac64a9908\" /></image.block>"
        + "<eos /><eop /></para></primary.notes>";

    private String invalidXml = "<primary.notes><para><bop /><bos /><paratext>The Sentencing Table "
        + "used to determine the guideline range follows:</paratext>";

    @Before
    public void setUp() throws Exception
    {
        final FileExtensionFilter filter = new FileExtensionFilter();
        filter.setAcceptedFileExtensions(new String[] {".xml"});
        final FileHandlingHelper ioHelper = new FileHandlingHelper();
        ioHelper.setFilter(filter);

        imgParserService = new XMLImageParserServiceImpl();
        imgParserService.setfileHandlingHelper(ioHelper);

        imgListFile = testFiles.newFile("jUnitImageListFile");
        docToImgMapFile = testFiles.newFile("jUnitDocToImgMapFile");

        xmlDir = testFiles.newFolder("XMLImageParserTest");
        emptyXmlFile = testFiles.newFile("emptyXMLFile.xml");

        xmlFile = new File(xmlDir, "xmlTestFile1_11111111112222222222.xml");
        final OutputStream outputStream = new FileOutputStream(xmlFile);
        outputStream.write(xmlText.getBytes());
        outputStream.flush();
        outputStream.close();

        xmlFile2 = new File(xmlDir, "xmlTestFile2_11111111112222222222.xml");
        final OutputStream outputStream2 = new FileOutputStream(xmlFile2);
        outputStream2.write(xmlText2.getBytes());
        outputStream2.flush();
        outputStream2.close();

        xmlFile3Sorted = new File(xmlDir, "xmlTestFile3Sorted_11112222222222.xml");
        final OutputStream outputStream3Sorted = new FileOutputStream(xmlFile3Sorted);
        outputStream3Sorted.write(xmlText3.getBytes());
        outputStream3Sorted.flush();
        outputStream3Sorted.close();

        invalidXmlFile = testFiles.newFile("invalidXmlTestFile_11112222222222.xml");
        final OutputStream outputStream3 = new FileOutputStream(invalidXmlFile);
        outputStream3.write(invalidXml.getBytes());
        outputStream3.flush();
        outputStream3.close();

        guidList = new ArrayList<String>();
        guidList.add("I5d463990094d11e085f5891ac64a9905");
        guidList.add("I8A302FE4920F47B00079B5381C71638B");

        docToImgMap = new HashMap<String, List<String>>();
        docToImgMap.put("Test02FE4920F47B00079B5381C71638B", guidList);
    }

    @Ignore
    public void testGuidListGenerationFromDirectoryXMLParsing()
    {
        try
        {
            final long initMapFileSize = docToImgMapFile.length();
            imgParserService.generateImageList(xmlDir, imgListFile, docToImgMapFile);
            assertTrue(initMapFileSize < docToImgMapFile.length());
        }
        catch (final EBookFormatException e)
        {
            fail("Encountered EBookFormatException when not expected.");
        }
    }

    @Test
    public void testFileParsing()
    {
        try
        {
            imgParserService.parseXMLFile(xmlFile, guidList, docToImgMap);
            assertEquals(4, guidList.size());
            final String fileGuid = xmlFile.getName().substring(0, xmlFile.getName().indexOf("."));
            assertTrue(docToImgMap.containsKey(fileGuid));
            assertEquals(2, docToImgMap.get(fileGuid).size());
        }
        catch (final EBookFormatException e)
        {
            fail("Encountered EBookFormatException when not expected.");
        }
    }

    @Test
    public void testFileParsingEmptyXMLFile()
    {
        try
        {
            imgParserService.parseXMLFile(emptyXmlFile, guidList, docToImgMap);
            fail("EBookFormatException was not thrown for empty XML file and it was expected.");
        }
        catch (final EBookFormatException e)
        {
            //Intentionally left blank
        }
    }

    @Test
    public void testSortedImgFileParsingXMLFile()
    {
        try
        {
            imgParserService.parseXMLFile(xmlFile3Sorted, guidList, docToImgMap);
            //Test all the images were parsed
            assertEquals(6, guidList.size());
            //Test that there are 2 document entries
            assertEquals(2, docToImgMap.keySet().size());
            //Test order of images in map
            final List<String> guids = docToImgMap.get("xmlTestFile3Sorted_11112222222222");
            assertEquals("I1d163990094d11e085f5891ac64a9905", guids.get(0));
            assertEquals("I1d163990094d11e085f5891ac64a9906", guids.get(1));
            assertEquals("I1d163990094d11e085f5891ac64a9907", guids.get(2));
            assertEquals("I1d163990094d11e085f5891ac64a9908", guids.get(3));
        }
        catch (final EBookFormatException e)
        {
            fail("Encountered EBookFormatException when not expected.");
        }
    }

    @Test
    public void testFileParsingInvalidXMLFile()
    {
        try
        {
            imgParserService.parseXMLFile(invalidXmlFile, guidList, docToImgMap);
            fail("EBookFormatException was not thrown for invalid XML file and it was expected.");
        }
        catch (final EBookFormatException e)
        {
            //Intentionally left blank
        }
    }

    @Test
    public void testListCreation()
    {
        final long initialSize = imgListFile.length();
        try
        {
            imgParserService.createImageList(imgListFile, guidList);
            final long newSize = imgListFile.length();
            assertTrue(initialSize < newSize);
        }
        catch (final EBookFormatException e)
        {
            fail("Encountered EBookFormatException when not expected.");
        }
    }

    @Test
    public void testListCreationNullGuid()
    {
        try
        {
            guidList.add(null);
            imgParserService.createImageList(imgListFile, guidList);
            fail("EBookFormatException was not thrown for null GUID.");
        }
        catch (final EBookFormatException e)
        {
            //Intentionally left blank
        }
    }

    @Test
    public void testListCreationShortGuid()
    {
        try
        {
            guidList.add("I5d463990094d11e085f5891ac64a"); //check 29 char GUID
            imgParserService.createImageList(imgListFile, guidList);
            fail("EBookFormatException was not thrown for short GUID.");
        }
        catch (final EBookFormatException e)
        {
            //Intentionally left blank
        }
    }

    @Test
    public void testListCreationLongGuid()
    {
        try
        {
            guidList.add("I5d463990094d11e085f5891ac64a995555567"); //check 37 char GUID
            imgParserService.createImageList(imgListFile, guidList);
            fail("EBookFormatException was not thrown for long GUID.");
        }
        catch (final EBookFormatException e)
        {
            //Intentionally left blank
        }
    }
}
