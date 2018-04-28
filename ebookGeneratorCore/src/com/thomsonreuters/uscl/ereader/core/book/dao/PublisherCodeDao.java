package com.thomsonreuters.uscl.ereader.core.book.dao;

import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DAO to manage PublisherCode entities
 */
public interface PublisherCodeDao extends JpaRepository<PublisherCode, Long> {
    // Intentionally left blank
}
