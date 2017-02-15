package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.StateCode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public final class CodeServiceIntegrationTest
{
    private static final Logger log = LogManager.getLogger(CodeServiceIntegrationTest.class);
    @Autowired
    private CodeService service;

    @Autowired
    private BookDefinitionService bookDefinitionService;

    @Test
    public void testGetAllStates()
    {
        final List<StateCode> stateCodes = service.getAllStateCodes();
        log.debug(stateCodes);
        Assert.assertEquals(51, stateCodes.size());
    }

    @Test
    public void testStateCodeCRUD()
    {
        // Create StateCode
        final StateCode createCode = new StateCode();
        createCode.setName("Test");
        service.saveStateCode(createCode);

        // Get
        StateCode readCode = service.getStateCodeById(createCode.getId());
        Assert.assertEquals(createCode, readCode);
        Assert.assertEquals("Test", readCode.getName());

        // Update
        readCode.setName("Test2");
        service.saveStateCode(readCode);

        // Get 2
        readCode = service.getStateCodeById(createCode.getId());
        Assert.assertEquals("Test2", readCode.getName());

        // Delete
        service.deleteStateCode(readCode);
        readCode = service.getStateCodeById(createCode.getId());
        Assert.assertEquals(null, readCode);
    }

    @Test
    public void testGetAllJuris()
    {
        final List<JurisTypeCode> codes = service.getAllJurisTypeCodes();
        log.debug(codes);
        Assert.assertEquals(54, codes.size());
    }

    @Test
    public void testJurisTypeCodeCRUD()
    {
        // Create StateCode
        final JurisTypeCode createCode = new JurisTypeCode();
        createCode.setName("Test");
        service.saveJurisTypeCode(createCode);

        // Get
        JurisTypeCode readCode = service.getJurisTypeCodeById(createCode.getId());
        Assert.assertEquals(createCode, readCode);
        Assert.assertEquals("Test", readCode.getName());

        // Update
        readCode.setName("Test2");
        service.saveJurisTypeCode(readCode);

        // Get 2
        readCode = service.getJurisTypeCodeById(createCode.getId());
        Assert.assertEquals("Test2", readCode.getName());

        // Delete
        service.deleteJurisTypeCode(readCode);
        readCode = service.getJurisTypeCodeById(createCode.getId());
        Assert.assertEquals(null, readCode);
    }

    @Test
    public void testGetAllPubType()
    {
        final List<PubTypeCode> codes = service.getAllPubTypeCodes();
        log.debug(codes);
        Assert.assertEquals(6, codes.size());
    }

    @Test
    public void testPubTypeCodeCRUD()
    {
        // Create StateCode
        final PubTypeCode createCode = new PubTypeCode();
        createCode.setName("Test");
        service.savePubTypeCode(createCode);

        // Get
        PubTypeCode readCode = service.getPubTypeCodeById(createCode.getId());
        Assert.assertEquals(createCode, readCode);
        Assert.assertEquals("Test", readCode.getName());

        // Update
        readCode.setName("Test2");
        service.savePubTypeCode(readCode);

        // Get 2
        readCode = service.getPubTypeCodeById(createCode.getId());
        Assert.assertEquals("Test2", readCode.getName());

        // Delete
        service.deletePubTypeCode(readCode);
        readCode = service.getPubTypeCodeById(createCode.getId());
        Assert.assertEquals(null, readCode);
    }

    @Test
    public void testGetAllDocumentType()
    {
        final List<DocumentTypeCode> codes = service.getAllDocumentTypeCodes();
        log.debug(codes);
        Assert.assertEquals(3, codes.size());
    }

    @Test
    public void testDocumentTypeCodeCRUD()
    {
        // Create StateCode
        final DocumentTypeCode createCode = new DocumentTypeCode();
        createCode.setName("Test");
        createCode.setAbbreviation("t");
        createCode.setUsePublishCutoffDateFlag(false);
        service.saveDocumentTypeCode(createCode);

        // Get
        DocumentTypeCode readCode = service.getDocumentTypeCodeById(createCode.getId());
        Assert.assertEquals(createCode, readCode);
        Assert.assertEquals("Test", readCode.getName());
        Assert.assertEquals("t", readCode.getAbbreviation());

        // Update
        readCode.setName("Test2");
        readCode.setAbbreviation("t2");
        service.saveDocumentTypeCode(readCode);

        // Get 2
        readCode = service.getDocumentTypeCodeById(createCode.getId());
        Assert.assertEquals("Test2", readCode.getName());
        Assert.assertEquals("t2", readCode.getAbbreviation());

        // Delete
        service.deleteDocumentTypeCode(readCode);
        readCode = service.getDocumentTypeCodeById(createCode.getId());
        Assert.assertEquals(null, readCode);
    }

    @Test
    public void testGetAllPublisher()
    {
        final List<PublisherCode> codes = service.getAllPublisherCodes();
        log.debug(codes);
        Assert.assertEquals(1, codes.size());
    }

    @Test
    public void testPublisherCodeCRUD()
    {
        // Create StateCode
        final PublisherCode createCode = new PublisherCode();
        createCode.setName("Test");
        service.savePublisherCode(createCode);

        // Get
        PublisherCode readCode = service.getPublisherCodeById(createCode.getId());
        Assert.assertEquals(createCode, readCode);
        Assert.assertEquals("Test", readCode.getName());

        // Update
        readCode.setName("Test2");
        service.savePublisherCode(readCode);

        // Get 2
        readCode = service.getPublisherCodeById(createCode.getId());
        Assert.assertEquals("Test2", readCode.getName());

        // Delete
        service.deletePublisherCode(readCode);
        readCode = service.getPublisherCodeById(createCode.getId());
        Assert.assertEquals(null, readCode);
    }

    @Test
    public void testGetAllKeywordCodes()
    {
        final List<KeywordTypeCode> codes = service.getAllKeywordTypeCodes();
        log.debug(codes);
        Assert.assertEquals(4, codes.size());
    }

    @Test
    public void testGetKeywordCodeCRUD()
    {
        // Create StateCode
        final KeywordTypeCode createCode = new KeywordTypeCode();
        createCode.setName("Test");
        service.saveKeywordTypeCode(createCode);

        final Collection<KeywordTypeValue> values = new ArrayList<>();
        for (int i = 0; i < 5; i++)
        {
            final KeywordTypeValue value = new KeywordTypeValue();
            value.setName(String.valueOf(i));
            value.setKeywordTypeCode(createCode);
            values.add(value);
            service.saveKeywordTypeValue(value);
        }
        createCode.setValues(values);
        //service.saveKeywordTypeCode(createCode);

        // Get
        KeywordTypeCode readCode = service.getKeywordTypeCodeById(createCode.getId());
        Assert.assertEquals(values, readCode.getValues());
        Assert.assertEquals(createCode, readCode);
        Assert.assertEquals("Test", readCode.getName());

        // Update
        readCode.setName("Test2");
        service.saveKeywordTypeCode(readCode);

        // Get 2
        readCode = service.getKeywordTypeCodeById(createCode.getId());
        Assert.assertEquals("Test2", readCode.getName());

        // Delete
        service.deleteKeywordTypeCode(readCode);
        readCode = service.getKeywordTypeCodeById(createCode.getId());
        final List<KeywordTypeValue> emptyValues = service.getAllKeywordTypeValues(createCode.getId());
        Assert.assertEquals(null, readCode);
        Assert.assertEquals(new ArrayList<KeywordTypeValue>(), emptyValues);
    }

    @Test
    @Ignore
    public void testGetAllKeywordValues()
    {
        final List<KeywordTypeValue> codes = service.getAllKeywordTypeValues();
        log.debug(codes);
        Assert.assertEquals(9, codes.size());
    }

    @Test
    @Ignore
    public void testGetAllKeywordValuesByCodeId()
    {
        final List<KeywordTypeValue> codes = service.getAllKeywordTypeValues(Long.parseLong("1"));
        log.debug(codes);
        Assert.assertEquals(2, codes.size());
    }

    @Test
    public void testGetKeywordValueCRUD()
    {
        // Create StateCode
        final KeywordTypeCode createCode = new KeywordTypeCode();
        createCode.setName("Test");
        service.saveKeywordTypeCode(createCode);

        final Collection<KeywordTypeValue> createValues = new ArrayList<>();
        for (int i = 0; i < 5; i++)
        {
            final KeywordTypeValue value = new KeywordTypeValue();
            value.setName(String.valueOf(i));
            value.setKeywordTypeCode(createCode);
            createValues.add(value);
            service.saveKeywordTypeValue(value);
        }

        final List<KeywordTypeValue> values = service.getAllKeywordTypeValues(createCode.getId());
        Assert.assertEquals(createValues, values);

        int size = values.size();
        for (final KeywordTypeValue value : values)
        {
            service.deleteKeywordTypeValue(value);
            size--;
            final List<KeywordTypeValue> newValues = service.getAllKeywordTypeValues(createCode.getId());
            Assert.assertEquals(size, newValues.size());
            Assert.assertFalse(newValues.contains(value));
        }

        // Delete
        final List<KeywordTypeValue> emptyValues = service.getAllKeywordTypeValues(createCode.getId());
        Assert.assertEquals(new ArrayList<KeywordTypeValue>(), emptyValues);
    }

    @Test
    public void testKeywordTypeValues()
    {
        final List<KeywordTypeValue> allValues = service.getAllKeywordTypeValues();
        final int keywordValueSize = allValues.size();
        final String titleId = "uscl/an/abcd1234_test";
        setupBookDef(titleId);

        BookDefinition book = bookDefinitionService.findBookDefinitionByTitle(titleId);
        log.debug(book);
        Set<KeywordTypeValue> values = book.getKeywordTypeValues();
        Assert.assertEquals(3, values.size());

        values.clear();
        book.setKeywordTypeValues(values);
        bookDefinitionService.saveBookDefinition(book);

        book = bookDefinitionService.findBookDefinitionByTitle(titleId);
        values = book.getKeywordTypeValues();
        Assert.assertEquals(0, values.size());

        Assert.assertEquals(keywordValueSize, service.getAllKeywordTypeValues().size());
    }

    private void setupBookDef(final String titleId)
    {
        final BookDefinition book = new BookDefinition();
        book.setFullyQualifiedTitleId(titleId);
        final DocumentTypeCode dtc = new DocumentTypeCode();
        dtc.setId(Long.parseLong("1"));
        book.setDocumentTypeCodes(dtc);

        final EbookName bookName = new EbookName();
        bookName.setBookNameText("Test book name");
        bookName.setSequenceNum(Integer.parseInt("1"));
        bookName.setEbookDefinition(book);
        book.getEbookNames().add(bookName);

        final PublisherCode publisherCode = new PublisherCode();
        publisherCode.setName("uscl");
        book.setPublisherCodes(publisherCode);

        book.setMaterialId("random");
        book.setCopyright("something");
        book.setSourceType(SourceType.NORT);
        book.setIsDeletedFlag(false);
        book.setEbookDefinitionCompleteFlag(false);
        book.setAutoUpdateSupportFlag(true);
        book.setSearchIndexFlag(true);
        book.setPublishedOnceFlag(false);
        book.setOnePassSsoLinkFlag(true);
        book.setKeyciteToplineFlag(true);
        book.setEnableCopyFeatureFlag(false);

        final Set<KeywordTypeValue> values = new HashSet<>();
        for (int i = 1; i < 4; i++)
        {
            final KeywordTypeValue value = new KeywordTypeValue();
            value.setId(Long.parseLong(Integer.toString(i)));
            values.add(value);
        }
        book.setKeywordTypeValues(values);

        bookDefinitionService.saveBookDefinition(book);
    }
}
