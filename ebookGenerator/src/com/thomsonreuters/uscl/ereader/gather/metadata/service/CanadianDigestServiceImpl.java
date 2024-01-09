package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.dao.CanadianDigestDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianDigest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanadianDigestServiceImpl implements CanadianDigestService {
    private final CanadianDigestDao canadianDigestDao;

    @Autowired
    public CanadianDigestServiceImpl(final CanadianDigestDao canadianDigestDao) {
        this.canadianDigestDao = canadianDigestDao;
    }

    @Override
    public List<CanadianDigest> findAllByJobInstanceId(final Long jobInstanceId) {
        return canadianDigestDao.findAllByJobInstanceId(jobInstanceId);
    }
}