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
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public final class SplitPartUniteServiceTest {

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
