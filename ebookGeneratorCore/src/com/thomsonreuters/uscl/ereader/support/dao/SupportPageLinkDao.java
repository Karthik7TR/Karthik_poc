package com.thomsonreuters.uscl.ereader.support.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;

/**
 * DAO to manage SupportPageLink entities.
 *
 */
public interface SupportPageLinkDao {
    void save(SupportPageLink spl);

    void delete(SupportPageLink spl);

    SupportPageLink findByPrimaryKey(Long id);

    List<SupportPageLink> findAllSupportPageLink();
}
