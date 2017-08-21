package com.thomsonreuters.uscl.ereader.xpp.transformation.externallinks.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.DirectoryContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.xpp.transformation.originalstructure.step.OriginalStructureTransformationStepIntegrationTestConfiguration;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = OriginalStructureTransformationStepIntegrationTestConfiguration.class)
@ActiveProfiles("IntegrationTests")
public final class ExternalLinksXppStepIntegrationTest
{
    private static final String MATERIAL_NUMBER = "88005553535";
    @Resource(name = "externalLinksXppStepBean")
    private ExternalLinksXppStep sut;

    @Autowired
    private XppFormatFileSystem fileSystem;

    private File sourceDir;
    private File expectedExternalLinksDir;
    private File expectedMapping;
    private File actualExternalLinksDir;
    private File actualMapping;
    private File htmlDir;

    @Before
    public void setUp() throws URISyntaxException, IOException
    {
        sourceDir = new File(ExternalLinksXppStepIntegrationTest.class.getResource("source").toURI());
        expectedExternalLinksDir = new File(ExternalLinksXppStepIntegrationTest.class.getResource("expected").toURI());
        expectedMapping = new File(ExternalLinksXppStepIntegrationTest.class.getResource("expectedmapping\\test.html").toURI());
        actualExternalLinksDir = fileSystem.getExternalLinksDirectory(sut, MATERIAL_NUMBER);
        actualMapping = fileSystem.getExternalLinksMappingFile(sut, MATERIAL_NUMBER, "test.html");
        htmlDir = fileSystem.getHtmlPagesDirectory(sut, MATERIAL_NUMBER);
        FileUtils.forceMkdir(htmlDir);
        FileUtils.copyDirectory(sourceDir, htmlDir);
    }

    @Test
    public void shouldTransformExternalLinks() throws Exception
    {
        //given
        //when
        sut.executeTransformation();
        //then
        assertThat(expectedExternalLinksDir, hasSameContentAs(actualExternalLinksDir, false));
        assertThat(expectedMapping, FileContentMatcher.hasSameContentAs(actualMapping));
    }

    @After
    public void cleanUp() throws IOException
    {
        FileUtils.deleteDirectory(actualExternalLinksDir.getParentFile().getParentFile().getParentFile());
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class GenerateFontsCssStepIntegrationTestConfiguration
    {
        @Bean(name = "externalLinksXppStepBean")
        public ExternalLinksXppStep externalLinksXppStepBean()
        {
            return new ExternalLinksXppStep();
        }
    }
}
