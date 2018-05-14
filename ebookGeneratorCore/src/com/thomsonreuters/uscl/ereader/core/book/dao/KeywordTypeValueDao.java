package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true, value = "jpaTransactionManager")
public interface KeywordTypeValueDao extends JpaRepository<KeywordTypeValue, Long> {
    List<KeywordTypeValue> findAllByOrderByNameAsc();
    List<KeywordTypeValue> findByKeywordTypeCode_IdOrderByNameAsc(Long id);
}
