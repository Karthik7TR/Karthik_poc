package com.thomsonreuters.uscl.ereader.core.job.dao;

import com.thomsonreuters.uscl.ereader.core.job.domain.AppParameter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppParameterDao extends JpaRepository<AppParameter, String> {

}
