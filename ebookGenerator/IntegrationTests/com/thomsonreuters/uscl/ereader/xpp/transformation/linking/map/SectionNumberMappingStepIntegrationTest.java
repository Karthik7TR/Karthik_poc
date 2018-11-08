package com.thomsonreuters.uscl.ereader.xpp.transformation.linking.map;

import static com.thomsonreuters.uscl.ereader.common.filesystem.FileContentMatcher.hasSameContentAs;
import static com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemImpl.SECTION_NUMBER_MAP_FILE;
import static org.junit.Assert.assertThat;

import java.io.File;

import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
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
@ContextConfiguration(classes = SectionNumberMappingStepIntegrationTest.Config.class)
@ActiveProfiles("IntegrationTests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public final class SectionNumberMappingStepIntegrationTest {
    private static final String MATERIAL_NUMBER = "88005553535";
    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.ORIGINAL_DIR;
    private static final XppFormatFileSystemDir DEST_DIR = XppFormatFileSystemDir.SECTION_NUMBERS_MAP_DIR;

    @Autowired
    private SectionNumberMappingStep sut;

    @Autowired
    private XppFormatFileSystem formatFileSystem;

    @Test
    public void shouldCreateSectionMap() throws Exception {
        test("1-Input_1.DIVXML.main", "expected_map.xml");
    }

    @Test
    public void shouldCreateSectionMapForPrimarySource() throws Exception {
        test("1-PrimaryInput_1.DIVXML.main", "expected_primary_map.xml");
    }

    @Test
    public void shouldCreateSectionMapForRutter() throws Exception {
        test("1-RutterInput_1.DIVXML.main", "expected_rutter_map.xml");
    }

    private void test(final String inputFileName, final String outputFileName) throws Exception {
        //given
        final File sourceDir = formatFileSystem.getDirectory(sut, SOURCE_DIR, MATERIAL_NUMBER);
        FileUtils.forceMkdir(sourceDir);
        final File input = new File(SectionNumberMappingStepIntegrationTest.class.getResource(inputFileName)
            .toURI());
        FileUtils.copyFileToDirectory(input, sourceDir);
        final File expectedOutput = new File(SectionNumberMappingStepIntegrationTest.class.getResource(outputFileName)
            .toURI());
        //when
        sut.executeTransformation();
        //then
        final File actual =
            new File(formatFileSystem.getDirectory(sut, DEST_DIR), SECTION_NUMBER_MAP_FILE);
        assertThat(actual, hasSameContentAs(expectedOutput));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public SectionNumberMappingStep sectionNumberMappingStep() {
            return new SectionNumberMappingStep();
        }
    }
}
