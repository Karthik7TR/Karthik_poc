package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.common.step.BookStep;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class XppFormatFileSystemImplTest
{
    private static final String ORIGINAL_DIR       = "workDirectory/Format/01_Original";
    private static final String SECTIONBREAKS_DIR  = "workDirectory/Format/02_Sectionbreaks";
    private static final String PAGEBREAKES_UP_DIR = "workDirectory/Format/02_PagebreakesUp";
    private static final String ORIGINAL_PARTS_DIR = "workDirectory/Format/03_OriginalParts";
    private static final String ORIGINAL_PAGES_DIR = "workDirectory/Format/04_OriginalPages";
    private static final String HTML_PAGES_DIR     = "workDirectory/Format/05_HtmlPages";
    private static final String TITLE_METADATA_DIR = "workDirectory/Format/07_title_metadata";

    private static final String FILE_NAME_XML         = "fileName.xml";
    private static final String FILE_NAME_ORIGINAL    = "fileName.original";
    private static final String FILE_NAME_MAIN        = "fileName.main";
    private static final String FILE_NAME_FOOTNOTES   = "fileName.footnotes";
    private static final String FILE_NAME_PART        = "fileName.part";
    private static final String FILE_NAME_1_MAIN_PART = "fileName_1_main.part";
    private static final String FILE_NAME_1_PAGE      = "fileName_1.page";
    private static final String FILE_NAME_HTML        = "fileName.html";
    private static final String FILE_NAME             = "fileName";
    private static final String TITLE_METADATA_XML    = "titleMetadata.xml";
    private static final String DOC_TO_IMAGE_MANIFEST = "workDirectory/Format/doc-to-image-manifest.txt";
    private static final String ANCHOR_TO_DOCUMENT_ID_MAP_FILE = "workDirectory/Format/anchorToDocumentIdMapFile.txt";

    private static final String MATERIAL_NUMBER = "11111111";
    private static final String MATERIAL_NUMBER_2 = "22222222";

    @InjectMocks
    private XppFormatFileSystemImpl fileSystem;
    @Mock
    private BookFileSystem bookFileSystem;
    @Mock
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException
    {
        given(bookFileSystem.getWorkDirectory(step)).willReturn(new File(temporaryFolder.getRoot(), "workDirectory"));
    }

    @Test
    public void shouldReturnOriginalDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalDirectory(step);
        //then
        assertThat(directory, hasPath(ORIGINAL_DIR));
    }

    @Test
    public void shouldReturnOriginalBundleDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalBundleDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(directory, hasPath(ORIGINAL_DIR + "/" + MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnOriginalFileForBundleStructure()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalFile(step, MATERIAL_NUMBER, FILE_NAME_XML);
        //then
        assertThat(directory, hasPath(ORIGINAL_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_MAIN));
    }

    @Test
    public void shouldReturnOriginalFile()
    {
        //given
        //when
        final File file = fileSystem.getOriginalFile(step, FILE_NAME_XML);
        //then
        assertThat(file, hasPath(ORIGINAL_DIR + "/" + FILE_NAME_MAIN));
    }

    @Test
    public void shouldReturnOriginalFiles() throws IOException
    {
        //given
        final File directory = fileSystem.getOriginalDirectory(step);
        directory.mkdirs();
        final File original = new File(directory, FILE_NAME_MAIN);
        final File footnotes = new File(directory, FILE_NAME_FOOTNOTES);
        original.createNewFile();
        footnotes.createNewFile();
        //when
        final Collection<File> files = fileSystem.getOriginalFiles(step);
        //then
        assertThat(files, contains(original));
    }

    @Test
    public void shouldReturnFootnotesFiles() throws IOException
    {
        //given
        final File directory = fileSystem.getOriginalDirectory(step);
        directory.mkdirs();
        final File original = new File(directory, FILE_NAME_MAIN);
        final File footnotes = new File(directory, FILE_NAME_FOOTNOTES);
        original.createNewFile();
        footnotes.createNewFile();
        //when
        final Collection<File> files = fileSystem.getFootnotesFiles(step);
        //then
        assertThat(files, contains(footnotes));
    }

    @Test
    public void shouldReturnFootnotesFileForBundleStructure()
    {
        //given
        //when
        final File file = fileSystem.getFootnotesFile(step, MATERIAL_NUMBER, FILE_NAME_XML);
        //then
        assertThat(file, hasPath(ORIGINAL_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_FOOTNOTES));
    }

    @Test
    public void shouldReturnFootnotesFile()
    {
        //given
        //when
        final File file = fileSystem.getFootnotesFile(step, FILE_NAME_XML);
        //then
        assertThat(file, hasPath(ORIGINAL_DIR + "/" + FILE_NAME_FOOTNOTES));
    }

    @Test
    public void shouldReturnOriginalMainAndFootnoteFiles() throws IOException
    {
        //given
        final File originalDir = mkdir(temporaryFolder.getRoot(), ORIGINAL_DIR);
        final File originalFile1 = mkfile(mkdir(originalDir, MATERIAL_NUMBER), FILE_NAME_XML);
        final File originalFile2 = mkfile(mkdir(originalDir, MATERIAL_NUMBER_2), FILE_NAME_XML);
        //when
        final Map<String, Collection<File>> map = fileSystem.getOriginalMainAndFootnoteFiles(step);
        //then
        assertTrue(map.get(MATERIAL_NUMBER).contains(originalFile1));
        assertTrue(map.get(MATERIAL_NUMBER_2).contains(originalFile2));
    }

    @Test
    public void shouldReturnSectionbreaksDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getSectionbreaksDirectory(step);
        //then
        assertThat(directory, hasPath(SECTIONBREAKS_DIR));
    }

    @Test
    public void shouldReturnSectionbreaksFile()
    {
        //given
        //when
        final File file = fileSystem.getSectionbreaksFile(step, FILE_NAME_MAIN);
        //then
        assertThat(file, hasPath(SECTIONBREAKS_DIR + "/" + FILE_NAME_MAIN));
    }

    @Test
    public void shouldReturnPageBreakesUpDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getPagebreakesUpDirectory(step);
        //then
        assertThat(directory, hasPath(PAGEBREAKES_UP_DIR));
    }

    @Test
    public void shouldReturnPageBreakesUpDirectoryForBundleStructure()
    {
        //given
        //when
        final File directory = fileSystem.getPagebreakesUpDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(directory, hasPath(PAGEBREAKES_UP_DIR + "/" + MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnPageBreakesUpFile()
    {
        //given
        //when
        final File file = fileSystem.getPagebreakesUpFile(step, FILE_NAME_MAIN);
        //then
        assertThat(file, hasPath(PAGEBREAKES_UP_DIR + "/" + FILE_NAME_MAIN));
    }

    @Test
    public void shouldReturnPageBreakesUpFileForBundleStructure()
    {
        //given
        //when
        final File file = fileSystem.getPagebreakesUpFile(step, MATERIAL_NUMBER, FILE_NAME_MAIN);
        //then
        assertThat(file, hasPath(PAGEBREAKES_UP_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_MAIN));
    }

    @Test
    public void shouldReturnPagebreakesUpFiles() throws IOException
    {
        //given
        final File pagebreakesUpDir = mkdir(temporaryFolder.getRoot(), PAGEBREAKES_UP_DIR);
        final File pageBreaksUpFile1 = mkfile(mkdir(pagebreakesUpDir, MATERIAL_NUMBER), FILE_NAME_XML);
        final File pageBreaksUpFile2 = mkfile(mkdir(pagebreakesUpDir, MATERIAL_NUMBER_2), FILE_NAME_XML);
        //when
        final Map<String, Collection<File>> map = fileSystem.getPagebreakesUpFiles(step);
        //then
        assertTrue(map.get(MATERIAL_NUMBER).contains(pageBreaksUpFile1));
        assertTrue(map.get(MATERIAL_NUMBER_2).contains(pageBreaksUpFile2));
    }

    @Test
    public void shouldReturnOriginalPartsDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalPartsDirectory(step);
        //then
        assertThat(directory, hasPath(ORIGINAL_PARTS_DIR));
    }

    @Test
    public void shouldReturnOriginalPartsDirectoryForBundleStructure()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalPartsDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(directory, hasPath(ORIGINAL_PARTS_DIR + "/" + MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnOriginalPartsFile()
    {
        //given
        //when
        final File file = fileSystem.getOriginalPartsFile(step, MATERIAL_NUMBER, FILE_NAME, 1, PartType.MAIN);
        //then
        assertThat(file, hasPath(ORIGINAL_PARTS_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_1_MAIN_PART));
    }

    @Test
    public void shouldReturnOriginalPagesDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalPagesDirectory(step);
        //then
        assertThat(directory, hasPath(ORIGINAL_PAGES_DIR));
    }

    @Test
    public void shouldReturnOriginalPagesDirectoryForBundleStructure()
    {
        //given
        //when
        final File directory = fileSystem.getOriginalPagesDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(directory, hasPath(ORIGINAL_PAGES_DIR + "/" + MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnOriginalPageFile()
    {
        //given
        //when
        final File file = fileSystem.getOriginalPageFile(step, FILE_NAME_ORIGINAL, 1);
        //then
        assertThat(file, hasPath(ORIGINAL_PAGES_DIR + "/" + FILE_NAME_1_PAGE));
    }

    @Test
    public void shouldReturnOriginalPageFileForBundleStructure()
    {
        //given
        //when
        final File file = fileSystem.getOriginalPageFile(step, MATERIAL_NUMBER, FILE_NAME_ORIGINAL, 1);
        //then
        assertThat(file, hasPath(ORIGINAL_PAGES_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_1_PAGE));
    }

    @Test
    public void shouldReturnOriginalPageFilesMap() throws IOException
    {
        //given
        final File originalPagesDir = mkdir(temporaryFolder.getRoot(), ORIGINAL_PAGES_DIR);
        final File file1 = mkfile(mkdir(originalPagesDir, MATERIAL_NUMBER), FILE_NAME_XML);
        final File file2 = mkfile(mkdir(originalPagesDir, MATERIAL_NUMBER_2), FILE_NAME_XML);
        //when
        final Map<String, Collection<File>> map = fileSystem.getOriginalPageFiles(step);
        //then
        assertTrue(map.get(MATERIAL_NUMBER).contains(file1));
        assertTrue(map.get(MATERIAL_NUMBER_2).contains(file2));
    }

    @Test
    public void shouldReturnHtmlPagesDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getHtmlPagesDirectory(step);
        //then
        assertThat(directory, hasPath(HTML_PAGES_DIR));
    }

    @Test
    public void shouldReturnHtmlPagesDirectoryForBundleStructure()
    {
        //given
        //when
        final File directory = fileSystem.getHtmlPagesDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(directory, hasPath(HTML_PAGES_DIR + "/" + MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnHtmlPageFile()
    {
        //given
        //when
        final File file = fileSystem.getHtmlPageFile(step, FILE_NAME_PART);
        //then
        assertThat(file, hasPath(HTML_PAGES_DIR + "/" + FILE_NAME_HTML));
    }

    @Test
    public void shouldReturnHtmlPageFileForBundleStructure()
    {
        //given
        //when
        final File file = fileSystem.getHtmlPageFile(step, MATERIAL_NUMBER, FILE_NAME_PART);
        //then
        assertThat(file, hasPath(HTML_PAGES_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_HTML));
    }

    @Test
    public void shouldReturnAnchorsMapFile()
    {
        //given
        //when
        final File file = fileSystem.getAnchorToDocumentIdMapFile(step);
        //then
        assertThat(file, hasPath(ANCHOR_TO_DOCUMENT_ID_MAP_FILE));
    }

    @Test
    public void shouldReturnDocToImagesMapFile()
    {
        //given
        //when
        final File file = fileSystem.getDocToImageMapFile(step);
        //then
        assertThat(file, hasPath(DOC_TO_IMAGE_MANIFEST));
    }

    @Test
    public void shouldReturnBundlePartTocFile()
    {
        //given
        //when
        final File file = fileSystem.getBundlePartTocFile(FILE_NAME_XML, MATERIAL_NUMBER, step);
        //then
        assertThat(file, hasPath("workDirectory/Format/06_Toc/11111111/toc_fileName.xml"));
    }

    @Test
    public void shouldReturnTitleMetadataDirectory()
    {
        //given
        //when
        final File file = fileSystem.getTitleMetadataDirectory(step);
        //then
        assertThat(file, hasPath(TITLE_METADATA_DIR));
    }

    @Test
    public void shouldReturnTitleMetadataFile()
    {
        //given
        //when
        final File file = fileSystem.getTitleMetadataFile(step);
        //then
        assertThat(file, hasPath(TITLE_METADATA_DIR + "/" + TITLE_METADATA_XML));
    }
}
