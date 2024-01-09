package com.thomsonreuters.uscl.ereader.deliver.service;

import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.runner.commonsio.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public final class SplitPartUniteServiceTest {
    private static final String TITLE_ID = "uscl/an/book3";
    private static final String VERSION = "/v1";
    private static final String PART_01 = TITLE_ID + VERSION;
    private static final String PART_02 = TITLE_ID + "_pt2" + VERSION;
    private static final String PART_03 = TITLE_ID + "_pt3" + VERSION;
    private static final String PART_20 = TITLE_ID + "_pt20" + VERSION;
    private SplitPartsUniteService splitPartsUniteService;

    private static Map<String, ProviewTitleContainer> proviewTitles;

    @BeforeClass
    public static void setUpBase() {
        proviewTitles = new PublishedTitleParser().process(getAllPublishedTitles());
    }

    @Before
    public void init() {
        splitPartsUniteService = new SplitPartsUniteServiceImpl();
    }

    @Test
    public void testUnion() {
        Map<String, ProviewTitleContainer> actualResult = splitPartsUniteService.getTitlesWithUnitedParts(proviewTitles);
        Map<String, ProviewTitleContainer> expectedResult = getExpectedMap();
        assertIsEqualWithoutOrder(actualResult, expectedResult);
        assertSplitPartsOrder(actualResult);
    }

    private void assertSplitPartsOrder(final Map<String, ProviewTitleContainer> actualResult) {
        List<String> splitParts = actualResult.get(TITLE_ID).getProviewTitleInfos().get(0).getSplitParts();
        assertEquals(PART_01, splitParts.get(0));
        assertEquals(PART_02, splitParts.get(1));
        assertEquals(PART_03, splitParts.get(2));
        assertEquals(PART_20, splitParts.get(3));
    }

    private void assertIsEqualWithoutOrder(Map<String, ProviewTitleContainer> actualResult, Map<String, ProviewTitleContainer> expectedResult) {
        actualResult.forEach((key, value) -> {
            Assert.assertTrue(expectedResult.containsKey(key));
            Assert.assertTrue(CollectionUtils.isEqualCollection(expectedResult.get(key).getProviewTitleInfos(), value.getProviewTitleInfos()));
        });
    }

    @SneakyThrows
    private static String getAllPublishedTitles() {
        return FileUtils.readFileToString(new File(SplitPartUniteServiceTest.class.getResource("proviewTitlesUniteTest.xml").toURI()));
    }
    @SneakyThrows
    private static Map<String, ProviewTitleContainer> getExpectedMap() {
        String str = FileUtils.readFileToString(new File(SplitPartUniteServiceTest.class.getResource("expected_proviewTitlesUniteTest.xml").toURI()));
        return new PublishedTitleParser().process(str);
    }
}
