package com.thomsonreuters.uscl.ereader.gather.services;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;

import com.thomsonreuters.uscl.ereader.gather.exception.GatherException;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.westgroup.novus.productapi.Document;
import com.westgroup.novus.productapi.Find;
import com.westgroup.novus.productapi.Novus;
import com.westgroup.novus.productapi.NovusException;

@RunWith(MockitoJUnitRunner.class)
public final class DocServiceMockitoTest {
    @InjectMocks
    private DocServiceImpl docService;
    @Mock
    private NovusFactory novusFactory;
    @Mock
    private NovusUtility novusUtility;
    @Mock
    private Novus novus;
    @Mock
    private Find finder;
    @Mock
    private Document document;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private File contentDestinationDirectory;
    private File metadataDestinationDirectory;

    @Before
    public void setUp() throws NovusException {
        contentDestinationDirectory = new File(temporaryFolder.getRoot(), "contentDestinationDirectory");
        metadataDestinationDirectory = new File(temporaryFolder.getRoot(), "metadataDestinationDirectory");
        contentDestinationDirectory.mkdirs();
        metadataDestinationDirectory.mkdirs();

        when(novusUtility.getDocRetryCount()).thenReturn("1");
        when(novusFactory.createNovus(anyBoolean())).thenReturn(novus);
        when(novus.getFind()).thenReturn(finder);
        when(finder.getDocument(any(), any())).thenReturn(document);
    }

    @Test
    public void testFetchDocumentsWithGatherException() throws NovusException, GatherException {
        thrown.expect(GatherException.class);
        thrown.expectMessage(Matchers.containsString("Novus error occurred while creating Novus object"));

        when(novusFactory.createNovus(anyBoolean())).thenThrow(new MockNovusException());

        docService.fetchDocuments(null, null, contentDestinationDirectory, metadataDestinationDirectory, false, false);
    }

    @Test
    public void testFetchDocumentsWithHandleException() throws Exception {
        thrown.expect(GatherException.class);
        thrown.expectMessage(Matchers.containsString("Exception happened in handleException"));

        when(finder.getDocument(any(), any())).thenThrow(new RuntimeException());
        when(novusUtility.handleException(any(), any(), any())).thenThrow(new RuntimeException());

        docService.fetchDocuments(Collections.singletonList("guid"), null, contentDestinationDirectory, metadataDestinationDirectory, false, false);
    }

    @Test
    public void testFetchDocumentsWithNovusException() throws Exception {
        thrown.expect(GatherException.class);
        thrown.expectMessage(Matchers.containsString("Novus error occurred fetching document"));

        when(document.getCollection()).thenThrow(new MockNovusException());

        docService.fetchDocuments(Collections.singletonList("guid"), null, contentDestinationDirectory, metadataDestinationDirectory, false, false);
    }

    @Test
    public void testFetchDocumentsWithNullPointerException() throws GatherException {
        thrown.expect(GatherException.class);
        thrown.expectMessage(Matchers.containsString("Null document fetching guid"));

        when(novus.getFind()).thenThrow(new NullPointerException());

        docService.fetchDocuments(Collections.EMPTY_LIST, null, contentDestinationDirectory, metadataDestinationDirectory, false, false);
    }

    @Test
    public void testFetchDocumentsWithGatherException2() throws Exception {
        thrown.expect(GatherException.class);
        thrown.expectMessage(Matchers.containsString("Null documents are found for the current ebook"));

        when(document.getText()).thenReturn("");
        when(document.getGuid()).thenReturn("");
        when(document.getErrorCode()).thenReturn("1");
        when(novusUtility.getDocRetryCount()).thenReturn("4");

        when(novusUtility.getShowMissDocsList()).thenReturn("Y");

        docService.fetchDocuments(Collections.singletonList("guid"), null, contentDestinationDirectory, metadataDestinationDirectory, false, false);
    }
}
