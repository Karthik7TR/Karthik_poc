package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit test to validate XSL include resolutions.
 *
 * @author <a href="mailto:Selvedin.Alic@thomsonreuters.com">Selvedin Alic</a> u0095869.
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568.
 */
public final class XSLIncludeResolverTest
{
    private XSLIncludeResolver resolver;
    private File sourceDir;

    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();

    @Before
    public void setUp() throws Exception
    {
        sourceDir = testFiles.newFolder("XSLIncludeResolverTest");

        final File emptyXSL = new File(sourceDir, "_Empty.xsl");
        assertTrue(emptyXSL.createNewFile());

        resolver = new XSLIncludeResolver();
        resolver.setEmptyXSL(emptyXSL);
        resolver.setPlatformDir(sourceDir);
        resolver.setWestlawNextDir(sourceDir);
    }

    @Test
    public void testNoneExistingXSL() throws Exception
    {
        final File xsl = new File(sourceDir, "testNoneExistingXSL");

        try
        {
            resolver.resolve(xsl.getName(), sourceDir.getCanonicalPath());
            fail("Test should throw a TransformerException since the XSL does not exist.");
        }
        catch (final TransformerException e)
        {
            //expected exception thrown
        }
    }

    @Test
    public void testDefaultPassthroughCase() throws Exception
    {
        final File xsl = new File(sourceDir, "testDefaultPassthroughCase.xsl");
        assertTrue(xsl.createNewFile());
        AddContentToXsl(xsl);

        assertEquals(0, resolver.getIncludedXSLTs().size());
        final Source src = resolver.resolve(xsl.getName(), xsl.toURI().toString());
        assertEquals(1, resolver.getIncludedXSLTs().size());
        assertTrue(src.getSystemId().endsWith("testDefaultPassthroughCase.xsl"));
    }

    @Test
    public void testSameXSLMultipleIncludes() throws Exception
    {
        final File xsl = new File(sourceDir, "testSameXSLMultipleIncludes.xsl");
        assertTrue(xsl.createNewFile());
        AddContentToXsl(xsl);

        assertEquals(0, resolver.getIncludedXSLTs().size());
        final Source src = resolver.resolve(xsl.getName(), xsl.toURI().toString());
        assertEquals(1, resolver.getIncludedXSLTs().size());
        assertTrue(src.getSystemId().endsWith("testSameXSLMultipleIncludes.xsl"));
        final Source src2 = resolver.resolve(xsl.getName(), xsl.toURI().toString());
        assertEquals(1, resolver.getIncludedXSLTs().size());
        assertTrue(src2.getSystemId().endsWith("_Empty.xsl"));

        final File xsl2 = new File(sourceDir, "testSameXSLMultipleIncludes.xsl");
        final Source src3 = resolver.resolve(xsl2.getName(), xsl2.toURI().toString());
        assertEquals(1, resolver.getIncludedXSLTs().size());
        assertTrue(src3.getSystemId().endsWith("_Empty.xsl"));
    }

    @Test
    public void testContextAnalysisInclude() throws Exception
    {
        final File xsl = new File(sourceDir, "ContextAndAnalysis.xsl");
        assertTrue(xsl.createNewFile());
        AddContentToXsl(xsl);

        final File xsl2 = new File(sourceDir, "eBookContextAndAnalysis.xsl");
        assertTrue(xsl2.createNewFile());

        resolver.setIncludeAnnotations(true);
        assertEquals(0, resolver.getIncludedXSLTs().size());
        final Source src = resolver.resolve(xsl.getName(), xsl.toURI().toString());
        assertEquals(1, resolver.getIncludedXSLTs().size());
        assertTrue(src.getSystemId().endsWith("eBookContextAndAnalysis.xsl"));
    }

    @Test
    public void testNotesOfDecisionsInclude() throws Exception
    {
        final File xsl = new File(sourceDir, "NotesOfDecisions.xsl");
        assertTrue(xsl.createNewFile());
        AddContentToXsl(xsl);

        final File xsl2 = new File(sourceDir, "eBookNotesOfDecisions.xsl");
        assertTrue(xsl2.createNewFile());

        resolver.setIncludeNotesOfDecisions(true);
        assertEquals(0, resolver.getIncludedXSLTs().size());
        final Source src = resolver.resolve(xsl.getName(), xsl.toURI().toString());
        assertEquals(1, resolver.getIncludedXSLTs().size());
        assertTrue(src.getSystemId().endsWith("eBookNotesOfDecisions.xsl"));
    }

    private void AddContentToXsl(final File file)
    {
        try
        {
            final FileWriter fileOut = new FileWriter(file);
            fileOut.write("<nothing></nothing>");
            fileOut.close();
        }
        catch (final FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
    }
}
