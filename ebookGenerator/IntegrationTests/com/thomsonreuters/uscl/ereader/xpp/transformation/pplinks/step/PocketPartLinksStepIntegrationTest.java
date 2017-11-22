package com.thomsonreuters.uscl.ereader.xpp.transformation.pplinks.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystem;
import com.thomsonreuters.uscl.ereader.xpp.transformation.service.XppFormatFileSystemDir;
import com.thomsonreuters.uscl.ereader.xpp.transformation.tohtml.step.TransformationToHtmlStepIntegrationTestConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TransformationToHtmlStepIntegrationTestConfiguration.class)
@ActiveProfiles("IntegrationTests")
public class PocketPartLinksStepIntegrationTest {
    private static final String MATERIAL_NUMBER_MAIN_CONTENT = "11111111";
    private static final String MATERIAL_NUMBER_POCKET_PART = "11111112";
    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.ORIGINAL_PAGES_DIR;
    private static final XppFormatFileSystemDir DESTINATION_DIR = XppFormatFileSystemDir.POCKET_PART_LINKS_DIR;

    @Resource(name = "pocketPartLinksTask")
    @InjectMocks
    private PocketPartLinksStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    @Mock
    private XppBundle xppBundle;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);
        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).thenReturn(Collections.singletonList(xppBundle));
        when(xppBundle.getProductType()).thenReturn("supp");
        when(xppBundle.getMaterialNumber()).thenReturn(MATERIAL_NUMBER_POCKET_PART);

        final File mainContentFolder = new File(PocketPartLinksStepIntegrationTest.class.getResource(MATERIAL_NUMBER_MAIN_CONTENT).toURI());
        final File pocketPartFolder = new File(PocketPartLinksStepIntegrationTest.class.getResource(MATERIAL_NUMBER_POCKET_PART).toURI());

        FileUtils.copyDirectory(mainContentFolder, mkdir(fileSystem.getDirectory(step, SOURCE_DIR, MATERIAL_NUMBER_MAIN_CONTENT)));
        FileUtils.copyDirectory(pocketPartFolder, mkdir(fileSystem.getDirectory(step, SOURCE_DIR, MATERIAL_NUMBER_POCKET_PART)));
    }

    @After
    public void clean() throws IOException {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldAddPocketPartsLinksPlaceholders() throws Exception {
        //when
        step.executeStep();
        //then
        final File transformedMain = fileSystem.getFile(step, DESTINATION_DIR, MATERIAL_NUMBER_MAIN_CONTENT, "1-LUPDRL.DIVXML_0002_I91dd17d0572311dca3950000837bc6dd.page");
        final File expectedMain = new File(PocketPartLinksStepIntegrationTest.class.getResource("expectedMainContent.page").toURI());
        assertThat(FileUtils.readFileToString(transformedMain), equalTo(FileUtils.readFileToString(expectedMain)));

        final File transformedPocketPart = fileSystem.getFile(step, DESTINATION_DIR, MATERIAL_NUMBER_POCKET_PART, "1-LUPDRL.DIVXML_0001_I91dd17d0572311dca3950000837bc6dd.page");
        final File expectedPocketPart = new File(PocketPartLinksStepIntegrationTest.class.getResource("expectedPocketPart.page").toURI());
        assertThat(FileUtils.readFileToString(transformedPocketPart), equalTo(FileUtils.readFileToString(expectedPocketPart)));
    }

}
