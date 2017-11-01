package com.thomsonreuters.uscl.ereader.request.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.request.dao.XppBundleArchiveDao;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("xppBundleArchiveService")
@Transactional(readOnly = true)
public class XppBundleArchiveService {
    private final XppBundleArchiveDao xppBundleArchiveDao;

    @Autowired
    public XppBundleArchiveService(final XppBundleArchiveDao xppBundleArchiveDao) {
        this.xppBundleArchiveDao = xppBundleArchiveDao;
    }

    @Transactional
    public Long saveRequest(final XppBundleArchive ebookRequest) {
        return xppBundleArchiveDao.saveRequest(ebookRequest);
    }

    public XppBundleArchive findByPrimaryKey(final long ebookRequestId) {
        return xppBundleArchiveDao.findByPrimaryKey(ebookRequestId);
    }

    @Transactional
    public void deleteRequest(final long ebookRequestId) {
        xppBundleArchiveDao.deleteRequest(ebookRequestId);
    }

    public XppBundleArchive findByRequestId(final String requestId) {
        return xppBundleArchiveDao.findByRequestId(requestId);
    }

    public XppBundleArchive findByMaterialNumber(final String materialNumber) {
        return xppBundleArchiveDao.findByMaterialNumber(materialNumber);
    }

    public List<XppBundleArchive> findAllRequests() {
        return xppBundleArchiveDao.findAllRequests();
    }

    public List<XppBundleArchive> findByMaterialNumberList(final List<String> sourceMaterialNumberList) {
        return xppBundleArchiveDao.findByMaterialNumberList(sourceMaterialNumberList);
    }
}
