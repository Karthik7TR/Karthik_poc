package com.thomsonreuters.uscl.ereader.core.book.statecode;

import java.util.Date;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

public class StateCodeServiceImpl implements StateCodeService
{
    private StateCodeDao dao;

    @Transactional(readOnly = true)
    @Override
    public List<StateCode> getAllStateCodes()
    {
        return dao.getAllStateCodes();
    }

    @Transactional(readOnly = true)
    @Override
    public StateCode getStateCodeById(@NotNull final Long stateCodeId)
    {
        return dao.getStateCodeById(stateCodeId);
    }

    @Transactional(readOnly = true)
    @Override
    public StateCode getStateCodeByName(@NotNull final String stateCodeName)
    {
        return dao.getStateCodeByName(stateCodeName);
    }

    @Transactional
    @Override
    public void saveStateCode(@NotNull final StateCode stateCode)
    {
        stateCode.setLastUpdated(new Date());
        if (stateCode.getId() == null)
        {
            dao.createStateCode(stateCode);
        }
        else
        {
            dao.updateStateCode(stateCode);
        }
    }

    @Transactional
    @Override
    public void deleteStateCode(@NotNull final StateCode stateCode)
    {
        dao.deleteStateCode(stateCode);
    }

    @Required
    public void setStateCodeDao(final StateCodeDao dao)
    {
        this.dao = dao;
    }
}
