package com.thomsonreuters.uscl.ereader.core.book.domain;

import org.junit.Assert;
import org.junit.Test;

public class KeywordTypeCodeTest {
    private KeywordTypeCode keywordTypeCode;

    @Test
    public void testCorrectExtraction() {
        keywordTypeCode = new KeywordTypeCode();
        keywordTypeCode.setName("jurisdiction");
        Assert.assertEquals("uscl", keywordTypeCode.getPublisher());
        Assert.assertEquals("jurisdiction", keywordTypeCode.getBaseName());
    }

    @Test
    public void testCorrectExtraction2() {
        keywordTypeCode = new KeywordTypeCode();
        keywordTypeCode.setName("jurisdiction ");
        Assert.assertEquals("uscl", keywordTypeCode.getPublisher());
        Assert.assertEquals("jurisdiction", keywordTypeCode.getBaseName());
    }

    @Test
    public void testCorrectExtraction3() {
        keywordTypeCode = new KeywordTypeCode();
        keywordTypeCode.setName("jurisdiction uscl");
        Assert.assertEquals("uscl", keywordTypeCode.getPublisher());
        Assert.assertEquals("jurisdiction", keywordTypeCode.getBaseName());
    }

    @Test
    public void testCorrectExtraction4() {
        keywordTypeCode = new KeywordTypeCode();
        keywordTypeCode.setName("jurisdiction cw");
        Assert.assertEquals("cw", keywordTypeCode.getPublisher());
        Assert.assertEquals("jurisdiction", keywordTypeCode.getBaseName());
    }

    @Test
    public void testCorrectExtraction5() {
        keywordTypeCode = new KeywordTypeCode();
        keywordTypeCode.setName("type cw");
        Assert.assertEquals("all_publishers", keywordTypeCode.getPublisher());
        Assert.assertEquals("type cw", keywordTypeCode.getBaseName());
    }

    @Test
    public void testCorrectExtraction6() {
        keywordTypeCode = new KeywordTypeCode();
        keywordTypeCode.setName("type ");
        Assert.assertEquals("all_publishers", keywordTypeCode.getPublisher());
        Assert.assertEquals("type ", keywordTypeCode.getBaseName());
    }

}
