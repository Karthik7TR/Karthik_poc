package com.thomsonreuters.uscl.ereader.gather.services;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;

import com.thomsonreuters.uscl.ereader.core.book.domain.ExcludeDocument;
import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.westgroup.novus.productapi.NortManager;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

@RunWith(MockitoJUnitRunner.class)
public final class NortServiceMockitoTest {
    @InjectMocks
    private NortServiceImpl nortService;
    @Mock
    private NovusFactory novusFactory;
    @Mock
    private NovusUtility novusUtility;
    @Mock
    private Novus novus;
    @Mock
    private NortManager nortManager;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFindTableOfContentsWithNovusException() throws NovusException, GatherException {
        thrown.expect(GatherException.class);
        thrown.expectMessage(Matchers.containsString("Novus error occurred while creating Novus object"));

        when(novusFactory.createNovus(anyBoolean())).thenThrow(new MockNovusException());

        nortService.findTableOfContents(null, null, null, null, null, null, false, false, null, 0);
    }

    @Test
    public void testFindTableOfContentsWithGatherException() throws GatherException, NovusException {
        thrown.expect(GatherException.class);
        thrown.expectMessage(Matchers.containsString("NORT Exception"));

        when(novusFactory.createNovus(anyBoolean())).thenReturn(novus);
        when(novus.getNortManager()).thenReturn(nortManager);

        nortService.findTableOfContents(null, null, new File(temporaryFolder.getRoot(), "nonexisting"), null, null, null, false, false, null, 0);
    }

    @Test
    public void testFindTableOfContentsWithExceptionAndExcludeDocuments() throws GatherException, NovusException {
        thrown.expect(GatherException.class);
        thrown.expectMessage(Matchers.containsString("Failed with EMPTY toc.xml for domain"));

        when(novusFactory.createNovus(anyBoolean())).thenReturn(novus);
        when(novus.getNortManager()).thenReturn(nortManager);
        when(novusUtility.getTocRetryCount()).thenReturn("0");

        nortService.findTableOfContents(null, null, new File(temporaryFolder.getRoot(), "nonexisting"), null,
            Collections.singletonList(new ExcludeDocument()),
            null, false, false, null, 0);
    }
}
