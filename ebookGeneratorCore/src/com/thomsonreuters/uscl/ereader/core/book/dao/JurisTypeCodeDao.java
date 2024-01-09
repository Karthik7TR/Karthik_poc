package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dao for JurisTypeCode
 */
public interface JurisTypeCodeDao extends JpaRepository<JurisTypeCode, Long> {
    @Transactional(value = "jpaTransactionManager", readOnly = true)
    JurisTypeCode findByNameIgnoreCase(String name);

    List<JurisTypeCode> findAllByOrderByNameAsc();
}
