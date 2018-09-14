package com.thomsonreuters.uscl.ereader.core.quality.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.core.quality.dao.QualityReportRecipientDao;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("qualityReportRecipientService")
public class QualityReportRecipientServiceImpl implements QualityReportsRecipientService {
    private final QualityReportRecipientDao dao;

    @Autowired
    public QualityReportRecipientServiceImpl(final QualityReportRecipientDao dao) {
        this.dao = dao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QualityReportRecipient> getAll() {
        return dao.findAll();
    }

    @Override
    @Transactional
    public QualityReportRecipient save(final String email) {
        return dao.save(new QualityReportRecipient(email));
    }

    @Override
    @Transactional
    public void delete(final String email) {
        dao.delete(email);
    }
}
