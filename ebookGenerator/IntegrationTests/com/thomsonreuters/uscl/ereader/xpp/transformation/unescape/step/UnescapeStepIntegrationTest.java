package com.thomsonreuters.uscl.ereader.xpp.transformation.unescape.step;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import org.apache.commons.io.FileUtils;
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
@ContextConfiguration(classes = UnescapeStepIntegrationTest.Conf.class)
@ActiveProfiles("IntegrationTests")
public final class UnescapeStepIntegrationTest {
    private static final String MATERIAL_NUMBER = "88005553535";
    @Autowired
    private UnescapeStep sut;

    @Autowired
    private XppFormatFileSystem formatFileSystem;

    private File extLinksDir;

    private File input;
    private File expectedOutput;

    @Before
    public void setUp() throws URISyntaxException, IOException {
        extLinksDir = formatFileSystem.getExternalLinksDirectory(sut, MATERIAL_NUMBER);
        FileUtils.forceMkdir(extLinksDir);
        input = new File(UnescapeStepIntegrationTest.class.getResource("input.html")
            .toURI());
        FileUtils.copyFileToDirectory(input, extLinksDir);
        expectedOutput = new File(UnescapeStepIntegrationTest.class.getResource("expected_output.html")
            .toURI());
    }

    @Test
    public void test() throws Exception {
        //given
        //when
        sut.executeStep();
        final File result = formatFileSystem.getFile(sut, XppFormatFileSystemDir.UNESCAPE_DIR, MATERIAL_NUMBER, input.getName());
        //then
        assertThat(expectedOutput, hasSameContentAs(result));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Conf {
        @Bean
        public UnescapeStep unescapeBean() {
            return new UnescapeStep();
        }
    }
}
