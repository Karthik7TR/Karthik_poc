package com.thomsonreuters.uscl.ereader.xpp.transformation.pplinks.step;

import static com.thomsonreuters.uscl.ereader.core.book.util.BookTestUtil.mkdir;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.thomsonreuters.uscl.ereader.JobParameterKey;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundle;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleWebBuildProductType;
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
    private static final String EXPECTED_MAIN_CONTENT_PAGE_WITH_LINK_TO_POCKET_PART =
        "expectedMainContentWithLinkToPocketPart.page";
    private static final String EXPECTED_MAIN_CONTENT_PAGE_WITH_LINK_TO_SUPPLEMENT =
        "expectedMainContentWithLinkToSupplement.page";
    private static final String EXPECTED_POCKET_PART_PAGE = "expectedPocketPart.page";
    private static final String MATERIAL_NUMBER_MAIN_CONTENT = "11111111";
    private static final String MATERIAL_NUMBER_POCKET_PART = "11111112";
    private static final String DIVXML_XML_MAIN = "1-LUPDRL.DIVXML_0002_I91dd17d0572311dca3950000837bc6dd.page";
    private static final String DIVXML_XML_SUPP = "1-LUPDRL.DIVXML_0001_I91dd17d0572311dca3950000837bc6dd.page";
    private static final XppFormatFileSystemDir SOURCE_DIR = XppFormatFileSystemDir.ORIGINAL_PAGES_DIR;
    private static final XppFormatFileSystemDir DESTINATION_DIR = XppFormatFileSystemDir.POCKET_PART_LINKS_DIR;

    @Resource(name = "pocketPartLinksTask")
    @InjectMocks
    private PocketPartLinksStep step;
    @Autowired
    private XppFormatFileSystem fileSystem;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChunkContext chunkContext;

    @Before
    public void setUp() throws Exception {
        org.mockito.MockitoAnnotations.initMocks(this);

        final File mainContentFolder =
            new File(PocketPartLinksStepIntegrationTest.class.getResource(MATERIAL_NUMBER_MAIN_CONTENT).toURI());
        final File pocketPartFolder =
            new File(PocketPartLinksStepIntegrationTest.class.getResource(MATERIAL_NUMBER_POCKET_PART).toURI());

        FileUtils.copyDirectory(
            mainContentFolder,
            mkdir(fileSystem.getDirectory(step, SOURCE_DIR, MATERIAL_NUMBER_MAIN_CONTENT)));
        FileUtils.copyDirectory(
            pocketPartFolder,
            mkdir(fileSystem.getDirectory(step, SOURCE_DIR, MATERIAL_NUMBER_POCKET_PART)));
    }

    @After
    public void clean() throws IOException {
        FileUtils.cleanDirectory(fileSystem.getFormatDirectory(step));
    }

    @Test
    public void shouldAddPocketPartsLinksPlaceholders() throws Exception {
        shouldAddSupplementLinksPlaceholders(
            XppBundleWebBuildProductType.POCKET_PART,
            EXPECTED_MAIN_CONTENT_PAGE_WITH_LINK_TO_POCKET_PART);
    }

    @Test
    public void shouldAddSupplementaryPamphletLinksPlaceholders() throws Exception {
        shouldAddSupplementLinksPlaceholders(
            XppBundleWebBuildProductType.SUPPLEMENTARY_PAMPHLET,
            EXPECTED_MAIN_CONTENT_PAGE_WITH_LINK_TO_SUPPLEMENT);
    }

    @Test
    public void shouldAddLooseleafSupplementLinksPlaceholders() throws Exception {
        shouldAddSupplementLinksPlaceholders(
            XppBundleWebBuildProductType.LOOSELEAF_SUPPLEMENT,
            EXPECTED_MAIN_CONTENT_PAGE_WITH_LINK_TO_SUPPLEMENT);
    }

    private void shouldAddSupplementLinksPlaceholders(
        final XppBundleWebBuildProductType pocketPartProductType,
        final String expectedMainContentFile) throws Exception, URISyntaxException, IOException {
        //given
        setBundlesToContext(pocketPartProductType);
        //when
        step.executeStep();
        //then
        final File transformedMain =
            fileSystem.getFile(step, DESTINATION_DIR, MATERIAL_NUMBER_MAIN_CONTENT, DIVXML_XML_MAIN);
        final File expectedMain =
            new File(PocketPartLinksStepIntegrationTest.class.getResource(expectedMainContentFile).toURI());
        assertThat(FileUtils.readFileToString(transformedMain), equalTo(FileUtils.readFileToString(expectedMain)));

        final File transformedPocketPart =
            fileSystem.getFile(step, DESTINATION_DIR, MATERIAL_NUMBER_POCKET_PART, DIVXML_XML_SUPP);
        final File expectedPocketPart =
            new File(PocketPartLinksStepIntegrationTest.class.getResource(EXPECTED_POCKET_PART_PAGE).toURI());
        assertThat(
            FileUtils.readFileToString(transformedPocketPart),
            equalTo(FileUtils.readFileToString(expectedPocketPart)));
    }

    private void setBundlesToContext(final XppBundleWebBuildProductType pocketPartProductType) {
        when(
            chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution()
                .getExecutionContext()
                .get(JobParameterKey.XPP_BUNDLES)).thenReturn(getXppBundlesList(pocketPartProductType));
    }

    private List<XppBundle> getXppBundlesList(final XppBundleWebBuildProductType pocketPartProductType) {
        return new ArrayList<>(getXppBundlesMap(pocketPartProductType).values());
    }

    private Map<String, XppBundle> getXppBundlesMap(final XppBundleWebBuildProductType pocketPartProductType) {
        final XppBundle mainBundle = new XppBundle();
        mainBundle.setMaterialNumber(MATERIAL_NUMBER_MAIN_CONTENT);
        mainBundle.setOrderedFileList(Arrays.asList(DIVXML_XML_MAIN));
        mainBundle.setProductType("bound");
        mainBundle.setWebBuildProductType(XppBundleWebBuildProductType.BOUND_VOLUME);

        final XppBundle suppBundle = new XppBundle();
        suppBundle.setMaterialNumber(MATERIAL_NUMBER_POCKET_PART);
        suppBundle.setOrderedFileList(Arrays.asList(DIVXML_XML_SUPP));
        suppBundle.setProductType("supp");
        suppBundle.setWebBuildProductType(pocketPartProductType);

        final Map<String, XppBundle> map = new HashMap<>();
        map.put(MATERIAL_NUMBER_MAIN_CONTENT, mainBundle);
        map.put(MATERIAL_NUMBER_POCKET_PART, suppBundle);
        return map;
    }
}
