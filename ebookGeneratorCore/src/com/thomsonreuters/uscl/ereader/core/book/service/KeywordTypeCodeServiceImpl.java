package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.thomsonreuters.uscl.ereader.core.book.dao.BookDao;
import com.thomsonreuters.uscl.ereader.core.book.dao.KeywordTypeCodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KeywordTypeCodeServiceImpl implements KeywordTypeCodeSevice {
    private final KeywordTypeCodeDao keywordTypeCodeDao;
    private final BookDao bookDao;

    @Autowired
    public KeywordTypeCodeServiceImpl(final KeywordTypeCodeDao keywordTypeCodeDao, final BookDao bookDao) {
        this.keywordTypeCodeDao = keywordTypeCodeDao;
        this.bookDao = bookDao;
    }

    @Override
    public List<KeywordTypeCode> getAllKeywordTypeCodes() {
        return keywordTypeCodeDao.findAllByOrderByNameAsc();
    }

    @Override
    public KeywordTypeCode getKeywordTypeCodeById(final Long keywordTypeCodeId) {
        return keywordTypeCodeDao.findOne(keywordTypeCodeId);
    }

    @Override
    public KeywordTypeCode getKeywordTypeCodeByName(final String keywordTypeCodeName) {
        return keywordTypeCodeDao.findFirtsByName(keywordTypeCodeName);
    }

    @Transactional("jpaTransactionManager")
    @Override
    public void saveKeywordTypeCode(final KeywordTypeCode keywordTypeCode) {
        final List<KeywordTypeValue> values = Optional.ofNullable(keywordTypeCode.getId())
            .map(this::getKeywordTypeCodeById)
            .map(KeywordTypeCode::getValues)
            .orElseGet(Collections::emptyList);
        keywordTypeCode.setLastUpdatedTimeStampForKeyWordCode(new Date());
        keywordTypeCode.setValues(values);
        keywordTypeCodeDao.save(keywordTypeCode);
    }

    @Transactional("jpaTransactionManager")
    @Override
    public void deleteKeywordTypeCode(final Long id) {
        final List<BookDefinition> books = bookDao.findByKeywordTypeValues_KeywordTypeCode_Id(id);
        final KeywordTypeCode keywordTypeCode = keywordTypeCodeDao.findOne(id);

        books.forEach(book -> book.getKeywordTypeValues().removeAll(keywordTypeCode.getValues()));
        bookDao.save(books);
        keywordTypeCodeDao.delete(id);
    }
}
