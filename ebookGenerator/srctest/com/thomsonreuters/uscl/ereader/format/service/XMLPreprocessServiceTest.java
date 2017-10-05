package com.thomsonreuters.uscl.ereader.format.service;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test various XML preprocessing test scenarios.
 *
 * @author <a href="mailto:zack.farrell@thomsonreuters.com">Zack Farrell</a> uc209819
 */
public final class XMLPreprocessServiceTest {
    private XMLPreprocessServiceImpl preprocessService;
    private File tempRootDir;
    private File tempXMLfile;

    private File srcDir;
    private File targetDir;
    private boolean isFinalStage;
    private List<DocumentCopyright> copyrights;
    private List<DocumentCurrency> currencies;

    @Before
    public void setup() {
        preprocessService = new XMLPreprocessServiceImpl();
        final FileExtensionFilter filter = new FileExtensionFilter();
        filter.setAcceptedFileExtensions(new String[] {"xml"});
        final FileHandlingHelper helper = new FileHandlingHelper();
        helper.setFilter(filter);
        preprocessService.setfileHandlingHelper(helper);

        tempRootDir = new File(System.getProperty("java.io.tmpdir") + "\\EvenMoreTemp");
        tempRootDir.mkdir();

        srcDir = new File(tempRootDir.getAbsolutePath(), "Source_Directory");
        srcDir.mkdir();
        makeFile(
            srcDir,
            "temp1.xml",
            "<test><include.currency n-include_guid=\"123456789\">This is a currency</include.currency></test>");
        makeFile(srcDir, "temp2.html", "HTML stream goes here");
        tempXMLfile = makeFile(
            srcDir,
            "temp3.xml",
            "<body><test><include.copyright n-include_guid=\"987654321\">This is a copyright</include.copyright></test>"
                + "<test><include.currency n-include_guid=\"123456789\">This is a currency</include.currency></test></body>");

        targetDir = new File(tempRootDir.getAbsolutePath(), "Target_Directory");
        targetDir.mkdir();

        isFinalStage = true;

        final DocumentCopyright copyright = new DocumentCopyright();
        copyright.setCopyrightGuid("987654321");
        copyright.setNewText("Copyright");
        copyrights = new ArrayList<>();
        copyrights.add(copyright);

        final DocumentCurrency currency = new DocumentCurrency();
        currency.setCurrencyGuid("123456789");
        currency.setNewText("Currency");
        currencies = new ArrayList<>();
        currencies.add(currency);
    }

    @After
    public void tearDown() throws Exception {
        /*  recursively deletes the root directory, and all its subdirectories and files  */
        FileUtils.deleteDirectory(tempRootDir);
    }

    /** makeFile( File directory, String name, String content )
     * 		helper method to streamline file creation
     * @param directory		Location the new file will be created in
     * @param name			Name of the new file
     * @param content		Content to be written into the new file
     * @return			returns a File object directing to the new file
     * 					returns null if any errors occur
     */
    private File makeFile(final File directory, final String name, final String content) {
        final File file = new File(directory, name);
        try (FileOutputStream out = new FileOutputStream(file)) {
            file.createNewFile();
            out.write(content.getBytes());
            out.flush();
            out.close();
            return file;
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * XMLPreprocesService should take a source directory with xml files and perform the first
     * step necessary to transform them into html files. The resulting "preprocess" files are
     * created in the target directory
     */
    @Test
    public void TestXMLPreprocessServiceHappyPath() {
        int numDocs = -1;
        boolean thrown = false;

        try {
            numDocs = preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
        } catch (final Exception e) {
            //e.printStackTrace();
            thrown = true;
        }
        assertTrue(!thrown);
        assertTrue(numDocs == 2);

        final File preprocess1 = new File(targetDir.getAbsolutePath(), "temp1.preprocess");
        final File preprocess2 = new File(targetDir.getAbsolutePath(), "temp3.preprocess");
        assertTrue(preprocess1.exists());
        assertTrue(preprocess2.exists());
    }

    @Test
    public void TestXMLPreprocessBadSourceDir() throws Exception {
        boolean thrown = false;

        try { /*  null source directory  */
            preprocessService.transformXML(null, targetDir, isFinalStage, copyrights, currencies);
        } catch (final IllegalArgumentException e) {
            /*  expected exception  */
            //e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;

        try { /*  file as a source directory  */
            preprocessService.transformXML(tempXMLfile, targetDir, isFinalStage, copyrights, currencies);
        } catch (final IllegalArgumentException e) {
            /*  expected exception  */
            //e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;
    }

    /**
     * the method should be able to handle a non-existent target directory by
     * creating it on the fly, assuming its parent directory is real
     */
    @Test
    public void TestXMLPreprocessBadTargetDir() {
        int numDocs = -1;
        boolean thrown = false;
        try { /*  targetDir does not exist  */
            targetDir = new File(tempRootDir.getAbsolutePath(), "not_real");
            numDocs = preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
        } catch (final Exception e) {
            //e.printStackTrace();
            thrown = true;
        }
        assertTrue(!thrown);
        assertTrue(numDocs == 2);
        thrown = false;
    }

    @Test
    public void TestXMLPreprocessBadXML() {
        boolean thrown = false;
        srcDir = new File(tempRootDir.getAbsolutePath(), "badXML");
        srcDir.mkdir();

        try { /*  source directory with no xml files  */
            preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
        } catch (final EBookFormatException e) {
            /*  expected exception  */
            //e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;

        makeFile(srcDir, "bad1.xml", "This is totally an file with xml format");
        makeFile(srcDir, "bad2.xml", "Excellent xml found in here");
        makeFile(srcDir, "bad3.html", "okay, this isn't even an xml file");

        try { /*  source directory with bad xml files  */
            preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
        } catch (final EBookFormatException e) {
            /*  expected exception  */
            //e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void TestExtraCopyrightCurrencyInfo() {
        boolean thrown = false;

        final DocumentCurrency currency = new DocumentCurrency();
        currency.setCurrencyGuid("111111111");
        currency.setNewText("Monay");
        currencies.add(currency);

        try { /*  extra currency info  */
            preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
        } catch (final EBookFormatException e) {
            /*  expected exception  */
            //e.printStackTrace();
            thrown = true;
        }
        assertTrue(thrown);
        thrown = false;

        final DocumentCopyright copyright = new DocumentCopyright();
        copyright.setCopyrightGuid("222222222");
        copyright.setNewText("Copyleft");
        copyrights.add(copyright);

        try { /*  extra copyright info  */
            preprocessService.transformXML(srcDir, targetDir, isFinalStage, copyrights, currencies);
        } catch (final EBookFormatException e) {
            /*  expected exception  */
            //e.printStackTrace();
            thrown = true;
        }
    }
}
