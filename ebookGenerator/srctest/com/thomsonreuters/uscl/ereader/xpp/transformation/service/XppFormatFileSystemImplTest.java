package com.thomsonreuters.uscl.ereader.xpp.transformation.service;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileSystemMatcher.hasPath;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkfile;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.thomsonreuters.uscl.ereader.common.filesystem.BookFileSystem;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.basefiles.BaseFilesIndex;
import com.thomsonreuters.uscl.ereader.common.filesystem.entity.partfiles.PartFilesIndex;
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
    private static final String MATERIAL_NUMBER = "11111111";
    private static final String MATERIAL_NUMBER_2 = "22222222";

    private static final String SECTION_UUID = "Ie0b9c12bf8fb11d99f28ffa0ae8c2575";
    private static final String SECTION_UUID_2 = "Ie0b9c12bf8fb11d99f28ffa0ae8c2576";
    private static final String CSS_DIR = "01_Css";
    private static final String ORIGINAL_DIR = "workDirectory/Format/02_Original";
    private static final String PROCESSED_CITE_QUERIES_DIR =
        "workDirectory/Format/02_Original/" + MATERIAL_NUMBER + "/Processed Cite Queries";
    private static final String SOURCE_DIR = "workDirectory/Format/03_StructureWithMetadata";
    private static final String SECTIONBREAKS_DIR = "workDirectory/Format/04_Sectionbreaks";
    private static final String SECTIONBREAKS_UP_DIR = "workDirectory/Format/05_SectionbreaksUp";
    private static final String ORIGINAL_PARTS_DIR = "workDirectory/Format/06_OriginalParts";
    private static final String ORIGINAL_PAGES_DIR = "workDirectory/Format/07_OriginalPages";
    private static final String HTML_PAGES_DIR = "workDirectory/Format/08_HtmlPages";
    private static final String EXTERNAL_LINKS_DIR = "workDirectory/Format/09_ExternalLinks";
    private static final String EXTERNAL_LINKS_MAPPING_DIR = "workDirectory/Format/09_ExternalLinks_Mapping";
    private static final String TOC_DIR = "workDirectory/Format/10_Toc";
    private static final String TITLE_METADATA_DIR = "workDirectory/Format/11_title_metadata";

    private static final String FILE_NAME_XML = "fileName.xml";
    private static final String FILE_NAME_MAIN = "fileName.main";
    private static final String FILE_NAME_FOOTNOTES = "fileName.footnotes";
    private static final String FILE_NAME_PART = "fileName.part";
    private static final String FILE_NAME_1_MAIN_PART = "fileName.DIVXML_1_main_Ie0b9c12bf8fb11d99f28ffa0ae8c2575.part";
    private static final String FILE_NAME_1_FOOTNOTES_PART =
        "fileName.DIVXML_1_footnotes_Ie0b9c12bf8fb11d99f28ffa0ae8c2575.part";
    private static final String FILE_NAME_2_MAIN_PART = "fileName.DIVXML_2_main_Ie0b9c12bf8fb11d99f28ffa0ae8c2576.part";
    private static final String FILE_NAME_2_FOOTNOTES_PART =
        "fileName.DIVXML_2_footnotes_Ie0b9c12bf8fb11d99f28ffa0ae8c2576.part";
    private static final String FILE_NAME_1_PAGE_WITH_GUID = "fileName_0001_Ie0b9c12bf8fb11d99f28ffa0ae8c2575.page";
    private static final String FILE_NAME_HTML = "fileName.html";
    private static final String FILE_NAME = "fileName";
    private static final String FILE_NAME_CSS = "fileName.css";
    private static final String BASE_FILE_NAME = "fileName.DIVXML";
    private static final String TITLE_METADATA_XML = "titleMetadata.xml";
    private static final String DOC_TO_IMAGE_MANIFEST = "workDirectory/Format/doc-to-image-manifest.txt";
    private static final String ANCHOR_TO_DOCUMENT_ID_MAP_FILE = "workDirectory/Format/anchorToDocumentIdMapFile.xml";

    private static final String DOC_FAMILY_GUID = SECTION_UUID;

    @InjectMocks
    private XppFormatFileSystemImpl fileSystem;
    @Mock
    private BookFileSystem bookFileSystem;
    @Mock
    private BookStep step;
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp()
    {
        given(bookFileSystem.getWorkDirectory(step)).willReturn(new File(temporaryFolder.getRoot(), "workDirectory"));
    }

    @Test
    public void shouldReturnStructureWithMetadataDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getStructureWithMetadataDirectory(step);
        //then
        assertThat(directory, hasPath(SOURCE_DIR));
    }

    @Test
    public void shouldReturnStructureWithMetadataBundleDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getStructureWithMetadataBundleDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(directory, hasPath(SOURCE_DIR + "/" + MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnStructureWithMetadataFiles() throws IOException
    {
        //given
        final File sourcelDir = mkdir(temporaryFolder.getRoot(), SOURCE_DIR);
        final File originalFile1 = mkfile(mkdir(sourcelDir, MATERIAL_NUMBER), FILE_NAME_XML);
        final File originalFile2 = mkfile(mkdir(sourcelDir, MATERIAL_NUMBER_2), FILE_NAME_XML);
        //when
        final Map<String, Collection<File>> map = fileSystem.getStructureWithMetadataFiles(step);
        //then
        assertTrue(map.get(MATERIAL_NUMBER).contains(originalFile1));
        assertTrue(map.get(MATERIAL_NUMBER_2).contains(originalFile2));
    }

    @Test
    public void shouldReturnStructureWithMetadataFilesIndex() throws IOException
    {
        //given
        final File sourcelDir = mkdir(temporaryFolder.getRoot(), SOURCE_DIR);
        final File originalFileMain = mkfile(mkdir(sourcelDir, MATERIAL_NUMBER), FILE_NAME_MAIN);
        final File originalFileFootnotes = mkfile(mkdir(sourcelDir, MATERIAL_NUMBER), FILE_NAME_FOOTNOTES);
        //when
        final BaseFilesIndex baseFilesIndex = fileSystem.getStructureWithMetadataFilesIndex(step);
        //then
        assertTrue(baseFilesIndex.get(MATERIAL_NUMBER, FILE_NAME, PartType.MAIN).equals(originalFileMain));
        assertTrue(baseFilesIndex.get(MATERIAL_NUMBER, FILE_NAME, PartType.FOOTNOTE).equals(originalFileFootnotes));
    }

    @Test
    public void shouldReturnStructureWithMetadataFile()
    {
        //given
        //when
        final File file = fileSystem.getStructureWithMetadataFile(step, MATERIAL_NUMBER, FILE_NAME_XML);
        //then
        assertThat(file, hasPath(SOURCE_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_XML));
    }

    @Test
    public void shouldReturnFontsCssDirectory()
    {
        //given
        //when
        final File dir = fileSystem.getFontsCssDirectory(step);
        //then
        assertThat(dir, hasPath(CSS_DIR));
    }

    @Test
    public void shouldReturnCssDirectory()
    {
        //given
        //when
        final File dir = fileSystem.getFontsCssDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(dir, hasPath(CSS_DIR));
        assertThat(dir, hasPath(MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnCssFile()
    {
        //given
        //when
        final File dir = fileSystem.getFontsCssFile(step, MATERIAL_NUMBER, FILE_NAME_XML);
        //then
        assertThat(dir, hasPath(CSS_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_CSS));
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
        final File directory = fileSystem.getOriginalDirectory(step, MATERIAL_NUMBER);
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
    public void shouldReturnFootnotesFileForBundleStructure()
    {
        //given
        //when
        final File file = fileSystem.getFootnotesFile(step, MATERIAL_NUMBER, FILE_NAME_XML);
        //then
        assertThat(file, hasPath(ORIGINAL_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_FOOTNOTES));
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
    public void shouldReturnProcessedCiteQueryDirectory()
    {
        //given
        //when
        final File res = fileSystem.getCiteQueryProcessedDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(res, hasPath(PROCESSED_CITE_QUERIES_DIR));
    }

    @Test
    public void shouldReturnProcessedCiteQueryFile()
    {
        //given
        //when
        final File res = fileSystem.getCiteQueryProcessedFile(step, MATERIAL_NUMBER, BASE_FILE_NAME + ".xml");
        //then
        assertThat(res, hasPath(PROCESSED_CITE_QUERIES_DIR + "/" + BASE_FILE_NAME + ".xml"));
    }

    @Test
    public void shouldReturnSectionbreaksDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getSectionbreaksDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(directory, hasPath(SECTIONBREAKS_DIR + "/" + MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnSectionbreaksDirectoryForBundleStructure()
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
        final File file = fileSystem.getSectionbreaksFile(step, MATERIAL_NUMBER, FILE_NAME_MAIN);
        //then
        assertThat(file, hasPath(SECTIONBREAKS_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_MAIN));
    }

    @Test
    public void shouldReturnSectionbreaksFiles() throws IOException
    {
        //given
        final File dir = mkdir(temporaryFolder.getRoot(), SECTIONBREAKS_DIR);
        final File file1 = mkfile(mkdir(dir, MATERIAL_NUMBER), FILE_NAME_XML);
        final File file2 = mkfile(mkdir(dir, MATERIAL_NUMBER_2), FILE_NAME_XML);
        //when
        final Map<String, Collection<File>> map = fileSystem.getSectionBreaksFiles(step);
        //then
        assertTrue(map.get(MATERIAL_NUMBER).contains(file1));
        assertTrue(map.get(MATERIAL_NUMBER_2).contains(file2));
    }

    @Test
    public void shouldReturnSectionbreaksUpDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getSectionbreaksUpDirectory(step);
        //then
        assertThat(directory, hasPath(SECTIONBREAKS_UP_DIR));
    }

    @Test
    public void shouldReturnSectionbreaksUpDirectoryForBundleStructure()
    {
        //given
        //when
        final File directory = fileSystem.getSectionbreaksUpDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(directory, hasPath(SECTIONBREAKS_UP_DIR + "/" + MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnSectionbreaksUpFileForBundleStructure()
    {
        //given
        //when
        final File file = fileSystem.getSectionbreaksUpFile(step, MATERIAL_NUMBER, FILE_NAME_MAIN);
        //then
        assertThat(file, hasPath(SECTIONBREAKS_UP_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_MAIN));
    }

    @Test
    public void shouldReturnSectionbreaksUpFiles() throws IOException
    {
        //given
        final File sectionbreaksUpDir = mkdir(temporaryFolder.getRoot(), SECTIONBREAKS_UP_DIR);
        final File sectionbreaksUpFile1 = mkfile(mkdir(sectionbreaksUpDir, MATERIAL_NUMBER), FILE_NAME_XML);
        final File sectionbreaksUpFile2 = mkfile(mkdir(sectionbreaksUpDir, MATERIAL_NUMBER_2), FILE_NAME_XML);
        //when
        final Map<String, Collection<File>> map = fileSystem.getSectionbreaksUpFiles(step);
        //then
        assertTrue(map.get(MATERIAL_NUMBER).contains(sectionbreaksUpFile1));
        assertTrue(map.get(MATERIAL_NUMBER_2).contains(sectionbreaksUpFile2));
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
    public void shouldReturnOriginalPartsFiles() throws IOException
    {
        //given
        final File originalPartsDir = mkdir(temporaryFolder.getRoot(), ORIGINAL_PARTS_DIR);
        final File file1Main = mkfile(mkdir(originalPartsDir, MATERIAL_NUMBER), FILE_NAME_1_MAIN_PART);
        final File file1Footnotes = mkfile(mkdir(originalPartsDir, MATERIAL_NUMBER), FILE_NAME_1_FOOTNOTES_PART);
        final File file2Main = mkfile(mkdir(originalPartsDir, MATERIAL_NUMBER_2), FILE_NAME_2_MAIN_PART);
        final File file2Footnotes = mkfile(mkdir(originalPartsDir, MATERIAL_NUMBER_2), FILE_NAME_2_FOOTNOTES_PART);
        //when
        final PartFilesIndex partFilesIndex = fileSystem.getOriginalPartsFiles(step);
        //then
        assertTrue(
            partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, SECTION_UUID, PartType.MAIN)
                .getFile()
                .equals(file1Main));
        assertTrue(
            partFilesIndex.get(MATERIAL_NUMBER, BASE_FILE_NAME, SECTION_UUID, PartType.FOOTNOTE)
                .getFile()
                .equals(file1Footnotes));
        assertTrue(
            partFilesIndex.get(MATERIAL_NUMBER_2, BASE_FILE_NAME, SECTION_UUID_2, PartType.MAIN)
                .getFile()
                .equals(file2Main));
        assertTrue(
            partFilesIndex.get(MATERIAL_NUMBER_2, BASE_FILE_NAME, SECTION_UUID_2, PartType.FOOTNOTE)
                .getFile()
                .equals(file2Footnotes));
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
    public void shouldReturnOriginalPageFileForBundleStructure()
    {
        //given
        //when
        final File file = fileSystem.getOriginalPageFile(step, MATERIAL_NUMBER, FILE_NAME, 1, DOC_FAMILY_GUID);
        //then
        assertThat(file, hasPath(ORIGINAL_PAGES_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_1_PAGE_WITH_GUID));
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
    public void shouldReturnHtmlPageFilesMap() throws IOException
    {
        //given
        final File dir = mkdir(temporaryFolder.getRoot(), HTML_PAGES_DIR);
        final File file1 = mkfile(mkdir(dir, MATERIAL_NUMBER), FILE_NAME_XML);
        final File file2 = mkfile(mkdir(dir, MATERIAL_NUMBER_2), FILE_NAME_XML);
        //when
        final Map<String, Collection<File>> map = fileSystem.getHtmlPageFiles(step);
        //then
        assertTrue(map.get(MATERIAL_NUMBER).contains(file1));
        assertTrue(map.get(MATERIAL_NUMBER_2).contains(file2));
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
    public void shouldReturnExternalLinksDirectory()
    {
        //given
        //when
        final File file = fileSystem.getExternalLinksDirectory(step);
        //then
        assertThat(file, hasPath(EXTERNAL_LINKS_DIR));
    }

    @Test
    public void shouldReturnExternalLinksDirectoryForMaterialNumber()
    {
        //given
        //when
        final File file = fileSystem.getExternalLinksDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(file, hasPath(EXTERNAL_LINKS_DIR + "/" + MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnExternalLinksFile()
    {
        //given
        //when
        final File file = fileSystem.getExternalLinksFile(step, MATERIAL_NUMBER, FILE_NAME_HTML);
        //then
        assertThat(file, hasPath(EXTERNAL_LINKS_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_HTML));
    }

    @Test
    public void shouldReturnExternalLinksFilesMap() throws IOException
    {
        //given
        final File dir = mkdir(temporaryFolder.getRoot(), EXTERNAL_LINKS_DIR);
        final File file1 = mkfile(mkdir(dir, MATERIAL_NUMBER), FILE_NAME_XML);
        final File file2 = mkfile(mkdir(dir, MATERIAL_NUMBER_2), FILE_NAME_XML);
        //when
        final Map<String, Collection<File>> map = fileSystem.getExternalLinksFiles(step);
        //then
        assertTrue(map.get(MATERIAL_NUMBER).contains(file1));
        assertTrue(map.get(MATERIAL_NUMBER_2).contains(file2));
    }

    @Test
    public void shouldReturnExternalLinksMappingDirectory()
    {
        //given
        //when
        final File file = fileSystem.getExternalLinksMappingDirectory(step);
        //then
        assertThat(file, hasPath(EXTERNAL_LINKS_MAPPING_DIR));
    }

    @Test
    public void shouldReturnExternalLinksMappingDirectoryForMaterialNumber()
    {
        //given
        //when
        final File file = fileSystem.getExternalLinksMappingDirectory(step, MATERIAL_NUMBER);
        //then
        assertThat(file, hasPath(EXTERNAL_LINKS_MAPPING_DIR + "/" + MATERIAL_NUMBER));
    }

    @Test
    public void shouldReturnExternalLinksMappingFile()
    {
        //given
        //when
        final File file = fileSystem.getExternalLinksMappingFile(step, MATERIAL_NUMBER, FILE_NAME_HTML);
        //then
        assertThat(file, hasPath(EXTERNAL_LINKS_MAPPING_DIR + "/" + MATERIAL_NUMBER + "/" + FILE_NAME_HTML));
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
    public void shouldReturnIndexBreaksFile()
    {
        //given
        //when
        final File file = fileSystem.getIndexBreaksFile(step, MATERIAL_NUMBER, FILE_NAME_MAIN);
        //then
        assertThat(
            file,
            hasPath("workDirectory/Format/indexBreaks/" + MATERIAL_NUMBER + "/indexbreak-" + FILE_NAME_MAIN));
    }

    @Test
    public void shouldReturnTocDirectory()
    {
        //given
        //when
        final File directory = fileSystem.getTocDirectory(step);
        //then
        assertThat(directory, hasPath(TOC_DIR));
    }

    @Test
    public void shouldReturnBundlePartTocFile()
    {
        //given
        //when
        final File file = fileSystem.getBundlePartTocFile(FILE_NAME_XML, MATERIAL_NUMBER, step);
        //then
        assertThat(file, hasPath("workDirectory/Format/10_Toc/11111111/toc_fileName.xml"));
    }

    @Test
    public void shouldReturnMergedTocFile()
    {
        //given
        //when
        final File file = fileSystem.getMergedBundleTocFile(MATERIAL_NUMBER, step);
        //then
        assertThat(file, hasPath("workDirectory/Format/10_Toc/11111111/toc_merged.xml"));
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
