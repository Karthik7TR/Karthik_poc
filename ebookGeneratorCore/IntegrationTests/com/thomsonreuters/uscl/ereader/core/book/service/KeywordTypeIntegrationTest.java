package com.thomsonreuters.uscl.ereader.core.book.service;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.KeywordTypeCodeDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.KeywordTypeValueDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition.SourceType;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookName;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CodeServiceIntegrationTestConf.class)
@ActiveProfiles("IntegrationTests")
public class KeywordTypeIntegrationTest {
    @Autowired
    private KeywordTypeCodeSevice codeService;
    @Autowired
    private KeywordTypeCodeDao codeDao;
    @Autowired
    private KeywordTypeValueService valueService;
    @Autowired
    private KeywordTypeValueDao valueDao;
    @Autowired
    private BookDao bookDao;

    @Before
    public void setUp() {
        bookDao.deleteAll();
        codeDao.deleteAll();

        final KeywordTypeCode jurisdiction = codeDao.save(code("jurisdiction", true));
        codeDao.save(code("type", false));
        final KeywordTypeCode publisher = codeDao.save(code("publisher", true));
        codeDao.save(code("subject", false));
        codeDao.save(code("Tip", false));

        final KeywordTypeValue al = valueDao.save(value(jurisdiction, "AL"));
        final KeywordTypeValue tx = valueDao.save(value(jurisdiction, "TX"));
        final KeywordTypeValue tr = valueDao.save(value(publisher, "TR"));
        valueDao.save(value(publisher, "Manning"));

        bookDao.save(book(al));
        bookDao.save(book(tx));
        bookDao.save(book(tr));
    }

    @Test
    public void getAllKeywordCodes() {
        final List<KeywordTypeCode> codes = codeService.getAllKeywordTypeCodes();
        assertThat(codes, contains(names("jurisdiction", "publisher", "subject", "Tip", "type")));

        final List<KeywordTypeValue> keywordValues = codes.get(1).getValues();
        assertThat(keywordValues, contains(names("Manning", "TR")));
    }

    @Test
    public void deleteKeywordTypeValue() {
        final KeywordTypeValue keywordTypeValue = valueService.getAllKeywordTypeValues().get(0);
        valueService.deleteKeywordTypeValue(keywordTypeValue.getId());

        final List<KeywordTypeValue> keywordValues = valueService.getAllKeywordTypeValues();
        assertThat(keywordValues, contains(names("Manning", "TR", "TX")));

        final List<BookDefinition> books = bookDao.findAll();
        assertThat(books.get(0).getKeywordTypeValues(), empty());
        assertThat(books.get(1).getKeywordTypeValues(), not(empty()));
        assertThat(books.get(2).getKeywordTypeValues(), not(empty()));
    }

    @Test
    public void deleteKeywordTypeCode() {
        codeService.deleteKeywordTypeCode(codeService.getKeywordTypeCodeByName("jurisdiction").getId());
        final List<KeywordTypeCode> codes = codeService.getAllKeywordTypeCodes();
        assertThat(codes, contains(names("publisher", "subject", "Tip", "type")));

        final List<KeywordTypeValue> keywordValues = valueService.getAllKeywordTypeValues();
        assertThat(keywordValues, contains(names("Manning", "TR")));

        final List<BookDefinition> books = bookDao.findAll();
        assertThat(books.get(0).getKeywordTypeValues(), empty());
        assertThat(books.get(1).getKeywordTypeValues(), empty());
        assertThat(books.get(2).getKeywordTypeValues(), not(empty()));
    }

    @Test
    public void renameKeywordTypeCode() {
        final KeywordTypeCode keywordTypeCode = codeService.getAllKeywordTypeCodes().get(0);
        keywordTypeCode.setName("new name");
        codeService.saveKeywordTypeCode(keywordTypeCode);

        final List<KeywordTypeCode> codes = codeService.getAllKeywordTypeCodes();
        assertThat(codes, contains(names("new name", "publisher", "subject", "Tip", "type")));

        final List<KeywordTypeValue> keywordValues = codeService.getAllKeywordTypeCodes().get(0).getValues();
        assertThat(keywordValues, contains(names("AL", "TX")));
    }

    private Matcher<Object>[] names(final String... names) {
        return Stream.of(names).map(name -> hasProperty("name", is(name))).toArray(size -> new Matcher[size]);
    }

    private BookDefinition book(final KeywordTypeValue value) {
        final BookDefinition book = new BookDefinition();
        book.setFullyQualifiedTitleId("title_id");
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
        book.setIsSplitBook(false);
        book.setIsSplitTypeAuto(true);
        book.setLastUpdated(new Date());

        final EbookName bookName = new EbookName();
        bookName.setBookNameText("Test book name");
        bookName.setEbookDefinition(book);
        book.getEbookNames().add(bookName);

        book.setKeywordTypeValues(Collections.singleton(value));
        return book;
    }

    private KeywordTypeValue value(final KeywordTypeCode code, final String name) {
        final KeywordTypeValue value = new KeywordTypeValue();
        value.setKeywordTypeCode(code);
        value.setName(name);
        value.setLastUpdated(new Date());
        return value;
    }

    private KeywordTypeCode code(final String name, final Boolean isRequired) {
        final KeywordTypeCode code = new KeywordTypeCode();
        code.setName(name);
        code.setIsRequired(isRequired);
        code.setLastUpdatedTimeStampForKeyWordCode(new Date());
        return code;
    }
}
