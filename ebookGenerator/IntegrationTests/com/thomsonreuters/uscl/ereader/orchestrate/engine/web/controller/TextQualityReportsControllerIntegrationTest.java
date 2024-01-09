package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.io.File;

import com.thomsonreuters.uscl.ereader.common.filesystem.FormatFileSystem;
import com.thomsonreuters.uscl.ereader.context.CommonTestContextConfiguration;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TextQualityReportsControllerIntegrationTest.Config.class)
@ActiveProfiles("IntegrationTests")
@TestPropertySource("/WEB-INF/spring/properties/default-spring.properties")
public final class TextQualityReportsControllerIntegrationTest {
    private static final Long JOB_ID = 1L;
    private static final String MATERIAL = "1111111";

    private MockMvc mockMvc;
    @Autowired
    private TextQualityReportsController sut;
    @Autowired
    @Qualifier("formatFileSystem")
    private FormatFileSystem formatFileSystem;
    private File reportTestFile;

    @Before
    @SneakyThrows
    public void onTestSetUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut).build();
        final File reportsDirectory = formatFileSystem.getFormatDirectory(JOB_ID).toPath()
            .resolve(XppFormatFileSystemDir.QUALITY_DIR.getDirName())
            .resolve(MATERIAL)
            .resolve("reports")
            .toFile();
        reportTestFile = new File(getClass().getResource("qualityReportTestFile.html").toURI());
        FileUtils.copyFileToDirectory(reportTestFile, reportsDirectory);
    }

    @Test
    @SneakyThrows
    public void shouldWriteErrorMessageToOutputAndStatus410() {
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/qualityreport/%s/%s/nonExistentReportFile.html", JOB_ID, MATERIAL)))
            .andExpect(MockMvcResultMatchers.status().isGone())
            .andExpect(MockMvcResultMatchers.content().string("Report file nonExistentReportFile for job 1 doesn't exist"));
    }

    @Test
    @SneakyThrows
    public void shouldWriteReportToOutputAndStatus200() {
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/qualityreport/%s/%s/qualityReportTestFile.html", JOB_ID, MATERIAL)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().bytes(FileUtils.readFileToByteArray(reportTestFile)));
    }

    @Configuration
    @Profile("IntegrationTests")
    @Import(CommonTestContextConfiguration.class)
    public static class Config {
        @Bean
        public TextQualityReportsController textQualityReportsController() {
            return new TextQualityReportsController();
        }
    }
}
