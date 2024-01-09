package com.thomsonreuters.uscl.ereader.request.dao;

import com.thomsonreuters.uscl.ereader.request.domain.PrintComponent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrintComponentDao extends JpaRepository<PrintComponent, Long> {

    PrintComponent findFirstByComponentName(String componentName);
    PrintComponent findFirstByMaterialNumber(String materialNumber);
}
