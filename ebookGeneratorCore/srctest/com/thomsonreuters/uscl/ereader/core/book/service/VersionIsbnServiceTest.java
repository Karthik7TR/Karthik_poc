package com.thomsonreuters.uscl.ereader.core.book.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Arrays;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.EbookAuditDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.VersionIsbnDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.VersionIsbn;
import com.thomsonreuters.uscl.ereader.core.book.model.TitleId;
import com.thomsonreuters.uscl.ereader.core.book.model.Version;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PrepareForTest({VersionIsbnServiceImpl.class, VersionIsbn.class, TitleId.class})
@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
public final class VersionIsbnServiceTest {
    private static final String TITLE_ID = "uscl/an/book";
    private static final String PT_2 = "_pt2";
    private static final String ISBN_1 = "978-5-3322-6109-1";
    private static final String ISBN_2 = "978-3-7303-6936-4";
    private static final String VERSION = "4.7";

    private VersionIsbnService versionIsbnService;

    @Mock
    private VersionIsbnDao mockVersionIsbnDao;
    @Mock
    private BookDefinitionService mockBookDefinitionService;
    @Mock
    private BookDefinition mockBookDefinition;
    @Mock
    private VersionIsbn mockVersionIsbn1;
    @Mock
    private VersionIsbn mockVersionIsbn2;
    private List<VersionIsbn> versionIsbns;

    @Before
    public void setUp() {
        versionIsbnService = new VersionIsbnServiceImpl(mockVersionIsbnDao, mockBookDefinitionService);
        versionIsbns = Arrays.asList(mockVersionIsbn1, mockVersionIsbn2);
    }

    @Test
    public void testGetIsbnOfTitleVersion() {
        when(mockBookDefinitionService.findBookDefinitionByTitle(TITLE_ID)).thenReturn(mockBookDefinition);
        when(mockVersionIsbnDao.findDistinctByEbookDefinitionAndVersion(mockBookDefinition, VERSION)).thenReturn(mockVersionIsbn1);
        when(mockVersionIsbn1.getIsbn()).thenReturn(ISBN_1);

        String isbn = versionIsbnService.getIsbnOfTitleVersion(TITLE_ID, VERSION);

        assertEquals(ISBN_1, isbn);
    }

    @Test
    public void testGetIsbnOfTitleVersion_noBookDefinition() {
        String isbn = versionIsbnService.getIsbnOfTitleVersion(TITLE_ID, VERSION);

        assertNull(isbn);
    }

    @Test
    public void testGetIsbnOfTitleVersion_noVersionIsbn() {
        when(mockBookDefinitionService.findBookDefinitionByTitle(TITLE_ID)).thenReturn(mockBookDefinition);

        String isbn = versionIsbnService.getIsbnOfTitleVersion(TITLE_ID, VERSION);

        assertNull(isbn);
    }

    @SneakyThrows
    @Test
    public void testSaveIsbn_newVersionIsbn() {
        when(mockBookDefinition.getTitleId()).thenReturn(TITLE_ID);
        whenNew(VersionIsbn.class).withArguments(mockBookDefinition, VERSION, ISBN_1).thenReturn(mockVersionIsbn1);

        versionIsbnService.saveIsbn(mockBookDefinition, VERSION, ISBN_1);

        verify(mockBookDefinition).getTitleId();
        verify(mockVersionIsbnDao).findDistinctByEbookDefinitionAndVersion(mockBookDefinition, VERSION);
        verify(mockVersionIsbnDao).save(mockVersionIsbn1);
    }

    @Test
    public void testSaveIsbn_versionIsbnExists() {
        when(mockBookDefinition.getTitleId()).thenReturn(TITLE_ID);
        when(mockVersionIsbnDao.findDistinctByEbookDefinitionAndVersion(mockBookDefinition, VERSION))
            .thenReturn(mockVersionIsbn1);

        versionIsbnService.saveIsbn(mockBookDefinition, VERSION, ISBN_1);

        verify(mockBookDefinition).getTitleId();
        verify(mockVersionIsbnDao).findDistinctByEbookDefinitionAndVersion(mockBookDefinition, VERSION);
        verify(mockVersionIsbn1).setIsbn(ISBN_1);
        verify(mockVersionIsbnDao).save(mockVersionIsbn1);
    }

    @Test
    public void deleteIsbn() {
        setUpDeleteIsbnMocks();

        versionIsbnService.deleteIsbn(TITLE_ID, Version.VERSION_PREFIX + VERSION);

        verifyDeleteIsbnMocks();
    }

