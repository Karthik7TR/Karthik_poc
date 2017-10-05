package com.thomsonreuters.uscl.ereader.support.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;

/**
 * Service to manage SupportPageLink entities.
 *
 */
public interface SupportPageLinkService {
    void save(SupportPageLink spl);

    void delete(SupportPageLink spl);

    SupportPageLink findByPrimaryKey(Long id);

    List<SupportPageLink> findAllSupportPageLink();
}
