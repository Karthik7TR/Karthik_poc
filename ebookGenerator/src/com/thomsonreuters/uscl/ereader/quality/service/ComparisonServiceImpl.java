package com.thomsonreuters.uscl.ereader.quality.service;

import java.util.List;

import com.thomsonreuters.uscl.ereader.quality.domain.request.CompareUnit;
import com.thomsonreuters.uscl.ereader.quality.domain.request.JsonRequest;
import com.thomsonreuters.uscl.ereader.quality.domain.response.JsonResponse;
import com.thomsonreuters.uscl.ereader.quality.helper.FtpManager;
import com.thomsonreuters.uscl.ereader.quality.helper.QualityUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Scope("prototype")
public class ComparisonServiceImpl implements ComparisonService {
    private FtpManager ftpManager;
    private QualityUtil qualityUtil;
    private RestTemplate restTemplate;
    private String dtApiUrl;

    @Autowired
    public ComparisonServiceImpl(
        final FtpManager ftpManager,
        final QualityUtil qualityUtil,
        @Qualifier("restTemplate") final RestTemplate restTemplate,
        @Value("${xpp.quality.webservice}") final String dtApiUrl) {
        this.ftpManager = ftpManager;
        this.qualityUtil = qualityUtil;
        this.restTemplate = restTemplate;
        this.dtApiUrl = dtApiUrl;
    }

    @Override
    public JsonResponse compare(final List<CompareUnit> compareUnitList) {
        ftpManager.connect();
        try {
            compareUnitList.forEach(this::uploadFilesToFtpServer);
        } finally {
            ftpManager.disconnect();
        }
        final JsonRequest jsonRequest = qualityUtil.createJsonRequest(compareUnitList);
        return restTemplate.postForObject(dtApiUrl, jsonRequest, JsonResponse.class);
    }

    @SneakyThrows
    private void uploadFilesToFtpServer(final CompareUnit compareUnit) {
        ftpManager.uploadFile(compareUnit.getSource());
        ftpManager.uploadFile(compareUnit.getTarget());
    }
}
