package com.thomsonreuters.uscl.ereader.core.job.dao;

import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;

public interface AppParameterDao {
    AppParameter findByPrimaryKey(String key);

    void save(AppParameter param);

    void delete(AppParameter param);
}