    @Test
    public void deleteIsbn_splitBook() {
        setUpDeleteIsbnMocks();

        versionIsbnService.deleteIsbn(TITLE_ID + PT_2, Version.VERSION_PREFIX + VERSION);

        verifyDeleteIsbnMocks();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void isIsbnExists_exists() {
        setUpVersionIsbnListMocks();

        final boolean actual = versionIsbnService.isIsbnExists(ISBN_1);

        verify(mockVersionIsbnDao).findAll();
        verify(mockVersionIsbn1).getIsbn();
        final boolean expected = true;
        assertEquals(expected, actual);
    }

    @Test
    public void isIsbnExists_doesNotExist() {
        setUpVersionIsbnListMocks();

        final boolean actual = versionIsbnService.isIsbnExists(ISBN_2);

        verifyVersionIsbnListMocks();
        final boolean expected = false;
        assertEquals(expected, actual);
    }

    @Test
    public void isIsbnChangedFromPreviousGeneration_changed() {
        setUpChangedIsbnMocks(ISBN_2);

        final boolean actual = versionIsbnService.isIsbnChangedFromPreviousGeneration(mockBookDefinition, VERSION);

        assertTrue(actual);
    }

    @Test
    public void isIsbnChangedFromPreviousGeneration_notChanged() {
        setUpChangedIsbnMocks(ISBN_1);

        final boolean actual = versionIsbnService.isIsbnChangedFromPreviousGeneration(mockBookDefinition, VERSION);

        assertFalse(actual);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void modifyIsbn() {
        when(mockBookDefinitionService.findBookDefinitionByTitle(TITLE_ID)).thenReturn(mockBookDefinition);
        when(mockVersionIsbnDao.getAllByEbookDefinition(mockBookDefinition)).thenReturn(versionIsbns);
        when(mockVersionIsbn1.getIsbn()).thenReturn(ISBN_2);
        when(mockVersionIsbn2.getIsbn()).thenReturn(ISBN_1);

        versionIsbnService.modifyIsbn(TITLE_ID, ISBN_1);

        mockVersionIsbn2.getIsbn().equals(EbookAuditDao.MODIFY_ISBN_TEXT + ISBN_1);
        verify(mockVersionIsbnDao).save(mockVersionIsbn2);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void resetIsbn() {
        when(mockBookDefinitionService.findBookDefinitionByTitle(TITLE_ID)).thenReturn(mockBookDefinition);
        when(mockVersionIsbnDao.getAllByEbookDefinition(mockBookDefinition)).thenReturn(versionIsbns);
        when(mockVersionIsbn1.getIsbn()).thenReturn(ISBN_2);
        when(mockVersionIsbn2.getIsbn()).thenReturn(EbookAuditDao.MODIFY_ISBN_TEXT + ISBN_1);

        versionIsbnService.resetIsbn(TITLE_ID, ISBN_1);

        mockVersionIsbn2.getIsbn().equals(ISBN_1);
        verify(mockVersionIsbnDao).save(mockVersionIsbn2);
    }

    private void setUpDeleteIsbnMocks() {
        when(mockBookDefinitionService.findBookDefinitionByTitle(TITLE_ID)).thenReturn(mockBookDefinition);
        when(mockVersionIsbnDao.findDistinctByEbookDefinitionAndVersion(mockBookDefinition, VERSION))
            .thenReturn(mockVersionIsbn1);
    }

    private void verifyDeleteIsbnMocks() {
        verify(mockBookDefinitionService).findBookDefinitionByTitle(TITLE_ID);
        verify(mockVersionIsbnDao).findDistinctByEbookDefinitionAndVersion(mockBookDefinition, VERSION);
        verify(mockVersionIsbnDao).delete(mockVersionIsbn1);
    }

    private void setUpVersionIsbnListMocks() {
        when(mockVersionIsbnDao.findAll()).thenReturn(versionIsbns);
        when(mockVersionIsbn1.getIsbn()).thenReturn(ISBN_1);
        when(mockVersionIsbn2.getIsbn()).thenReturn(ISBN_1);
    }

    private void setUpChangedIsbnMocks(final String isbn) {
        when(mockBookDefinition.getIsbn()).thenReturn(isbn);
        final VersionIsbn versionIsbn = new VersionIsbn(mockBookDefinition, VERSION, ISBN_1);
        when(mockVersionIsbnDao.findDistinctByEbookDefinitionAndVersion(mockBookDefinition, VERSION))
            .thenReturn(versionIsbn);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void verifyVersionIsbnListMocks() {
        verify(mockVersionIsbnDao).findAll();
        verify(mockVersionIsbn1).getIsbn();
        verify(mockVersionIsbn2).getIsbn();
    }
}
