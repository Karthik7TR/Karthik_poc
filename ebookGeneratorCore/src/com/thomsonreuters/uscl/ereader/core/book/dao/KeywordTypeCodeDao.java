package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.KeywordTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true, value = "jpaTransactionManager")
public interface KeywordTypeCodeDao extends JpaRepository<KeywordTypeCode, Long> {
    KeywordTypeCode findFirtsByName(String name);

    List<KeywordTypeCode> findAllByOrderByNameAsc();
}
