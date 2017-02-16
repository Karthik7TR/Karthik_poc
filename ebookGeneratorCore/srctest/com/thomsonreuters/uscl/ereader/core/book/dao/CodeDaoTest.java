package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.ArrayList;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import org.easymock.EasyMock;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class CodeDaoTest
{
    private final JurisTypeCode JURIS_TYPE_CODE = new JurisTypeCode();
    private final Long JURIS_TYPE_CODES_ID = Long.valueOf("1");
    private final List<JurisTypeCode> ALL_JURIS_TYPE_CODES = new ArrayList<>();

    private final PubTypeCode PUB_TYPE_CODE = new PubTypeCode();
    private final Long PUB_TYPE_CODES_ID = Long.valueOf("1");
    private final List<PubTypeCode> ALL_PUB_TYPE_CODES = new ArrayList<>();

    private final DocumentTypeCode DOCUMENT_TYPE_CODE = new DocumentTypeCode();
    private final Long DOCUMENT_TYPE_CODES_ID = Long.valueOf("4");
    private final List<DocumentTypeCode> ALL_DOCUMENT_TYPE_CODES = new ArrayList<>();

    private final PublisherCode PUBLISHER_CODE = new PublisherCode();
    private final Long PUBLISHER_CODES_ID = Long.valueOf("5");
    private final List<PublisherCode> ALL_PUBLISHER_CODES = new ArrayList<>();

    private final KeywordTypeCode KEYWORD_TYPE_CODE = new KeywordTypeCode();
    private final Long KEYWORD_TYPE_CODES_ID = Long.valueOf("6");
    private final List<KeywordTypeCode> ALL_KEYWORD_TYPE_CODES = new ArrayList<>();

    private final KeywordTypeValue KEYWORD_TYPE_VALUE = new KeywordTypeValue();
    private final Long KEYWORD_TYPE_VALUES_ID = Long.valueOf("7");
    private final List<KeywordTypeValue> ALL_KEYWORD_TYPE_VALUES = new ArrayList<>();

    private SessionFactory mockSessionFactory;
    private Session mockSession;
    private Criteria mockCriteria;
    private CodeDaoImpl dao;

    @Before
    public void setUp()
    {
        mockSessionFactory = EasyMock.createMock(SessionFactory.class);
        mockSession = EasyMock.createMock(Session.class);
        mockCriteria = EasyMock.createMock(Criteria.class);
        dao = new CodeDaoImpl(mockSessionFactory);

        JURIS_TYPE_CODE.setId(JURIS_TYPE_CODES_ID);
        PUB_TYPE_CODE.setId(PUB_TYPE_CODES_ID);
        DOCUMENT_TYPE_CODE.setId(DOCUMENT_TYPE_CODES_ID);
        PUBLISHER_CODE.setId(PUBLISHER_CODES_ID);
        KEYWORD_TYPE_CODE.setId(KEYWORD_TYPE_CODES_ID);
        KEYWORD_TYPE_VALUE.setId(KEYWORD_TYPE_VALUES_ID);
    }

    @Test
    public void testGetPubTypeCode()
    {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(PubTypeCode.class, PUB_TYPE_CODES_ID)).andReturn(PUB_TYPE_CODE);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final PubTypeCode actual = dao.getPubTypeCodeById(PUB_TYPE_CODES_ID);
        final PubTypeCode expected = new PubTypeCode();
        expected.setId(PUB_TYPE_CODES_ID);

        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }

    @Test
    public void testGetAllPubTypeCodes()
    {
        ALL_PUB_TYPE_CODES.add(PUB_TYPE_CODE);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(PubTypeCode.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(ALL_PUB_TYPE_CODES);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<PubTypeCode> actual = dao.getAllPubTypeCodes();
        final List<PubTypeCode> expected = new ArrayList<>();
        expected.add(PUB_TYPE_CODE);
        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testGetJurisTypeCode()
    {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(JurisTypeCode.class, PUB_TYPE_CODES_ID)).andReturn(JURIS_TYPE_CODE);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final JurisTypeCode actual = dao.getJurisTypeCodeById(JURIS_TYPE_CODES_ID);
        final JurisTypeCode expected = new JurisTypeCode();
        expected.setId(PUB_TYPE_CODES_ID);

        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }

    @Test
    public void testGetAllJurisTypeCodes()
    {
        ALL_JURIS_TYPE_CODES.add(JURIS_TYPE_CODE);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(JurisTypeCode.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(ALL_JURIS_TYPE_CODES);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<JurisTypeCode> actual = dao.getAllJurisTypeCodes();
        final List<JurisTypeCode> expected = new ArrayList<>();
        expected.add(JURIS_TYPE_CODE);
        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testGetDocumentTypeCode()
    {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(DocumentTypeCode.class, DOCUMENT_TYPE_CODES_ID)).andReturn(DOCUMENT_TYPE_CODE);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final DocumentTypeCode actual = dao.getDocumentTypeCodeById(DOCUMENT_TYPE_CODES_ID);
        final DocumentTypeCode expected = new DocumentTypeCode();
        expected.setId(DOCUMENT_TYPE_CODES_ID);

        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }

    @Test
    public void testGetAllDocumentTypeCodes()
    {
        ALL_DOCUMENT_TYPE_CODES.add(DOCUMENT_TYPE_CODE);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(DocumentTypeCode.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(ALL_DOCUMENT_TYPE_CODES);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<DocumentTypeCode> actual = dao.getAllDocumentTypeCodes();
        final List<DocumentTypeCode> expected = new ArrayList<>();
        expected.add(DOCUMENT_TYPE_CODE);
        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testGetPublisherCode()
    {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(PublisherCode.class, PUBLISHER_CODES_ID)).andReturn(PUBLISHER_CODE);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final PublisherCode actual = dao.getPublisherCodeById(PUBLISHER_CODES_ID);
        final PublisherCode expected = new PublisherCode();
        expected.setId(PUBLISHER_CODES_ID);

        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }

    @Test
    public void testGetAllPublisherCodes()
    {
        ALL_PUBLISHER_CODES.add(PUBLISHER_CODE);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(PublisherCode.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(ALL_PUBLISHER_CODES);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<PublisherCode> actual = dao.getAllPublisherCodes();
        final List<PublisherCode> expected = new ArrayList<>();
        expected.add(PUBLISHER_CODE);
        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testGetKeywordTypeCode()
    {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(KeywordTypeCode.class, KEYWORD_TYPE_CODES_ID)).andReturn(KEYWORD_TYPE_CODE);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final KeywordTypeCode actual = dao.getKeywordTypeCodeById(KEYWORD_TYPE_CODES_ID);
        final KeywordTypeCode expected = new KeywordTypeCode();
        expected.setId(KEYWORD_TYPE_CODES_ID);

        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }

    @Test
    public void testGetAllKeywordTypeCodes()
    {
        KEYWORD_TYPE_VALUE.setName("Value");
        KEYWORD_TYPE_CODE.getValues().add(KEYWORD_TYPE_VALUE);
        KEYWORD_TYPE_CODE.setName("Key");
        ALL_KEYWORD_TYPE_CODES.add(KEYWORD_TYPE_CODE);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(KeywordTypeCode.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(ALL_KEYWORD_TYPE_CODES);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<KeywordTypeCode> actual = dao.getAllKeywordTypeCodes();
        final List<KeywordTypeCode> expected = new ArrayList<>();
        expected.add(KEYWORD_TYPE_CODE);
        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }

    @Test
    public void testGetKeywordTypeValue()
    {
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.get(KeywordTypeValue.class, KEYWORD_TYPE_VALUES_ID)).andReturn(KEYWORD_TYPE_VALUE);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);

        final KeywordTypeValue actual = dao.getKeywordTypeValueById(KEYWORD_TYPE_VALUES_ID);
        final KeywordTypeValue expected = new KeywordTypeValue();
        expected.setId(KEYWORD_TYPE_VALUES_ID);

        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
    }

    @Test
    public void testGetAllKeywordTypeValues()
    {
        ALL_KEYWORD_TYPE_VALUES.add(KEYWORD_TYPE_VALUE);
        EasyMock.expect(mockSessionFactory.getCurrentSession()).andReturn(mockSession);
        EasyMock.expect(mockSession.createCriteria(KeywordTypeValue.class)).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.addOrder(EasyMock.anyObject(Order.class))).andReturn(mockCriteria);
        EasyMock.expect(mockCriteria.list()).andReturn(ALL_KEYWORD_TYPE_VALUES);
        EasyMock.replay(mockSessionFactory);
        EasyMock.replay(mockSession);
        EasyMock.replay(mockCriteria);

        final List<KeywordTypeValue> actual = dao.getAllKeywordTypeValues();
        final List<KeywordTypeValue> expected = new ArrayList<>();
        expected.add(KEYWORD_TYPE_VALUE);
        Assert.assertEquals(expected, actual);

        EasyMock.verify(mockSessionFactory);
        EasyMock.verify(mockSession);
        EasyMock.verify(mockCriteria);
    }
}
