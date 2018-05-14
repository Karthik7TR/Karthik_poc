package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.KeywordTypeCodeDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.KeywordTypeValueDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KeywordTypeValueServiceImpl implements KeywordTypeValueService {
    private final KeywordTypeValueDao keywordTypeValueDao;
    private final KeywordTypeCodeDao keywordTypeCodeDao;
    private final BookDao bookDao;

    @Autowired
    public KeywordTypeValueServiceImpl(
        final KeywordTypeValueDao keywordTypeValueDao,
        final KeywordTypeCodeDao keywordTypeCodeDao,
        final BookDao bookDao) {
        this.keywordTypeValueDao = keywordTypeValueDao;
        this.keywordTypeCodeDao = keywordTypeCodeDao;
        this.bookDao = bookDao;
    }

    @Override
    public List<KeywordTypeValue> getAllKeywordTypeValues() {
        return keywordTypeValueDao.findAllByOrderByNameAsc();
    }

    @Override
    public List<KeywordTypeValue> getAllKeywordTypeValues(final Long keywordTypeCodeId) {
        return keywordTypeValueDao.findByKeywordTypeCode_IdOrderByNameAsc(keywordTypeCodeId);
    }

    @Override
    public KeywordTypeValue getKeywordTypeValueById(final Long keywordTypeValueId) {
        return keywordTypeValueDao.findOne(keywordTypeValueId);
    }

    @Override
    public void saveKeywordTypeValue(final KeywordTypeValue keywordTypeValue) {
        keywordTypeValue.setLastUpdated(new Date());
        keywordTypeValueDao.save(keywordTypeValue);
    }

    @Transactional("jpaTransactionManager")
    @Override
    public void deleteKeywordTypeValue(final Long id) {
        final List<BookDefinition> books = bookDao.findByKeywordTypeValues_Id(id);
        final KeywordTypeValue keywordTypeValue = keywordTypeValueDao.findOne(id);

        books.forEach(book -> book.getKeywordTypeValues().remove(keywordTypeValue));
        bookDao.save(books);

        final KeywordTypeCode keywordTypeCode = keywordTypeValue.getKeywordTypeCode();
        keywordTypeCode.getValues().remove(keywordTypeValue);
        keywordTypeCodeDao.save(keywordTypeCode);
    }
}
