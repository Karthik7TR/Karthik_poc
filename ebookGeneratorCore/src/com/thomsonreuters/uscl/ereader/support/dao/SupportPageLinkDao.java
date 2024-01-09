package com.thomsonreuters.uscl.ereader.support.dao;

import java.util.List;

import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DAO to manage SupportPageLink entities.
 *
 */
public interface SupportPageLinkDao extends JpaRepository<SupportPageLink, Long> {

    List<SupportPageLink> findAllByOrderByLinkDescriptionDesc();
}
