package com.thomsonreuters.uscl.ereader.gather.codesworkbench.parsinghandler;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit test to validate Novus Doc XML parsing.
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568.
 */
public final class NovusDocFileParserTest {
    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();
    private File cwbDir;
    private NovusDocFileParser parser;
    private GatherResponse gatherResponse;
    private Map<String, Integer> docGuidsMap;
    private Map<String, Map<Integer, String>> documentLevelMap;
    private Integer documentLevel = 2;

    private Integer numberOfDocuments = 0;

    @Before
    public void setUp() throws IOException {
        cwbDir = testFiles.newFolder("cwb");
        docGuidsMap = new HashMap<>();
        docGuidsMap.put("1", 1);
        docGuidsMap.put("2", 1);
        numberOfDocuments = docGuidsMap.size();
        gatherResponse = new GatherResponse();
        documentLevelMap = new HashMap<>();
        parser = new NovusDocFileParser(
            "collectionName",
            docGuidsMap,
            cwbDir,
            cwbDir,
            gatherResponse,
            0,
            documentLevel,
            documentLevelMap);
    }

    @Test(expected = GatherException.class)
    public void testNoneExistingFile() throws GatherException {
        parser.parseXML(new File(cwbDir, "none.xml"));
    }

    @Test(expected = GatherException.class)
    public void testInvalidXML() throws GatherException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(nort, "asdasd");
        parser.parseXML(nort);
    }

    @Test
    public void testDocFound() throws GatherException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load>"
                + "<n-document guid=\"1\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
                + "<n-document guid=\"2\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
                + "</n-load>");

        parser.parseXML(nort);

        assertGatherResponse(2);
    }

    @Test
    public void testDocFoundWithDuplicates() throws GatherException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load>"
                + "<n-document guid=\"1\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
                + "<n-document guid=\"2\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
                + "<n-document guid=\"1\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
                + "<n-document guid=\"2\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
                + "</n-load>");

        parser.parseXML(nort);

        assertGatherResponse(2);
    }

    @Test
    public void testDocFoundWithExtra() throws GatherException {
        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load>"
                + "<n-document guid=\"1\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
                + "<n-document guid=\"2\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
                + "<n-document guid=\"3\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
                + "<n-document guid=\"4\" control=\"ADD\"><n-metadata></n-metadata><n-docbody></n-docbody></n-document>"
                + "</n-load>");

        parser.parseXML(nort);

        assertGatherResponse(2);
    }

    @Test
    public void testDuplicateDocInAnotherContentSet() throws GatherException {
        final String docGuid = "1";
        docGuidsMap.clear();
        docGuidsMap.put(docGuid + "-" + documentLevel, 1);
        numberOfDocuments = docGuidsMap.size();

        final Map<Integer, String> levelMap = new HashMap<>();
        levelMap.put(1, docGuid);
        documentLevelMap.put(docGuid, levelMap);

        final File nort = new File(cwbDir, "none.xml");
        addContentToFile(
            nort,
            "<n-load>"
                + "<n-document guid=\""
                + docGuid
                + "\" control=\"ADD\"><n-metadata><md.uuid>"
                + docGuid
                + "</md.uuid>"
                + "</n-metadata><n-docbody></n-docbody></n-document></n-load>");

        parser.parseXML(nort);

        assertGatherResponse(1);
    }

    private void assertGatherResponse(final Integer tocSequence) {
        assertEquals(numberOfDocuments.intValue(), gatherResponse.getDocCount());
        assertEquals(numberOfDocuments.intValue(), gatherResponse.getDocCount2());
        assertEquals(tocSequence, parser.getTocSequence());
    }

    private void addContentToFile(final File file, final String text) {
        try (FileWriter fileOut = new FileWriter(file)) {
            fileOut.write(text);
            fileOut.close();
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
