package com.thomsonreuters.uscl.ereader.core.quality.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.thomsonreuters.uscl.ereader.core.job.dao.AppParameterDao;
import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;
import com.thomsonreuters.uscl.ereader.core.quality.dao.QualityReportRecipientDao;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportParams;
import com.thomsonreuters.uscl.ereader.core.quality.domain.QualityReportRecipient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("qualityReportsAdminService")
public class QualityReportsAdminServiceImpl implements QualityReportsAdminService {
    private static final String QUALITY_STEP_ENABLED_PROP_KEY = "QualityStepEnabled";
    private final AppParameterDao appParameterDao;
    private final QualityReportRecipientDao dao;

    @Autowired
    public QualityReportsAdminServiceImpl(final AppParameterDao appParameterDao, final QualityReportRecipientDao dao) {
        this.appParameterDao = appParameterDao;
        this.dao = dao;
    }

    @Override
    @Transactional(readOnly = true)
    public QualityReportParams getParams() {
        final boolean qualityStepEnabled = Optional.ofNullable(appParameterDao.findOne(QUALITY_STEP_ENABLED_PROP_KEY))
            .map(AppParameter::getValue)
            .map(Boolean::parseBoolean)
            .orElse(true);
        final List<String> recipients = dao.findAll().stream()
            .map(QualityReportRecipient::getEmail)
            .collect(Collectors.toList());
        return new QualityReportParams(qualityStepEnabled, recipients);
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


    @Override
    @Transactional
    public void changeQualityStepEnableParameter(final boolean isEnable) {
        final AppParameter appParameter = new AppParameter();
        appParameter.setKey(QUALITY_STEP_ENABLED_PROP_KEY);
        appParameter.setValue(Boolean.toString(isEnable));
        appParameterDao.save(appParameter);
    }
}
