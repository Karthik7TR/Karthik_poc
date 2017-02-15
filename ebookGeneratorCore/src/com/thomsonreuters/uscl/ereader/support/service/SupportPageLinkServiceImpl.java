package com.thomsonreuters.uscl.ereader.support.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.support.dao.SupportPageLinkDao;
import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to manage SupportPageLink entities.
 *
 */

public class SupportPageLinkServiceImpl implements SupportPageLinkService
{
    private SupportPageLinkDao dao;

    @Override
    @Transactional
    public void save(final SupportPageLink spl)
    {
        dao.save(spl);
    }

    @Override
    @Transactional
    public void delete(final SupportPageLink spl)
    {
        dao.delete(spl);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportPageLink findByPrimaryKey(final Long id)
    {
        return dao.findByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportPageLink> findAllSupportPageLink()
    {
        return dao.findAllSupportPageLink();
    }

    @Required
    public void setSupportPageLinkDao(final SupportPageLinkDao dao)
    {
        this.dao = dao;
    }
}
