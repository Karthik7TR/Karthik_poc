package com.thomsonreuters.uscl.ereader.xpp.transformation.linking.recovery;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.xpp.common.PubType;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = InternalLinksRecoveryStepIntegrationTest.Config.class)
@ActiveProfiles("IntegrationTests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public final class InternalLinksRecoveryStepIntegrationTest {
    private static final String MATERIAL_NUMBER = "88005553535";
    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.ORIGINAL_PAGES_DIR;

    @Autowired
    @InjectMocks
    private InternalLinksRecoveryStep sut;

    @Autowired
    private XppFormatFileSystem formatFileSystem;

    private File input;
    private File expectedOutput;

    @Test
    public void shouldRecoverInternalLinks() throws Exception {
        test(PubType.COMMON);
    }

    @Test
    public void shouldRecoverInternalLinksForPrimarySource() throws Exception {
        test(PubType.PRIMARY);
    }

    @Test
    public void shouldRecoverInternalLinksForRutters() throws Exception {
        test(PubType.RUTTER);
    }

    private void setUp(final PubType type) throws URISyntaxException, IOException {
        final File pagesDir = formatFileSystem.getDirectory(sut, SOURCE_DIR, MATERIAL_NUMBER);
        FileUtils.forceMkdir(pagesDir);
        input = new File(InternalLinksRecoveryStepIntegrationTest.class.getResource(String.format("data/%s/input_Index.DIVXML.xml", type))
                .toURI());
        FileUtils.copyFileToDirectory(input, pagesDir);
        expectedOutput = new File(InternalLinksRecoveryStepIntegrationTest.class.getResource(String.format("data/%s/expected_output.xml", type))
                .toURI());
        final File sectionNumbersDir = formatFileSystem.getDirectory(sut, XppFormatFileSystemDir.SECTION_NUMBERS_MAP_DIR);
        final File sectionNumbersMap =
                new File(InternalLinksRecoveryStepIntegrationTest.class.getResource(String.format("data/%s/section-number-map.xml", type))
                        .toURI());
        FileUtils.copyFileToDirectory(sectionNumbersMap, sectionNumbersDir);
    }

    private void test(final PubType type) throws Exception {
        //when
        setUp(type);
        sut.executeTransformation();
        final File result = formatFileSystem
                .getFile(sut, XppFormatFileSystemDir.INTERNAL_LINKS_RECOVERY_DIR, MATERIAL_NUMBER, input.getName());
        //then
        assertThat(expectedOutput, hasSameContentAs(result));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public InternalLinksRecoveryStep internalLinksRecoveryStep() {
            return new InternalLinksRecoveryStep();
        }
    }
}
