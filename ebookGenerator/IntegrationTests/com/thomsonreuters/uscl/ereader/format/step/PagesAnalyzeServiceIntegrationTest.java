package com.thomsonreuters.uscl.ereader.format.step;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.core.service.JsoupService;
import com.thomsonreuters.uscl.ereader.core.service.JsoupServiceTestImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public final class PagesAnalyzeServiceIntegrationTest {
    @InjectMocks
    private PagesAnalyzeService service;
    @Spy
    private JsoupServiceTestImpl jsoupService;

    private File resourceDir;

    @Before
    public void setUp() throws URISyntaxException {
        resourceDir = new File(PagesAnalyzeServiceIntegrationTest.class.getResource("resourcePagesAnalyzeService").toURI());
    }

    @Test
    public void shouldFindPagebreks() {
        assertTrue(service.checkIfDocumentsContainPagebreaks(new File(resourceDir, "withPagebreaks")));
    }

    @Test
    public void shouldNotFindPagebreks() {
        assertFalse(service.checkIfDocumentsContainPagebreaks(new File(resourceDir, "noPagebreaks")));
    }
}
