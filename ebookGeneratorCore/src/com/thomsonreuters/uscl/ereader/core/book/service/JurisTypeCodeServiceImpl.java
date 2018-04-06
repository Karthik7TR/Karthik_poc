package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.JurisTypeCodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.JurisTypeCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JurisTypeCodeServiceImpl implements JurisTypeCodeService {
    private final JurisTypeCodeDao jurisTypeCodeDao;

    @Autowired
    public JurisTypeCodeServiceImpl(final JurisTypeCodeDao jurisTypeCodeDao) {
        this.jurisTypeCodeDao = jurisTypeCodeDao;
    }

    @Override
    public List<JurisTypeCode> getAllJurisTypeCodes() {
        return jurisTypeCodeDao.findAll();
    }

    @Override
    public JurisTypeCode getJurisTypeCodeById(final Long jurisTypeCodeId) {
        return jurisTypeCodeDao.findOne(jurisTypeCodeId);
    }

    @Override
    public JurisTypeCode getJurisTypeCodeByName(final String jurisTypeCodeName) {
        return jurisTypeCodeDao.findByNameIgnoreCase(jurisTypeCodeName);
    }

    @Override
    public void saveJurisTypeCode(final JurisTypeCode jurisTypeCode) {
        jurisTypeCode.setLastUpdated(new Date());
        jurisTypeCodeDao.save(jurisTypeCode);
    }

    @Override
    public void deleteJurisTypeCode(final JurisTypeCode jurisTypeCode) {
        jurisTypeCodeDao.delete(jurisTypeCode);
    }
}
