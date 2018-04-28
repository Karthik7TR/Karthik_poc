package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class CodeServiceTest {
    private final PubTypeCode PUB_TYPE_CODE = new PubTypeCode();
    private final Long PUB_TYPE_CODES_ID = Long.valueOf("3");
    private final List<PubTypeCode> ALL_PUB_TYPE_CODES = new ArrayList<>();

    private final KeywordTypeCode KEYWORD_TYPE_CODE = new KeywordTypeCode();
    private final Long KEYWORD_TYPE_CODES_ID = Long.valueOf("6");
    private final List<KeywordTypeCode> ALL_KEYWORD_TYPE_CODES = new ArrayList<>();

    private final KeywordTypeValue KEYWORD_TYPE_VALUE = new KeywordTypeValue();
    private final Long KEYWORD_TYPE_VALUES_ID = Long.valueOf("7");
    private final List<KeywordTypeValue> ALL_KEYWORD_TYPE_VALUES = new ArrayList<>();

    private CodeServiceImpl service;
    private CodeDao mockCodeDao;

    @Before
    public void setUp() {
        mockCodeDao = EasyMock.createMock(CodeDao.class);

        service = new CodeServiceImpl(mockCodeDao);

        PUB_TYPE_CODE.setId(PUB_TYPE_CODES_ID);
        KEYWORD_TYPE_CODE.setId(KEYWORD_TYPE_CODES_ID);
        KEYWORD_TYPE_VALUE.setId(KEYWORD_TYPE_VALUES_ID);
    }

    @Test
    public void testGetPubTypeCode() {
        EasyMock.expect(mockCodeDao.getPubTypeCodeById(PUB_TYPE_CODES_ID)).andReturn(PUB_TYPE_CODE);
        EasyMock.replay(mockCodeDao);
        final PubTypeCode actual = service.getPubTypeCodeById(PUB_TYPE_CODES_ID);
        Assert.assertEquals(PUB_TYPE_CODE, actual);
        EasyMock.verify(mockCodeDao);
    }

    @Test
    public void testGetAllPubTypeCodes() {
        ALL_PUB_TYPE_CODES.add(PUB_TYPE_CODE);
        EasyMock.expect(mockCodeDao.getAllPubTypeCodes()).andReturn(ALL_PUB_TYPE_CODES);
        EasyMock.replay(mockCodeDao);
        final List<PubTypeCode> actual = service.getAllPubTypeCodes();
        final List<PubTypeCode> expected = new ArrayList<>();
        expected.add(PUB_TYPE_CODE);

        Assert.assertEquals(expected, actual);
        EasyMock.verify(mockCodeDao);
    }

    @Test
    public void testGetKeywordTypeCode() {
        EasyMock.expect(mockCodeDao.getKeywordTypeCodeById(KEYWORD_TYPE_CODES_ID)).andReturn(KEYWORD_TYPE_CODE);
        EasyMock.replay(mockCodeDao);
        final KeywordTypeCode actual = service.getKeywordTypeCodeById(KEYWORD_TYPE_CODES_ID);
        Assert.assertEquals(KEYWORD_TYPE_CODE, actual);
        EasyMock.verify(mockCodeDao);
    }

    @Test
    public void testGetAllKeywordTypeCodes() {
        ALL_KEYWORD_TYPE_CODES.add(KEYWORD_TYPE_CODE);
        EasyMock.expect(mockCodeDao.getAllKeywordTypeCodes()).andReturn(ALL_KEYWORD_TYPE_CODES);
        EasyMock.replay(mockCodeDao);
        final List<KeywordTypeCode> actual = service.getAllKeywordTypeCodes();
        final List<KeywordTypeCode> expected = new ArrayList<>();
        expected.add(KEYWORD_TYPE_CODE);

        Assert.assertEquals(expected, actual);
        EasyMock.verify(mockCodeDao);
    }

    @Test
    public void testGetKeywordTypeValue() {
        EasyMock.expect(mockCodeDao.getKeywordTypeValueById(KEYWORD_TYPE_VALUES_ID)).andReturn(KEYWORD_TYPE_VALUE);
        EasyMock.replay(mockCodeDao);
        final KeywordTypeValue actual = service.getKeywordTypeValueById(KEYWORD_TYPE_VALUES_ID);
        Assert.assertEquals(KEYWORD_TYPE_VALUE, actual);
        EasyMock.verify(mockCodeDao);
    }

    @Test
    public void testGetAllKeywordTypeValues() {
        ALL_KEYWORD_TYPE_VALUES.add(KEYWORD_TYPE_VALUE);
        EasyMock.expect(mockCodeDao.getAllKeywordTypeValues()).andReturn(ALL_KEYWORD_TYPE_VALUES);
        EasyMock.replay(mockCodeDao);
        final List<KeywordTypeValue> actual = service.getAllKeywordTypeValues();
        final List<KeywordTypeValue> expected = new ArrayList<>();
        expected.add(KEYWORD_TYPE_VALUE);

        Assert.assertEquals(expected, actual);
        EasyMock.verify(mockCodeDao);
    }
}
