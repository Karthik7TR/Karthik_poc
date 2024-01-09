package com.thomsonreuters.uscl.ereader.proview;

import static com.thomsonreuters.uscl.ereader.core.CoreConstants.RELEASE_NOTES_HEADER;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.Author;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.FrontMatterPage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class TitleMetadataTest {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private static final String BLACK_PEARLS_ASSET_NAME = "BlackPearl.png";
    private static final String PIRATES_COVE_ASSET_NAME = "PiratesCove.png";
    private static final String TORTUGA_ASSET_NAME = "Tortuga.png";
    private static final String RELEASE_NOTES = "Test release notes";

    private String titleId = "yarr/pirates";
    private String displayName = "YARR! The Comprehensive Guide to Plundering the Seven Seas.";
    private String materialId = "Plunder2";
    private String copyright = "The High Seas Trading Company.";
    private String isbnNormalized = "9783161484100";
    private String frontMatterTocLabel = "tocLabel";
    private Artwork artwork = new Artwork("swashbuckling.gif");
    private Date publishedDate;

    private final List<Asset> assets = Arrays.asList(
        new Asset("BlackPearl", BLACK_PEARLS_ASSET_NAME),
        new Asset("PiratesCove", PIRATES_COVE_ASSET_NAME),
        new Asset("Tortuga", TORTUGA_ASSET_NAME));
    private final List<Keyword> keywords = Arrays.asList(
        new Keyword("publisher", "High Seas Trading Company"),
        new Keyword("jurisdiction", "International Waters"));

    @Before
    public void setUp() {
        publishedDate = new Date();
    }

    @Test
    public void testBuilderWithBookDefinition() {
        final BookDefinition bookDefinitionMock = mock(BookDefinition.class);
        when(bookDefinitionMock.getFullyQualifiedTitleId()).thenReturn(titleId);
        when(bookDefinitionMock.getPublishedDate()).thenReturn(publishedDate);
        when(bookDefinitionMock.getReleaseNotes()).thenReturn(RELEASE_NOTES);
        when(bookDefinitionMock.getKeyWords()).thenReturn(keywords);
        when(bookDefinitionMock.getAuthors()).thenReturn(createAuthors());
        when(bookDefinitionMock.getIsPilotBook()).thenReturn(true);
        when(bookDefinitionMock.getIsbnNormalized()).thenReturn(isbnNormalized);
        when(bookDefinitionMock.getMaterialId()).thenReturn(materialId);
        when(bookDefinitionMock.getCopyright()).thenReturn(copyright);
        when(bookDefinitionMock.getProviewDisplayName()).thenReturn(displayName);
        when(bookDefinitionMock.getFrontMatterTocLabel()).thenReturn(frontMatterTocLabel);
        when(bookDefinitionMock.getFrontMatterPages()).thenReturn(createFrontMatterPages());

        checkFieldsValues(TitleMetadata.builder(bookDefinitionMock).build());
    }

    private void checkFieldsValues(final TitleMetadata actualMetadata) {
        assertEquals(titleId, actualMetadata.getTitleId());
        checkPublishedDate(actualMetadata.getPublishedDate());
        checkInfoFields(actualMetadata.getInfoFields());
        checkCollectionValues(actualMetadata.getKeywords(), keywords.toArray(new Keyword[0]));
        checkCollectionValues(actualMetadata.getAuthorNames(), "Jack Sparrow", "Davey Jones");
        assertTrue(actualMetadata.getIsPilotBook());
        assertEquals(isbnNormalized, actualMetadata.getIsbn());
        assertEquals(materialId, actualMetadata.getMaterialId());
        assertEquals(copyright, actualMetadata.getCopyright());
        assertEquals(displayName, actualMetadata.getDisplayName());
        assertEquals(frontMatterTocLabel, actualMetadata.getFrontMatterTocLabel());
        checkCollectionValues(
            actualMetadata.getFrontMatterPages(),
            createFrontMatterPages().toArray(new FrontMatterPage[0]));
    }

    private List<Author> createAuthors() {
        final Author jackSparrow = new Author();
        jackSparrow.setAuthorFirstName("Jack");
        jackSparrow.setAuthorLastName("Sparrow");

        final Author daveyJones = new Author();
        daveyJones.setAuthorFirstName("Davey");
        daveyJones.setAuthorLastName("Jones");

        return Arrays.asList(jackSparrow, daveyJones);
    }

    private List<FrontMatterPage> createFrontMatterPages() {
        final FrontMatterPage pageOne = new FrontMatterPage();
        pageOne.setId(1L);

        final FrontMatterPage pageTwo = new FrontMatterPage();
        pageTwo.setId(2L);

        return Arrays.asList(pageOne, pageTwo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderArtworkFileNullSetup() {
        TitleMetadata.builder().artworkFile(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderArtworkNonExistentFileSetup() {
        final File artworkFile = mock(File.class);
        when(artworkFile.exists()).thenReturn(false);
        TitleMetadata.builder().artworkFile(artworkFile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderArtworkFileNameNullSetup() {
        TitleMetadata.builder().artworkFile(null);
    }

    @Test
    public void testBuilderArtworkFileSetup() {
        final File artworkFile = getResourceFileMock("swashbuckling.gif");
        when(artworkFile.exists()).thenReturn(true);

        final Artwork actualArtwork = TitleMetadata.builder().artworkFile(artworkFile).build().getArtwork();
        assertEquals(artwork, actualArtwork);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderAssetsDirectoryNullSetup() {
        TitleMetadata.builder().assetFilesFromDirectory(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderAssetsNotDirectorySetup() {
        final File directory = mock(File.class);
        when(directory.isDirectory()).thenReturn(false);
        TitleMetadata.builder().assetFilesFromDirectory(directory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderAssetsFileNullSetup() {
        TitleMetadata.builder().assetFile(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilderAssetsNonExistentFileSetup() {
        final File assetFile = mock(File.class);
        when(assetFile.exists()).thenReturn(false);
        TitleMetadata.builder().assetFile(assetFile);
    }

    @Test
    public void testBuilderAssetsFromDirectorySetup() {
        final File directory = mock(File.class);
        final File blackPearlFile = getResourceFileMock(BLACK_PEARLS_ASSET_NAME);
        final File piratesCoveFile = getResourceFileMock(PIRATES_COVE_ASSET_NAME);
        final File tortugaFile = getResourceFileMock(TORTUGA_ASSET_NAME);

        when(directory.isDirectory()).thenReturn(true);
        when(directory.listFiles()).thenReturn(new File[] {blackPearlFile, piratesCoveFile, tortugaFile});

        final List<Asset> actualAssets = TitleMetadata.builder().assetFilesFromDirectory(directory).build().getAssets();
        checkCollectionValues(actualAssets, assets.toArray(new Asset[0]));
    }

    @Test
    public void testBuilderAssetsFromFilesSetup() {
        final File blackPearlFile = getResourceFileMock(BLACK_PEARLS_ASSET_NAME);
        final File piratesCoveFile = getResourceFileMock(PIRATES_COVE_ASSET_NAME);
        final File tortugaFile = getResourceFileMock(TORTUGA_ASSET_NAME);

        when(blackPearlFile.exists()).thenReturn(true);
        when(piratesCoveFile.exists()).thenReturn(true);
        when(tortugaFile.exists()).thenReturn(true);

        final List<Asset> actualAssets = TitleMetadata.builder()
            .assetFile(blackPearlFile)
            .assetFile(piratesCoveFile)
            .assetFile(tortugaFile)
            .build()
            .getAssets();
        checkCollectionValues(actualAssets, assets.toArray(new Asset[0]));
    }

    @Test
    public void testBuilderAssetsFromFilesNamesSetup() {
        final List<Asset> actualAssets = TitleMetadata.builder()
            .assetFileName(BLACK_PEARLS_ASSET_NAME)
            .assetFileName(PIRATES_COVE_ASSET_NAME)
            .assetFileName(TORTUGA_ASSET_NAME)
            .build()
            .getAssets();
        checkCollectionValues(actualAssets, assets.toArray(new Asset[0]));
    }

    @Test
    public void testBuilderAssetsFromFilesNamesListSetup() {
        final Set<String> names =
            new HashSet<>(Arrays.asList(BLACK_PEARLS_ASSET_NAME, PIRATES_COVE_ASSET_NAME, TORTUGA_ASSET_NAME));

        final List<Asset> actualAssets = TitleMetadata.builder().assetFileNames(names).build().getAssets();
        checkCollectionValues(actualAssets, assets.toArray(new Asset[0]));
    }

    private File getResourceFileMock(final String fileName) {
        final File file = mock(File.class);
        when(file.getName()).thenReturn(fileName);
        return file;
    }

    private void checkPublishedDate(final String actualPublishedDate) {
        assertEquals(DATE_FORMAT.format(publishedDate), actualPublishedDate);
    }

    private void checkInfoFields(final List<InfoField> infoFields) {
        assertNotNull(infoFields);
        assertEquals(1, infoFields.size());
        InfoField releaseNotes = infoFields.get(0);
        assertEquals(RELEASE_NOTES_HEADER, releaseNotes.getHeader());
        assertEquals(RELEASE_NOTES, releaseNotes.getNote());
    }

    private <I> void checkCollectionValues(final Collection<I> actualCollection, final I... expectedValues) {
        final int expectedSize = expectedValues.length;

        if (expectedSize == 0) {
            throw new UnsupportedOperationException("expectedValues could not be null or empty");
        }

        assertThat(actualCollection, hasSize(expectedSize));
        for (final I expectedValue : expectedValues) {
            assertThat("item " + expectedValue + " not found", actualCollection, hasItem(expectedValue));
        }
    }

    @Test
    public void testBuilderAssetsAndArtworkWasNotSetup() {
        final TitleMetadata titleMetadata = TitleMetadata.builder().build();
        assertThat(titleMetadata.getArtwork(), nullValue());
        assertThat(titleMetadata.getAssets(), nullValue());
    }

    @Test
    public void testBuilderInlineToc() {
        final TitleMetadata titleMetadata = TitleMetadata.builder().inlineToc(true).build();
        assertTrue(titleMetadata.isInlineToc());
    }
}
