package com.thomsonreuters.uscl.ereader.gather.metadata.service;

import com.thomsonreuters.uscl.ereader.gather.metadata.dao.CanadianTopicCodeDao;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.CanadianTopicCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanadianTopicCodeServiceImpl implements CanadianTopicCodeService {
    @Autowired
    private CanadianTopicCodeDao canadianTopicCodeDao;

    @Override
    public List<CanadianTopicCode> findAllCanadianTopicCodesForTheBook(final Long jobInstanceId) {
        return canadianTopicCodeDao.findAllByJobInstanceId(jobInstanceId);
    }
}
