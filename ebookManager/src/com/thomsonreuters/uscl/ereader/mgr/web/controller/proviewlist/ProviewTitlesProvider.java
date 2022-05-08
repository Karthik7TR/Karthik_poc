package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewlist;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.thomsonreuters.uscl.ereader.deliver.exception.ProviewException;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewHandler;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleContainer;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleInfo;
import com.thomsonreuters.uscl.ereader.deliver.service.ProviewTitleReportInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviewTitlesProvider {
    private volatile Map<String, ProviewTitleContainer> allProviewTitleInfo;
    private final ProviewHandler proviewHandler;

    @Autowired
    private ProviewTitlesProvider(final ProviewHandler proviewHandler) {
        this.proviewHandler = proviewHandler;
    }

    public Map<String, ProviewTitleContainer> provideAll(final boolean isRefresh) throws ProviewException {
        if (allProviewTitleInfo == null || isRefresh) {
            synchronized (ProviewTitlesProvider.class) {
                if (allProviewTitleInfo == null || isRefresh) {
                    allProviewTitleInfo = new ConcurrentHashMap<>(proviewHandler.getTitlesWithUnitedParts());
                }
            }
        }
        return allProviewTitleInfo;
    }

    public List<ProviewTitleInfo> provideAllLatest() throws ProviewException {
        return new CopyOnWriteArrayList<>(proviewHandler.getAllLatestProviewTitleInfo(this.provideAll(false)));
    }

    public List<ProviewTitleReportInfo> provideAllLatestProviewTitleReport() throws ProviewException {
        return proviewHandler.getAllProviewTitles();
    }
}
