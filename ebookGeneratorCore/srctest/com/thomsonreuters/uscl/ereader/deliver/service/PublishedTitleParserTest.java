package com.thomsonreuters.uscl.ereader.deliver.service;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sonar.runner.commonsio.FileUtils;

import java.io.File;
import java.util.Map;

public class PublishedTitleParserTest {
    private PublishedTitleParser publishedTitleParser;

    @Before
    public void init() {
        publishedTitleParser = new PublishedTitleParser();
    }

    @Test
    public void testParserWithCanadianTitleIds() {
        Map<String, ProviewTitleContainer> result = publishedTitleParser.process(getAllPublishedTitles());
        result.forEach((titleId, container) -> {
            Assert.assertEquals(titleId.toLowerCase(), titleId);
            container.getProviewTitleInfos()
                    .forEach(titleInfo -> Assert.assertEquals(titleInfo.getTitleId(), titleInfo.getTitleId().toLowerCase()));
        });
    }


    @SneakyThrows
    private static String getAllPublishedTitles() {
        return FileUtils.readFileToString(new File(SplitPartUniteServiceTest.class.getResource("proviewTitlesWithCapsTitleIds.xml").toURI()));
    }
}
