package com.thomsonreuters.uscl.ereader.gather.step.service.impl;

import com.thomsonreuters.uscl.ereader.gather.step.service.PrepareSourcesService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class PrepareSourcesServiceImplTest {
    private static final String TITLE_ID = "uscl/an/test";
    private static final String TITLE_ID_WITH_DASHES = TITLE_ID.replace("/", "-");
    private PrepareSourcesService prepareSourcesService;
    @Rule
    public TemporaryFolder testFiles = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        prepareSourcesService = new PrepareSourcesServiceImpl();
    }

    @Test
    public void shouldGetTocFile() throws IOException {
        File tocXmlFile = testFiles.newFile("toc.xml");
        File actual = prepareSourcesService.getTocFile(tocXmlFile, TITLE_ID);
        assertEquals("toc!" + TITLE_ID_WITH_DASHES + ".xml", actual.getName());
    }

    @Test
    public void shouldGetDocsGuidsFile() throws IOException {
        File docsGuidsFile = testFiles.newFile("docs-guids.txt");
        File actual = prepareSourcesService.getDocsGuidsFile(docsGuidsFile, TITLE_ID);
        assertEquals("docs-guids!" + TITLE_ID_WITH_DASHES + ".txt", actual.getName());
    }
}
