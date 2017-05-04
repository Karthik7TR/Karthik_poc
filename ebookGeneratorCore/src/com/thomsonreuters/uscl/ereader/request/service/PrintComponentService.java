package com.thomsonreuters.uscl.ereader.request.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.dao.PrintComponentDao;
import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;

public class PrintComponentService
{
    private PrintComponentDao printComponentDao;

    public Long saveRequest(final PrintComponent ebookRequest)
    {
        return printComponentDao.saveRequest(ebookRequest);
    }

    public PrintComponent findByPrimaryKey(final long printComponentId)
    {
        return printComponentDao.findByPrimaryKey(printComponentId);
    }

    public void deleteComponent(final long printComponentId)
    {
        printComponentDao.deleteComponent(printComponentId);
    }

    public PrintComponent findByComponentName(final String messageId)
    {
        return printComponentDao.findByComponentName(messageId);
    }

    public PrintComponent findByMaterialNumber(final String materialNumber)
    {
        return printComponentDao.findByMaterialNumber(materialNumber);
    }

    public List<PrintComponent> findAllRequests()
    {
        return printComponentDao.findAllRequests();
    }

    public void setPrintComponentDao(final PrintComponentDao dao)
    {
        printComponentDao = dao;
    }
}
