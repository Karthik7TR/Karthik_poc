package com.thomsonreuters.uscl.ereader.request.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.dao.XppBundleArchiveDao;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import org.springframework.beans.factory.annotation.Required;

public class XppBundleArchiveService
{
    private XppBundleArchiveDao xppBundleArchiveDao;

    public Long saveRequest(final XppBundleArchive ebookRequest)
    {
        return xppBundleArchiveDao.saveRequest(ebookRequest);
    }

    public XppBundleArchive findByPrimaryKey(final long ebookRequestId)
    {
        return xppBundleArchiveDao.findByPrimaryKey(ebookRequestId);
    }

    public void deleteRequest(final long ebookRequestId)
    {
        xppBundleArchiveDao.deleteRequest(ebookRequestId);
    }

    public XppBundleArchive findByRequestId(final String requestId)
    {
        return xppBundleArchiveDao.findByRequestId(requestId);
    }

    public XppBundleArchive findByMaterialNumber(final String materialNumber)
    {
        return xppBundleArchiveDao.findByMaterialNumber(materialNumber);
    }

    public List<XppBundleArchive> findAllRequests()
    {
        return xppBundleArchiveDao.findAllRequests();
    }

    @Required
    public void setXppBundleArchiveDao(final XppBundleArchiveDao dao)
    {
        xppBundleArchiveDao = dao;
    }
}
