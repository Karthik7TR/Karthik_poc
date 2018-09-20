package com.thomsonreuters.uscl.ereader.xpp.transformation.anchor;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.EXTERNAL_LINKS_DIR;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir.SPLIT_ANCHORS_DIR;
import static org.junit.Assert.assertThat;

import java.io.File;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import lombok.SneakyThrows;
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
@ContextConfiguration(classes = SplitAnchorsStepIntegrationTest.Conf.class)
@ActiveProfiles("IntegrationTests")
public final class SplitAnchorsStepIntegrationTest {
    private static final String MATERIAL_NUMBER = "88005553535";
    @Autowired
    private SplitAnchorsStep sut;

    @Autowired
    private XppFormatFileSystem formatFileSystem;

    private File extLinksDir;
    private File input;
    private File expectedOutput;

    @Before
    @SneakyThrows
    public void setUp() {
        extLinksDir = formatFileSystem.getDirectory(sut, EXTERNAL_LINKS_DIR, MATERIAL_NUMBER);
        FileUtils.forceMkdir(extLinksDir);
        input = new File(SplitAnchorsStepIntegrationTest.class.getResource("input.html")
                .toURI());
        FileUtils.copyFileToDirectory(input, extLinksDir);
        expectedOutput = new File(SplitAnchorsStepIntegrationTest.class.getResource("expected_output.html")
                .toURI());
    }

    @After
    @SneakyThrows
    public void tearDown() {
        FileUtils.forceDelete(extLinksDir.getParentFile().getParentFile().getParentFile());
    }

    @Test
    public void executeTransformation() {
        //when
        sut.executeTransformation();
        final File result = formatFileSystem.getFile(sut, SPLIT_ANCHORS_DIR, MATERIAL_NUMBER, input.getName());
        //then
        assertThat(expectedOutput, hasSameContentAs(result));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Conf {
        @Bean
        public SplitAnchorsStep splitAnchorsStep() {
            return new SplitAnchorsStep();
        }
    }
}
