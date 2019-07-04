package com.thomsonreuters.uscl.ereader.mgr.web.service.support;

import java.util.List;

import com.thomsonreuters.uscl.ereader.support.dao.SupportPageLinkDao;
import com.thomsonreuters.uscl.ereader.support.domain.SupportPageLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to manage SupportPageLink entities.
 *
 */
@Service("supportPageLinkService")
public class SupportPageLinkServiceImpl implements SupportPageLinkService {
    private final SupportPageLinkDao dao;

    @Autowired
    public SupportPageLinkServiceImpl(final SupportPageLinkDao dao) {
        this.dao = dao;
    }

    @Override
    @Transactional
    public void save(final SupportPageLink spl) {
        dao.save(spl);
    }

    @Override
    @Transactional
    public void delete(final SupportPageLink spl) {
        dao.delete(spl);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportPageLink findByPrimaryKey(final Long id) {
        return dao.findOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportPageLink> findAllSupportPageLink() {
        return dao.findAllByOrderByLinkDescriptionDesc();
    }
}
