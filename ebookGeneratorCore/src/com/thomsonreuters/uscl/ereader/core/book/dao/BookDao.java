package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.BookDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true, value = "jpaTransactionManager")
public interface BookDao extends JpaRepository<BookDefinition, Long> {
    List<BookDefinition> findByKeywordTypeValues_Id(Long keywordTypeValueId);
    List<BookDefinition> findByKeywordTypeValues_KeywordTypeCode_Id(Long keywordTypeValueId);
}
