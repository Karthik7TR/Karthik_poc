package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.CodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class CodeServiceTest {
    private final PubTypeCode PUB_TYPE_CODE = new PubTypeCode();
    private final Long PUB_TYPE_CODES_ID = Long.valueOf("3");
    private final List<PubTypeCode> ALL_PUB_TYPE_CODES = new ArrayList<>();

    private CodeServiceImpl service;
    private CodeDao mockCodeDao;

    @Before
    public void setUp() {
        mockCodeDao = EasyMock.createMock(CodeDao.class);
        service = new CodeServiceImpl(mockCodeDao);
        PUB_TYPE_CODE.setId(PUB_TYPE_CODES_ID);
    }

    @Test
    public void testGetPubTypeCode() {
        EasyMock.expect(mockCodeDao.findOne(PUB_TYPE_CODES_ID)).andReturn(PUB_TYPE_CODE);
        EasyMock.replay(mockCodeDao);
        final PubTypeCode actual = service.getPubTypeCodeById(PUB_TYPE_CODES_ID);
        Assert.assertEquals(PUB_TYPE_CODE, actual);
        EasyMock.verify(mockCodeDao);
    }

    @Test
    public void testGetAllPubTypeCodes() {
        ALL_PUB_TYPE_CODES.add(PUB_TYPE_CODE);
        EasyMock.expect(mockCodeDao.findAllByOrderByNameAsc()).andReturn(ALL_PUB_TYPE_CODES);
        EasyMock.replay(mockCodeDao);
        final List<PubTypeCode> actual = service.getAllPubTypeCodes();
        final List<PubTypeCode> expected = new ArrayList<>();
        expected.add(PUB_TYPE_CODE);

        Assert.assertEquals(expected, actual);
        EasyMock.verify(mockCodeDao);
    }
}
