package com.thomsonreuters.uscl.ereader.core.book.service;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sequenceOf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CodeServiceIntegrationTestConf.class)
@ActiveProfiles("IntegrationTests")
public class CodeServiceIntegrationTest {
    private static final Logger log = LogManager.getLogger(CodeServiceIntegrationTest.class);
    @Autowired
    private DataSource dataSource;
    @Autowired
    private CodeService service;
    @Autowired
    private BookDefinitionService bookDefinitionService;

    @Test
    public void testGetAllPubType() {
        final DbSetup dbSetup = new DbSetup(
            new DataSourceDestination(dataSource),
            sequenceOf(
                Operations.deleteAllFrom("PUB_TYPE_CODES"),
                insertInto("PUB_TYPE_CODES")
                    .columns("PUB_TYPE_CODES_ID", "PUB_TYPE_CODES_NAME", "LAST_UPDATED")
                    .values(1, "Local", new Date())
                    .values(2, "State", new Date())
                    .values(3, "Fed", new Date())
                    .values(4, "FedRule", new Date())
                    .values(5, "FedDist", new Date())
                    .values(6, "Bankr", new Date())
                    .build()));
        dbSetup.launch();

        final List<PubTypeCode> codes = service.getAllPubTypeCodes();
        log.debug(codes);
        Assert.assertEquals(6, codes.size());
    }

    @Test
    public void testPubTypeCodeCRUD() {
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
    public void testGetAllKeywordCodes() {
        final DbSetup dbSetup = new DbSetup(
            new DataSourceDestination(dataSource),
            sequenceOf(
                Operations.deleteAllFrom("KEYWORD_TYPE_CODES"),
                insertInto("KEYWORD_TYPE_CODES")
                    .columns("KEYWORD_TYPE_CODES_ID", "KEYWORD_TYPE_CODES_NAME", "LAST_UPDATED", "IS_REQUIRED")
                    .values(1, "jurisdiction", new Date(), "Y")
                    .values(2, "type", new Date(), "N")
                    .values(3, "publisher", new Date(), "Y")
                    .values(4, "subject", new Date(), "N")
                    .build()));
        dbSetup.launch();

        final List<KeywordTypeCode> codes = service.getAllKeywordTypeCodes();
        log.debug(codes);
        Assert.assertEquals(4, codes.size());
    }

    @Test
    @Ignore
    public void testGetKeywordCodeCRUD() {
        // Create StateCode
        final KeywordTypeCode createCode = new KeywordTypeCode();
        createCode.setName("Test");
        service.saveKeywordTypeCode(createCode);

        final Collection<KeywordTypeValue> values = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
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
    public void testGetAllKeywordValues() {
        final List<KeywordTypeValue> codes = service.getAllKeywordTypeValues();
        log.debug(codes);
        Assert.assertEquals(9, codes.size());
    }

    @Test
    @Ignore
    public void testGetAllKeywordValuesByCodeId() {
        final List<KeywordTypeValue> codes = service.getAllKeywordTypeValues(Long.parseLong("1"));
        log.debug(codes);
        Assert.assertEquals(2, codes.size());
    }

    @Test
    @Ignore
    public void testGetKeywordValueCRUD() {
        // Create StateCode
        final KeywordTypeCode createCode = new KeywordTypeCode();
        createCode.setName("Test");
        service.saveKeywordTypeCode(createCode);

        final Collection<KeywordTypeValue> createValues = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final KeywordTypeValue value = new KeywordTypeValue();
            value.setName(String.valueOf(i));
            value.setKeywordTypeCode(createCode);
            createValues.add(value);
            service.saveKeywordTypeValue(value);
        }

        final List<KeywordTypeValue> values = service.getAllKeywordTypeValues(createCode.getId());
        Assert.assertEquals(createValues, values);

        int size = values.size();
        for (final KeywordTypeValue value : values) {
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
    @Ignore
    public void testKeywordTypeValues() {
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

    private void setupBookDef(final String titleId) {
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
        for (int i = 1; i < 4; i++) {
            final KeywordTypeValue value = new KeywordTypeValue();
            value.setId(Long.parseLong(Integer.toString(i)));
            values.add(value);
        }
        book.setKeywordTypeValues(values);

        bookDefinitionService.saveBookDefinition(book);
    }
}
