package com.thomsonreuters.uscl.ereader.core.book.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.domain.PubTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeDao extends JpaRepository<PubTypeCode, Long> {
    List<PubTypeCode> findAllByOrderByNameAsc();

    PubTypeCode findFirstByNameIgnoreCase(String pubTypeCodeName);
}
