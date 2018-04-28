package com.thomsonreuters.uscl.ereader.core.book.service;

import java.util.Date;
import java.util.List;

import com.thomsonreuters.uscl.ereader.core.book.dao.PublisherCodeDao;
import com.thomsonreuters.uscl.ereader.core.book.domain.PublisherCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublisherCodeServiceImpl implements PublisherCodeService {
    private final PublisherCodeDao publisherCodeDao;

    @Autowired
    public PublisherCodeServiceImpl(final PublisherCodeDao publisherCodeDao) {
        this.publisherCodeDao = publisherCodeDao;
    }

    @NotNull
    @Override
    public List<PublisherCode> getAllPublisherCodes() {
        return publisherCodeDao.findAll();
    }

    @Nullable
    @Override
    public PublisherCode getPublisherCodeById(@NotNull final Long publisherCodeId) {
        return publisherCodeDao.findOne(publisherCodeId);
    }

    @Override
    public void savePublisherCode(@NotNull final PublisherCode publisherCode) {
        publisherCode.setLastUpdatedTimeStampForPubCode(new Date());
        publisherCodeDao.save(publisherCode);
    }

    @Override
    public void deletePublisherCode(@NotNull final PublisherCode publisherCode) {
        publisherCodeDao.delete(publisherCode);
    }
}
