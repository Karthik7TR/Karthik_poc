package com.thomsonreuters.uscl.ereader.xpp.transformation.fonts;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppGatherFileSystem;
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
@ContextConfiguration
@ActiveProfiles("IntegrationTests")
public final class GenerateFontsCssStepIntegrationTest {
    private static final String MATERIAL_NUMBER = "11111111";

    @Autowired
    private XppGatherFileSystem xppGatherFileSystem;
    @Autowired
    private XppFormatFileSystem fileSystem;
    @Resource(name = "generateFontsStepBean")
    private GenerateFontsCssStep sut;

    private File xpp;
    private File expectedCss;
    private File workingDir;

    @Before
    public void setUp() throws URISyntaxException, Exception {
        xpp = new File(GenerateFontsCssStepIntegrationTest.class.getResource("sampleXpp.xml").toURI());
        expectedCss = new File(GenerateFontsCssStepIntegrationTest.class.getResource("fonts.css").toURI());
        workingDir = xppGatherFileSystem.getXppBundleMaterialNumberDirectory(sut, MATERIAL_NUMBER);
    }

    @After
    public void cleanUp() throws IOException {
        FileUtils.deleteDirectory(workingDir);
    }

    @Test
    public void shouldReturnFontsCssFile() throws IOException, Exception {
        //given
        final File xppDir = workingDir.toPath().resolve("bundleName").resolve("XPP").toFile();
        FileUtils.forceMkdir(xppDir);
        FileUtils.copyFileToDirectory(xpp, xppDir);
        //when
        sut.executeStep();
        //then
        final File fontsCss = fileSystem.getFontsCssFile(sut, MATERIAL_NUMBER, "sampleXpp");
        assertThat(expectedCss, hasSameContentAs(fontsCss));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class GenerateFontsCssStepIntegrationTestConfiguration {
        @Bean(name = "generateFontsStepBean")
        public GenerateFontsCssStep generateFontsStepBean() {
            return new GenerateFontsCssStep();
        }
    }
}
