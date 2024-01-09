package com.thomsonreuters.uscl.ereader.format.parsinghandler;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.SplitDocument;
import com.thomsonreuters.uscl.ereader.format.service.AutoSplitGuidsServiceTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class AutoSplitGuiIdsHandlerTest {
    private AutoSplitNodesHandler splitBookFilter;
    private static final String FINE_NAME = "toc.xml";
    private InputStream tocXml;
    private File tocFile;

    @Before
    public void setUp() throws Exception {
        final URL url = AutoSplitGuidsServiceTest.class.getResource(FINE_NAME);
        tocFile = new File(url.toURI());
    }

    @After
    public void tearDown() {
        splitBookFilter = null;
    }

    @Test
    public void testMargin() {
        splitBookFilter = new AutoSplitNodesHandler(7500, 10);
        Assert.assertEquals(Integer.valueOf(750), splitBookFilter.getMargin(7500));
    }

    @Test
    public void testMarginWithuneven() {
        splitBookFilter = new AutoSplitNodesHandler(7523, 10);
        Assert.assertEquals(Integer.valueOf(752), splitBookFilter.getMargin(7523));
    }

    @Test
    public void testWithFile() throws Exception {
        tocXml = new FileInputStream(tocFile);
        final Map<String, String> splitTocGuidList = testHelper(tocXml, 25, 10);
        Assert.assertEquals(splitTocGuidList.size(), 4);
    }

    @Test
    public void test() {
        final BookDefinition book = new BookDefinition();
        final List<SplitDocument> expectTedGuidList = new ArrayList<>();

        final SplitDocument s = new SplitDocument();
        s.setTocGuid("TABLEOFCONTENTS33CHARACTERSLONG_4");
        s.setBookDefinition(book);
        expectTedGuidList.add(s);

        book.setSplitDocuments(new HashSet<>(expectTedGuidList));

        System.out.println(book.getSplitDocuments().toString());
    }

    @Test
    public void testWithString() {
        final String xmlTestStr = "<EBook>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_1</Guid><DocumentGuid>DOC_GUID1</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_2</Guid>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_3</Guid><DocumentGuid>DOC_GUID2</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_4</Guid><DocumentGuid>DOC_GUID3</DocumentGuid></EBookToc>"
            + "<EBookToc><Name>BLARGH</Name><Guid>TABLEOFCONTENTS33CHARACTERSLONG_5</Guid><DocumentGuid>DOC_GUID4</DocumentGuid></EBookToc></EBookToc>"
            + "</EBook>";

        final List<String> expectTedGuidList = new ArrayList<>();
        expectTedGuidList.add("TABLEOFCONTENTS33CHARACTERSLONG_5");

        final ByteArrayInputStream input = new ByteArrayInputStream(xmlTestStr.getBytes());
        final Map<String, String> splitTocGuidList = testHelper(input, 2, 10);

        Assert.assertEquals(splitTocGuidList.size(), expectTedGuidList.size());
        for (final Map.Entry<String, String> entry : splitTocGuidList.entrySet()) {
            final String uuid = entry.getKey();
            Assert.assertEquals(expectTedGuidList.get(0).toString(), uuid);
        }
    }

    public Map<String, String> testHelper(final InputStream inputXML, final int splitSize, final int percent) {
        Map<String, String> splitTocGuidList = new HashMap<>();
        ByteArrayOutputStream output = null;
        try {
            output = new ByteArrayOutputStream();

            splitBookFilter = new AutoSplitNodesHandler(splitSize, percent);
            splitBookFilter.parseInputStream(inputXML);

            //splitBookFilter.setDeterminedPartSize(splitSize);

            splitTocGuidList = splitBookFilter.getSplitTocTextMap();

            for (final Map.Entry<String, String> entry : splitTocGuidList.entrySet()) {
                final String uuid = entry.getKey();
                System.out.println(entry.getValue() + "----------------Toc UUID-----" + uuid);
            }
        } catch (final Exception e) {
            fail("Encountered exception during test: " + e.getMessage());
        } finally {
            try {
                if (inputXML != null) {
                    inputXML.close();
                }
                if (output != null) {
                    output.close();
                }
            } catch (final Exception e) {
                fail("Couldn't clean up resources: " + e.getMessage());
            }
        }

        return splitTocGuidList;
    }
}
