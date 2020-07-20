package com.thomsonreuters.uscl.ereader.assemble.service;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.format.step.StepIntegrationTestRunner;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Tests for the PlaceholderDocumentService.
 *
 * @author <a href="mailto:christopher.schwartz@thomsonreuters.com">Chris Schwartz</a> u0081674
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PlaceholderDocumentServiceImplTest.Config.class, StepIntegrationTestRunner.Config.class})
public final class PlaceholderDocumentServiceImplTest {
    @Autowired
    private PlaceholderDocumentService placeholderDocumentService;
    private Resource mockPlaceholderDocumentResource;

    @Before
    public void setUp() throws Exception {
        mockPlaceholderDocumentResource = EasyMock.createMock(Resource.class);
        final ByteArrayInputStream testDocumentTemplateInputStream =
            new ByteArrayInputStream("<html><head/><body><displaytext/></body></html>".getBytes());
        EasyMock.expect(mockPlaceholderDocumentResource.getInputStream()).andReturn(testDocumentTemplateInputStream);
    }

    @After
    public void tearDown() {
        //Intentionally left blank
    }

    private void replayAll() {
        EasyMock.replay(mockPlaceholderDocumentResource);
    }

    private void verifyAll() {
        EasyMock.verify(mockPlaceholderDocumentResource);
    }

    @Test
    public void placeholderDocumentServiceImplHappyPath() throws Exception {
        final ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        placeholderDocumentService
            .generatePlaceholderDocument(resultStream, "documentTocGuid", "tocGuid", new ArrayList<String>());
        final String expected = "<a name=\"tocGuid\">documentTocGuid</a>";
        Assert.assertTrue(new String(resultStream.toByteArray()).contains(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void placeholderDocumentServiceThrowsIllegalArgumentExceptionNullFirstArgument() throws Exception {
        replayAll();
        placeholderDocumentService.generatePlaceholderDocument(null, "YARR!", "tocGuid", new ArrayList<String>());
        verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void placeholderDocumentServiceThrowsIllegalArgumentExceptionNullSecondArgument() throws Exception {
        replayAll();
        placeholderDocumentService
            .generatePlaceholderDocument(new ByteArrayOutputStream(), null, "tocGuid", new ArrayList<String>());
        verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void placeholderDocumentServiceThrowsIllegalArgumentExceptionNullThirdArgument() throws Exception {
        replayAll();
        placeholderDocumentService
            .generatePlaceholderDocument(new ByteArrayOutputStream(), "YARR!", null, new ArrayList<String>());
        verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadlyConfiguredTemplateLocation() throws Exception {
        final ResourceLoader resourceLoader = EasyMock.createMock(ResourceLoader.class);
        EasyMock.expect(resourceLoader.getResource(null))
            .andThrow(new NullPointerException("ARR! YE NULL BE POINTY, LANDLUBBER!"));
        placeholderDocumentService = new PlaceholderDocumentServiceImpl();

        EasyMock.replay(resourceLoader);
        placeholderDocumentService
            .generatePlaceholderDocument(new ByteArrayOutputStream(), "YARR!", "tocGuid", new ArrayList<String>());
        EasyMock.verify(resourceLoader);
    }

    @Configuration
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public PlaceholderDocumentService placeholderDocumentService() {
            PlaceholderDocumentServiceImpl placeholderDocumentService = new PlaceholderDocumentServiceImpl();
            placeholderDocumentService.setPlaceholderDocumentTemplate(new DefaultResourceLoader()
                    .getResource("classpath:templates/placeholderDocumentTemplate.xml"));;
            return placeholderDocumentService;
        }
    }
}
